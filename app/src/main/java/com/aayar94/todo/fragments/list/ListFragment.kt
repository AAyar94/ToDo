package com.aayar94.todo.fragments.list

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.aayar94.todo.BuildConfig
import com.aayar94.todo.R
import com.aayar94.todo.data.models.ToDoData
import com.aayar94.todo.data.viewmodel.ToDoViewModel
import com.aayar94.todo.databinding.FragmentListBinding
import com.aayar94.todo.fragments.SharedViewModel
import com.aayar94.todo.fragments.list.adapter.ListAdapter
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.recyclerview.animators.FadeInDownAnimator
import kotlin.system.exitProcess

class ListFragment : Fragment(), SearchView.OnQueryTextListener {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val adapter: ListAdapter by lazy { ListAdapter() }
    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.mSharedViewModel = mSharedViewModel


        setupRecyclerView()



        mToDoViewModel.getAllData.observe(viewLifecycleOwner, Observer { data ->
            mSharedViewModel.checkIfDatabaseEmpty(data)
            adapter.setData(data)
        })


        // Inflate the layout for this fragment
        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }

        binding.listLayout.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_updateFragment2)
        }

        setHasOptionsMenu(true)


        return binding.root
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.recyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
                //LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.itemAnimator = FadeInDownAnimator().apply {
            addDuration = 300
        }

        swipeToDelete(recyclerView)
    }


    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDeleteCallBack = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val itemToDelete = adapter.dataList[viewHolder.adapterPosition]
                mToDoViewModel.deleteItem(itemToDelete)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
                // Toast.makeText(requireContext(), "Succesfully Removed '${itemToDelete.title}'", Toast.LENGTH_LONG).show()

                restoreDeletedData(viewHolder.itemView, itemToDelete, viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallBack)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun restoreDeletedData(view: View, deletedItem: ToDoData, position: Int) {
        val snackbar = Snackbar.make(
            view, getString(R.string.Deleted) + " '${deletedItem.title}'",
            Snackbar.LENGTH_LONG
        )
        snackbar.setAction(getString(R.string.Undo)) {
            mToDoViewModel.insertData(deletedItem)
            //adapter.notifyItemChanged(position)
        }
        snackbar.show()
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu, menu)
        if (menu is MenuBuilder) menu.setOptionalIconsVisible(true)
        val search: MenuItem = menu.findItem(R.id.menu_search)
        val searchView: SearchView? = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_all -> confirmRemovalAll()
            R.id.menu_priority_high -> mToDoViewModel.sortByHighPriority.observe(
                this,
                Observer { adapter.setData(it) })
            R.id.menu_priority_low -> mToDoViewModel.sortByLowPriority.observe(
                this,
                Observer { adapter.setData(it) })
            R.id.menu_feedback -> {
                val feedbackIntent = Intent(Intent.ACTION_VIEW)
                val i =
                    "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                feedbackIntent.data = Uri.parse(i)
                startActivity(feedbackIntent)
            }
            R.id.exit_from_app -> {
                exitProcess(0)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun confirmRemovalAll() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(getString(R.string.Yes)) { _, _ ->
            mToDoViewModel.deleteAll()
            Toast.makeText(
                requireContext(), getString(R.string.Successfully_removed_everything),
                Toast.LENGTH_SHORT
            ).show()

        }

        builder.setNegativeButton(getString(R.string.No)) { _, _ -> }
        builder.setTitle(getString(R.string.Delete_everything))
        builder.setMessage(getString(R.string.Are_u_sure_delete_everything))
        builder.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchThroughDatabase(query)
        }
        return true
    }


    override fun onQueryTextChange(query: String): Boolean {
        if (query != null) {
            searchThroughDatabase(query)
        }
        return true
    }

    private fun searchThroughDatabase(query: String) {
        val searchQuery = "%$query%"
        mToDoViewModel.searchDatabase(searchQuery).observe(this, Observer { list ->
            list.let {
                adapter.setData(it)
            }
        })
    }
}