package com.linkcircle.fj.agorasignal.inter;

/**
 * @author lifuhai@linkcircle.cn
 * @date 2018/9/25 09:38
 */
public interface HttpRequestListener {
    void onRequestSuccess(String pResult);

    void onRequestFail(int pCode, String pReason);
}
