package com.ke.wechat

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition

class PosterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wechat_share_activity_poster)

        val image = findViewById<ImageView>(R.id.image)
        val wechat = findViewById<View>(R.id.wechat)
        val friend = findViewById<View>(R.id.friend)
        val imageUrl = intent.getStringExtra(EXTRA_IMAGE_URL)!!
        Glide.with(this)
            .load(imageUrl)
//            .addListener(object : RequestListener<Drawable> {
//                override fun onLoadFailed(
//                    e: GlideException?,
//                    model: Any?,
//                    target: Target<Drawable>?,
//                    isFirstResource: Boolean
//                ): Boolean {
//
//                    return true
//                }
//
//                override fun onResourceReady(
//                    resource: Drawable?,
//                    model: Any?,
//                    target: Target<Drawable>?,
//                    dataSource: DataSource?,
//                    isFirstResource: Boolean
//                ): Boolean {
//
//
//                    return true
//                }
//
//            })
            .into(image)



        image.transitionName = imageUrl

        wechat.setOnClickListener {
            shareImage(WechatShareService.Scene.Session)
        }
        friend.setOnClickListener {
            shareImage(WechatShareService.Scene.Timeline)
        }
    }

    private fun shareImage(scene: WechatShareService.Scene) {
        val imageUrl = intent.getStringExtra(EXTRA_IMAGE_URL)!!


        Glide.with(this).asBitmap().load(imageUrl).into(object : SimpleTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

                WechatShareService.instance().shareImage(Bitmap.createBitmap(resource), scene)
                sendBroadcast(Intent(PosterListActivity.ACTION_FINISH))
                finish()
            }

        })


    }

    companion object {
        const val EXTRA_IMAGE_URL = "EXTRA_IMAGE_URL"
    }
}