package com.example.newsapplication.dummy;

import java.util.List;

public class NewsItem {

    private String mChannel;
    private String mTitle;
    private String mAuthor;
    private String mPubTime;
    private List<String> mImages;
    private String mContent;
    private String mVideo = null;
    private boolean isInHistory = false;
    private boolean isInFavorite = false;
    private String mNewsID;

    public List<String> getmKeywords() {
        return mKeywords;
    }

    public void setmKeywords(List<String> mKeywords) {
        this.mKeywords = mKeywords;
    }

    private List<String> mKeywords;

    public String getmNewsID() {
        return mNewsID;
    }

    public void setmNewsID(String newsID) {
        this.mNewsID = newsID;
    }

    public boolean isInHistory() {
        return isInHistory;
    }

    public void setInHistory(boolean inHistory) {
        isInHistory = inHistory;
    }

    public boolean isInFavorite() {
        return isInFavorite;
    }

    public void setInFavorite(boolean inFavorite) {
        isInFavorite = inFavorite;
    }

    private int mType = 0;

    final int NO_IMG = 0;
    final int SINGLE_IMG = 1;
    final int MULTI_IMGS = 2;
    final int VIDEO = 3;

    public NewsItem() {}
    public NewsItem(String Title)
    {
        this.mTitle = Title;

    }

    public NewsItem(String mTitle, String mAuthor, String mPubTime, List<String> mImages, String mContent, String newsID) {

        this.mTitle = mTitle;
        this.mAuthor = mAuthor;
        this.mPubTime = mPubTime;
        this.mImages = mImages;
        this.mContent = mContent;
        this.mNewsID = newsID;

        if(mImages == null || mImages.size() == 0) mType = NO_IMG;
        else mType = (mImages.size() >= 3) ? MULTI_IMGS : SINGLE_IMG;

        if(mVideo != null)
            mType = VIDEO;
    }

    public NewsItem(String mTitle, String mAuthor, String mPubTime, List<String> mImages, String mContent, String id, int mType) {
        this.mTitle = mTitle;
        this.mAuthor = mAuthor;
        this.mPubTime = mPubTime;
        this.mImages = mImages;
        this.mContent = mContent;
        this.mNewsID = id;
        this.mType = mType;


    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setmAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    public void setmImages(List<String> mImages) {
        this.mImages = mImages;
    }

    public void setmPubTime(String mPubTime) {
        this.mPubTime = mPubTime;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }

    public int getmType() {
        return mType;
    }

    public String getmVideo() { return mVideo; }

    public void setmVideo(String mVideo) { this.mVideo = mVideo; }

    public List<String> getmImages() {
        return mImages;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public String getmContent() {
        return mContent;
    }

    public String getmPubTime() {
        return mPubTime;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmChannel() {
        return mChannel;
    }

    public void setmChannel(String mChannel) {
        this.mChannel = mChannel;
    }

}
