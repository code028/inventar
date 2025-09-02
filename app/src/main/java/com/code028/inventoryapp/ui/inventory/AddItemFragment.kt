package com.code028.inventoryapp.ui.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.code028.inventoryapp.data.model.Equipment
import com.code028.inventoryapp.databinding.FragmentAddItemBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class AddItemFragment : Fragment() {
    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InventoryViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnSave.setOnClickListener {
            val item = Equipment(
                id = UUID.randomUUID().toString(),
                name = binding.etName.text.toString(),
                category = binding.etCategory.text.toString(),
                quantity = binding.etQuantity.text.toString().toIntOrNull() ?: 0,
                description = binding.etDescription.text.toString()
            )

            val task = viewModel.addItem(item)
            if (task != null) {
                task.addOnSuccessListener {
                    Toast.makeText(requireContext(), "Item added", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "No logged in user", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
