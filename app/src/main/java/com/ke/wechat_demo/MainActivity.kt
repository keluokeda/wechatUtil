package com.ke.wechat_demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ke.wechat.ShareDialog
import com.ke.wechat.WechatShareService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bean = ShareDialog.ShareBean()
        
        val imageUrl = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2Fd58b0ecfde7121920625fff16b7946ae707b3683cb97-fc3l3X_fw658&refer=http%3A%2F%2Fhbimg.b0.upaiyun.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1625282318&t=8a9d785a715f9a1843099936c1956b02"
        bean.defaultShareContent = "给你发送一条短信"
        bean.shareContent = ""
        bean.shareImg =
            imageUrl
        bean.shareUrl =
            imageUrl
        bean.sharePoster =
            imageUrl
        bean.sharePostArr = listOf(
            imageUrl,
            imageUrl
        )
        WechatShareService.init(application, "")

        button.setOnClickListener {
            ShareDialog(bean, this)
        }

    }
}