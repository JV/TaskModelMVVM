package com.example.taskmodelmvvm.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.taskmodelmvvm.entity.ElementModel;
import com.example.taskmodelmvvm.repository.ElementModelRepository;

import java.util.List;

public class ElementViewModel extends AndroidViewModel {

    private ElementModelRepository repository;
    private LiveData<List<ElementModel>> allElements;

    private LiveData<List<ElementModel>> allElementsMoved;

    private ElementModelRepository repositoryMoved;


    public ElementViewModel(@NonNull Application application) {
        super(application);
        repository = new ElementModelRepository(application);
        allElements = repository.getAllElements();
        repositoryMoved = new ElementModelRepository(application);
        allElementsMoved = repositoryMoved.getAllElementsMoved();
    }

    public void insert(ElementModel elementModel) {
        repository.insert(elementModel);
    }

    public void update(ElementModel elementModel) {
        repository.update(elementModel);
    }

    public void delete(ElementModel elementModel) {
        repository.delete(elementModel);
    }

    public void deleteAllElements() {
        repository.deleteAllElements();
    }

    public LiveData<List<ElementModel>> getAllElements() {
        if (allElements == null) {
            allElements = repository.getAllElements();
        }
        return allElements;
    }

    public LiveData<List<ElementModel>> getAllElementsMoved() {
        if (allElementsMoved == null) {
            allElementsMoved = repositoryMoved.getAllElementsMoved();
        }
        return allElementsMoved;
    }

    public void moveElement(int fromPosition, int toPosition) {
        repositoryMoved.move(fromPosition, toPosition);
    }
}
