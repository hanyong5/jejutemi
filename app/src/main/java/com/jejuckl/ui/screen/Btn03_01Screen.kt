package com.jejuckl.ui.screen

import android.content.Context
import com.jejuckl.ui.component.WebDialog

fun showBtn03_01Dialog(context: Context) {
    val url = "https://example.com/edu03_01"
     val dialog = WebDialog(context, url)
    dialog.show()
} 