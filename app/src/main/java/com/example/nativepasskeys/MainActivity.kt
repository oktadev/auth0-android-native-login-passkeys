package com.example.nativepasskeys

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.auth0.android.Auth0
import com.example.nativepasskeys.ui.theme.NativePasskeysTheme

class MainActivity : ComponentActivity() {

    private lateinit var auth0: Auth0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        auth0 = Auth0(
            getString(R.string.auth0_client_id),
            getString(R.string.auth0_domain)
        )

        setContentView(R.layout.activity_main)
    }
}