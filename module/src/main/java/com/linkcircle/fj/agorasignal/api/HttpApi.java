package com.linkcircle.fj.agorasignal.api;

import android.util.Log;

import com.linkcircle.fj.agorasignal.bean.LoginResultCode;
import com.linkcircle.fj.agorasignal.inter.HttpRequestListener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * 网络请求 使用HttpURLConnection 不用第三方库
 *
 * @author lifuhai@linkcircle.cn
 * @date 2018/8/29 10:58
 */
final class HttpApi {
    private final static String TAG = "HttpUtil";
    private final static String BASE_URL = "http://sfapp.linkcircle.net/";//请求基类地址
    private final static String LOGIN_URL = BASE_URL + "scrambles/logincheck";//登录地址
    private final static int CONNECT_TIMEOUT = 5 * 1000;//连接超时时间
    private final static int READ_TIMEOUT = 5 * 1000;//读取超时时间
    private static HttpApi sInstance;

    public static HttpApi getInstance() {
        if (null == sInstance) {
            synchronized (HttpApi.class) {
                if (null == sInstance) {
                    sInstance = new HttpApi();
                }
            }
        }

        return sInstance;
    }

    /**
     * post请求
     *
     * @param pParamMap            参数
     * @param pHttpRequestListener 监听
     */
    public void post(HashMap<String, String> pParamMap, HttpRequestListener pHttpRequestListener) {
        try {
            //合成参数
            StringBuilder paramBuilder = new StringBuilder();

            for (String key : pParamMap.keySet()) {
                paramBuilder.append("&");
                paramBuilder.append(String.format("%s=%s", key, URLEncoder.encode(pParamMap.get(key), "utf-8")));
            }

            paramBuilder.deleteCharAt(0);//删除第一个&

            String param = paramBuilder.toString();
            byte[] postData = param.getBytes();// 请求的参数转换为byte数组
            URL url = new URL(LOGIN_URL);// 新建一个URL对象
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();// 打开一个HttpURLConnection连接
            httpURLConnection.setConnectTimeout(CONNECT_TIMEOUT);// 设置连接超时时间
            httpURLConnection.setReadTimeout(READ_TIMEOUT);// 设置从主机读取数据超时
            httpURLConnection.setDoOutput(true);// Post请求必须设置允许输出 默认false
            httpURLConnection.setDoInput(true);// 设置请求允许输入 默认是true
            httpURLConnection.setUseCaches(false);// Post请求不能使用缓存
            httpURLConnection.setRequestMethod("POST");// 设置为Post请求
            httpURLConnection.setInstanceFollowRedirects(true);// 设置本次连接是否自动处理重定向
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");// 配置请求Content-Type
            httpURLConnection.connect();// 开始连接
            // 发送请求参数
            DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.write(postData);
            dataOutputStream.flush();
            dataOutputStream.close();
            int responseCode = httpURLConnection.getResponseCode();

            if (200 <= responseCode && 300 > responseCode) {//请求成功
                String result = streamToString(httpURLConnection.getInputStream());
                Log.d(TAG, "post请求成功, result--->" + result);

                if (null != pHttpRequestListener) {
                    pHttpRequestListener.onRequestSuccess(result);
                }
            } else {
                String errorResult = streamToString(httpURLConnection.getErrorStream());
                Log.d(TAG, "post请求失败, code--->" + responseCode + " result--->" + errorResult);

                if (null != pHttpRequestListener) {
                    pHttpRequestListener.onRequestFail(LoginResultCode.REQUEST_FAIL, errorResult);
                }
            }

            httpURLConnection.disconnect();// 关闭连接
        } catch (Exception pE) {
            pE.printStackTrace();

            if (null != pHttpRequestListener) {
                pHttpRequestListener.onRequestFail(LoginResultCode.REQUEST_EXCEPTION, pE.getMessage());
            }
        }
    }

    /**
     * InputStream 转成 String
     *
     * @param pInputStream 输入流
     * @return 字符串
     */
    private String streamToString(InputStream pInputStream) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;

            while ((length = pInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }

            byteArrayOutputStream.close();
            pInputStream.close();
            byte[] bytes = byteArrayOutputStream.toByteArray();
            return new String(bytes);
        } catch (Exception pE) {
            pE.printStackTrace();
            return null;
        }
    }
}
