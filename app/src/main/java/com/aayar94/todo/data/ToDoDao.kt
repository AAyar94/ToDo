package com.aayar94.todo.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.aayar94.todo.data.models.ToDoData

@Dao
interface ToDoDao {

    @Query("SELECT * FROM todo_table ORDER BY id ASC")
    fun getAllData(): LiveData<List<ToDoData>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertData(ToDoData: ToDoData)

    @Update
    suspend fun updateData(data: ToDoData)

    @Delete
    suspend fun deleteItem(data: ToDoData)

    @Query("Delete From todo_table")
    suspend fun deleteAll()

}