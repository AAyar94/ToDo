package com.aayar94.todo.fragments.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.aayar94.todo.R
import com.aayar94.todo.data.models.Priority
import com.aayar94.todo.data.models.ToDoData
import com.aayar94.todo.databinding.RowLayoutBinding

class ListAdapter : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    var dataList = emptyList<ToDoData>()

    class MyViewHolder(val binding: RowLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = RowLayoutBinding.inflate(layoutInflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.titleTxt.text = dataList[position].title
        holder.binding.descriptionTxt.text = dataList[position].description
        holder.binding.rowBackground.setOnClickListener {
            val action =
                ListFragmentDirections.actionListFragmentToUpdateFragment2(dataList[position])
            holder.itemView.findNavController().navigate(action)
        }

        when (dataList[position].priority) {
            Priority.HIGH -> holder.binding.priorityIndicator.setCardBackgroundColor(
                ContextCompat.getColor(
                    holder.binding.priorityIndicator.context,
                    R.color.red
                )
            )
            Priority.MEDIUM -> holder.binding.priorityIndicator.setCardBackgroundColor(
                ContextCompat.getColor(
                    holder.binding.priorityIndicator.context,
                    R.color.yellow
                )
            )
            Priority.LOW -> holder.binding.priorityIndicator.setCardBackgroundColor(
                ContextCompat.getColor(
                    holder.binding.priorityIndicator.context,
                    R.color.green
                )
            )
        }
    }

    fun setData(toDoData: List<ToDoData>) {
        this.dataList = toDoData
        notifyDataSetChanged()

    }
}