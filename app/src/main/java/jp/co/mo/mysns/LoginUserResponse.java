package jp.co.mo.mysns;

public class LoginUserResponse extends BaseResponse {

    private LoginUserInfoResponse info;

    public LoginUserResponse(String msg, LoginUserInfoResponse info) {
        super(msg);
        this.info = info;
    }

    public LoginUserInfoResponse getInfo() {
        return info;
    }

    public void setInfo(LoginUserInfoResponse info) {
        this.info = info;
    }
}
