package com.linkcircle.fj.agorasignal.api;

import android.content.Context;
import android.text.TextUtils;

import com.linkcircle.fj.agorasignal.R;
import com.linkcircle.fj.agorasignal.bean.LoginResultCode;
import com.linkcircle.fj.agorasignal.inter.HttpRequestListener;
import com.linkcircle.fj.agorasignal.inter.OnLoginListener;
import com.linkcircle.fj.agorasignal.util.LCSignalLog;
import com.linkcircle.fj.agorasignal.util.PhoneUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * LoginApi
 *
 * @author lifuhai@linkcircle.cn
 * @date 2018/9/25 10:38
 */
public class LoginApi {
    private static final String TAG = "LoginApi";
    private static final String DOMAIN = "sfappcall.linkcircle.net:5030";//sip默认域名
    private static String sDomain = DOMAIN; //sip域名
    private static LoginApi sInstance;
    private OnLoginListener mLoginListener;
    private String mPassword;

    public static LoginApi getInstance() {
        if (null == sInstance) {
            synchronized (LoginApi.class) {
                if (null == sInstance) {
                    sInstance = new LoginApi();
                }
            }
        }
        return sInstance;
    }

    public void setDomain(String pDomain) {
        sDomain = pDomain;
    }

    public void setLoginUrl(String pLoginUrl) {
        HttpApi.getInstance().setLoginUrl(pLoginUrl);
    }

    public void setLoginListener(OnLoginListener pLoginListener) {
        mLoginListener = pLoginListener;
    }

    /**
     * 获取登录密码
     *
     * @return 登录密码
     */
    public String getPassword() {
        return mPassword;
    }

    public static String getDomain() {
        return sDomain;
    }

    /**
     * 登录
     * <p>
     * api 23及以上调用时需要动态申请android.permission.READ_PHONE_STATE权限
     *
     * @param pContext 上下文
     * @param pAccount 账号
     */
    public void login(final Context pContext, final String pAccount) {
        if (null == pContext) {
            String reason = "context null";
            LCSignalLog.d("class: " + TAG + ", login: 账号登录失败, code--->" + LoginResultCode.ILLEGAL_ARGUMENT_EXCEPTION + " reason--->" + reason);
            mLoginListener.onLoginFail(LoginResultCode.ILLEGAL_ARGUMENT_EXCEPTION, reason);

            return;
        }

        if (TextUtils.isEmpty(pAccount)) {
            String reason = "账号为空";
            LCSignalLog.d("class: " + TAG + ", login: 账号登录失败, code--->" + LoginResultCode.ILLEGAL_ARGUMENT_EXCEPTION + " reason--->" + reason);
            mLoginListener.onLoginFail(LoginResultCode.ILLEGAL_ARGUMENT_EXCEPTION, reason);

            return;
        }

        if (null == mLoginListener) {
            String reason = "监听未设置";
            LCSignalLog.d("class: " + TAG + ", login: 账号登录失败, code--->" + LoginResultCode.ILLEGAL_ARGUMENT_EXCEPTION + " reason--->" + reason);
            mLoginListener.onLoginFail(LoginResultCode.ILLEGAL_ARGUMENT_EXCEPTION, reason);

            return;
        }

        String imei = PhoneUtil.getIMEI(pContext);

        if (TextUtils.isEmpty(imei)) {
            String reason = formatErrorString(pContext, LoginResultCode.OBTAIN_IMET_FAIL);
            LCSignalLog.d("class: " + TAG + ", login: 账号登录失败, code--->" + LoginResultCode.OBTAIN_IMET_FAIL + " reason--->" + reason);
            mLoginListener.onLoginFail(LoginResultCode.OBTAIN_IMET_FAIL, reason);

            return;
        }

        final HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("agentId", pAccount);
        paramMap.put("imei", imei);

        new Thread() {
            @Override
            public void run() {
                HttpApi.getInstance().post(paramMap, new HttpRequestListener() {
                    @Override
                    public void onRequestSuccess(String pResult) {
                        try {
                            JSONObject jsonObject = new JSONObject(pResult);
                            int code = jsonObject.optInt("code");
                            String message = jsonObject.optString("message");

                            if (LoginResultCode.SUCCESS == code) {
                                String result = jsonObject.optString("result");
                                if (result.contains("_")) {
                                    String[] arr = result.split("_");
                                    mPassword = arr[0];
                                } else {
                                    mPassword = result;
                                }
                                LCSignalLog.d("class: " + TAG + ", login: 账号登录成功");
                                if (null != mLoginListener) {
                                    mLoginListener.onLoginSuccess(pAccount);
                                }
                            } else {
                                if (TextUtils.isEmpty(message)) {
                                    message = formatErrorString(pContext, code);
                                }

                                LCSignalLog.d("class: " + TAG + ", login: 账号登录失败, code--->" + code + " reason--->" + message);
                                mLoginListener.onLoginFail(code, message);
                            }
                        } catch (JSONException pE) {
                            String message = pE.getMessage();

                            if (TextUtils.isEmpty(message)) {
                                message = formatErrorString(pContext, LoginResultCode.JSON_PARSE_EXCEPTION);
                            }

                            LCSignalLog.d("class: " + TAG + ", login: 账号登录失败, code--->" + LoginResultCode.JSON_PARSE_EXCEPTION + " reason--->" + message);
                            mLoginListener.onLoginFail(LoginResultCode.JSON_PARSE_EXCEPTION, pE.getMessage());
                        }
                    }

                    @Override
                    public void onRequestFail(int pCode, String pReason) {
                        String message = !TextUtils.isEmpty(pReason) ? pReason : formatErrorString(pContext, pCode);
                        LCSignalLog.d("class: " + TAG + ", login: 账号登录失败, code--->" + pCode + " reason--->" + message);
                        mLoginListener.onLoginFail(pCode, message);
                    }
                });
            }
        }.start();
    }

