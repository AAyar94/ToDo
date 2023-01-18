package com.aayar94.todo.fragments.add

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.aayar94.todo.R
import com.aayar94.todo.data.models.ToDoData
import com.aayar94.todo.data.viewmodel.ToDoViewModel
import com.aayar94.todo.databinding.FragmentAddBinding
import com.aayar94.todo.fragments.SharedViewModel


class AddFragment : Fragment() {
    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()

    private lateinit var binding: FragmentAddBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAddBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        binding.spinnerPriorities.onItemSelectedListener = mSharedViewModel.listener
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_add) {
            insertDataToDb()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun insertDataToDb() {
        val mTitle = binding.etTitle.text.toString()
        val mPriority = binding.spinnerPriorities.selectedItem.toString()
        val mDescription = binding.etDescription.text.toString()

        val validation = mSharedViewModel.verifyDataFromUser(mTitle, mDescription)

        if (validation) {
            val newData = ToDoData(
                0, mTitle, mSharedViewModel.parsePriority(mPriority), mDescription
            )
            mToDoViewModel.insertData(newData)
            Toast.makeText(requireContext(), "Seccesfully Added", Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        } else {
            Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_LONG).show()
        }


    }


}