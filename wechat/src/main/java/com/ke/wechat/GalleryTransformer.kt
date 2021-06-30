package com.ke.wechat

import android.view.View
import androidx.viewpager.widget.ViewPager

class GalleryTransformer : ViewPager.PageTransformer {
    //自由控制缩放比例
    private val MAX_SCALE = 1f
    private val MIN_SCALE = 0.95f //0.85f


    override fun transformPage(page: View, position: Float) {
        if (position <= 1) {
            val scaleFactor = MIN_SCALE + (1 - Math.abs(position)) * (MAX_SCALE - MIN_SCALE)
            page.scaleX = scaleFactor
            if (position > 0) {
                page.translationX = -scaleFactor * 2
            } else if (position < 0) {
                page.translationX = scaleFactor * 2
            }
            page.scaleY = scaleFactor
        } else {
            page.scaleX = MIN_SCALE
            page.scaleY = MIN_SCALE
        }
    }
}