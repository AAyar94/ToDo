package com.aayar94.todo.fragments.update

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.aayar94.todo.R
import com.aayar94.todo.data.models.ToDoData
import com.aayar94.todo.data.viewmodel.ToDoViewModel
import com.aayar94.todo.databinding.FragmentUpdateBinding
import com.aayar94.todo.fragments.SharedViewModel


class UpdateFragment : Fragment() {
    private lateinit var mBinding: FragmentUpdateBinding
    private val args by navArgs<UpdateFragmentArgs>()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private val mToDoViewModel: ToDoViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentUpdateBinding.inflate(inflater, container, false)
        mBinding.currentEtTitle.setText(args.currentItem.title)
        mBinding.currentEtDescription.setText(args.currentItem.description)
        mBinding.currentSpinnerPriorities.setSelection(mSharedViewModel.parsePriorityToInt(args.currentItem.priority))
        mBinding.currentSpinnerPriorities.onItemSelectedListener = mSharedViewModel.listener
        setHasOptionsMenu(true)

        // Inflate the layout for this fragment
        return mBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_save -> updateItem()
            R.id.menu_delete -> confirmItemRemoval()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun confirmItemRemoval() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            mToDoViewModel.deleteItem(args.currentItem)
            Toast.makeText(
                requireContext(), "Succesfully Removed : ${args.currentItem.title}",
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }

        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Delete '${args.currentItem.title}'?")
        builder.setMessage("Are u sure want to remove '${args.currentItem.title}'")
        builder.create().show()
    }

    private fun updateItem() {
        val title = mBinding.currentEtTitle.text.toString()
        val description = mBinding.currentEtDescription.text.toString()
        val getPriority = mBinding.currentSpinnerPriorities.selectedItem.toString()

        val validation = mSharedViewModel.verifyDataFromUser(title, description)
        if (validation) {
            val updatedItem = ToDoData(
                args.currentItem.id,
                title,
                mSharedViewModel.parsePriority(getPriority),
                description
            )
            mToDoViewModel.updateData(updatedItem)
            Toast.makeText(requireContext(), "Updated", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        } else {
            Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT)
                .show()
        }
    }

}