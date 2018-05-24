package com.linkcircle.fj.agorasignal.http.bean;

/**
 * 登录返回
 *
 * @author lifuhai@linkcircle.cn
 * @date 2018/5/17 18:47
 */
public class LoginResultBean {
    private String result;
    private int code;
    private String password;
    private String message;

    public String getResult() {
        return result;
    }

    public void setResult(String pResult) {
        result = pResult;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int pCode) {
        code = pCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String pPassword) {
        password = pPassword;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String pMessage) {
        message = pMessage;
    }
}
