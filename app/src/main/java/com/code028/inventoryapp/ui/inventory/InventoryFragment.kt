package com.code028.inventoryapp.ui.inventory

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener

import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.code028.inventoryapp.data.model.Equipment
import com.code028.inventoryapp.databinding.FragmentInventoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InventoryFragment : Fragment() {
    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InventoryViewModel by viewModels()

    private lateinit var adapter: InventoryAdapter
    private var fullItemList: List<Equipment> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = InventoryAdapter { item ->
            val action = InventoryFragmentDirections
                .actionInventoryFragmentToItemDetailFragment(item.id)
            findNavController().navigate(action)
        }

        binding.rvInventory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvInventory.adapter = adapter

        if (adapter.itemCount == 0) {
            binding.emptyStateLayout2.apply {
                visibility = View.VISIBLE
                alpha = 0f
                animate().alpha(1f).setDuration(300).start()
            }
        } else {
            binding.emptyStateLayout2.visibility = View.GONE
        }

        // Firestore listener
        viewModel.getUserItems()?.addSnapshotListener { snapshot, e ->
            if (e != null) {
                println("Firestore error: $e")
                return@addSnapshotListener
            }

            fullItemList = snapshot?.toObjects(Equipment::class.java) ?: emptyList()
            adapter.submitList(fullItemList)

            binding.emptyStateLayout2.visibility =
                if (fullItemList.isEmpty()) View.VISIBLE else View.GONE

            if (fullItemList.isEmpty()) {
                binding.emptyStateLayout.visibility = View.GONE
            }
        }

        binding.searchEditText.addTextChangedListener { editable ->
            val query = editable?.toString().orEmpty().trim()
            val filteredList = if (query.isEmpty()) {
                fullItemList
            } else {
                fullItemList.filter {
                    it.name.contains(query, ignoreCase = true) ||
                            it.category.contains(query, ignoreCase = true) ||
                            it.location.contains(query, ignoreCase = true) ||
                            it.description.contains(query, ignoreCase = true) ||
                            it.quantity.toString().contains(query, ignoreCase = true) ||
                            (if (it.status) "активна" else "отписана").contains(query, ignoreCase = true)
                }
            }

            adapter.submitList(filteredList)

            if (fullItemList.isNotEmpty() && filteredList.isEmpty()) {
                binding.emptyStateLayout.apply {
                    visibility = View.VISIBLE
                    alpha = 0f
                    animate().alpha(1f).setDuration(300).start()
                }
            } else {
                binding.emptyStateLayout.visibility = View.GONE
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
