package com.example.musicplayer.framework

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class PermissionHandler(val permissions: List<String>, props: MethodBlock<PermissionHandler> = {}) {
    companion object {
        var lastRequestCode: Int = 0
    }

    private var activity = G.currentActivity
    private var requestCode: Int = ++lastRequestCode

    var onGrant: FunctionBlock? = null
    var permissionRequiredTitle: String = "Permission Required"
    var whyPermissionRequired: String = "Please grant all permissions..."

    init {
        activity.addPermissionHandler(this)
        this.props()
        request()
    }


    private fun canShowReason(): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true
            }
        }

        return false
    }


    private fun alert(message: String, onPositive: (DialogInterface, Int) -> Unit) {
        AlertDialog.Builder(activity)
            .setTitle(permissionRequiredTitle)
            .setMessage(message)
            .setPositiveButton("ok", onPositive)
            .setNegativeButton("cancel", { dialog, i ->
                activity.finish()
            })
            .create()
            .show()
    }

    private fun request() {
        if (isGranted()) {
            return
        }

        if (canShowReason()) {
            alert(whyPermissionRequired) { dialog, i ->
                requestPermission()
            }
        } else {
            requestPermission()
        }
    }

    private fun isGranted(): Boolean {
        for (permission in permissions) {
            if (PERMISSION_GRANTED != ContextCompat.checkSelfPermission(
                    G.currentActivity,
                    permission
                )
            ) {
                return false
            }
        }

        return true
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            G.currentActivity,
            permissions.toTypedArray(),
            requestCode
        )
    }

    fun processOnPermissionResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {
        if (requestCode != this.requestCode) {
            return false
        }

        //it's mine
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            if (onGrant != null) {
                onGrant!!.invoke()
            }

            return true
        }

        if (canShowReason()) {
            request()
            //is denied
        } else {
            //is always denied
            alert(whyPermissionRequired) { dialog, i ->
                activity.openAppSettings()
            }
        }

        return true
    }
}