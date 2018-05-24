package com.linkcircle.fj.agorasignal;

/**
 * 常量定义
 *
 * @author lifuhai@linkcircle.cn
 * @date 2018/5/15 09:15
 */
public class AgoraSignalConstants {
    public final static int SINGALING_CALL = 0x10001;
    public final static int SIGNALING_REFUSE = 0x10002;
    public final static int SIGNALING_ACCEPT = 0x10003;
    public final static int SIGNALING_JOIN_CHANNEL = 0x10004;
    public final static int SIGNALING_LOGIN = 0x10005;
    public final static int SIGNALING_LEAVE_CHANNEL = 0x10006;
    public final static int SIGNALING_END = 0x10007;
    public final static int SIGNALING_LOGOUT = 0x10008;
    public final static int SIGNALING_QUERY_LOGIN = 0x10009;
    public final static int SIGNALING_INSTANT_SEND = 0x10010;

    public final static int MEDIA_JOINCHANNEL = 0x20001;
    public final static int MEDIA_LEAVECHANNEL = 0x20002;

    public final static int WORKER_EXIT = 0x30001;
}
