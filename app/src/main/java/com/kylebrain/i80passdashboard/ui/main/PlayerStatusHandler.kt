package com.kylebrain.i80passdashboard.ui.main

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import com.wowza.gocoder.sdk.api.status.WOWZPlayerStatus
import com.wowza.gocoder.sdk.api.status.WOWZPlayerStatusCallback

enum class StatusHandlerCode {
    VIDEO_LOADED,
    VIDEO_ERROR
}

class StatusCallback(handler: Handler) : WOWZPlayerStatusCallback {
    private val handler = handler

    override fun onWZStatus(wzStatus: WOWZPlayerStatus) {
        if(wzStatus.state == WOWZPlayerStatus.PlayerState.PLAYING)
        {
            Log.i("WOWZ_DEV", "DISABLING SPINNER!")
            handler.sendMessage(
                Message().apply {
                    what = StatusHandlerCode.VIDEO_LOADED.ordinal
                }
            )
        }

        val lastError = wzStatus.lastError
        if(lastError != null)
        {
            Log.e("WOWZ_DEV", wzStatus.toString())

            handler.sendMessage(
                Message().apply {
                    obj = lastError.errorDescription
                    arg1 = lastError.errorCode
                    what = StatusHandlerCode.VIDEO_ERROR.ordinal
                }
            )
        } else
        {
            Log.i("WOWZ_DEV", wzStatus.toString())
        }
    }

    override fun onWZError(wzStatus: WOWZPlayerStatus) {
        Log.e("WOWZ_DEV", wzStatus.toString() + "END ERROR")
    }
}

class PlayerStatusHandler(context : Context?, spinner : ProgressBar) : Handler()
{
    private val context = context
    private  val spinner = spinner

    override fun handleMessage(msg: Message) {
        when(msg.what)
        {
            // Video has been loaded and spinner can be removed
            StatusHandlerCode.VIDEO_LOADED.ordinal ->
            {
                spinner.visibility = View.GONE
            }

            // Video has errored
            // arg1 stores the error code, obj stores the error message
            StatusHandlerCode.VIDEO_ERROR.ordinal ->
            {

                val builder = AlertDialog.Builder(context).apply {
                    setCancelable(true)
                    setTitle("WOWZ Error Code " + msg.arg1)
                    setMessage(msg.obj.toString())
                }

                Log.d("WOWZ", "Displayed error dialog!")
                builder.show()
            }
        }
    }
}