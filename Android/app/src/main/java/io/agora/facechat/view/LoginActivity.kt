package io.agora.facechat.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import java.util.concurrent.locks.ReentrantLock


private const val PERMISSIONS_REQUEST_CODE = 10
private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)

@Composable
fun BodyWidget(navController: NavController) {
    Column(
        modifier = Modifier
            .padding(start = 8.dp, top = 20.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
    ) {
        Spacer(modifier = Modifier.height(15.dp))
        Headers()
        Spacer(modifier = Modifier.height(100.dp))
        UsernameTextField(navController = navController)
    }
}

@Composable
fun Headers(){
    Column() {
        Text(text = "Hi there,", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "Welcome to Facechat!",
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF039dfd),
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(text = "Enter your username to continue", style = MaterialTheme.typography.h6)
    }
}

@Composable
fun UsernameTextField(navController: NavController){
    val inputvalue = remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextField(
            value = inputvalue.value,
            onValueChange = { inputvalue.value = it },
            placeholder = { Text(text = "Enter your username") },
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                autoCorrect = true,
                keyboardType = KeyboardType.Text,
            ),
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Rounded.AccountCircle, contentDescription = "Username")
            },
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Black,
            ),
        )
        Spacer(modifier = Modifier.height(100.dp))
        JoinButton(navController = navController, value = inputvalue.value)
    }
}

@Composable
fun JoinButton(navController: NavController, value: TextFieldValue){
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Button(
            enabled = true,
            onClick = {
                val lock = ReentrantLock()

                lock.lock()
                try {
                    if (!hasPermissions(context)) {
                        requestPermissions(
                            context as Activity,
                            PERMISSIONS_REQUIRED,
                            PERMISSIONS_REQUEST_CODE
                        )
                    }
                }finally {
                    lock.unlock()
                }

                val intent = Intent(context, LobbyActivity::class.java)
                intent.putExtra("USERNAME", value.text)
                startActivity(context, intent, Bundle())
            },
            contentPadding = PaddingValues(horizontal = 100.dp, vertical = 15.dp),
            colors = ButtonDefaults.textButtonColors(
                backgroundColor = Color(0xFF039dfd),
            ),
            shape = RoundedCornerShape(30),
            modifier = Modifier.padding(vertical = 50.dp)
        ) {
            Text(
                text = "Join",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier,
                style = MaterialTheme.typography.body1,
                color = Color.White
            )
        }
    }
}

fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
    ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
}
