package com.example.recognizemusic

import android.Manifest
import android.annotation.TargetApi
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RecognizeMusicActivity : AppCompatActivity() {

    lateinit var webView: WebView
    var uploadMessage: ValueCallback<Array<Uri>>? = null
    private var mUploadMessage: ValueCallback<Uri>? = null
    //    var imageView: ImageView? = null
    val REQUEST_SELECT_FILE: Int = 100
    private val FILECHOOSER_RESULTCODE = 2
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    private val RECORD_AUDIO_PERMISSION_REQUEST_CODE = 123
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recognize_music)
        initView()
    }
    fun initView() {
        webView=findViewById(R.id.WebView)
        val webSettings: WebSettings = webView.getSettings()
        webView.settings.allowFileAccess =true
        webView.settings.domStorageEnabled = true
        webView.settings.setSupportZoom(true)
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false
        webView.settings.allowFileAccess = true
        webView.settings.allowFileAccessFromFileURLs = true
        webView.settings.allowUniversalAccessFromFileURLs = true
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.settings.javaScriptCanOpenWindowsAutomatically=true
        val timestamp = System.currentTimeMillis()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)
            }
        }
        webSettings.setAllowContentAccess(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE)
        webSettings.setDomStorageEnabled(true)
        webSettings.setBuiltInZoomControls(false);
        webSettings.setJavaScriptEnabled(true)
        //  webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE)
        webSettings.setLoadWithOverviewMode(true)
        webSettings.setAllowFileAccess(true)
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                //view.loadUrl("file:///android_asset/index.html?timestamp=$timestamp")
                //view.loadUrl("http://www.baidu.com")
                return false
            }
        }

        WebView.setWebContentsDebuggingEnabled(true)

        webView.setWebChromeClient(object : WebChromeClient() {

            override fun onPermissionRequest(request: PermissionRequest?) {
                val permissions = request?.resources ?: return
                if (permissions.contains(PermissionRequest.RESOURCE_AUDIO_CAPTURE)) {
                    val parent = webView
                    if (ContextCompat.checkSelfPermission(this@RecognizeMusicActivity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this@RecognizeMusicActivity, arrayOf(
                            Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_PERMISSION_REQUEST_CODE)
                    } else {
                        request.grant(arrayOf(PermissionRequest.RESOURCE_AUDIO_CAPTURE))
                    }
                }
            }
            // For Lollipop 5.0+ Devices
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onShowFileChooser(
                mWebView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                if (uploadMessage != null) {
                    uploadMessage!!.onReceiveValue(null)
                    uploadMessage = null
                }

                uploadMessage = filePathCallback

                val intent = fileChooserParams.createIntent()
                try {
                    startActivityForResult(intent, REQUEST_SELECT_FILE)
                } catch (e: ActivityNotFoundException) {
                    uploadMessage = null
                    Toast.makeText(baseContext, "Cannot Open File Chooser", Toast.LENGTH_LONG)
                        .show()
                    return false
                }
                return true
            }



        })
        val url = "file:///android_asset/index.html?timestamp=$timestamp"
        //val url = "https://mos9527.github.io/ncm-afp/"
        webView.loadUrl(url)
        //webView.loadUrl("https://www.baidu.com")
//        webView.addJavascriptInterface(new LocalJavaScript(this),"anime")





    }

    @Override
    public override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null) return
                uploadMessage!!.onReceiveValue(
                    WebChromeClient.FileChooserParams.parseResult(
                        resultCode,
                        intent
                    )
                )
                uploadMessage = null
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) return
            // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
            // Use RESULT_OK only if you're implementing WebView inside an Activity
            val result =
                if (intent == null || resultCode != RESULT_OK) null else intent.data
            mUploadMessage!!.onReceiveValue(result)
            mUploadMessage = null
        } else Toast.makeText(baseContext, "无法上传", Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RECORD_AUDIO_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 用户授权了录音权限
                    webView.reload() // 重新加载 WebView 来应用权限
                } else {
                    // 用户拒绝了录音权限
                }
            }
        }
    }





}