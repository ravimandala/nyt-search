package com.ravimandala.labs.nytimessearch.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Date;

public class Settings implements Parcelable {
    Date beginDate;
    boolean isOldestFirst;
    int newsDeskValues;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(beginDate != null ? beginDate.getTime() : -1);
        dest.writeByte(isOldestFirst ? (byte) 1 : (byte) 0);
        dest.writeInt(this.newsDeskValues);
    }

    public Settings() {
        this.beginDate = Calendar.getInstance().getTime();
        this.isOldestFirst = false;
        this.newsDeskValues = 0;
    }

    protected Settings(Parcel in) {
        long tmpBeginDate = in.readLong();
        this.beginDate = tmpBeginDate == -1 ? null : new Date(tmpBeginDate);
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

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
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
}
