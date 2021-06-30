package com.ke.wechat_demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ke.wechat.ShareBean
import com.ke.wechat.ShareDialog
import com.ke.wechat.WechatShareService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val imageUrl =
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2Fd58b0ecfde7121920625fff16b7946ae707b3683cb97-fc3l3X_fw658&refer=http%3A%2F%2Fhbimg.b0.upaiyun.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1625282318&t=8a9d785a715f9a1843099936c1956b02"
        val bean = ShareBean(
            shareUrl = imageUrl,
            shareContent = "",
            shareImg = imageUrl,
            sharePostArr = emptyList(),
            defaultShareContent = "给你发送一条短信",
            shareTitle = "分享"
        )



        WechatShareService.init(application, "")

        share_with_poster.setOnClickListener {
            bean.sharePostArr = listOf(
                "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F013789580ffbaea84a0e282b4456b9.jpg&refer=http%3A%2F%2Fimg.zcool.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1627625540&t=7dd366601b449297cbe96faf40ba1afb",
                "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F01dab35854ff7aa801219c77d19b4d.jpg%401280w_1l_2o_100sh.jpg&refer=http%3A%2F%2Fimg.zcool.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1627625567&t=f4a599fd7584c6d85e34b3e2cbd10760",
                "https://gimg2.baidu.com/image_search/src=http%3A%2F%2F5b0988e595225.cdn.sohucs.com%2Fimages%2F20200308%2F28b863a584634efd8af56a1444461b9b.jpeg&refer=http%3A%2F%2F5b0988e595225.cdn.sohucs.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1627625567&t=1e78325e859231b4bfb519d370c37669"
            )
            ShareDialog(bean, this)
        }

        share_without_poster.setOnClickListener {
            bean.sharePostArr = listOf()
            ShareDialog(bean, this)
        }
    }
}