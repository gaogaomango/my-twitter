package jp.co.mo.mysns;

import java.util.List;

public class LoginUserResponse extends BaseResponse {

    protected List<LoginUserInfoResponse> info;

    public LoginUserResponse(String msg, List<LoginUserInfoResponse> info) {
        super(msg);
        this.info = info;
    }

    public List<LoginUserInfoResponse> getInfo() {
        return info;
    }

    public void setInfo(List<LoginUserInfoResponse> info) {
        this.info = info;
    }
}
