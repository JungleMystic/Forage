package com.lrm.forage.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.lrm.forage.ForageApplication
import com.lrm.forage.ForageViewModel
import com.lrm.forage.ForageViewModelFactory
import com.lrm.forage.adapter.ItemAdapter
import com.lrm.forage.databinding.FragmentItemListBinding

class ItemListFragment : Fragment() {

    private var _binding: FragmentItemListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ForageViewModel by activityViewModels {
        ForageViewModelFactory(
            (activity?.application as ForageApplication).database.itemDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addFab.setOnClickListener {
            val action = ItemListFragmentDirections.actionItemListFragmentToAddItemFragment()
            this.findNavController().navigate(action)
        }

        val adapter = ItemAdapter {
            val action = ItemListFragmentDirections.actionItemListFragmentToItemDetailFragment(it.id)
            this.findNavController().navigate(action)
        }
        binding.recyclerView.adapter = adapter

        viewModel.allItems.observe(this.viewLifecycleOwner) { items ->
            items.let { adapter.submitList(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}