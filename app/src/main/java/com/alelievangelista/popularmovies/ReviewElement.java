package com.alelievangelista.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by aevangelista on 15-08-18.
 */
public class ReviewElement implements Parcelable {

    private String reviewAuthor;
    private String reviewContent;

    public ReviewElement(String reviewAuthor, String reviewContent){
        this.reviewAuthor = reviewAuthor;
        this.reviewContent = reviewContent;
    }

    @Override
    public String toString() {
        return "Review Author: " + this.reviewAuthor + "\n" +
                "Review Content: " + this.reviewContent ;
    }

    /**
     * Getters and Setters
     */

    public String getReviewAuthor() { return reviewAuthor; }

    public void setReviewAuthor(String a) { this.reviewAuthor = a; }

    public String getReviewContent() { return reviewContent; }

    public void setReviewContent(String a) { this.reviewContent = a; }

    /**
     * Parcelable Creator
     */
    private ReviewElement(Parcel in) {
        reviewAuthor = in.readString();
        reviewContent = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel p, int flags) {
        p.writeString(reviewAuthor);
        p.writeString(reviewContent);
    }

    /**
     * Receiving and decoding parcels
     */
    public final Parcelable.Creator<ReviewElement> CREATOR = new Parcelable.Creator<ReviewElement>() {

        @Override
        public ReviewElement createFromParcel(Parcel parcel) {
            return new ReviewElement(parcel);
        }

        @Override
        public ReviewElement[] newArray(int size) {
            return new ReviewElement[size];
        }

    };
}
