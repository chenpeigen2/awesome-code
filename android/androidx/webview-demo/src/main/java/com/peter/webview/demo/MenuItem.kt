package com.peter.webview.demo

import android.content.Context
import android.content.Intent
import com.peter.webview.demo.advanced.BidirectionalActivity
import com.peter.webview.demo.advanced.WebMessagePortActivity
import com.peter.webview.demo.basics.AndroidCallJsActivity
import com.peter.webview.demo.basics.JsCallAndroidActivity
import com.peter.webview.demo.intermediate.JsPromptActivity
import com.peter.webview.demo.intermediate.UrlSchemeActivity

data class MenuItem(
    val title: String,
    val description: String = "",
    val isHeader: Boolean = false,
    val intent: Intent? = null
)

fun createAndroidCallJsIntent(context: Context) = Intent(context, AndroidCallJsActivity::class.java)
fun createJsCallAndroidIntent(context: Context) = Intent(context, JsCallAndroidActivity::class.java)
fun createUrlSchemeIntent(context: Context) = Intent(context, UrlSchemeActivity::class.java)
fun createJsPromptIntent(context: Context) = Intent(context, JsPromptActivity::class.java)
fun createBidirectionalIntent(context: Context) = Intent(context, BidirectionalActivity::class.java)
fun createWebMessagePortIntent(context: Context) = Intent(context, WebMessagePortActivity::class.java)
