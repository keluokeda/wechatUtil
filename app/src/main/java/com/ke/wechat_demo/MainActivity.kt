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
        bean.defaultShareContent = ""
        bean.shareContent = ""
        bean.shareImg =
            "http://admi.gdcws.cn////upload//qrcode//api//acbiz//sales//share//max//20//09//446THIRD.jpg"
        bean.shareUrl =
            "http://admi.gdcws.cn////upload//qrcode//api//acbiz//sales//share//max//20//09//446THIRD.jpg"
        bean.sharePoster =
            "http://admi.gdcws.cn////upload//qrcode//api//acbiz//sales//share//max//20//09//446THIRD.jpg"
        bean.sharePostArr = listOf(
            "http://admi.gdcws.cn////upload//qrcode//api//acbiz//sales//share//max//20//09//446THIRD.jpg",
            "http://admi.gdcws.cn////upload//qrcode//api//acbiz//sales//share//max//20//09//446THIRD.jpg"
        )
        WechatShareService.init(application, "")

        button.setOnClickListener {
            ShareDialog(bean, this)
        }

    }
}