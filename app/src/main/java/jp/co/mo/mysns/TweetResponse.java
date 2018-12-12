package jp.co.mo.mysns;

import java.util.List;

public class TweetResponse extends BaseResponse {

    protected List<TweetInfoResponse> info;

    public TweetResponse(String msg, List<TweetInfoResponse> info) {
        super(msg);
        this.info = info;
    }

    public List<TweetInfoResponse> getInfo() {
        return info;
    }

    public void setInfo(List<TweetInfoResponse> info) {
        this.info = info;
    }
}
