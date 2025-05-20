package com.jejuckl.ui.screen

import android.content.Context
import com.jejuckl.ui.component.WebDialog

fun showBtn02_03Dialog(context: Context) {
    val url = "https://jejutemi.netlify.app/room"
    val dialog = WebDialog(context, url)
    dialog.show()
} 