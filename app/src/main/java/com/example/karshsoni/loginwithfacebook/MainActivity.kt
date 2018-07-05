package com.example.karshsoni.loginwithfacebook

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import com.facebook.*
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Toast
import com.facebook.FacebookException
import com.facebook.FacebookCallback
import com.facebook.login.LoginManager
import com.facebook.CallbackManager
import com.facebook.login.widget.ProfilePictureView
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog


class MainActivity : AppCompatActivity() {
    val EMAIL = "email"
    lateinit var firstName: String
    lateinit var lastName: String
    lateinit var email: String
    lateinit var id : String
    lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        generateHashKey()

        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)

        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired

        callbackManager = CallbackManager.Factory.create();

        var shareDialog = ShareDialog(this)

        btnShare.setOnClickListener {
            val content = ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("https://developers.facebook.com"))
                    .build()

            shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
        }

//        login_button.setReadPermissions(Arrays.asList(EMAIL))
        login_button.setReadPermissions(Arrays.asList("public_profile","email"))
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration

        login_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                // App code
                var userId = loginResult.accessToken.userId

                var graphRequest = GraphRequest.newMeRequest(loginResult.accessToken
                ) { `object`, response ->
                    displayUserInfo(`object`)
                }

                var bundle = Bundle()
                bundle.putString("fields", "first_name, last_name, email, id")
                graphRequest.parameters = bundle
                graphRequest.executeAsync()

            }

            override fun onCancel() {
                // App code
            }

            override fun onError(exception: FacebookException) {
                // App code
            }
        })


    }

    fun displayUserInfo(jsonObject: JSONObject) {
        firstName = jsonObject.getString("first_name")
        lastName = jsonObject.getString("last_name")
        email = jsonObject.getString("email")
        id = jsonObject.getString("id")

        fullName.text = " $firstName $lastName"
        emailId.text = email
        userId.text = id
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun generateHashKey() {
        try {
            var info: PackageInfo = packageManager.getPackageInfo("com.example.karshsoni.loginwithfacebook", PackageManager.GET_SIGNATURES)
            for (signature: Signature in info.signatures) {
                var md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.i("KEY1234", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }
}
