package com.example.multibhashanews.model

import android.os.Parcelable
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Param
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserInfo(
    val email : String? = null,
    val password: String? = null
):Parcelable