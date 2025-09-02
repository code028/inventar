package com.code028.inventoryapp.ui.profile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.bumptech.glide.Glide
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.code028.inventoryapp.R
import com.code028.inventoryapp.databinding.FragmentProfileBinding
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.view.isGone


@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    // Profile Image from gallery function
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.updateUserProfileImage(it) { success, url ->
                if (success) {
                    Glide.with(requireContext())
                        .load(url)
                        .placeholder(R.drawable.ic_profile)
                        .into(binding.imgAvatar)
                } else {
                    Toast.makeText(requireContext(), "Грешка при upload-у", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val user = viewModel.currentUser()

        binding.tvUserName.text = user?.displayName ?: "Guest"
        binding.tvUserEmail.text = user?.email ?: "Guest"
        user?.metadata?.let { metadata ->
            val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            binding.tvCreatedAt.text = "Налог креиран: ${dateFormat.format(Date(metadata.creationTimestamp))}"
            binding.tvLastLogin.text = "Последња пријава: ${dateFormat.format(Date(metadata.lastSignInTimestamp))}"
        }

        binding.userInfoHeader.setOnClickListener {
            val content = binding.userInfoContent
            val icon = binding.ivExpandIcon

            if (content.isGone) {
                content.visibility = View.VISIBLE
                content.alpha = 0f
                content.animate().alpha(1f).setDuration(200).start()
                icon.animate().rotation(180f).setDuration(200).start()
            } else {
                content.animate().alpha(0f).setDuration(200).withEndAction {
                    content.visibility = View.GONE
                }.start()
                icon.animate().rotation(0f).setDuration(200).start()
            }
        }



        viewModel.getUserItemCount { count ->
            binding.tvItemCount.text = when {
                count == 0 -> "Ваш инвентар је празан"
                count == 1 -> "У вашем инвентару имате 1 ставку"
                count in 2..4 -> "У вашем инвентару имате $count ставке"
                else -> "У вашем инвентару имате $count ставки"
            }
        }

        viewModel.getUserData { user ->
            if (user != null) {
                binding.tvUserName.text = user.name
                binding.tvUserEmail.text = user.email

                Glide.with(requireContext())
                    .load(user.profileImageUrl ?: R.drawable.ic_profile)
                    .placeholder(R.drawable.ic_profile)
                    .circleCrop()
                    .into(binding.imgAvatar)
            } else {
                Toast.makeText(requireContext(), "Грешка при учитавању података", Toast.LENGTH_SHORT).show()
            }
        }

        // Image picker caller {FIREBASE STORAGE DOESN'T ALLOW - FREE TIER - NO SUPPORT}
        // binding.imgAvatar.setOnClickListener {
        //     pickImageLauncher.launch("image/*")
        // }

        binding.imgAvatar.setOnClickListener {
            showChangeImageDialog()
        }

        // Logout bind for fun and fragment
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            findNavController().navigate(R.id.authFragment)
        }

    }


private fun showChangeImageDialog() {
    val dialogView = layoutInflater.inflate(R.layout.dialog_change_image, null)
    val editText = dialogView.findViewById<EditText>(R.id.etImageUrl)

    var dialog = AlertDialog.Builder(requireContext())
        .setTitle("Промена слике профила")
        .setView(dialogView)
        .setPositiveButton("Сачувај") { _, _ ->
            val url = editText.text.toString().trim()
            if (url.isNotBlank()) {
                viewModel.updateProfileImageUrl(url) { success, error ->
                    if (success) {
                        Glide.with(requireContext())
                            .load(url)
                            .placeholder(R.drawable.ic_profile)
                            .circleCrop()
                            .into(binding.imgAvatar)
                    } else {
                        Toast.makeText(requireContext(), "Грешка: $error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        .setNegativeButton("Откажи", null)
        .create()


        dialog.window?.setBackgroundDrawable("#1E1E1E".toColorInt().toDrawable())
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor("#08bd6b".toColorInt())
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor("#E53935".toColorInt())
}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
