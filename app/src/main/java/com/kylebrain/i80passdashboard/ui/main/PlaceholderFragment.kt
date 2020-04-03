package com.kylebrain.i80passdashboard.ui.main

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.wowza.gocoder.sdk.api.configuration.WOWZMediaConfig
import com.wowza.gocoder.sdk.api.logging.WOWZLog
import com.wowza.gocoder.sdk.api.player.WOWZPlayerConfig
import com.wowza.gocoder.sdk.api.player.WOWZPlayerView
import com.wowza.gocoder.sdk.api.status.WOWZBroadcastStatus.BroadcastState
import com.wowza.gocoder.sdk.api.status.WOWZPlayerStatus
import com.wowza.gocoder.sdk.api.status.WOWZPlayerStatusCallback


class StatusCallback(handler: Handler, spinner: ProgressBar) : WOWZPlayerStatusCallback {
    private val handler = handler
    private val spinner = spinner

    override fun onWZStatus(wzStatus: WOWZPlayerStatus) {
        if(wzStatus.state == WOWZPlayerStatus.PlayerState.PLAYING)
        {
            Log.i("WOWZ", "DISABLING SPINNER!")
            handler.post(
                Runnable {
                    spinner.visibility = View.GONE
                }
            )
        }
        Log.i("WOWZ", wzStatus.toString())
    }

    override fun onWZError(wzStatus: WOWZPlayerStatus) {
        Log.e("WOWZ", wzStatus.toString())
    }
}

/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragment : Fragment() {

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
        val root = inflater.inflate(R.layout.fragment_main, container, false)
        val textView: TextView = root.findViewById(R.id.section_label)
        pageViewModel.text.observe(this, Observer<String> {
            textView.text = it
        })

        var STREAM_NAME = ""
        if ((arguments?.getInt(ARG_SECTION_NUMBER) ?: 1) == 1)
        {
            STREAM_NAME = "80_donner_summit.stream"
        } else
        {
            //STREAM_NAME = "80_kingvale_eb.stream"
            STREAM_NAME = "ERROR"
        }

        spinner = root.findViewById(R.id.spinner)
        mStreamPlayerView = root.findViewById(R.id.vwStreamPlayer)
        mStreamPlayerView.scaleMode = WOWZMediaConfig.FILL_VIEW
        mStreamPlayerConfig = WOWZPlayerConfig()
        mStreamPlayerConfig.isPlayback = true
        mStreamPlayerConfig.hostAddress = "wzmedia.dot.ca.gov"
        mStreamPlayerConfig.applicationName = "D3"
        mStreamPlayerConfig.streamName = STREAM_NAME
        mStreamPlayerConfig.portNumber = 1935

        var mStreamCallBack = StatusCallback(Handler(Looper.getMainLooper()), spinner)

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
        var mStreamCallBack = StatusCallback(Handler(Looper.getMainLooper()), spinner)
        spinner.visibility = View.VISIBLE
        mStreamPlayerView.play(mStreamPlayerConfig, mStreamCallBack)
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
        fun newInstance(sectionNumber: Int): PlaceholderFragment {
            return PlaceholderFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}