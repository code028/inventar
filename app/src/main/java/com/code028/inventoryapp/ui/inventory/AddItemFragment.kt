package com.code028.inventoryapp.ui.inventory

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.code028.inventoryapp.R
import com.code028.inventoryapp.data.model.Equipment
import com.code028.inventoryapp.databinding.FragmentAddItemBinding
import com.google.android.material.textfield.MaterialAutoCompleteTextView
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // USERS CATEGORIES FOR LOCATION TEXT INPUT
        viewModel.getAllUserCategories { categories ->
            if (categories.isNotEmpty()) {
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, categories)
                binding.etCategory.setAdapter(adapter)
            }
        }
        binding.etCategory.threshold = 0
        binding.etCategory.setOnTouchListener { v, event ->
            binding.etCategory.showDropDown()
            false
        }
        // USERS LOCATION FOR LOCATION TEXT INPUT
        viewModel.getAllUserLocations { locations ->
            if (locations.isNotEmpty()) {
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, locations)
                binding.etLocation.setAdapter(adapter)
            }
        }
        binding.etLocation.threshold = 0
        binding.etLocation.setOnTouchListener { v, event ->
            binding.etLocation.showDropDown()
            false
        }
        binding.etLocation.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Sačekaj da tastatura izađe pa onda scroll
                binding.root.postDelayed({
                    binding.scrollView.smoothScrollTo(0, binding.root.height)
                }, 200)
            }
        }
        binding.etLocation.setOnClickListener {
            binding.root.postDelayed({
                binding.scrollView.smoothScrollTo(0, binding.root.height)
            }, 200)
        }

        binding.switchStatus.setOnCheckedChangeListener { _, isChecked ->
            binding.tvStatusLabel.text = if (isChecked) {
                "Чува се као активна ставка"
            } else {
                "Чува се као оптисана ставка"
            }
        }

        // Save button
        binding.btnSave.setOnClickListener {
            val item = Equipment(
                id = UUID.randomUUID().toString(),
                name = binding.etName.text.toString(),
                category = binding.etCategory.text.toString(),
                location = binding.etLocation.text.toString(),
                quantity = binding.etQuantity.text.toString().toIntOrNull() ?: 0,
                description = binding.etDescription.text.toString(),
                status = binding.switchStatus.isChecked
            )

            if (listOf(item.name, item.category, item.location, item.description).any { it.isEmpty() }) {
                Toast.makeText(requireContext(), "Молимо попуните сва поља", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (item.quantity <= 0) {
                Toast.makeText(requireContext(), "Количина мора бити већа од 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val task = viewModel.addItem(item)
            task?.addOnSuccessListener {
                Toast.makeText(requireContext(), "Успешно додато у инвентар", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_addItemFragment_to_inventoryFragment)
            }?.addOnFailureListener {
                Toast.makeText(requireContext(), "Грешка: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
