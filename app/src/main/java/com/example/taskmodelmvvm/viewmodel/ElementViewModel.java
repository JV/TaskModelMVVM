package com.example.taskmodelmvvm.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.taskmodelmvvm.persistance.ElementModel;
import com.example.taskmodelmvvm.persistance.ElementModelRepository;

import java.util.List;

public class ElementViewModel extends AndroidViewModel {

    private LiveData<List<ElementModel>> allElements;
    private ElementModelRepository elementModelRepository;
    private List<ElementModel> elementModels;

    public ElementViewModel(@NonNull Application application) {
        super(application);
        elementModelRepository = ElementModelRepository.getInstance(application);
        allElements = elementModelRepository.getAllElements();
        elementModels = elementModelRepository.getAllElementsList();
    }

    public void moveElement(int fromPosition, int toPosition) {
        elementModelRepository.move(fromPosition, toPosition);
    }

    public void insert(ElementModel elementModel) {
        elementModelRepository.insert(elementModel);
    }

    public void update(ElementModel elementModel) {
        elementModelRepository.update(elementModel);
    }

    public void delete(ElementModel elementModel) {
        elementModelRepository.delete(elementModel);
    }

    public void deleteAllElements() {
        elementModelRepository.deleteAllElements();
    }



    public LiveData<List<ElementModel>> getAllElements() {
        if (allElements == null) {
            allElements = elementModelRepository.getAllElements();
        }
        return allElements;
    }

    public LiveData<List<ElementModel>> getAllElementsMoved() {
        if (allElements == null) {
            allElements = elementModelRepository.getAllElementsMoved();
        }
        return allElements;
    }

    public List<ElementModel> getAllElementsList() {
        if (elementModels == null) {
            elementModels = elementModelRepository.getAllElementsList();
        }
        return elementModels;
    }


}
