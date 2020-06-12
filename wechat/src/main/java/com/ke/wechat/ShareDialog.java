package com.ke.wechat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ShareDialog {


    public ShareDialog(@NonNull ShareBean shareBean, @NonNull Activity activity) {


        CompositeDisposable compositeDisposable = new CompositeDisposable();


        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);


        View contentView = View.inflate(activity, R.layout.dialog_share, null);


        View downloadImageView = contentView.findViewById(R.id.ll_share_img_download);

        ImageView posterImage = contentView.findViewById(R.id.img_poster);


        View sharePosterView = contentView.findViewById(R.id.ll_share_poster);
        sharePosterView.setVisibility(TextUtils.isEmpty(shareBean.sharePoster) ? View.GONE : View.VISIBLE);
        sharePosterView.setOnClickListener(
                v -> {
                    //点下海报分享 就隐藏海报分享按钮 显示下载图片按钮
                    sharePosterView.setVisibility(View.GONE);
                    downloadImageView.setVisibility(View.VISIBLE);


                    Disposable disposable = Observable.just(shareBean.sharePoster)
                            .observeOn(Schedulers.io())
                            .map((Function<String, Bitmap>) s -> {

                                return Glide.with(activity).asBitmap().load(s).submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                            }).observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Bitmap>() {
                                @Override
                                public void accept(Bitmap resource) throws Exception {
                                    posterImage.setImageBitmap(resource);
//
                                    contentView.findViewById(R.id.ll_share_wechat).setOnClickListener(v12 -> wechatShareImage(resource, WechatShareService.Scene.Session, bottomSheetDialog));

                                    contentView.findViewById(R.id.ll_share_wechat_friends).setOnClickListener(v1 -> wechatShareImage(resource, WechatShareService.Scene.Timeline, bottomSheetDialog));

                                    contentView.findViewById(R.id.ll_share_img_download).setOnClickListener(v13 -> {
                                        saveBitmapToGallery(resource, activity);
                                        bottomSheetDialog.dismiss();
                                    });
                                    try {
                                        Class clazz = bottomSheetDialog.getClass();
                                        Field field = clazz.getDeclaredField("behavior");
                                        field.setAccessible(true);
                                        BottomSheetBehavior bottomSheetBehavior = (BottomSheetBehavior) field.get(bottomSheetDialog);
                                        //让dialog滚动到顶部
                                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    compositeDisposable.add(disposable);

//                    Glide.with(activity)
//                            .load(shareBean.sharePoster)
//                            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//                            .into()
//                            .into(new SimpleTarget<Bitmap>() {
//                                @Override
//                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                                    posterImage.setImageBitmap(resource);
//
//                                    contentView.findViewById(R.id.ll_share_wechat).setOnClickListener(v12 -> wechatShareImage(resource, WechatShareService.Scene.Session, bottomSheetDialog));
//
//                                    contentView.findViewById(R.id.ll_share_wechat_friends).setOnClickListener(v1 -> wechatShareImage(resource, WechatShareService.Scene.Timeline, bottomSheetDialog));
//
//                                    contentView.findViewById(R.id.ll_share_img_download).setOnClickListener(v13 -> {
//                                        saveBitmapToGallery(resource, activity);
//                                        bottomSheetDialog.dismiss();
//                                    });
//                                    try {
//                                        Class clazz = bottomSheetDialog.getClass();
//                                        Field field = clazz.getDeclaredField("behavior");
//                                        field.setAccessible(true);
//                                        BottomSheetBehavior bottomSheetBehavior = (BottomSheetBehavior) field.get(bottomSheetDialog);
//                                        //让dialog滚动到顶部
//                                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//
//                                }
//                            });

                }
        );


        contentView.findViewById(R.id.ll_share_wechat).setOnClickListener(v -> wechatShareUrl(shareBean, activity, compositeDisposable, WechatShareService.Scene.Session, bottomSheetDialog));

        contentView.findViewById(R.id.ll_share_wechat_friends).setOnClickListener(v -> wechatShareUrl(shareBean, activity, compositeDisposable, WechatShareService.Scene.Timeline, bottomSheetDialog));


//        contentView.findViewById(R.id.ll_share_qr).setOnClickListener(v -> {
//            Disposable disposable = new RxPermissions((FragmentActivity) activity).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    .subscribe(aBoolean -> {
//                        if (aBoolean != null && aBoolean) {
//                            shareQRImage(shareBean, activity, bottomSheetDialog, compositeDisposable);
//                        }
//                    });
//
//            compositeDisposable.add(disposable);
//
//
//        });


//        contentView.findViewById(R.id.ll_share_contacts).setOnClickListener(v -> {
//
//            Intent intent = new Intent(Intent.ACTION_SEND);
//            intent.setType("text/plain");
//            intent.putExtra(Intent.EXTRA_TEXT, shareBean.defaultShareContent);
//            activity.startActivity(Intent.createChooser(intent, "分享到"));
//            bottomSheetDialog.dismiss();
//
//        });

        contentView.findViewById(R.id.tv_cancel).setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.setContentView(contentView);

        //关闭dialog的时候取消所有异步任务
        bottomSheetDialog.setOnDismissListener(dialog -> compositeDisposable.dispose());


        Objects.requireNonNull(bottomSheetDialog.getWindow()).findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        bottomSheetDialog.show();
    }

    private void saveBitmapToGallery(Bitmap bitmap, Activity activity) {
        String galleryPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                + Environment.DIRECTORY_DCIM + File.separator + "Camera" + File.separator;

        File file = null;
        FileOutputStream fileOutputStream = null;

        try {
            file = new File(galleryPath, System.currentTimeMillis() + ".jpg");
//            file.mkdir();


            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (file == null) {
            return;
        }

        MediaStore.Images.Media.insertImage(activity.getContentResolver(), bitmap, file.toString(), null);


    }

    private void wechatShareImage(Bitmap bitmap, WechatShareService.Scene scene, BottomSheetDialog bottomSheetDialog) {
        WechatShareService.instance().shareImage(bitmap, scene);
        bottomSheetDialog.dismiss();
    }

    private void wechatShareUrl(@NonNull ShareBean shareBean, @NonNull Activity activity, @NonNull CompositeDisposable compositeDisposable, @NonNull WechatShareService.Scene scene, @NonNull BottomSheetDialog bottomSheetDialog) {
        Disposable disposable =
                Observable.just(shareBean.shareImg).observeOn(Schedulers.io())
                        .map(s -> Glide.with(activity).asBitmap().load(s).into(120, 120).get())
                        .map(bitmap -> {
                            ByteArrayOutputStream output = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
                            int options = 100;
                            while (output.toByteArray().length > 32 && options != 10) {
                                output.reset(); //清空output
                                bitmap.compress(Bitmap.CompressFormat.JPEG, options, output);//这里压缩options%，把压缩后的数据存放到output中
                                options -= 10;
                            }
                            return output.toByteArray();
                        }).subscribe(bytes -> {
                    WechatShareService.instance().shareWeb(scene, shareBean.shareTitle, shareBean.shareContent, shareBean.shareUrl, bytes);
                    bottomSheetDialog.dismiss();

                }, throwable -> {
                    bottomSheetDialog.dismiss();

                });
        compositeDisposable.add(disposable);
    }


    @SuppressWarnings("all")
    private void shareQRImage(@NonNull ShareBean shareBean, @NonNull Activity activity, BottomSheetDialog bottomSheetDialog, CompositeDisposable compositeDisposable) {
        Disposable disposable = Observable.just(shareBean.shareUrl)
                .observeOn(Schedulers.io())
                .map(s -> QRCodeEncoder.syncEncodeQRCode(s, 300))
                .observeOn(Schedulers.io())
                .map(bitmap -> {

                    String imagePath = Environment.getExternalStorageDirectory() + File.separator;
                    File imageFile = new File(imagePath, "share.jpg");

                    if (imageFile.exists()) {
                        imageFile.delete();
                    }
//                    Optional

                    FileOutputStream fileOutputStream = new FileOutputStream(imageFile);

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    bitmap.recycle();

                    //兼容7.0
                    return FileProvider.getUriForFile(activity, "com.ke.wechat.keProvider", imageFile);


//                    return Uri.fromFile(imageFile);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(uri -> {

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    intent.setType("image/*");

                    activity.startActivity(Intent.createChooser(intent, "分享到："));
                    bottomSheetDialog.dismiss();
                }, throwable ->
                        bottomSheetDialog.dismiss());

        compositeDisposable.add(disposable);
    }


    public static final class ShareBean {
        String shareTitle;
        String shareUrl;
        String shareImg;
        String defaultShareContent;
        String shareContent;
        String sharePoster;

        @Override
        public String toString() {
            return "ShareBean{" +
                    "shareTitle='" + shareTitle + '\'' +
                    ", shareUrl='" + shareUrl + '\'' +
                    ", shareImg='" + shareImg + '\'' +
                    ", defaultShareContent='" + defaultShareContent + '\'' +
                    ", shareContent='" + shareContent + '\'' +
                    ", sharePoster='" + sharePoster + '\'' +
                    '}';
        }
    }
}
