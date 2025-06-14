package com.ludotor.ludotor.activity

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.ludotor.ludotor.R
import com.ludotor.ludotor.adapter.LoanAdapter
import com.ludotor.ludotor.data.Loan
import com.ludotor.ludotor.data.Status
import com.ludotor.ludotor.viewModel.BoardGameViewModel
import com.ludotor.ludotor.viewModel.LoanViewModel
import com.ludotor.ludotor.viewModel.StatusViewModel
import java.sql.Date
import java.util.Locale

class GameDetailActivity : AppCompatActivity() {

    private val boardGameViewModel: BoardGameViewModel by viewModels()
    private val statusViewModel: StatusViewModel by viewModels()
    private val loanViewModel: LoanViewModel by viewModels()

    private var currentGameId: Int = 0

    private lateinit var nameField: TextView
    private lateinit var playersField: TextView
    private lateinit var playTimeField: TextView
    private lateinit var hasSleeveField: TextView
    private lateinit var dateIncorporationField: TextView
    private lateinit var commentField: TextView
    private lateinit var hasSleevesLayout: LinearLayout
    private lateinit var dateIncorporationLayout: LinearLayout
    private lateinit var commentLayout: LinearLayout
    private lateinit var loansLayout: LinearLayout
    // private lateinit var gameImageField: EditText // Si tienes campo para imagen

     private lateinit var loansRecyclerView: RecyclerView // Si usas RecyclerView
     private lateinit var loanAdapter: LoanAdapter // Si usas RecyclerView

