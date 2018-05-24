package com.linkcircle.fj.agorasignal;

/**
 * 状态
 *
 * @author lifuhai@linkcircle.cn
 * @date 2018/5/15 09:49
 */
public class Status {
    public enum PatternStatus {
        NONE,
        APP_VOIP,
        APP_PSTN,
        PSTN_APP,
        SYS_CALL
    }

    public enum CallStatus {
        NONE,
        INVITING,
        MEDIA_JOINED,
    }
}
