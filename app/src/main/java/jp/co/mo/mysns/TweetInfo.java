package jp.co.mo.mysns;

public class TweetInfo {

    private String tweetId;
    private String tweetText;
    private String tweetPicture;
    private String tweetDate;
    private String userId;
    private String firstName;
    private String picturePath;

    public TweetInfo(String tweetId, String tweetText, String tweetPicture, String tweetDate, String userId, String firstName, String picturePath) {
        this.tweetId = tweetId;
        this.tweetText = tweetText;
        this.tweetPicture = tweetPicture;
        this.tweetDate = tweetDate;
        this.userId = userId;
        this.firstName = firstName;
        this.picturePath = picturePath;
    }

    public String getTweetId() {
        return tweetId;
    }

    public void setTweetId(String tweetId) {
        this.tweetId = tweetId;
    }

    public String getTweetText() {
        return tweetText;
    }

    public void setTweetText(String tweetText) {
        this.tweetText = tweetText;
    }

    public String getTweetPicture() {
        return tweetPicture;
    }

    public void setTweetPicture(String tweetPicture) {
        this.tweetPicture = tweetPicture;
    }

    public String getTweetDate() {
        return tweetDate;
    }

    public void setTweetDate(String tweetDate) {
        this.tweetDate = tweetDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }
}
