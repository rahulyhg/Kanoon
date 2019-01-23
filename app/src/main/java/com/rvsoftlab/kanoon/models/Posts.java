package com.rvsoftlab.kanoon.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;
import com.rvsoftlab.kanoon.helper.Constants;
import com.rvsoftlab.kanoon.helper.Helper;

import java.util.Date;

public class Posts {
    private int PostType = Constants.POST_TYPE.TEXT;
    private String PostText="";
    private String PostImagePath="";
    private int PostLikeCount = 0;
    private int PostCommentCount = 0;
    private String userImage = "";
    @ServerTimestamp
    private Date AddedDateTime = new Date();
    private boolean isAnonymous = true;
    private String uuid;
    private String addedBy;

    public int getPostType() {
        return PostType;
    }

    public void setPostType(int postType) {
        PostType = postType;
    }

    public String getPostText() {
        return PostText;
    }

    public void setPostText(String postText) {
        PostText = postText;
    }

    public String getPostImagePath() {
        return PostImagePath;
    }

    public void setPostImagePath(String postImagePath) {
        PostImagePath = postImagePath;
    }

    public int getPostLikeCount() {
        return PostLikeCount;
    }

    public void setPostLikeCount(int postLikeCount) {
        PostLikeCount = postLikeCount;
    }

    public int getPostCommentCount() {
        return PostCommentCount;
    }

    public void setPostCommentCount(int postCommentCount) {
        PostCommentCount = postCommentCount;
    }

    /*public String getAddedDateTime() {
        return AddedDateTime;
    }

    public void setAddedDateTime(String addedDateTime) {
        AddedDateTime = addedDateTime;
    }*/

    public Date getAddedDateTime() {
        return AddedDateTime;
    }

    public void setAddedDateTime(Date addedDateTime) {
        AddedDateTime = addedDateTime;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }
}
