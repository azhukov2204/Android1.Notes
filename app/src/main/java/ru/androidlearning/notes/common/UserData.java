package ru.androidlearning.notes.common;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import ru.androidlearning.notes.R;

public class UserData implements Parcelable {
    private String userID;
    private String userName;
    private Uri userAvatarUri;

    public UserData() {
        userID = "";
        userName = "";
        userAvatarUri = null;
    }

    protected UserData(Parcel in) {
        userID = in.readString();
        userName = in.readString();
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
        String userAvatarUriString;
        if (userID == null) {
            userID = "";
        }

        if (userName == null) {
            userName = "";
        }

        if (userAvatarUri == null) {
            userAvatarUriString = "";
        } else {
            userAvatarUriString = userAvatarUri.toString();
        }
        dest.writeString(userID);
        dest.writeString(userName);
        dest.writeString(userAvatarUriString);
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserAvatarUri(Uri userAvatarUri) {
        this.userAvatarUri = userAvatarUri;
    }

    public Uri getUserAvatarUri() {
        return userAvatarUri;
    }
}
