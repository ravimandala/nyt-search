package com.ravimandala.labs.nytimessearch.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Article implements Parcelable {
    private static final String TAG = "NYTSearch";

    String webUrl;
    String headline;
    String thumbnailURL;

    public String getHeadline() {
        return headline;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public Article(JSONObject jsonObject) {
        try {
            webUrl = jsonObject.optString("web_url");
            if (jsonObject.optString("headline") != null) {
                headline = jsonObject.optJSONObject("headline").getString("main");
            }
            if (jsonObject.optJSONArray("multimedia") != null) {
                thumbnailURL= "";
                JSONArray multimedia = jsonObject.getJSONArray("multimedia");
                for (int i=0; i<multimedia.length(); i++) {
                    JSONObject jsonMultiMedia = multimedia.getJSONObject(i);
                    if ("thumbnail".equals(jsonMultiMedia.getString("subtype"))) {
                        thumbnailURL = "http://www.nytimes.com/" + jsonMultiMedia.optString("url");
                        break;
                    }
                }
            }
            Log.d(TAG, "Creating Article - " + this.toString());
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Article> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Article> articles = new ArrayList<>();

        for (int i=0; i<jsonArray.length(); i++) {
            try {
                articles.add(new Article(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return articles;
    }

    @Override
    public String toString() {
        return "WebURL: " + webUrl + "; headline: " + headline + "; thumbnailURL: " + thumbnailURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.webUrl);
        dest.writeString(this.headline);
        dest.writeString(this.thumbnailURL);
    }

    protected Article(Parcel in) {
        this.webUrl = in.readString();
        this.headline = in.readString();
        this.thumbnailURL = in.readString();
    }

    public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
        public Article createFromParcel(Parcel source) {
            return new Article(source);
        }

        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}

