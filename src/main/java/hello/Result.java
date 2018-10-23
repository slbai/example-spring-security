package hello;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;


public class Result {
    protected String code;
    protected String msg;
    protected Object data;

    public static Result succMsg(String msg) {
        return new Result(Const.SUCCESS, msg);
    }

    public static Result succ(Object data) {
        return new Result(Const.SUCCESS, Const.SUCCESS_MSG, data);
    }

    public static Result succ() {
        return new Result(Const.SUCCESS, Const.SUCCESS_MSG);
    }

    public static Result succ(String msg, Object data) {
        return new Result(Const.SUCCESS, msg, data);
    }

    public static Result fail() {
        return new Result(Const.FAIL, Const.FAIL_MSG);
    }

    public static Result fail(String msg) {
        return new Result(Const.FAIL, msg);
    }


    public Result(String code,String msg,Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    private Result(String code) {
        this.code = code;
        if (Const.SUCCESS.equals(code)) {
            this.msg = Const.SUCCESS_MSG;
        } else {
            this.msg = Const.FAIL_MSG;
        }
    }

    private Result(String code,String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static class Const{
        public static String SUCCESS = "0000";
        public static String FAIL = "9999";

        public static String SUCCESS_MSG = "success";
        public static String FAIL_MSG = "fail";
    }
}

