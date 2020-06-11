package com.ke.wechat;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WechatShareService {

    public static void init(@NonNull Application application, @NonNull String id) {
        if (sWechatShareService == null) {
            sWechatShareService = new WechatShareService(application, id);
        }
    }


    private static WechatShareService sWechatShareService;

    public static WechatShareService instance() {
        return sWechatShareService;
    }

    private final IWXAPI mIWXAPI;

    private WechatShareService(Application application, String id) {
        mIWXAPI = WXAPIFactory.createWXAPI(application, id);
    }

    public void shareImage(@NonNull Bitmap bitmap, Scene scene) {
        WXImageObject wxImageObject = new WXImageObject(bitmap);
        WXMediaMessage wxMediaMessage = new WXMediaMessage();
        wxMediaMessage.mediaObject = wxImageObject;

        Bitmap thumbBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 10, bitmap.getHeight() / 10, true);
        bitmap.recycle();

        wxMediaMessage.setThumbImage(thumbBitmap);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "img";
        req.message = wxMediaMessage;
        req.scene = scene == Scene.Session ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        mIWXAPI.sendReq(req);
    }


    public void shareWeb(Scene scene, String title, String content, String shareUrl, byte[] imageData) {
        WXWebpageObject wxWebpageObject = new WXWebpageObject();

        wxWebpageObject.webpageUrl = shareUrl;

        WXMediaMessage mediaMessage = new WXMediaMessage(wxWebpageObject);

        mediaMessage.title = title;
        mediaMessage.description = content;
        mediaMessage.thumbData = imageData;

        SendMessageToWX.Req request = new SendMessageToWX.Req();

        request.transaction = "webpage";
        request.message = mediaMessage;
        request.scene = scene == Scene.Session ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;

        mIWXAPI.sendReq(request);

    }

    public enum Scene {
        Session,
        Timeline


    }
}
