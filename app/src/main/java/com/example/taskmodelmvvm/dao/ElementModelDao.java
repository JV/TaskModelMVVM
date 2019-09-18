package com.example.taskmodelmvvm.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.taskmodelmvvm.entity.ElementModel;

import java.util.List;

@Dao
public interface ElementModelDao {

    @Insert
    void insert(ElementModel elementModel);

    @Update
    void update(ElementModel elementModel);

    @Delete
    void delete(ElementModel elementModel);

    @Query("DELETE FROM element_table")
    void deleteAllElements();

    @Query("SELECT * FROM element_table ORDER BY pocetak DESC")
    LiveData<List<ElementModel>> getAllElements();

    @Query("SELECT * FROM element_table ORDER BY currentPosition DESC")
    LiveData<List<ElementModel>> getAllElementsMoved();

}
