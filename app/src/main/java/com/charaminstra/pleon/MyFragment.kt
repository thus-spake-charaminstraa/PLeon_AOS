package com.charaminstra.pleon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.charaminstra.pleon.R
import com.charaminstra.pleon.databinding.FragmentMyBinding


class MyFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMyBinding.inflate(inflater, container, false)

        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host)
        binding.logoutBtn.setOnClickListener{
            navController.popBackStack()
            navController.navigate(com.charaminstra.pleon.login.R.id.nav_login)
//            prefs.delete("token")
//            if(prefs.getString("token","").isEmpty()) {
//                navController.navigate(com.charaminstra.pleon.login.R.id.nav_login)
//            }
        }
        return binding.root
    }

}