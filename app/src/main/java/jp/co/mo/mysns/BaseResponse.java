package jp.co.mo.mysns;

public class BaseResponse {

    public static String SUCCESS = "success";
    public static String FAILED = "failed";

    private String msg;

    public BaseResponse(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
