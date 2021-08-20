package io.agora.facechat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.agora.facechat.ui.theme.FacechatTheme
import io.agora.facechat.view.BodyWidget
import io.agora.facechat.view.LobbyActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FacechatTheme() {
                Surface(color = MaterialTheme.colors.background){
                    ComposeNavigation()
                }
            }
        }
    }
}

@Composable
fun ComposeNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "first_screen"
    ) {
        composable("first_screen") {
            BodyWidget(navController = navController)
        }
        composable("second_screen") {
            LobbyActivity()
        }
    }
}
