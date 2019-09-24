package com.example.taskmodelmvvm.persistance;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ElementModelDao {

    @Insert
    long[] insert(ElementModel... elementModels);

    @Update
    int update(ElementModel... elementModels);

    @Delete
    int delete(ElementModel... elementModels);

    @Query("DELETE FROM element_table")
    void deleteAllElements();


    //select original

    @Query("SELECT * FROM element_table ORDER BY pocetak DESC")
    LiveData<List<ElementModel>> getAllElements();

    // select list only

    @Query("SELECT * FROM element_table ORDER BY pocetak DESC")
    List<ElementModel> getAllElementsList();

    //select user edited list, reordered

    @Query("SELECT * FROM element_table ORDER BY current_position DESC")
    LiveData<List<ElementModel>> getAllElementsMoved();
    //select list sublist, search for tag

    @Query("SELECT * FROM element_table WHERE tag = :tag")
    LiveData<List<ElementModel>> getAllElementsQuerryTag(String tag);
    //select list sublist, search for name

    @Query("SELECT * FROM element_table WHERE naziv LIKE :naziv")
    LiveData<List<ElementModel>> getAllElementsQuerryName(String naziv);

}
