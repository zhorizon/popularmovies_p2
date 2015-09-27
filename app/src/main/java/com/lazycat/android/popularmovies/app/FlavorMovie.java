package com.lazycat.android.popularmovies.app;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Cencil on 8/9/2015.
 */
public class FlavorMovie implements Parcelable {
    // Hold movie data base on Json from themoviedb in POJO
    // e.g.
    //        "adult":false,
    //        "backdrop_path":"/sLbXneTErDvS3HIjqRWQJPiZ4Ci.jpg",
    //        "genre_ids":-[10751,16,12,35],
    //        "id":211672,
    //        "original_language":"en",
    //        "original_title":"Minions",
    //        "overview":"Minions Stuart, Kevin and Bob are recruited by Scarlet Overkill, a super-villain who, alongside her inventor husband Herb, hatches a plot to take over the world.",
    //        "release_date":"2015-06-25",
    //        "poster_path":"/qARJ35IrJNFzFWQGcyWP4r1jyXE.jpg",
    //        "popularity":47.804014,
    //        "title":"Minions",
    //        "video":false,
    //        "vote_average":7.1,
    //        "vote_count":860

    private int id;
    private String title;
    private String originalTitle;
    private String overview;
    private String posterPath;
    private String backdropPath;
    private Date releaseDate;
    private float popularity;
    private int voteAverage;
    private int voteCount;
    private boolean adult = false;
    private boolean video = false;

    public FlavorMovie() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public String getReleaseDateString() {
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        return df.format(releaseDate);
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public float getPopularity() {
        return popularity;
    }

    public void setPopularity(float popularity) {
        this.popularity = popularity;
    }

    public String getRating() {
        return voteAverage + "/" + voteCount;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public int getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(int voteAverage) {
        this.voteAverage = voteAverage;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public String toString() {
        return title + " (" + id + ")";
    }

    public static final Parcelable.Creator<FlavorMovie> CREATOR
            = new Parcelable.Creator<FlavorMovie>() {
        @Override
        public FlavorMovie createFromParcel(Parcel in) {
            return new FlavorMovie(in);
        }

        @Override
        public FlavorMovie[] newArray(int size) {
            return new FlavorMovie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(title);
        out.writeString(originalTitle);
        out.writeString(overview);
        out.writeString(posterPath);
        out.writeString(backdropPath);
        out.writeLong(releaseDate.getTime());
        out.writeFloat(popularity);
        out.writeInt(voteAverage);
        out.writeInt(voteCount);
        out.writeString(Boolean.toString(adult));
        out.writeString(Boolean.toString(video));
    }

    private FlavorMovie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        originalTitle = in.readString();
        overview = in.readString();
        posterPath = in.readString();
        backdropPath = in.readString();
        releaseDate = new Date(in.readLong());
        popularity = in.readFloat();
        voteAverage = in.readInt();
        voteCount = in.readInt();
        adult = Boolean.parseBoolean(in.readString());
        video = Boolean.parseBoolean(in.readString());
    }
}
