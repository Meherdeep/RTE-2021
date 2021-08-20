package io.agora.facechat.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import io.agora.agorauikit_android.AgoraConnectionData
import io.agora.agorauikit_android.AgoraVideoViewer
import io.agora.agorauikit_android.requestPermissions
import io.agora.facechat.R
import io.agora.facechat.config.APP_ID
import io.agora.facechat.config.token
import io.agora.rtc.Constants

class VideoActivity : AppCompatActivity() {
    var agView: AgoraVideoViewer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_activity)
        try {
            agView = AgoraVideoViewer(
                this, AgoraConnectionData(APP_ID, appToken = token),
            )
        } catch (e: Exception) {
            print("Could not initialise AgoraVideoViewer. Check your App ID is valid.")
            print(e.message)
            return
        }
        agView!!.style = AgoraVideoViewer.Style.FLOATING

        val set = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)

        this.addContentView(agView, set)

        // Check that the camera and mic permissions are accepted before attempting to join
        if (AgoraVideoViewer.requestPermissions(this)) {
            agView!!.join("test", role = Constants.CLIENT_ROLE_BROADCASTER)
        } else {
            val joinButton = Button(this)
            joinButton.text = "Allow Camera and Microphone, then click here"
            joinButton.setOnClickListener(View.OnClickListener {
                // When the button is clicked, check permissions again and join channel
                // if permissions are granted.
                if (AgoraVideoViewer.requestPermissions(this)) {
                    (joinButton.parent as ViewGroup).removeView(joinButton)
                    agView!!.join("test", role=Constants.CLIENT_ROLE_BROADCASTER)
                }
            })
            joinButton.setBackgroundColor(Color.GREEN)
            joinButton.setTextColor(Color.RED)

            this.addContentView(joinButton, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 300))
        }
    }
}