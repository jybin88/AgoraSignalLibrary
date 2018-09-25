package com.linkcircle.fj.agorasignal.bean;

/**
 * LoginResultCode
 *
 * @author lifuhai@linkcircle.cn
 * @date 2018/5/17 18:52
 */
public class LoginResultCode {
    /*验证成功*/
    public static final int SUCCESS = 0;
    /*分机账号不存在*/
    public static final int ACCOUNT_NOT_EXIST = 1;
    /*分机账号错误*/
    public static final int ACCOUNT_ERROR = 2;
    /*imei不存在（没有接收到imei）*/
    public static final int IMEI_NOT_EXIST = 3;
    /*imei不存在(数据库未查询到imei)*/
    public static final int IMEI_NOT_EXIST_IN_DB = 4;
    /*imei值错误*/
    public static final int IMEI_ERROR = 5;
    /*未知异常*/
    public static final int UNKNOWN_EXCEPTION = 6;
    /*获取不到手机imei*/
    public static final int OBTAIN_IMET_FAIL = 7;
    /*请求失败*/
    public static final int REQUEST_FAIL = 8;
    /*请求异常*/
    public static final int REQUEST_EXCEPTION = 9;
    /*json字符解析出错*/
    public static final int JSON_PARSE_EXCEPTION = 10;
    /*参数缺少*/
    public static final int ILLEGAL_ARGUMENT_EXCEPTION = 11;
}
