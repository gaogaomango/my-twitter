package jp.co.mo.mysns;

public class LoginUserInfoResponse {

    private String user_id;
    private String first_name;
    private String email;
    private String password;
    private String picture_path;

    public LoginUserInfoResponse(String user_id, String first_name, String email, String password, String picture_path) {
        this.user_id = user_id;
        this.first_name = first_name;
        this.email = email;
        this.password = password;
        this.picture_path = picture_path;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPicture_path() {
        return picture_path;
    }

    public void setPicture_path(String picture_path) {
        this.picture_path = picture_path;
    }
}
