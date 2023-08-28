package com.example.basic.utils

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class GoogleSignIn {

    private fun googleSignInOptions(context: Context): GoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(com.example.basic.R.string.default_web_client_id))
                .requestEmail()
                .build()

    fun googleSignInClient(context: Context): GoogleSignInClient =
            GoogleSignIn.getClient(context, googleSignInOptions(context))



}