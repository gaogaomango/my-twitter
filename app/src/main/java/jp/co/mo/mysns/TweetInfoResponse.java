package jp.co.mo.mysns;

public class TweetInfoResponse {

    private String tweet_id;
    private String tweet_text;
    private String tweet_picture;
    private String tweet_date;
    private String user_id;
    private String first_name;
    private String picture_path;

    public TweetInfoResponse(String tweet_id, String tweet_text, String tweet_picture, String tweet_date, String user_id, String first_name, String picture_path) {
        this.tweet_id = tweet_id;
        this.tweet_text = tweet_text;
        this.tweet_picture = tweet_picture;
        this.tweet_date = tweet_date;
        this.user_id = user_id;
        this.first_name = first_name;
        this.picture_path = picture_path;
    }

    public String getTweet_id() {
        return tweet_id;
    }

    public void setTweet_id(String tweet_id) {
        this.tweet_id = tweet_id;
    }

    public String getTweet_text() {
        return tweet_text;
    }

    public void setTweet_text(String tweet_text) {
        this.tweet_text = tweet_text;
    }

    public String getTweet_picture() {
        return tweet_picture;
    }

    public void setTweet_picture(String tweet_picture) {
        this.tweet_picture = tweet_picture;
    }

    public String getTweet_date() {
        return tweet_date;
    }

    public void setTweet_date(String tweet_date) {
        this.tweet_date = tweet_date;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getPicture_path() {
        return picture_path;
    }

    public void setPicture_path(String picture_path) {
        this.picture_path = picture_path;
    }
}
