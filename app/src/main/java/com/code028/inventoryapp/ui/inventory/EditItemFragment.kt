package com.code028.inventoryapp.ui.inventory

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.code028.inventoryapp.data.model.Equipment
import com.code028.inventoryapp.databinding.FragmentEditItemBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditItemFragment : Fragment() {
    private var _binding: FragmentEditItemBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InventoryViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val itemId = arguments?.getString("id") ?: return

        // Pre-fill existing data
        viewModel.getItem(itemId).addOnSuccessListener {
            val item = it.toObject(Equipment::class.java) ?: return@addOnSuccessListener
            binding.etName.setText(item.name)
            binding.etCategory.setText(item.category)
            binding.etQuantity.setText(item.quantity.toString())
            binding.etDescription.setText(item.description)
        }

        // Save updated data
        binding.btnSave.setOnClickListener {
            val updates = mapOf(
                "name" to binding.etName.text.toString(),
                "category" to binding.etCategory.text.toString(),
                "quantity" to (binding.etQuantity.text.toString().toIntOrNull() ?: 0),
                "description" to binding.etDescription.text.toString()
            )
            viewModel.updateItem(itemId, updates).addOnSuccessListener {
                Toast.makeText(requireContext(), "Item updated", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}