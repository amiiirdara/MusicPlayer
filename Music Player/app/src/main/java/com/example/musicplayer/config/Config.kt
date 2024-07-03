package com.example.musicplayer.config

import com.example.musicplayer.framework.Configurator
import com.example.musicplayer.framework.Debug
import com.example.musicplayer.framework.G
import com.example.musicplayer.framework.LOG_LEVEL
import com.example.musicplayer.framework.lang.FaLang

class Config : Configurator {

    override fun config() {
        Debug.logTag = "AmirDropNote"
        Debug.logLevel = LOG_LEVEL.INFO
        G.language = FaLang()
    }
}