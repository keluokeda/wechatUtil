package com.ke.wechat;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ShareDialog {


    public ShareDialog(@NonNull ShareBean shareBean, @NonNull FragmentActivity activity) {


        CompositeDisposable compositeDisposable = new CompositeDisposable();


        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);


        View contentView = View.inflate(activity, R.layout.wechat_share_dialog_share, null);


        View copyView = contentView.findViewById(R.id.ll_share_copy);
        copyView.setOnClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData mClipData = ClipData.newPlainText("Label", shareBean.getShareUrl());
            clipboardManager.setPrimaryClip(mClipData);
            Toast.makeText(activity.getApplicationContext(), "复制成功", Toast.LENGTH_LONG).show();
            bottomSheetDialog.dismiss();
        });

//        ImageView posterImage = contentView.findViewById(R.id.image_view);


        View sharePosterView = contentView.findViewById(R.id.ll_share_poster);

        sharePosterView.setVisibility(shareBean.getSharePostArr().isEmpty() ? View.GONE : View.VISIBLE);
        sharePosterView.setOnClickListener(v -> toPosterListView(shareBean, activity, bottomSheetDialog));


        contentView.findViewById(R.id.ll_share_wechat).setOnClickListener(v -> wechatShareUrl(shareBean, activity, compositeDisposable, WechatShareService.Scene.Session, bottomSheetDialog));

        contentView.findViewById(R.id.ll_share_wechat_friends).setOnClickListener(v -> wechatShareUrl(shareBean, activity, compositeDisposable, WechatShareService.Scene.Timeline, bottomSheetDialog));

        contentView.findViewById(R.id.tv_cancel).setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.setContentView(contentView);

        //关闭dialog的时候取消所有异步任务
        bottomSheetDialog.setOnDismissListener(dialog -> compositeDisposable.dispose());


        Objects.requireNonNull(bottomSheetDialog.getWindow()).findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        bottomSheetDialog.show();

//        scrollToBottom(bottomSheetDialog);
    }

    private void toPosterListView(ShareBean shareBean, Activity activity, BottomSheetDialog bottomSheetDialog) {
        Intent intent = new Intent(activity, PosterListActivity.class);
        intent.putExtra(PosterListActivity.EXTRA_SHARE_BEAN, shareBean);
        activity.startActivity(intent);
        bottomSheetDialog.dismiss();
    }

//    private void saveBitmapToGallery(Bitmap bitmap, Activity activity) {
//        String galleryPath = activity.getExternalFilesDir(null) + File.separator;
//
//        File file = null;
//        FileOutputStream fileOutputStream = null;
//
//        try {
//            file = new File(galleryPath, System.currentTimeMillis() + ".jpg");
////            file.mkdir();
//
//
//            fileOutputStream = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (fileOutputStream != null) {
//                try {
//                    fileOutputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        if (file == null) {
//            return;
//        }
//
//        MediaStore.Images.Media.insertImage(activity.getContentResolver(), bitmap, file.toString(), null);
//
//
//        Toast.makeText(activity.getApplication(), "保存图片成功", Toast.LENGTH_SHORT).show();
//    }

    private void wechatShareImage(Bitmap bitmap, WechatShareService.Scene scene, BottomSheetDialog bottomSheetDialog) {
        WechatShareService.instance().shareImage(bitmap, scene);
        bottomSheetDialog.dismiss();
    }

    private void wechatShareUrl(@NonNull ShareBean shareBean, @NonNull Activity activity, @NonNull CompositeDisposable compositeDisposable, @NonNull WechatShareService.Scene scene, @NonNull BottomSheetDialog bottomSheetDialog) {
        Disposable disposable =
                Observable.just(shareBean.getShareImg()).observeOn(Schedulers.io())
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
                    WechatShareService.instance().shareWeb(scene, shareBean.getShareTitle(), shareBean.getShareContent(), shareBean.getShareUrl(), bytes);
                    bottomSheetDialog.dismiss();

                }, throwable -> {
                    bottomSheetDialog.dismiss();

                });
        compositeDisposable.add(disposable);
    }


//    @SuppressWarnings("all")
//    private void shareQRImage(@NonNull ShareBean shareBean, @NonNull Activity activity, BottomSheetDialog bottomSheetDialog, CompositeDisposable compositeDisposable) {
//        Disposable disposable = Observable.just(shareBean.shareUrl)
//                .observeOn(Schedulers.io())
//                .map(s -> QRCodeEncoder.syncEncodeQRCode(s, 300))
//                .observeOn(Schedulers.io())
//                .map(bitmap -> {
//
//
//                    String imagePath = activity.getExternalFilesDir(null) + File.separator;
//                    File imageFile = new File(imagePath, "share.jpg");
//
//                    if (imageFile.exists()) {
//                        imageFile.delete();
//                    }
////                    Optional
//
//                    FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
//
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
//                    fileOutputStream.flush();
//                    fileOutputStream.close();
//                    bitmap.recycle();
//
//                    //兼容7.0
//                    return FileProvider.getUriForFile(activity, activity.getPackageName() + ".shareDialogProvider", imageFile);
//
//
////                    return Uri.fromFile(imageFile);
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(uri -> {
//
//                    Intent intent = new Intent(Intent.ACTION_SEND);
//                    intent.putExtra(Intent.EXTRA_STREAM, uri);
//                    intent.setType("image/*");
//
//                    activity.startActivity(Intent.createChooser(intent, "分享到："));
//                    bottomSheetDialog.dismiss();
//                }, throwable ->
//                        bottomSheetDialog.dismiss());
//
//        compositeDisposable.add(disposable);
//    }



}
