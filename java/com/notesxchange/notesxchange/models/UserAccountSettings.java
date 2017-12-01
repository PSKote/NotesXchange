package com.notesxchange.notesxchange.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class to set and get useraccountaettings in database. It canalso be parsed
 */

public class UserAccountSettings implements Parcelable{

    private String college;
    private String display_name;
    private long following;
    private long uploads;
    private String profile_photo;
    private String username;
    private String year;
    private String branch;
    private String user_id;

    public UserAccountSettings(String college, String display_name,
                               long following, long uploads, String profile_photo, String username,
                               String year, String branch, String user_id) {
        this.college = college;
        this.display_name = display_name;
        this.following = following;
        this.uploads = uploads;
        this.profile_photo = profile_photo;
        this.username = username;
        this.year = year;
        this.branch = branch;
        this.user_id = user_id;
    }
    public UserAccountSettings() {

    }

    protected UserAccountSettings(Parcel in) {
        college = in.readString();
        display_name = in.readString();
        following = in.readLong();
        uploads = in.readLong();
        profile_photo = in.readString();
        username = in.readString();
        year = in.readString();
        branch = in.readString();
        user_id = in.readString();
    }

    public static final Creator<UserAccountSettings> CREATOR = new Creator<UserAccountSettings>() {
        @Override
        public UserAccountSettings createFromParcel(Parcel in) {
            return new UserAccountSettings(in);
        }

        @Override
        public UserAccountSettings[] newArray(int size) {
            return new UserAccountSettings[size];
        }
    };

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public long getFollowing() {
        return following;
    }

    public void setFollowing(long following) {
        this.following = following;
    }

    public long getUploads() {
        return uploads;
    }

    public void setUploads(long uploads) {
        this.uploads = uploads;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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


    @Override
    public String toString() {
        return "UserAccountSettings{" +
                "college='" + college + '\'' +
                ", display_name='" + display_name + '\'' +
                ", following=" + following +
                ", uploads=" + uploads +
                ", profile_photo='" + profile_photo + '\'' +
                ", username='" + username + '\'' +
                ", year='" + year + '\'' +
                ", branch='" + branch + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(college);
        dest.writeString(display_name);
        dest.writeLong(following);
        dest.writeLong(uploads);
        dest.writeString(profile_photo);
        dest.writeString(username);
        dest.writeString(year);
        dest.writeString(branch);
        dest.writeString(user_id);
    }
}