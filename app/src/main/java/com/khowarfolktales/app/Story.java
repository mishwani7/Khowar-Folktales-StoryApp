package com.khowarfolktales.app;

public class Story {
    private String title;
    private String date;
    private String youtubeLink;
    private String videoDownloadLink;  // New field for the download link
    private int views;
    private String urduText;
    private String khowarText;

    public Story() {
        // Default constructor
    }

    public Story(String title, String date, String youtubeLink, int views, String urduText, String khowarText, String videoDownloadLink) {
        this.title = title;
        this.date = date;
        this.youtubeLink = youtubeLink;
        this.views = views;
        this.urduText = urduText;
        this.khowarText = khowarText;
        this.videoDownloadLink = videoDownloadLink;  // Initialize the download link
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public String getVideoDownloadLink() {
        return videoDownloadLink;  // Getter for download link
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getUrduText() {
        return urduText;
    }

    public String getKhowarText() {
        return khowarText;
    }
}
