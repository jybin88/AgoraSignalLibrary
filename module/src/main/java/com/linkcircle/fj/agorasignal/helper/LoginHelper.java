package com.linkcircle.fj.agorasignal.helper;

import android.Manifest;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.lfh.custom.common.util.permission.IPermissionClient;
import com.lfh.custom.common.util.permission.PermissionUtil;
import com.linkcircle.fj.agorasignal.SignalType;
import com.linkcircle.fj.agorasignal.http.HttpClient;
import com.linkcircle.fj.agorasignal.http.LoginApi;
import com.linkcircle.fj.agorasignal.http.bean.LoginResultBean;
import com.linkcircle.fj.agorasignal.http.bean.LoginResultCode;
import com.linkcircle.fj.agorasignal.inter.OnLoginListener;
import com.linkcircle.fj.agorasignal.util.PhoneUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * LoginHelper
 *
 * @author lifuhai@linkcircle.cn
 * @date 2018/5/15 17:02
 */
public class LoginHelper {
    private static final String TAG = "LoginHelper";
    private static final String DOMAIN = "sfappcall.linkcircle.net:5030";
    private LoginApi mLoginApi;
    private OnLoginListener mOnLoginListener;
    private static LoginHelper sInstance;
    @SignalType.Type
    private String mSignalType = SignalType.CQT_SIGNAL;//信号类型
    private String mPassword = "";

    /**
     * 改用方法 LoginHelper.getInstance()
     *
     * @deprecated 修改了方法名，弃用
     */
    public static LoginHelper getLoginHelper() {
        if (null == sInstance) {
            synchronized (LoginHelper.class) {
                sInstance = new LoginHelper();
            }
        }

        return sInstance;
    }

    public static LoginHelper getInstance() {
        if (null == sInstance) {
            synchronized (LoginHelper.class) {
                sInstance = new LoginHelper();
            }
        }

        return sInstance;
    }

    public void setOnLoginListener(OnLoginListener pOnLoginListener) {
        mOnLoginListener = pOnLoginListener;
    }

    @SignalType.Type
    public String getSignalType() {
        return mSignalType;
    }

    public String getPassword() {
        return mPassword;
    }

    public String getDomain() {
        return DOMAIN;
    }

    private LoginHelper() {
        mLoginApi = HttpClient.getInstance().createLoginApi();
    }

    /**
     * 登录
     *
     * @param pContext 上下文
     * @param pAccount 账号
     */
    public void login(Context pContext, final String pAccount) {
        PermissionUtil.request(pContext, new IPermissionClient() {
            @Override
            public void onSuccess(final Context pContext) {
                final Call<LoginResultBean> loginRequest = mLoginApi.login(pAccount, PhoneUtil.getIMEI(pContext));
                loginRequest.enqueue(new Callback<LoginResultBean>() {

                    @Override
                    public void onResponse(Call<LoginResultBean> call, Response<LoginResultBean> response) {
                        Log.i(TAG, "account: " + pAccount + " login success");
                        LoginResultBean loginResult = response.body();

                        if (null != loginResult) {
                            if (LoginResultCode.SUCCESS == loginResult.getCode()) {
                                String result = loginResult.getResult();

                                if (result.contains("_")) {
                                    String[] arr = result.split("_");
                                    mPassword = arr[0];
                                    
                                    if (!TextUtils.isEmpty(arr[1])) {
                                        mSignalType = arr[1];
                                    }
                                } else {
                                    mPassword = result;
                                }

                                if (null != mOnLoginListener) {
                                    mOnLoginListener.onLoginSuccess(pAccount);
                                }
                            } else {
                                Log.i(TAG, "login fail code = " + loginResult.getCode() + " result= " + loginResult.getResult());

                                if (null != mOnLoginListener) {
                                    mOnLoginListener.onLoginFail(loginResult.getCode(), loginResult.getMessage());
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResultBean> call, Throwable t) {
                        Log.i(TAG, "account: " + pAccount + " login fail, reason: " + t.getMessage());

                        if (null != mOnLoginListener) {
                            mOnLoginListener.onLoginFail(LoginResultCode.UNKNOWN_EXCEPTION, t.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onFailure(Context pContext) {
                Log.i(TAG, "android.permission.READ_PHONE_STATE deny");

                if (null != mOnLoginListener) {
                    mOnLoginListener.onLoginFail(LoginResultCode.UNKNOWN_EXCEPTION, "android.permission.READ_PHONE_STATE deny");
                }
            }
        }, Manifest.permission.READ_PHONE_STATE);
    }
}
