package com.jejuckl.ui.screen

import android.content.Context
import com.jejuckl.ui.component.WebDialog

fun showBtn02_02Dialog(context: Context) {
    val url = "https://winloc.netlify.app/"
    val dialog = WebDialog(context, url)
    dialog.show()
} 