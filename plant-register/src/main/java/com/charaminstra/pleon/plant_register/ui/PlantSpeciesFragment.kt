package com.charaminstra.pleon.plant_register.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.charaminstra.pleon.common_ui.CustomDialog
import com.charaminstra.pleon.common_ui.showErrorToast
import com.charaminstra.pleon.plant_register.PlantRegisterViewModel
import com.charaminstra.pleon.plant_register.PlantSearchAdapter
import com.charaminstra.pleon.plant_register.R
import com.charaminstra.pleon.plant_register.databinding.FragmentPlantSpeciesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlantSpeciesFragment : Fragment() {
    private lateinit var binding: FragmentPlantSpeciesBinding
    private lateinit var adapter: PlantSearchAdapter
    private val searchViewModel: PlantSearchViewModel by viewModels()
    private val viewModel: PlantRegisterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlantSpeciesBinding.inflate(layoutInflater)
        val navController = this.findNavController()

        initList()
        observeViewModel()
        searchViewModel.getPlantSpecies()
        binding.plantSpeciesRecyclerview.adapter = adapter
        binding.plantSpeciesRecyclerview.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))

        binding.plantSpeciesSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            //확인 누르면
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            //글자 칠때 마다 변함
            override fun onQueryTextChange(newText: String): Boolean {
                if(newText.isEmpty()){
                    binding.plantSpeciesEt.visibility = View.VISIBLE
                    binding.plantSpeciesRecyclerview.visibility = View.GONE
                }else{
                    binding.plantSpeciesEt.visibility = View.GONE
                    binding.plantSpeciesRecyclerview.visibility = View.VISIBLE
                }
                searchViewModel.searchFilter(newText)
                return true
            }
        })

        binding.plantSpeciesNextBtn.setOnClickListener {
            navController.navigate(R.id.plant_species_fragment_to_plant_name_fragment)
            if(viewModel.urlResponse.value.isNullOrBlank()){
                Toast(activity).showErrorToast(resources.getString(R.string.plant_species_fragment_error),binding.plantSpeciesEt.y,requireActivity())
            }else{
                //test
                //navController.navigate(R.id.plant_register_fragment_to_plant_light_fragment)
            }
        }

        binding.plantSpeciesSkipBtn.setOnClickListener {
            val dlg = CustomDialog(requireContext())
            dlg.setOnOKClickedListener {
                activity?.finish()
            }
            dlg.start(
                resources.getString(R.string.plant_register_skip_dialog_title),
                resources.getString(R.string.plant_register_skip_dialog_desc),
                resources.getString(R.string.plant_register_skip_dialog_cancel_btn),
                resources.getString(R.string.plant_register_skip_dialog_skip_btn)
            )
        }
        return binding.root
    }

    private fun initList() {
        adapter = PlantSearchAdapter()
        adapter.onItemClicked = { name ->
            binding.plantSpeciesEt.visibility = View.VISIBLE
            binding.plantSpeciesEt.setText(name)
            binding.plantSpeciesRecyclerview.visibility = View.GONE

        }
    }

    private fun observeViewModel() {
        searchViewModel.plantSpeciesList.observe(viewLifecycleOwner, Observer {
            adapter.refreshItems(it)
        })
        searchViewModel.searchResult.observe(viewLifecycleOwner, Observer {
            adapter.refreshItems(it)
        })
    }
}