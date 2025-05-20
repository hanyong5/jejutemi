package com.jejuckl.ui.screen

import android.content.Context
import com.jejuckl.ui.component.WebDialog

fun showBtn03_01Dialog(context: Context) {
    val url = "https://jejutemi.netlify.app/usage"
     val dialog = WebDialog(context, url)
    dialog.show()
} 