    /**
     * 格式化错误提示信息
     *
     * @param pContext 上下文
     * @param pCode    错误码
     * @return 错误提示信息
     */
    private String formatErrorString(Context pContext, int pCode) {
        String codeString = "";

        switch (pCode) {
            case LoginResultCode.ACCOUNT_NOT_EXIST:
                codeString = pContext.getString(R.string.lcSignal_account_not_exist);
                break;
            case LoginResultCode.ACCOUNT_ERROR:
                codeString = pContext.getString(R.string.lcSignal_account_error);
                break;
            case LoginResultCode.IMEI_NOT_EXIST:
                codeString = pContext.getString(R.string.lcSignal_imei_not_exist);
                break;
            case LoginResultCode.IMEI_NOT_EXIST_IN_DB:
                codeString = pContext.getString(R.string.lcSignal_imei_not_exist_in_db);
                break;
            case LoginResultCode.IMEI_ERROR:
                codeString = pContext.getString(R.string.lcSignal_imei_error);
                break;
            case LoginResultCode.UNKNOWN_EXCEPTION:
                codeString = pContext.getString(R.string.lcSignal_unknown_exception);
                break;
            case LoginResultCode.OBTAIN_IMET_FAIL:
                codeString = pContext.getString(R.string.lcSignal_obtain_imei_fail);
                break;
            case LoginResultCode.REQUEST_FAIL:
                codeString = pContext.getString(R.string.lcSignal_request_fail);
                break;
            case LoginResultCode.REQUEST_EXCEPTION:
                codeString = pContext.getString(R.string.lcSignal_request_exception);
                break;
            case LoginResultCode.JSON_PARSE_EXCEPTION:
                codeString = pContext.getString(R.string.lcSignal_json_parse_exception);
                break;
            default:
                break;
        }

        return codeString;
    }
}
