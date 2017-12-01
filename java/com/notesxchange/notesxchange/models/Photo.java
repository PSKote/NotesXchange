package com.notesxchange.notesxchange.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Class to get and set photos to databse. It cal be parced from activity to another.
 */

public class Photo implements Parcelable {

    private String title;
    private String date_created;
    private String image_path;
    private String photo_id;
    private String user_id;
    private String tags;
    private String category;
    private String privacy;
    private String college;
    private String year;
    private String branch;
    private List<Like> likes;
    private List<Comment> comments;


    public Photo() {

    }

    public Photo(String title, String date_created, String image_path, String photo_id,
                 String user_id, String tags, List<Like> likes, List<Comment> comments,
                 String category, String privacy, String college, String year, String branch) {
        this.title = title;
        this.date_created = date_created;
        this.image_path = image_path;
        this.photo_id = photo_id;
        this.user_id = user_id;
        this.tags = tags;
        this.likes = likes;
        this.comments = comments;
        this.category = category;
        this.privacy = privacy;
        this.college = college;
        this.year = year;
        this.branch = branch;
    }

    protected Photo(Parcel in) {
        title = in.readString();
        date_created = in.readString();
        image_path = in.readString();
        photo_id = in.readString();
        user_id = in.readString();
        tags = in.readString();
        category = in.readString();
        privacy = in.readString();
        college = in.readString();
        year = in.readString();
        branch = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(date_created);
        dest.writeString(image_path);
        dest.writeString(photo_id);
        dest.writeString(user_id);
        dest.writeString(tags);
        dest.writeString(category);
        dest.writeString(privacy);
        dest.writeString(college);
        dest.writeString(year);
        dest.writeString(branch);
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public static Creator<Photo> getCREATOR() {
        return CREATOR;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "title='" + title + '\'' +
                ", date_created='" + date_created + '\'' +
                ", image_path='" + image_path + '\'' +
                ", photo_id='" + photo_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", tags='" + tags + '\'' +
                ", category='" + category + '\'' +
                ", privacy='" + privacy + '\'' +
                ", college='" + college + '\'' +
                ", year='" + year + '\'' +
                ", branch='" + branch + '\'' +
                ", likes=" + likes +
                '}';
    }


}