package com.andevcba.githubmvp.presentation.show_repos.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.andevcba.githubmvp.presentation.show_repos.view.ViewType;

/**
 * UI model of a Repo.
 * Parcelable data to transfer between fragments and to save/restore on orientation changes.
 *
 * @author lucas.nobile
 */
public class RepoUI implements ViewType, Parcelable {

    private String name;
    private String url;

    public RepoUI(String name, String url) {
        this.name = name;
        this.url = url;
    }

    protected RepoUI(Parcel in) {
        name = in.readString();
        url = in.readString();
    }

    @Override
    public int getType() {
        return REPO_ITEM;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(url);
    }

    public static final Creator<RepoUI> CREATOR = new Creator<RepoUI>() {
        @Override
        public RepoUI createFromParcel(Parcel in) {
            return new RepoUI(in);
        }

        @Override
        public RepoUI[] newArray(int size) {
            return new RepoUI[size];
        }
    };
}
