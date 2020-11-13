package com.kontranik.koreader.utils

import android.Manifest
import android.content.DialogInterface
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.florent37.runtimepermission.PermissionResult
import com.github.florent37.runtimepermission.RuntimePermission
import com.google.android.material.snackbar.Snackbar
import com.kontranik.koreader.R


class PermissionsHelper(private val activity: AppCompatActivity) {

    private var listener: PermissionsHelperListener? = null

    init {
        listener = activity as PermissionsHelperListener
    }

    interface PermissionsHelperListener {
        fun onAccessGrantedReadExternalStorage() {}
        fun onAccessGrantedWriteSettings() {}
    }

    fun checkPermissionsExternalStorage(mainView: View) {
        RuntimePermission.askPermission(activity)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .onAccepted { result: PermissionResult? ->
                    //all permissions already granted or just granted
                    listener!!.onAccessGrantedReadExternalStorage()
                }
                .onDenied { result: PermissionResult ->
                    Snackbar.make(mainView, activity.getString(R.string.permissions_externalstorage_needed), Snackbar.LENGTH_SHORT).show()
                    //permission denied, but you can ask again, eg:
                    AlertDialog.Builder(activity.applicationContext)
                            .setMessage(activity.getString(R.string.give_permission_storage))
                            .setPositiveButton(activity.getString(R.string.okay_string)) { dialog: DialogInterface?, which: Int -> result.askAgain() } // ask again
                            .setNegativeButton(activity.getString(R.string.no_string)) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
                            .show()
                }
                .onForeverDenied { result: PermissionResult ->
                    Snackbar.make(mainView, activity.getString(R.string.permissions_externalstorage_needed), Snackbar.LENGTH_SHORT)
                            .setAction(activity.getString(R.string.go_to_settings)) { view: View? -> result.goToSettings() }.show()
                }
                .ask()
    }

}