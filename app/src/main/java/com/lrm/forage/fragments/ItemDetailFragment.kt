package com.lrm.forage.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.lrm.forage.ForageApplication
import com.lrm.forage.ForageViewModel
import com.lrm.forage.ForageViewModelFactory
import com.lrm.forage.R
import com.lrm.forage.database.Item
import com.lrm.forage.databinding.FragmentItemDetailBinding

class ItemDetailFragment : Fragment() {

    private var _binding: FragmentItemDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ForageViewModel by activityViewModels {
        ForageViewModelFactory(
            (activity?.application as ForageApplication).database.itemDao()
        )
    }

    private val navigationArgs: ItemDetailFragmentArgs by navArgs()
    private lateinit var item: Item

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = navigationArgs.itemId

        viewModel.retrieveItem(id).observe(viewLifecycleOwner) {selectedItem ->
            item = selectedItem
            bind(item)
        }
    }

    private fun bind(item: Item) {
        binding.apply {
            itemName.text = item.name
            itemAddress.text = item.address
            itemNotes.text = item.notes
        }

        if (item.inSeason) {
            binding.itemInSeason.text = getString(R.string.currently_in_season)
        } else {
            binding.itemInSeason.text = getString(R.string.out_of_season)
        }

        binding.itemAddress.setOnClickListener { launchMap() }
        binding.editItemFab.setOnClickListener {
            val action = ItemDetailFragmentDirections.actionItemDetailFragmentToAddItemFragment(item.id)
            this.findNavController().navigate(action)
        }
    }

    private fun launchMap() {
        val address = item.address

        val mapUri = Uri.parse("geo:0,0?q=$address")
        val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}