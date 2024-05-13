package com.kahana.cleansky.util

import android.view.View

inline var View?.visible
    get() = (this?.visibility ?: View.GONE) == View.VISIBLE
    set(value) { this?.visibility = if (value) View.VISIBLE else View.GONE }