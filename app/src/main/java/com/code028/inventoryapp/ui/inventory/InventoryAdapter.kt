package com.code028.inventoryapp.ui.inventory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.code028.inventoryapp.data.model.Equipment
import com.code028.inventoryapp.databinding.ItemInventoryBinding

class InventoryAdapter(
    private val onItemClick: (Equipment) -> Unit
) : ListAdapter<Equipment, InventoryAdapter.InventoryViewHolder>(DiffCallback()) {

    class InventoryViewHolder(
        private val binding: ItemInventoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Equipment, onClick: (Equipment) -> Unit) {
            binding.tvName.text = item.name
            binding.tvCategory.text = item.category
            binding.tvQuantity.text = "Qty: ${item.quantity}"
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Equipment>() {
        override fun areItemsTheSame(oldItem: Equipment, newItem: Equipment) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Equipment, newItem: Equipment) =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val binding = ItemInventoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return InventoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }
}
