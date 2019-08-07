package com.linkcircle.fj.agorasignal.api;

import android.content.Context;

import com.linkcircle.fj.agorasignal.inter.OnCallListener;
import com.linkcircle.fj.agorasignal.inter.OnSipInitListener;
import com.linkcircle.fj.agorasignal.inter.OnSipLoginListener;

/**
 * @author lifuhai@linkcircle.cn
 * @date 2018/9/25 09:12
 */
abstract class BaseSignalApi {
    /**
     * 初始化
     *
     * @param pContext           上下文
     * @param pOnSipInitListener sip初始化监听
     */
    public abstract void init(Context pContext, OnSipInitListener pOnSipInitListener);

    /**
     * 设置sip登录监听
     *
     * @param pOnSipLoginListener sip登录监听
     */
    public abstract void setSipLoginListener(OnSipLoginListener pOnSipLoginListener);

    /**
     * 设置sip拨打电话监听
     *
     * @param pOnCallListener sip拨打电话监听
     */
    public abstract void setCallListener(OnCallListener pOnCallListener);

    /**
     * 登录sip。用户在进行任何操作前，必须先登录
     * 成功回调 onLoginSuccess
     * 失败回调 onLoginFailed
     *
     * @param pAccount 用户账号，最大 128 字节可见字符（不能使用空格），必须保证唯一
     */
    public abstract void loginSip(String pAccount);

    /**
     * 登出sip
     */
    public abstract void logoutSip();

    /**
     * 呼叫
     *
     * @param pCallNumber 呼叫的号码
     */
    public abstract void call(String pCallNumber);

    /**
     * 挂断
     */
    public abstract void endCall();

    /**
     * 接听电话
     */
    public abstract void acceptCall();

    /**
     * 拒绝接听
     */
    public abstract void refuseCall();

    /**
     * 释放资源
     */
    public abstract void release();

    /**
     * 移除sip监听
     */
    public abstract void removeSipCallback();
}
