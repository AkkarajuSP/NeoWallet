package com.example.neowallet_app

import android.os.Bundle
import android.view.WindowManager
import io.flutter.embedding.android.FlutterActivity

class MainActivity : FlutterActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Prevent screenshots and screen recordings of the app window.
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }
}
