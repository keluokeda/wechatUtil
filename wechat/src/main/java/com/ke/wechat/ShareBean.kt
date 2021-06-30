package com.ke.wechat

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ShareBean(
    val shareTitle: String,
    val shareUrl: String,
    val shareImg: String,
    val defaultShareContent: String,
    val shareContent: String,
    var sharePostArr: List<String>
) : Parcelable