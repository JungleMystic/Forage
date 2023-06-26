package com.lrm.forage.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lrm.forage.ForageApplication
import com.lrm.forage.ForageViewModel
import com.lrm.forage.ForageViewModelFactory
import com.lrm.forage.R
import com.lrm.forage.database.Item
import com.lrm.forage.databinding.FragmentAddItemBinding

class AddItemFragment : Fragment() {

    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ForageViewModel by activityViewModels {
        ForageViewModelFactory(
            (activity?.application as ForageApplication).database.itemDao()
        )
    }

    val navigationArgs: AddItemFragmentArgs by navArgs()
    private lateinit var item: Item

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddItemBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backIcon.setOnClickListener { findNavController().navigateUp() }

        val id = navigationArgs.itemId

        if (id > 0) {
            viewModel.retrieveItem(id).observe(this.viewLifecycleOwner) {selectedItem ->
                item = selectedItem
                bind(item)
            }

            binding.saveButton.setOnClickListener { updateItem() }

            binding.fragmentLabel.text = getString(R.string.edit_item)
            binding.deleteButton.visibility = View.VISIBLE
            binding.deleteButton.setOnClickListener { showConfirmationDialog() }

        } else {
            binding.fragmentLabel.text = getString(R.string.add_item)
            binding.saveButton.setOnClickListener { addNewItem() }
        }

    }

    private fun bind(item: Item) {
        binding.apply {
            itemName.setText(item.name, TextView.BufferType.SPANNABLE)
            itemAddress.setText(item.address, TextView.BufferType.SPANNABLE)
            inSeasonCheckbox.isChecked = item.inSeason
            itemNotes.setText(item.notes, TextView.BufferType.SPANNABLE)
        }
    }

    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) {_, _ -> }
            .setPositiveButton(getString(R.string.yes)) {_, _ ->
                deleteItem()
            }
            .show()
    }

    private fun deleteItem() {
        viewModel.deleteItem(item)
        val action = AddItemFragmentDirections.actionAddItemFragmentToItemListFragment()
        this.findNavController().navigate(action)
    }

    private fun addNewItem() {
        if (isEntryValid()) {
            viewModel.addNewItem(
                binding.itemName.text.toString(),
                binding.itemAddress.text.toString(),
                binding.inSeasonCheckbox.isChecked,
                binding.itemNotes.text.toString()
            )

            val action = AddItemFragmentDirections.actionAddItemFragmentToItemListFragment()
            this.findNavController().navigate(action)
        }
    }

    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.itemName.text.toString(),
            binding.itemAddress.text.toString()
        )
    }

    private fun updateItem() {
        if (isEntryValid()) {
            viewModel.updateItem(
                this.navigationArgs.itemId,
                this.binding.itemName.text.toString(),
                this.binding.itemAddress.text.toString(),
                binding.inSeasonCheckbox.isChecked,
                this.binding.itemNotes.text.toString()
            )

            val action = AddItemFragmentDirections.actionAddItemFragmentToItemListFragment()
            this.findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}