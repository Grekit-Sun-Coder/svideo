# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-optimizationpasses 7
-dontusemixedcaseclassnames
-useuniqueclassmembernames
-flattenpackagehierarchy cn.weli.svideo
-repackageclass cn.weli.svideo
-overloadaggressively
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes *JavascriptInterface*

#默认保留
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}

-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keep class **.R$* {
 *;
}
-keepclassmembers class * {
    void *(**On*Event);
}

-keep class android.support.v4.content.FileProvider{ *;}

#lib
-keep class cn.weli.common.libs.**{*;}

#Presenter
-keepclassmembers  public class * extends cn.weli.svideo.baselib.presenter.IPresenter{ *;}
-keepclassmembers  public class * implements cn.weli.svideo.baselib.presenter.IPresenter{ *;}

#Bean
-keep public class cn.etouch.retrofit.response.**{*;}
-keep public class cn.weli.svideo.advert.bean.**{*;}
-keep public class cn.weli.svideo.advert.kuaima.**{*;}
-keep public class cn.weli.svideo.common.http.bean.**{*;}
-keep public class cn.weli.svideo.module.main.model.bean.**{*;}
-keep public class cn.weli.svideo.module.mine.model.bean.**{*;}
-keep public class cn.weli.svideo.module.video.model.bean.**{*;}
-keep public class cn.weli.svideo.module.task.model.bean.**{*;}
-keep public class cn.weli.svideo.wxapi.model.bean.**{*;}

#webview
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
}

#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder

#ButterKnife avoid error
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# Gson
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.** { *;}
-dontwarn com.google.gson.**

# Rxbus
-dontwarn com.hwangjr.rxbus.**
-keep class com.hwangjr.rxbus.** { *;}
-keepclassmembers class ** {
    @com.hwangjr.rxbus.annotation.Subscribe public *;
    @com.hwangjr.rxbus.annotation.Produce public *;
}

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-dontnote retrofit2.Platformc
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature
-keepattributes Exceptions

-dontwarn javax.annotation.**
-dontwarn javax.inject.**

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

#okhttp
-keep class okhttp3.**{*;}
-dontwarn okhttp3.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.** { *;}
-dontwarn okio.**

# Gaode
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}
-keep class com.autonavi.**{*;}

#UM
-keep class com.umeng.** {*;}
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
#UM分享
-dontoptimize
-dontwarn com.google.android.maps.**
-dontwarn android.webkit.WebView
-dontwarn com.umeng.**
-keep public class javax.**
-keep public class android.webkit.**
-dontwarn android.support.v4.**
-keep enum com.facebook.**
-keepattributes Exceptions,InnerClasses,Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public interface com.tencent.**
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**
-keep public class com.umeng.socialize.* {*;}
-keep class com.umeng.scrshot.**
-keep public class com.tencent.** {*;}
-keep class * extends com.umeng.socialize.net.base.SocializeReseponse {
   *;
}
-keep class com.umeng.socialize.sensor.**
-keep class com.umeng.socialize.handler.**
-keep class com.umeng.socialize.handler.*
-keep class com.umeng.weixin.handler.**
-keep class com.umeng.weixin.handler.*
-keep class com.umeng.qq.handler.**
-keep class com.umeng.qq.handler.*
-keep class UMMoreHandler{*;}
-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}
-keep class com.tencent.mm.sdk.** {
   *;
}
-keep class com.tencent.mm.opensdk.** {
   *;
}
-keep class com.tencent.wxop.** {
   *;
}
-keep class com.tencent.mm.sdk.** {
   *;
}
-keep class com.tencent.** {*;}
-dontwarn com.tencent.**
-keep public class com.umeng.com.umeng.soexample.R$*{
    public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class com.tencent.open.TDialog$*
-keep class com.tencent.open.TDialog$* {*;}
-keep class com.tencent.open.PKDialog
-keep class com.tencent.open.PKDialog {*;}
-keep class com.tencent.open.PKDialog$*
-keep class com.tencent.open.PKDialog$* {*;}
-keep class com.umeng.socialize.impl.ImageImpl {*;}
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
-keepattributes Signature

#个推
-dontwarn com.igexin.**
-keep class com.igexin.**{*;}
-dontwarn com.getui.**
-keep class com.getui.**{*;}
-ignorewarnings

# X5 Webview
-dontwarn com.tencent.**
-dontwarn MTT.ThirdAppInfoNew

-keep class com.tencent.**{*;}
-keep class MTT.ThirdAppInfoNew {
	*;
}

# 获取手机唯一标识
-keep class com.bun.miitmdid.** {*;}
-keep class cn.weli.svideo.common.helper.MiitDeviceHelper {*;}

#头条广告
-keep class com.androidquery.callback.** {*;}
-keep class com.ss.android.** {*;}
-keep public interface com.bytedance.sdk.openadsdk.downloadnew.** {*;}
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
-keep class com.ttshell.sdk.api.**{ public *; }
-keep class com.bytedance.sdk.openadsdk.** {*;}
-keep public interface com.bytedance.sdk.openadsdk.downloadnew.** {*;}
-keep class com.pgl.sys.ces.* {*;}

#linkedme
-keep class com.microquation.linkedme.android.** { *; }

#PictureSelector 2.0
-keep class com.luck.picture.lib.** { *; }
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

#Baidu
-keep class com.baidu.mobads.** { *; }
-keep class com.baidu.mobad.** { *; }