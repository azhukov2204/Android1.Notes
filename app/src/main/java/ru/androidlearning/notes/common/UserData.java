package ru.androidlearning.notes.common;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class UserData implements Parcelable {
    private String userName;
    private String userEmail;
    private Uri userAvatarUri;

    public UserData() {
        userName = null;
        userEmail = null;
        userAvatarUri = null;
    }

    protected UserData(Parcel in) {
        userName = in.readString();
        userEmail = in.readString();
        userAvatarUri = Uri.parse(in.readString());
    }

    public static final Creator<UserData> CREATOR = new Creator<UserData>() {
        @Override
        public UserData createFromParcel(Parcel in) {
            return new UserData(in);
        }

        @Override
        public UserData[] newArray(int size) {
            return new UserData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(userEmail);
        dest.writeString(userAvatarUri.toString());
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserAvatarUri(Uri userAvatarUri) {
        this.userAvatarUri = userAvatarUri;
    }

    public Uri getUserAvatarUri() {
        return userAvatarUri;
    }
}
