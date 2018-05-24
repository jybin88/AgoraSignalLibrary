package com.linkcircle.fj.agorasignal.http;

import com.linkcircle.fj.agorasignal.http.bean.LoginResultBean;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * LoginApi
 *
 * @author lifuhai@linkcircle.cn
 * @date 2018/5/15 14:11
 */
public interface LoginApi {
    @FormUrlEncoded
    @POST("scrambles/logincheck")
    public Call<LoginResultBean> login(@Field("agentId") String pAccount, @Field("imei") String pImei);
}
