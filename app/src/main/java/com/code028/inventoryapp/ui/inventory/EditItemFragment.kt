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
        itemId.let { id ->
            viewModel.getItem(id).addOnSuccessListener { doc ->
                val item = doc.toObject(Equipment::class.java)
                item?.let {
                    binding.etName.setText(it.name)
                    binding.etCategory.setText(it.category)
                    binding.etQuantity.setText(it.quantity.toString())
                    binding.etDescription.setText(it.description)
                    binding.etLocation.setText(it.location)
                    binding.switchStatus.isChecked = it.status
                }
            }
        }

        // Save updated data
        binding.btnSave.setOnClickListener {
            val updates = mapOf(
                "name" to binding.etName.text.toString(),
                "category" to binding.etCategory.text.toString(),
                "quantity" to (binding.etQuantity.text.toString().toIntOrNull() ?: 0),
                "description" to binding.etDescription.text.toString(),
                "location" to binding.etLocation.text.toString(),
                "status" to binding.switchStatus.isChecked
            )

            if (listOf(
                    updates["name"] as String,
                    updates["category"] as String,
                    updates["location"] as String,
                    updates["description"] as String
                ).any { it.isEmpty() }
            ) {
                Toast.makeText(requireContext(), "Молимо попуните сва поља", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if ((updates["quantity"] as Int) <= 0) {
                Toast.makeText(requireContext(), "Количина мора бити већа од 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            itemId.let { id ->
                viewModel.updateItem(id, updates)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Ставка ажурирана", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Грешка: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        // Cancel button
        binding.btnCancel.setOnClickListener{
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}