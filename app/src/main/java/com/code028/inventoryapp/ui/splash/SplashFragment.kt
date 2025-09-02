package com.code028.inventoryapp.ui.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.code028.inventoryapp.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment(R.layout.fragment_splash) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            delay(300)
            val navController = findNavController()
            val destination = if (FirebaseAuth.getInstance().currentUser != null) {
                R.id.inventoryFragment
            } else {
                R.id.authFragment
            }
            navController.navigate(
                destination,
                null,
                NavOptions.Builder().setPopUpTo(R.id.splashFragment, true).build()
            )
        }
    }
}

