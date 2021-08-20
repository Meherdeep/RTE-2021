package io.agora.facechat.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.sharp.Phone
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import io.agora.agorauikit_android.*
import io.agora.facechat.R
import io.agora.facechat.config.APP_ID
import io.agora.facechat.ui.theme.FacechatTheme
import io.agora.rtm.*
import java.lang.RuntimeException
import io.agora.rtm.RtmMessage
import java.util.concurrent.locks.ReentrantLock


// RTM client instance
var mRtmClient : RtmClient? = null
// RTM channel instance
var mRtmChannel : RtmChannel? = null

var channelName : String? = ""

class LobbyActivity() : ComponentActivity() {
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val username = intent.getStringExtra("USERNAME")
        setContent() {
            FacechatTheme() {
                Surface(color = MaterialTheme.colors.background) {
                    val openDialog = remember { mutableStateOf(false)  }
                    val userList = mutableStateListOf<String>()
                    initializeRtmAndReceiveMessages(this, username = username.toString(), userList = userList, openDialog = openDialog)
                    LobbyScreen(userList = userList, openDialog = openDialog, username = username.toString())
                }
            }
        }
    }
}


@ExperimentalMaterialApi
@Composable
fun LobbyScreen(userList: SnapshotStateList<String>, openDialog: MutableState<Boolean>, username: String) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painterResource(R.drawable.bg_image),
                contentDescription = "Background Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        }
        Column() {
            LobbyHeaders(userList = userList, openDialog = openDialog, username = username)
        }
    }
}


@ExperimentalMaterialApi
@Composable
fun LobbyHeaders(userList: SnapshotStateList<String>, openDialog: MutableState<Boolean>, username: String){
    Box(modifier = Modifier.fillMaxSize()){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Facechat!",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF039dfd),
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "List of all the active users",
                style = MaterialTheme.typography.body1,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(20.dp))
            UserList( userList = userList , openDialog = openDialog, username = username)
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun UserCard(username: String, openDialog: MutableState<Boolean>){
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),

        onClick = {
            println("USER CARD CLICKED")
            userCardClicked(context = context, username = username, openDialog = openDialog)
        },

        backgroundColor = Color.Transparent,
        border = BorderStroke(0.dp, Color.Transparent,
        ),
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)) {
            Icon(
                Icons.Rounded.AccountCircle,
                "User",
                tint = Color.White,
                modifier = Modifier
                    .size(40.dp)
                    .align(alignment = Alignment.CenterVertically),
            )
            Spacer(modifier = Modifier.fillMaxWidth(fraction = 0.1f))
            Text(
                text = username,
                style = MaterialTheme.typography.h6,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(alignment = Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.fillMaxWidth(fraction = 0.8f))
            Icon(
                Icons.Sharp.Phone,
                "Call",
                tint = Color.Green,
                modifier = Modifier
                    .size(40.dp)
                    .align(alignment = Alignment.CenterVertically)
            )
        }
    }
}

