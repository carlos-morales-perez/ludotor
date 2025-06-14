package com.ludotor.ludotor.activity

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.ludotor.ludotor.viewModel.BoardGameViewModel
import com.ludotor.ludotor.R
import com.ludotor.ludotor.data.BoardGame
import java.util.Date
import java.util.Locale
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream

class EditGameActivity : AppCompatActivity() {

    private val viewModel: BoardGameViewModel by viewModels()

    private var currentGameId: Int = 0
    private var isNewGame: Boolean = true

    private lateinit var nameField: EditText
    private lateinit var minPlayersField: EditText
    private lateinit var maxPlayersField: EditText
    private lateinit var playTimeField: EditText
    private lateinit var btnSave: Button
    private lateinit var ivGameImage: ImageView
    private lateinit var btnSelectImage: Button
    private lateinit var btnTakePhoto: Button

    private var currentImageUri: Uri? = null
    private var tempImageUriForCamera: Uri? = null

    private lateinit var requestCameraPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var requestStoragePermissionLauncher: ActivityResultLauncher<String>
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private lateinit var takePhotoLauncher: ActivityResultLauncher<Uri>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_game)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_game_detail)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        nameField = findViewById(R.id.etGameName)
        minPlayersField = findViewById(R.id.etMinPlayers)
        maxPlayersField = findViewById(R.id.etMaxPlayers)
        playTimeField = findViewById(R.id.etPlayTime)
        btnSave = findViewById(R.id.btnSave)
        ivGameImage = findViewById(R.id.ivGameImage)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)

        setupLaunchers()

        currentGameId = intent.getIntExtra("game_id", 0)
        isNewGame = (currentGameId == 0)

        if (!isNewGame) {
            title = "Editar Juego"
            viewModel.getGameById(currentGameId)
        } else {
            title = "Añadir Juego" // Opcional
        }

        viewModel.currentGame.observe(this) { game ->
            game?.let {
                if (!isNewGame) {
                    nameField.setText(it.name)
                    minPlayersField.setText(it.playerMin.toString())
                    maxPlayersField.setText(it.playerMax.toString())
                    playTimeField.setText(it.playTime)
                    if (it.gameImage.isNotEmpty()) {
                        currentImageUri = it.gameImage.toUri()
                        ivGameImage.setImageURI(currentImageUri)
                    }
                }
            }
        }

        btnSelectImage.setOnClickListener {
            checkStoragePermissionAndPickImage()
        }

        btnTakePhoto.setOnClickListener {
            checkCameraPermissionAndTakePhoto()
        }

        btnSave.setOnClickListener {
            saveGameDetails()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveGameDetails() {
        var isEmpty = false
        val name = nameField.text.toString().trim()
        val minPlayersStr = minPlayersField.text.toString().trim()
        val maxPlayersStr = maxPlayersField.text.toString().trim()
        val playTime = playTimeField.text.toString().trim()
        val gameImage = currentImageUri?.toString()

        if (name.isEmpty()) {
            isEmpty = true
            nameField.error = "El nombre no puede estar vacío"
            nameField.requestFocus()
        }
        if (minPlayersStr.isEmpty()) {
            isEmpty = true
            minPlayersField.error = "Mín. de jugadores no puede estar vacío"
            minPlayersField.requestFocus()
        }
        if (maxPlayersStr.isEmpty()) {
            isEmpty = true
            maxPlayersField.error = "Máx. de jugadores no puede estar vacío"
            maxPlayersField.requestFocus()
        }
        if (playTime.isEmpty()) {
            isEmpty = true
            playTimeField.error = "La duración no puede estar vacía"
            playTimeField.requestFocus()
        }

        if (isEmpty) {
            Toast.makeText(this, "Hay errores en campos requeridos", Toast.LENGTH_SHORT).show()
            return
        }

        val minPlayers = minPlayersStr.toIntOrNull()
        val maxPlayers = maxPlayersStr.toIntOrNull()

        if (minPlayers == null || minPlayers <= 0) {
            minPlayersField.error = "Valor inválido para mín. de jugadores"
            minPlayersField.requestFocus()
            return
        }
        if (maxPlayers == null || maxPlayers < minPlayers) {
            maxPlayersField.error = "Máx. de jugadores debe ser mayor o igual al mínimo"
            maxPlayersField.requestFocus()
            return
        }

        val gameToSave = BoardGame(
            boardGameId = if (isNewGame) 0 else currentGameId,
            name = name,
            playerMin = minPlayers,
            playerMax = maxPlayers,
            playTime = playTime,
            gameImage = gameImage?: ""
        )

        viewModel.saveGame(gameToSave)
        setResult(RESULT_OK)
        Toast.makeText(this, "Juego guardado", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun setupLaunchers() {
        requestCameraPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    launchCamera()
                } else {
                    Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
                }
            }

        requestStoragePermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    launchGallery()
                } else {
                    Toast.makeText(this, "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT).show()
                }
            }

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val internalFileName = "IMG_GALLERY_${timeStamp}.jpg"
                val internalUri = copyUriToInternalStorage(it, internalFileName)
                if (internalUri != null) {
                    currentImageUri = internalUri
                    ivGameImage.setImageURI(currentImageUri)
                    Log.d("EditGameActivity", "Copiado a URI interna: $currentImageUri")
                } else {
                    currentImageUri = it
                    ivGameImage.setImageURI(currentImageUri)
                    Log.w("EditGameActivity", "Fallo al copiar al almacenamiento interno, usando URI original: $currentImageUri")
                }
            }
        }

        takePhotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
            if (success) {
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val internalFileName = "IMG_GALLERY_${timeStamp}.jpg"
                val internalUri = copyUriToInternalStorage(tempImageUriForCamera!!, internalFileName)

                if (internalUri != null) {
                    currentImageUri = internalUri
                    ivGameImage.setImageURI(currentImageUri)
                }

                Log.d("EditGameActivity", "Cámara URI: $currentImageUri")
            } else {
                Log.e("EditGameActivity", "Fallo al tomar la foto.")
                tempImageUriForCamera?.let { contentResolver.delete(it, null, null) }
            }
        }
    }

    private fun checkStoragePermissionAndPickImage() {
        val permissionToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(
                this,
                permissionToRequest
            ) == PackageManager.PERMISSION_GRANTED -> {
                launchGallery()
            }

            shouldShowRequestPermissionRationale(permissionToRequest) -> {

                Toast.makeText(
                    this,
                    "Se necesita permiso de almacenamiento para seleccionar imágenes.",
                    Toast.LENGTH_LONG
                ).show()
                requestStoragePermissionLauncher.launch(permissionToRequest)
            }

            else -> {
                requestStoragePermissionLauncher.launch(permissionToRequest)
            }
        }
    }

    private fun launchGallery() {
        pickImageLauncher.launch("image/*")
    }

    private fun checkCameraPermissionAndTakePhoto() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                launchCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                Toast.makeText(this, "Se necesita permiso de cámara para tomar fotos.", Toast.LENGTH_LONG).show()
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            else -> {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun createImageUriForCamera(): Uri? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$imageFileName.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/LudotorApp")
            }
        }

        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }

    private fun launchCamera() {
        tempImageUriForCamera = createImageUriForCamera()
        if (tempImageUriForCamera == null) {
            Toast.makeText(this, "Error al preparar para tomar foto", Toast.LENGTH_SHORT).show()
            return
        }
        takePhotoLauncher.launch(tempImageUriForCamera!!)
    }

    private fun copyUriToInternalStorage(uri: Uri, fileName: String): Uri? {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val file = File(filesDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            Log.d("EditGameActivity", "Archivo copiado")
            return Uri.fromFile(file)
        } catch (e: Exception) {
            Log.e("EditGameActivity", "Error al copiar archivo", e)
            return null
        }
    }
}