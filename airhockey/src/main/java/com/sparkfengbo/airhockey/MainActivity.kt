package com.sparkfengbo.airhockey

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.ActivityManager
import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    lateinit var glSurfaceView: GLSurfaceView
    var rendererSet = false


    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.CUPCAKE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        glSurfaceView = GLSurfaceView(this)

        var activityManager: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        var configuration = activityManager.deviceConfigurationInfo

        /**
         * 真机判断 configuration.reqGlEsVersion >= 0x20000
         * 后面是模拟器GPU的一个bug，只使用configuration.reqGlEsVersion >= 0x20000 无法判断
         */
        var supportEs2 = configuration.reqGlEsVersion >= 0x20000
                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 && (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")))

        if (supportEs2) {
            glSurfaceView.setEGLContextClientVersion(2)
            glSurfaceView.setRenderer(AirHockeyRenderer())
            rendererSet = true
        } else {
            Toast.makeText(this, "不支持OpenGL ES2", Toast.LENGTH_LONG).show()
        }

        setContentView(glSurfaceView)
    }


    override fun onPause() {
        super.onPause()
        if (rendererSet) {
            glSurfaceView.onPause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (rendererSet) {
            glSurfaceView.onResume()
        }
    }


}
