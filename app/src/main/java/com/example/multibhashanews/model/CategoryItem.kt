package com.example.multibhashanews.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryItem(
    val image: Int? = null,
    val title: String? = null
):Parcelable