package com.example.musicplayer.framework


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.config.Config

abstract class XActivity: AppCompatActivity() {
    private val permissionHandlers = mutableListOf<PermissionHandler>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeConfigurator()
        G.currentActivity = this
        G.applicationContext = this.applicationContext
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        initializeConfigurator()
        G.currentActivity = this
    }

    override fun onResume() {
        super.onResume()
        G.currentActivity = this
    }

    override fun onStart() {
        super.onStart()
        G.currentActivity = this
        Debug.logWarning("Activity ${this::class.java.simpleName} Started")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (permissionHandler in permissionHandlers) {
            if (permissionHandler.processOnPermissionResult(requestCode, permissions, grantResults)) {
                return
            }
        }
    }

    fun addPermissionHandler(permissionHandler: PermissionHandler) {
        permissionHandlers.add(permissionHandler)
    }

    fun openAppSettings() {
        Toast.makeText(this , "Please Allow permissions in app settings" , Toast.LENGTH_LONG).show()
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
        finish()
    }

    private fun initializeConfigurator () {
        if (G.config ==null) {
            G.config = Config()
        }
    }

}