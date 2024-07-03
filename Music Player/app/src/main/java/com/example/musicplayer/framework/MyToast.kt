package com.example.musicplayer.framework

import android.widget.Toast

class MToast(message: String, isShort: Boolean = true, usingApplicationContext: Boolean = false) {
    init {
        if (usingApplicationContext) {
            if (isShort) {
                Toast.makeText(G.applicationContext, message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(G.applicationContext, message, Toast.LENGTH_LONG).show()
            }

        } else {
            if (isShort) {
                Toast.makeText(G.currentActivity, message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(G.currentActivity, message, Toast.LENGTH_LONG).show()
            }
        }


    }
}