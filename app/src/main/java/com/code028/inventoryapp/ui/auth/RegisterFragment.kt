package com.code028.inventoryapp.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.code028.inventoryapp.R
import com.code028.inventoryapp.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.fragment.app.viewModels

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (name.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(requireContext(), "Попуните сва поља", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 8) {
                Toast.makeText(requireContext(), "Кратка лозинка (8 карактера)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.register(email, password, name)
        }

        viewModel.authResult.observe(viewLifecycleOwner) { (success, msg) ->
            if (success) {
                findNavController().navigate(R.id.inventoryFragment)
            } else {
                Toast.makeText(requireContext(), msg ?: "Registration failed", Toast.LENGTH_SHORT).show()
            }
        }

        binding.goToLoginFragment.setOnClickListener {
            findNavController().navigate(R.id.action_register_to_login)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
