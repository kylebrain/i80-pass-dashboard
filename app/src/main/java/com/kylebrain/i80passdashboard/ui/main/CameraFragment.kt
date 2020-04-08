package com.kylebrain.i80passdashboard.ui.main

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.kylebrain.i80passdashboard.R
import com.wowza.gocoder.sdk.api.WowzaGoCoder
import com.wowza.gocoder.sdk.api.configuration.WOWZMediaConfig
import com.wowza.gocoder.sdk.api.logging.WOWZLog
import com.wowza.gocoder.sdk.api.player.WOWZPlayerConfig
import com.wowza.gocoder.sdk.api.player.WOWZPlayerView
import com.wowza.gocoder.sdk.api.status.WOWZBroadcastStatus.BroadcastState
import com.wowza.gocoder.sdk.api.status.WOWZPlayerStatus
import com.wowza.gocoder.sdk.api.status.WOWZPlayerStatusCallback

private const val SDK_SAMPLE_APP_LICENSE_KEY = "GOSK-A547-010C-3669-DDA0-23E0"
private const val STREAM_NAME = "80_donner_summit.stream"

/**
 * Displays the WOWZ highway stream
 */
class CameraFragment : Fragment() {

    private lateinit var pageViewModel: PageViewModel

    private lateinit var mStreamPlayerView : WOWZPlayerView
    private lateinit var mStreamPlayerConfig : WOWZPlayerConfig
    private lateinit var spinner : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.camera_layout, container, false)
        val textView: TextView = root.findViewById(R.id.section_label)

        val sGoCoderSDK = WowzaGoCoder.init(context, SDK_SAMPLE_APP_LICENSE_KEY)

        if (sGoCoderSDK == null)
        {
            textView.text = "Failed to intialize Wowza Stream!"
            return root;
        }

        textView.text = "Donner Summit"

        spinner = root.findViewById(R.id.spinner)
        mStreamPlayerView = root.findViewById(R.id.vwStreamPlayer)
        mStreamPlayerView.scaleMode = WOWZMediaConfig.FILL_VIEW
        mStreamPlayerConfig = WOWZPlayerConfig().apply {
            isPlayback = true
            hostAddress = "wzmedia.dot.ca.gov"
            applicationName = "D3"
            streamName = STREAM_NAME
            portNumber = 1935
        }


        var mStreamCallBack = StatusCallback(PlayerStatusHandler(context, spinner))

        spinner.visibility = View.VISIBLE
        mStreamPlayerView.play(mStreamPlayerConfig, mStreamCallBack)

        return root
    }

    fun disableLoading()
    {
        spinner.visibility = View.GONE
    }

    override fun onPause() {
        mStreamPlayerView.stop()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        spinner.visibility = View.VISIBLE

        if(!mStreamPlayerView.isReadyToPlay)
        {
            mStreamPlayerView.stop()
            while (!mStreamPlayerView.isReadyToPlay) {}
        }

        mStreamPlayerView.play(mStreamPlayerConfig, StatusCallback(PlayerStatusHandler(context, spinner)))
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): CameraFragment {
            return CameraFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}