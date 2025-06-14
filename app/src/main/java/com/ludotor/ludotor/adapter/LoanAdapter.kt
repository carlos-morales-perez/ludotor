package com.ludotor.ludotor.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ludotor.ludotor.data.Loan
import com.ludotor.ludotor.databinding.ItemLoanBinding
import java.text.SimpleDateFormat
import java.util.Locale

class LoanAdapter(
    // private val onItemClicked: (Loan) -> Unit, // Opcional: para manejar clics en el ítem
    // private val onMarkAsReturnedClicked: (Loan) -> Unit // Opcional: para el botón de devolver
) : ListAdapter<Loan, LoanAdapter.LoanViewHolder>(LoanDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoanViewHolder {
        val binding = ItemLoanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LoanViewHolder, position: Int) {
        val loan = getItem(position)
        holder.bind(loan)
    }

    inner class LoanViewHolder(private val binding: ItemLoanBinding) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        fun bind(loan: Loan) {
            binding.tvBorrower.text = loan.borrower

            loan.loanDate?.let {
                binding.tvLoanDate.text = dateFormat.format(it)
            } ?: run {
                binding.tvLoanDate.text = "N/A"
            }

            loan.targetDate?.let {
                binding.tvLoanTargetDate.text = dateFormat.format(it)
            } ?: run {
                binding.tvLoanTargetDate.text = "N/A"
            }

            if (System.currentTimeMillis() >= loan.targetDate.time) {
                binding.tvLoanStatusNotification.text = "Sí"
            } else {
                binding.tvLoanStatusNotification.text = "No"
            }

//            if (loan.returnDate != null) {
//                binding.llReturnDateInfo.visibility = View.VISIBLE
//                binding.tvLoanReturnDate.text = dateFormat.format(loan.returnDate)
//                // Opcional: Ocultar botón de "Marcar como devuelto" si ya está devuelto
//                // binding.btnMarkReturned.visibility = View.GONE
//            } else {
//                binding.llReturnDateInfo.visibility = View.GONE
//                // Opcional: Mostrar botón de "Marcar como devuelto"
//                // binding.btnMarkReturned.visibility = View.VISIBLE
//            }

            // Opcional: Listener para el ítem completo
            // binding.root.setOnClickListener {
            //     onItemClicked(loan)
            // }

            // Opcional: Listener para el botón de "Marcar como devuelto"
            // binding.btnMarkReturned.setOnClickListener {
            //    onMarkAsReturnedClicked(loan)
            // }
        }
    }
}

class LoanDiffCallback : DiffUtil.ItemCallback<Loan>() {
    override fun areItemsTheSame(oldItem: Loan, newItem: Loan): Boolean {
        return oldItem.loanId == newItem.loanId
    }

    override fun areContentsTheSame(oldItem: Loan, newItem: Loan): Boolean {
        return oldItem == newItem
    }
}