@Composable
fun initializeRtmAndReceiveMessages(context: Context, username: String, userList: SnapshotStateList<String>, openDialog: MutableState<Boolean>){

    val lock = ReentrantLock()

    fun initializeRtm(){
        try {
            mRtmClient = RtmClient.createInstance(context, APP_ID,
                object : RtmClientListener {
                    override fun onConnectionStateChanged(p0: Int, p1: Int) {
                        TODO("Not yet implemented")
                    }

                    override fun onMessageReceived(rtmMessage: RtmMessage, peerId: String?) {
                        print("Peer message received from ${peerId.toString()}, "+ rtmMessage.text.toString())

                        if(rtmMessage.text.startsWith("%")){
                            // Channel Message
                            openDialog.value = true

                        }else{
                            // UserList message
                            if(!userList.contains(peerId.toString())){
                                userList.add(peerId.toString())
                            }
                        }
                    }

                    override fun onImageMessageReceivedFromPeer(p0: RtmImageMessage?, p1: String?) {
                        TODO("Not yet implemented")
                    }

                    override fun onFileMessageReceivedFromPeer(p0: RtmFileMessage?, p1: String?) {
                        TODO("Not yet implemented")
                    }

                    override fun onMediaUploadingProgress(p0: RtmMediaOperationProgress?, p1: Long) {
                        TODO("Not yet implemented")
                    }

                    override fun onMediaDownloadingProgress(p0: RtmMediaOperationProgress?, p1: Long) {
                        TODO("Not yet implemented")
                    }

                    override fun onTokenExpired() {
                        TODO("Not yet implemented")
                    }

                    override fun onPeersOnlineStatusChanged(p0: MutableMap<String, Int>?) {
                        TODO("Not yet implemented")
                    }
                })
        }catch (e: Exception){
            print("Exception occurred while initializing the RTM SDK: $e")
        }
    }


    fun loginRtm(){
        mRtmClient!!.login(null, username, object : ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                val text = "User logged in successfully"
                print(text)
                Log.d(text, "onSuccess: RTM Initialization")
            }
            override fun onFailure(errorInfo: ErrorInfo) {
                val text = "Failed to log in to the RTM system + ${errorInfo.toString()}"
                print(text)
                Log.e(text, "onFailure: RTM Initialization")
            }
        } )
    }

    fun createRtmChannel(){
        try {
            // Create an RTM channel
            mRtmChannel = mRtmClient!!.createChannel("lobby", object : RtmChannelListener {
                override fun onMemberCountUpdated(p0: Int) {
                    TODO("Not yet implemented")
                }

                override fun onAttributesUpdated(p0: MutableList<RtmChannelAttribute>?) {
                    TODO("Not yet implemented")
                }

                override fun onMessageReceived(rtmMessage: RtmMessage, rtmChannelMember: RtmChannelMember) {
                    println("Message: ${rtmMessage.text} from ${rtmChannelMember.channelId}")
                    if(!userList.contains(rtmMessage.text)){
                        userList.add(rtmMessage.text)
                    }
                    println(userList)
                }

                override fun onImageMessageReceived(p0: RtmImageMessage?, p1: RtmChannelMember?) {
                    TODO("Not yet implemented")
                }

                override fun onFileMessageReceived(p0: RtmFileMessage?, p1: RtmChannelMember?) {
                    TODO("Not yet implemented")
                }

                override fun onMemberJoined(rtmChannelMember: RtmChannelMember) {
                    println("Member joined: ${rtmChannelMember.userId}")
                    if(!userList.contains(rtmChannelMember.userId)){
                        userList.add(rtmChannelMember.userId)
                    }
                    val message = mRtmClient!!.createMessage()
                    message.text = username
                    mRtmClient!!.sendMessageToPeer(rtmChannelMember.userId, message, SendMessageOptions(), object: ResultCallback<Void>{
                        override fun onSuccess(p0: Void?) {
                            println("Message sent to ")
                        }

                        override fun onFailure(p0: ErrorInfo?) {
                            TODO("Not yet implemented")
                        }
                    })
                }

                override fun onMemberLeft(rtmChannelMember: RtmChannelMember) {
                    userList.remove(rtmChannelMember.userId)
                }
            })
        } catch (e: RuntimeException) {
        }
    }

    fun joinRtmChannel() {
        mRtmChannel!!.join(object : ResultCallback<Void> {
            override fun onSuccess(responseInfo: Void?) {
                val text = "Join channel success"
                print(text)
                Log.d(text, "onSuccess: RTM Channel Joined Successfully")
                val message = mRtmClient!!.createMessage()
                message.text = username
                mRtmChannel!!.sendMessage(message, object : ResultCallback<Void> {
                    override fun onSuccess(p0: Void?) {
                        val text = "Channel message sent successfully. Message: $username"
                        print(text)
                        Log.d(text, "onSuccess: Channel message sent successfully")
                    }

                    override fun onFailure(errorInfo: ErrorInfo) {
                        val text = "Message failed to send ${errorInfo.errorDescription}, errorCode: ${errorInfo.errorCode}"
                        Log.e(text, "onFailure: Channel message failed to send ", )
                    }
                })
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                val text = "Failed to join the channel"
                print(text)
                Log.e(text, "onFailure: Channel Join Failure")
            }
        })
    }

    lock.lock()
    try {
        initializeRtm()
    }finally {
        lock.unlock()
    }

    lock.lock()
    try {
        loginRtm()
    }finally {
        lock.unlock()
    }

    lock.lock()
    try {
        createRtmChannel()
    }finally {
        lock.unlock()
    }

    lock.lock()
    try {
        joinRtmChannel()
    }finally {
        lock.unlock()
    }

}

@ExperimentalMaterialApi
@Composable
fun UserList(userList: SnapshotStateList<String>, openDialog: MutableState<Boolean>, username: String){
    val context = LocalContext.current

    Box() {
        LazyColumn(){
            items(userList){ user ->
                UserCard(username = user, openDialog = openDialog)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        if(openDialog.value){
            AlertDialog(
                onDismissRequest = {
                },
                title = {
                    Text("Incoming Call")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            println("Call received")
                            openDialog.value = false
                            val intent = Intent(context, VideoActivity::class.java)
                            print("Channel name being shared $channelName")
                            intent.putExtra("CHANNEL_NAME", channelName)
                            ContextCompat.startActivity(context, intent, Bundle())
                        },
                    ) {
                        Text("Answer")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            // Change the state to close the dialog
                            openDialog.value = false
                        },
                    ) {
                        Text("Decline")
                    }
                },
                text = {
                    Text("Incoming call from $username")
                },
            )
        }
    }

}


fun userCardClicked(context: Context, username: String, openDialog: MutableState<Boolean>){

    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    channelName = (1..10).map { allowedChars.random() }.joinToString("")
    print("Channel name generated : $channelName")
    val message = mRtmClient!!.createMessage()
    message.text = "%" + channelName
    println("Channel Name: ${message.text}")
    mRtmClient!!.sendMessageToPeer(username, message, SendMessageOptions(), object : ResultCallback<Void>{
        override fun onSuccess(p0: Void?) {
            val intent = Intent(context, VideoActivity::class.java)
            intent.putExtra("CHANNEL_NAME", channelName)
            ContextCompat.startActivity(context, intent, Bundle())
        }

        override fun onFailure(p0: ErrorInfo?) {
            TODO("Not yet implemented")
        }
    })

}

