package com.linkcircle.fj.agorasignal;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 信号类型
 * Created by Jason on 2018/6/9 10:50.
 */
public class SignalType {
    public static final String CQT_SIGNAL = "0";
    public static final String AGORA_SIGNAL = "1";

    @StringDef({CQT_SIGNAL, AGORA_SIGNAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }
}
