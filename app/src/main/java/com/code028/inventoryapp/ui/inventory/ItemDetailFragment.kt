package com.code028.inventoryapp.ui.inventory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.code028.inventoryapp.R
import com.code028.inventoryapp.databinding.FragmentItemDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.code028.inventoryapp.data.model.Equipment

@AndroidEntryPoint
class ItemDetailFragment : Fragment() {
    private var _binding: FragmentItemDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InventoryViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val itemId = arguments?.getString("id") ?: return

        viewModel.getItem(itemId).addOnSuccessListener {
            val item = it.toObject(Equipment::class.java)

            binding.tvName.text = item?.name
            binding.tvCategory.text = "${item?.category}"
            binding.tvQuantity.text = "Количина: ${item?.quantity}"
            binding.tvLocation.text = item?.location ?: ""

            if (item?.status == true) {
                binding.tvStatus.text = "Активна ставка"
                binding.ivStatusIcon.setImageResource(R.drawable.ic_status)
            } else {
                binding.tvStatus.text = "Отписана ставка"
                binding.ivStatusIcon.setImageResource(R.drawable.ic_status2)
            }

            binding.tvDescription.text = item?.description
        }

        binding.btnDelete.setOnClickListener {
            viewModel.deleteItem(itemId).addOnSuccessListener {
                Toast.makeText(requireContext(), "Успешно обрисана ставка", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }

        binding.btnEdit.setOnClickListener {
            val action = ItemDetailFragmentDirections.actionItemDetailFragmentToEditItemFragment(itemId)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
