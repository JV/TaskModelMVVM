package com.example.taskmodelmvvm.entity;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "element_table")
public class ElementModel implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String naziv;
    private Long pocetak;
    private Long kraj;
    private String tag;

    private int startPosition;
    private int currentPosition;

    public ElementModel(String naziv, Long pocetak, Long kraj, String tag, int currentPosition) {
        this.naziv = naziv;
        this.pocetak = pocetak;
        this.kraj = kraj;
        this.tag = tag;
//        this.startPosition = startPosition;
        this.currentPosition = currentPosition;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setStartPosition(int position) {
        this.startPosition = position;
    }

    public void setCurrentPosition(int position) {
        this.currentPosition = position;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public Long getPocetak() {
        return pocetak;
    }

    public Long getKraj() {
        return kraj;
    }

    public String getTag() {
        return tag;
    }

    @Override
    public String toString() {
        return "ElementModel{" +
                "id=" + id +
                ", naziv='" + naziv + '\'' +
                ", pocetak=" + pocetak +
                ", kraj=" + kraj +
                ", tag='" + tag + '\'' +
                '}';
    }


}
