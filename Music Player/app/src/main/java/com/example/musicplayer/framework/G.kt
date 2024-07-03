package com.example.musicplayer.framework

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Environment
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.FileProvider
import com.example.musicplayer.framework.lang.EnLang
import com.example.musicplayer.framework.lang.Lang
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.regex.Pattern

open class G : Application() {
    companion object {
        lateinit var currentActivity: XActivity
        lateinit var applicationContext: Context
        lateinit var language: Lang
        var config: Configurator? = null
            set(value) {
                field = value
                value?.config()
            }

        //this is default values you can change it in the Config class which is NOT in the Framework
        fun config() {
            Debug.logTag = "Amir"
            Debug.logLevel = LOG_LEVEL.INFO
            language = EnLang()
        }
        //added ones
    }


    override fun onCreate() {
        super.onCreate()
        config()
    }


    fun copyToClipBoard(
        text: String,
        context: Context = currentActivity,
        label: String = "Copied Text"
    ) {
        val clipboardManager: ClipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        // Create a ClipData object
        val clipData: ClipData = ClipData.newPlainText(label, text)
        // Set the ClipData object to the ClipboardManager
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(context, L.copyToClipBoardMessage, Toast.LENGTH_SHORT).show()
    }

    @Throws(IOException::class)
    fun takeScreenshotAndShare(view: View, fileName: String = "screenshot.png") {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        val file = File(view.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()
        val copiedFile =
            File(view.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)
        val uri = FileProvider.getUriForFile(
            view.context,
            "com.example.dropnote.fileprovider",
            copiedFile
        )
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("image/png")
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        view.context.startActivity(shareIntent)
    }

    fun phoneNumberFinder(text: String, numList: ArrayList<String>) {
        val regex = "((\\+98)|0)?9\\d{9}"
        val pattern = Pattern.compile(regex)

        // Create a Matcher object to find matches in the text
        val matcher = pattern.matcher(text)
        // Find all matches and print them to the console
        while (matcher.find()) {
            numList.add(matcher.group())
        }
    }

    fun handleDarkMode() {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Debug.logInfo("night mode no")
        } else {
            Debug.logInfo("night mode yes")
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }


    fun persianNumToEnglish(persianStr: String): String {
        var result = ""
        var en = '0'
        for (ch in persianStr) {
            en = ch
            when (ch) {
                '۰' -> en = '0'
                '۱' -> en = '1'
                '۲' -> en = '2'
                '۳' -> en = '3'
                '۴' -> en = '4'
                '۵' -> en = '5'
                '۶' -> en = '6'
                '۷' -> en = '7'
                '۸' -> en = '8'
                '۹' -> en = '9'
            }
            result = "${result}$en"
        }
        return result
    }

    fun lineThroughTextView(textView: TextView) {
        textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }

}

val L = G.language
