package com.x.a_technologies.kelajak_book.datas

import android.net.Uri

interface ImageSelectedCallBack{
    fun imageSelectedListener(uri: Uri)
}

object ImageTracker {
    var imageSelectedCallBack:ImageSelectedCallBack? = null
}