    private lateinit var editGameLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_game)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_game_detail)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        title = "Detalles del Juego"

        nameField = findViewById(R.id.tv_detail_game_name)
        playersField = findViewById(R.id.tv_detail_players)
        playTimeField = findViewById(R.id.tv_detail_play_time)
        hasSleeveField = findViewById(R.id.tv_has_sleeve)
        dateIncorporationField = findViewById(R.id.tv_add_date)
        commentField = findViewById(R.id.tv_comment)
        hasSleevesLayout = findViewById(R.id.ll_has_sleeve)
        dateIncorporationLayout = findViewById(R.id.ll_date_incorporation)
        commentLayout = findViewById(R.id.ll_status)
        loansLayout = findViewById(R.id.ll_status_list)

         loansRecyclerView = findViewById(R.id.rv_status_list)
         loanAdapter = LoanAdapter()
         loansRecyclerView.adapter = loanAdapter
         loansRecyclerView.layoutManager = LinearLayoutManager(this)

        currentGameId = intent.getIntExtra("game_id", 0)

        editGameLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                if (currentGameId != 0) {
                    boardGameViewModel.loadGameById(currentGameId)
                }
                Toast.makeText(this, "Juego actualizado", Toast.LENGTH_SHORT).show()
            }
        }

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        boardGameViewModel.loadGameById(currentGameId)
        boardGameViewModel.currentGameWithDetails.observe(this) { gameWithDetails ->
            gameWithDetails?.let { gwd ->

                val game = gwd.boardGame
                nameField.text = game.name
                playersField.text = "${game.playerMin} - ${game.playerMax}"
                playTimeField.text = "${game.playTime} minutos"
                // gameImageField.setText(it.gameImage)

                gwd.status?.let { status ->

                    var stringDate = ""
                    if (status.dateOfIncorporation != null) {
                        stringDate = sdf.format(status.dateOfIncorporation)
                    }

                    commentField.text = status.condition
                    hasSleeveField.text = if (status.sleeves) "Sí" else "No"

                    if (stringDate.isEmpty())  {
                        hasSleevesLayout.visibility = View.GONE
                    } else {
                        dateIncorporationField.text = stringDate
                        dateIncorporationLayout.visibility = View.VISIBLE
                    }

                    if (commentField.text.isEmpty()) {
                        commentLayout.visibility = View.GONE
                    } else {
                        commentLayout.visibility = View.VISIBLE
                    }

                    hasSleevesLayout.visibility = View.VISIBLE

                } ?: run {

                    hasSleevesLayout = findViewById(R.id.ll_has_sleeve)
                    dateIncorporationLayout = findViewById(R.id.ll_date_incorporation)
                    commentLayout = findViewById(R.id.ll_status)
                    hasSleevesLayout.visibility = View.GONE
                    dateIncorporationLayout.visibility = View.GONE
                    commentLayout.visibility = View.GONE

                }

                if (gwd.loans.isNotEmpty()) {
                    loanAdapter.submitList(gwd.loans)
                    loansLayout.visibility = View.VISIBLE
                } else {
                    loanAdapter.submitList(emptyList())
                    loansLayout.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_game_detail, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val deleteItem = menu.findItem(R.id.action_delete_game)
        val editItem = menu.findItem(R.id.action_edit_game)
        val borrowItem = menu.findItem(R.id.action_borrow_game)
        val statusItem = menu.findItem(R.id.action_detail_game)


        val isExistingGame = currentGameId != 0
        deleteItem?.isVisible = isExistingGame
        editItem?.isVisible = isExistingGame
        borrowItem?.isVisible = isExistingGame
        statusItem?.isVisible = isExistingGame

        return super.onPrepareOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }

            R.id.action_borrow_game -> {
                if (currentGameId != 0) {
                    showAddLoanDialog()
                }
                true
            }

            R.id.action_detail_game -> {
                if (currentGameId != 0) {
                    showAddStatusDialog()
                }
                true
            }

            R.id.action_edit_game -> {
                if (currentGameId != 0) {
                    val intent = Intent(this, EditGameActivity::class.java).apply {
                        putExtra("game_id", currentGameId)
                    }
                    editGameLauncher.launch(intent)
                }
                true
            }

            R.id.action_delete_game -> {
                showDeleteConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDeleteConfirmationDialog() {
        if (currentGameId == 0) return

        AlertDialog.Builder(this)
            .setTitle("Confirmar Borrado")
            .setMessage("¿Estás seguro de que quieres borrar este juego? Esta acción no se puede deshacer.")
            .setPositiveButton("Borrar") { dialog, which ->
                boardGameViewModel.deleteGameById(currentGameId)
                 Toast.makeText(this, "Juego borrado", Toast.LENGTH_SHORT).show()
                finish()
            }
            .setNegativeButton("Cancelar", null)
            .setIcon(R.drawable.ic_delete)
            .show()
    }

    private fun showAddStatusDialog() {
        val dialogView =
            LayoutInflater.from(this).inflate(R.layout.dialog_add_status, null)
        val etStatusComment = dialogView.findViewById<TextInputEditText>(R.id.et_status_comment)
        val cbHasSleeves = dialogView.findViewById<CheckBox>(R.id.cb_has_sleeves)
        val etStatusDate = dialogView.findViewById<TextInputEditText>(R.id.et_status_date)

        val calendar = Calendar.getInstance()
        val initialSdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        etStatusDate.setText(initialSdf.format(calendar.time))

        etStatusDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    etStatusDate.setText(sdf.format(selectedDate.time))
                    etStatusDate.error = null
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Añadir Detalles extras")
            .setView(dialogView)
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val comment = etStatusComment.text.toString().trim()
                val hasSleeves = cbHasSleeves.isChecked
                val dateString = etStatusDate?.text.toString().trim()

                var currentDateString = Date(0)

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                if (dateString.isNotEmpty()) {
                    try {
                        val date = sdf.parse(dateString)
                        currentDateString = Date(date.time)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this, "Formato de fecha inválido", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }

                val newStatus = Status(
                    boardGameId = currentGameId,
                    condition = comment,
                    sleeves = hasSleeves,
                    dateOfIncorporation = currentDateString
                )
                statusViewModel.insertStatus(newStatus)
                boardGameViewModel.loadGameById(currentGameId)

                Toast.makeText(this, "Detalles extras guardados", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun showAddLoanDialog() {
        val dialogView =
            LayoutInflater.from(this).inflate(R.layout.dialog_add_loan, null)
        val etLoanBorrower = dialogView.findViewById<TextInputEditText>(R.id.et_loan_borrower)
        val etLoanTargetDate = dialogView.findViewById<TextInputEditText>(R.id.et_loan_target_date)

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 2)
        val initialSdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        etLoanTargetDate.setText(initialSdf.format(calendar.time))

        etLoanTargetDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    etLoanTargetDate.setText(sdf.format(selectedDate.time))
                    etLoanTargetDate.error = null
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Añadir préstamo")
            .setView(dialogView)
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val loanBorrowerText = etLoanBorrower.text.toString().trim()
                val loanTargetDateString = etLoanTargetDate?.text.toString().trim()

                var loanTargetDate = Date(0)
                var currentDateString = Date(0)

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                if (loanTargetDateString.isNotEmpty()) {
                    try {
                        val date = sdf.parse(loanTargetDateString)
                        loanTargetDate = Date(date.time)

                        val auxDate = Calendar.getInstance().time
                        currentDateString = Date(auxDate.time)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this, "Formato de fecha inválido", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }

                val newLoan = Loan(
                    boardGameId = currentGameId,
                    borrower = loanBorrowerText,
                    targetDate = loanTargetDate,
                    loanDate = currentDateString
                )
                loanViewModel.insertLoan(newLoan)
                boardGameViewModel.loadGameById(currentGameId)

                Toast.makeText(this, "Préstamo guardado", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
        dialog.show()
    }


}