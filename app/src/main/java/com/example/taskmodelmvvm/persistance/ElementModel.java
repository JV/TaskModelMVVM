package com.example.taskmodelmvvm.persistance;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "element_table")
public class ElementModel implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "naziv")
    private String naziv;
    @ColumnInfo(name = "pocetak")
    private Long pocetak;
    @ColumnInfo(name = "kraj")
    private Long kraj;
    @ColumnInfo(name = "tag")
    private String tag;
    @ColumnInfo(name = "timestamp")
    private String timestamp;
    @ColumnInfo(name = "current_position")
    private int currentPosition;

    @Ignore
    public ElementModel() {
    }

    public ElementModel(String naziv, Long pocetak, Long kraj, String tag, int currentPosition, String timestamp) {
        this.naziv = naziv;
        this.pocetak = pocetak;
        this.kraj = kraj;
        this.tag = tag;
        this.currentPosition = currentPosition;
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getCurrentPosition() {
        return currentPosition;
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
                ", timestamp='" + timestamp + '\'' +
                ", currentPosition=" + currentPosition +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.naziv);
        dest.writeValue(this.pocetak);
        dest.writeValue(this.kraj);
        dest.writeString(this.tag);
        dest.writeString(this.timestamp);
        dest.writeInt(this.currentPosition);
    }

    protected ElementModel(Parcel in) {
        this.id = in.readInt();
        this.naziv = in.readString();
        this.pocetak = (Long) in.readValue(Long.class.getClassLoader());
        this.kraj = (Long) in.readValue(Long.class.getClassLoader());
        this.tag = in.readString();
        this.timestamp = in.readString();
        this.currentPosition = in.readInt();
    }

    public static final Creator<ElementModel> CREATOR = new Creator<ElementModel>() {
        @Override
        public ElementModel createFromParcel(Parcel source) {
            return new ElementModel(source);
        }

        @Override
        public ElementModel[] newArray(int size) {
            return new ElementModel[size];
        }
    };
}
