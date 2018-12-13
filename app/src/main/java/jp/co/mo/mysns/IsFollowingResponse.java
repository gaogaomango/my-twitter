package jp.co.mo.mysns;

public class IsFollowingResponse extends BaseResponse{

    public static String SUBSCRIBER = "subscriber";
    public static String NOT_SUBSCRIBER = "not subscriber";

    private String info;

    public IsFollowingResponse(String msg, String info) {
        super(msg);
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
