package com.ravimandala.labs.nytimessearch.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.ravimandala.labs.nytimessearch.utils.Constants;

import java.util.Calendar;

public class Settings implements Parcelable {
    Calendar beginDate;
    boolean isOldestFirst;
    int newsDeskValues;

    public Calendar getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Calendar beginDate) {
        this.beginDate = beginDate;
    }

    public boolean isOldestFirst() {
        return isOldestFirst;
    }

    public void setIsOldestFirst(boolean isOldestFirst) {
        this.isOldestFirst = isOldestFirst;
    }

    public int getNewsDeskValues() {
        return newsDeskValues;
    }

    public void setNewsDeskValues(int newsDeskValues) {
        this.newsDeskValues = newsDeskValues;
    }

    public Settings() {
        this.beginDate = Calendar.getInstance();
        this.isOldestFirst = false;
        this.newsDeskValues = 0;

        Log.d(Constants.TAG, "Created Settings: " + this.toString());
    }

    public Settings(Settings settings) {
        if (settings != null) {
            this.beginDate = (Calendar) settings.getBeginDate().clone();
            this.isOldestFirst = settings.isOldestFirst();
            this.newsDeskValues = settings.getNewsDeskValues();
        } else {
            this.beginDate = Calendar.getInstance();
            this.isOldestFirst = false;
            this.newsDeskValues = 0;
        }
        Log.d(Constants.TAG, "Created Settings: " + this.toString());
    }

    @Override
    public String toString() {
        return "BeginDate: " + beginDate.toString() + "; isOldestFirst = " + isOldestFirst + "; newsDeskValues = " + newsDeskValues;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.beginDate);
        dest.writeByte(isOldestFirst ? (byte) 1 : (byte) 0);
        dest.writeInt(this.newsDeskValues);
    }

    protected Settings(Parcel in) {
        this.beginDate = (Calendar) in.readSerializable();
        this.isOldestFirst = in.readByte() != 0;
        this.newsDeskValues = in.readInt();
    }

    public static final Parcelable.Creator<Settings> CREATOR = new Parcelable.Creator<Settings>() {
        public Settings createFromParcel(Parcel source) {
            return new Settings(source);
        }

        public Settings[] newArray(int size) {
            return new Settings[size];
        }
    };
}
