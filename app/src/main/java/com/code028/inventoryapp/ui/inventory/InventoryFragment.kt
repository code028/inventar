package com.code028.inventoryapp.ui.inventory

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.code028.inventoryapp.data.model.Equipment
import com.code028.inventoryapp.databinding.FragmentInventoryBinding
import dagger.hilt.android.AndroidEntryPoint
import com.code028.inventoryapp.R

@AndroidEntryPoint
class InventoryFragment : Fragment() {
    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InventoryViewModel by viewModels()

    private lateinit var adapter: InventoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = InventoryAdapter { item ->
            val action = InventoryFragmentDirections.actionInventoryFragmentToItemDetailFragment(item.id)
            findNavController().navigate(action)
        }
        binding.rvInventory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvInventory.adapter = adapter

        binding.btnAddItem.setOnClickListener {
            findNavController().navigate(R.id.addItemFragment)
        }

        viewModel.getItems().addSnapshotListener { snapshot, _ ->
            val list = snapshot?.toObjects(Equipment::class.java) ?: emptyList()
            adapter.submitList(list)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
