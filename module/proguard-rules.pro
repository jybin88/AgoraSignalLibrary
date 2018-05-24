-dontwarn javax.annotation.**
-dontwarn javax.inject.**
# OkHttp3
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}
-dontwarn okio.**
-keep class okio.**{*;}
# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.**{*;}
-dontwarn retrofit2.converter.gson.**
-keep class retrofit2.converter.gson.**{*;}
# Gson
-keep class com.google.gson.stream.** {*;}
# RxJava RxAndroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
#rxpermissions
-dontwarn com.tbruyelle.rxpermissions.**
-keep class com.tbruyelle.rxpermissions.**{*;}

-dontwarn com.lfh.custom.common.**
-keep class com.lfh.custom.common.**{*;}

-dontwarn io.agora.**
-keep class io.agora.**{*;}

-keep class com.linkcircle.fj.agorasignal.http.bean.**{*;}
