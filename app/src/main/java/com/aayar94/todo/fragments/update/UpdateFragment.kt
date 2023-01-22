package com.aayar94.todo.fragments.update

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.aayar94.todo.R
import com.aayar94.todo.data.models.ToDoData
import com.aayar94.todo.data.viewmodel.ToDoViewModel
import com.aayar94.todo.databinding.FragmentUpdateBinding
import com.aayar94.todo.fragments.SharedViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class UpdateFragment : Fragment() {
    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<UpdateFragmentArgs>()


    private val mSharedViewModel: SharedViewModel by viewModels()
    private val mToDoViewModel: ToDoViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)

        binding.args = args
        //spinner item selected listener
        binding.currentSpinnerPriorities.onItemSelectedListener = mSharedViewModel.listener


        setHasOptionsMenu(true)

        // Inflate the layout for this fragment
        return binding.root
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_fragment_menu, menu)
        if (menu is MenuBuilder) (menu as MenuBuilder).setOptionalIconsVisible(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_save -> updateItem()
            R.id.menu_delete -> confirmItemRemoval()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun confirmItemRemoval() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setPositiveButton(getString(R.string.Yes)) { _, _ ->
            mToDoViewModel.deleteItem(args.currentItem)
            Toast.makeText(
                requireContext(), getString(R.string.successfully_removed)+" ${args.currentItem.title}",
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }

        builder.setNegativeButton(getString(R.string.No)) { _, _ -> }
        builder.setTitle(getString(R.string.delete) +" '${args.currentItem.title}'?")
        builder.setMessage(getString(R.string.are_u_sure)+" '${args.currentItem.title}'")
        builder.create().show()
    }

    private fun updateItem() {
        val title = binding.currentEtTitle.text.toString()
        val description = binding.currentEtDescription.text.toString()
        val getPriority = binding.currentSpinnerPriorities.selectedItem.toString()

        val validation = mSharedViewModel.verifyDataFromUser(title, description)
        if (validation) {
            val updatedItem = ToDoData(
                args.currentItem.id,
                title,
                mSharedViewModel.parsePriority(getPriority),
                description
            )
            mToDoViewModel.updateData(updatedItem)
            Toast.makeText(requireContext(), getString(R.string.Updated), Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.please_fill_out_all_fields),
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}