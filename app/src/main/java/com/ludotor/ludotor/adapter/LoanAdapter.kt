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