package com.ke.wechat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import java.util.*

class PosterListActivity : AppCompatActivity() {

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_FINISH) {
                finish()
            }
        }

    }

    private var currentView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wechat_share_activity_poster_list)

        val shareBean = intent.getParcelableExtra<ShareBean>(EXTRA_SHARE_BEAN)
            ?: throw IllegalArgumentException("需要传入 share bean")

        val viewPager: ViewPager = findViewById(R.id.view_pager)
        initViewPager(viewPager, shareBean)


        val shareView = findViewById<View>(R.id.share)
        shareView.setOnClickListener {
            val url = shareBean.sharePostArr[viewPager.currentItem]
            val intent = Intent(this, PosterActivity::class.java)
            intent.putExtra(PosterActivity.EXTRA_IMAGE_URL, url)
            startActivity(
                intent,
                ActivityOptionsCompat.makeSceneTransitionAnimation(this, currentView!!, url)
                    .toBundle()
            )
        }
        registerReceiver(receiver, IntentFilter(ACTION_FINISH))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    private fun initViewPager(
        viewPager: ViewPager,
        shareBean: ShareBean
    ) {
        viewPager.pageMargin = 20
        viewPager.offscreenPageLimit = shareBean.sharePostArr.size
        viewPager.setPageTransformer(true, GalleryTransformer())
        viewPager.adapter = object : PagerAdapter() {
            override fun getCount(): Int {
                return shareBean.sharePostArr.size
            }

            override fun isViewFromObject(view: View, `object`: Any): Boolean {
                return view == `object`
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val imageView = ImageView(this@PosterListActivity)
                val url = shareBean.sharePostArr[position]
                Glide.with(this@PosterListActivity).load(url)
                    .into(imageView)
                container.addView(imageView)
                imageView.transitionName = url
                return imageView
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                container.removeView(`object` as View)
            }

            override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
                super.setPrimaryItem(container, position, `object`)
                currentView = `object` as? View
            }
        }

        viewPager.currentItem = 1
    }


    companion object {
        const val EXTRA_SHARE_BEAN = "EXTRA_SHARE_BEAN"
        const val ACTION_FINISH = "ACTION_FINISH"
    }

    fun dismiss(view: View) {
        onBackPressed()
    }


}