package com.alelievangelista.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by aevangelista on 15-08-18.
 */
public class TrailerElement implements Parcelable {

    private String trailerName;
    private String trailerKey;
    private String trailerSite;
    private String trailerSize;
    private String trailerImageUrl;
    private String trailerUrl;


    public TrailerElement(String trailerName, String trailerKey,
                          String trailerSite, String trailerSize,
                          String trailerImageUrl, String trailerUrl) {
        this.trailerName = trailerName;
        this.trailerKey = trailerKey;
        this.trailerSite = trailerSite;
        this.trailerSize = trailerSize;
        this.trailerImageUrl = trailerImageUrl;
        this.trailerUrl = trailerUrl;
    }

    @Override
    public String toString() {
        return "Trailer Name: " + this.trailerName + "\n" +
                "Trailer Key: " + this.trailerKey + "\n"
                + "Trailer Site: " + this.trailerSite + "\n" +
                "Trailer Size: " + this.trailerSize + "\n" +
                "Image URL: " + this.trailerImageUrl+ "\n" +
                "Trailer URL: " + this.trailerUrl;
    }

    /**
     * Getters and Setters
     */

    public String getTrailerName() { return trailerName; }

    public void setTrailerName(String a) { this.trailerName = a; }

    public String getTrailerKey() { return trailerKey; }

    public void setTrailerKey(String a) { this.trailerKey = a; }

    public String getTrailerSite() { return trailerSite; }

    public void setTrailerSite(String a) { this.trailerSite = a; }

    public String getTrailerSize() { return trailerSize; }

    public void setTrailerSize(String a) { this.trailerSize = a; }

    public String getTrailerImageUrl() {
        return trailerImageUrl;
    }

    public void setTrailerImageUrl(String a) {
        this.trailerImageUrl = a;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public void setTrailerUrl(String a) {
        this.trailerUrl = a;
    }

    /**
     * Parcelable Creator
     */
    private TrailerElement(Parcel in) {
        trailerName = in.readString();
        trailerKey = in.readString();
        trailerSite = in.readString();
        trailerSize = in.readString();
        trailerImageUrl = in.readString();
        trailerUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel p, int flags) {
        p.writeString(trailerName);
        p.writeString(trailerKey);
        p.writeString(trailerSite);
        p.writeString(trailerSize);
        p.writeString(trailerImageUrl);
        p.writeString(trailerUrl);
    }

    /**
     * Receiving and decoding parcels
     */
    public final Parcelable.Creator<TrailerElement> CREATOR = new Parcelable.Creator<TrailerElement>() {

        @Override
        public TrailerElement createFromParcel(Parcel parcel) {
            return new TrailerElement(parcel);
        }

        @Override
        public TrailerElement[] newArray(int size) {
            return new TrailerElement[size];
        }

    };
}
