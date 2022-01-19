package com.charuniverse.curious.data.source

import android.content.Context
import com.charuniverse.curious.data.model.User
import com.charuniverse.curious.util.Constant
import com.charuniverse.curious.util.Preferences
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val firebaseAuth: FirebaseAuth,
    private val dispatcherContext: CoroutineDispatcher = Dispatchers.IO
) {

    fun buildGoogleSignInClient(context: Context): GoogleSignInClient {
        return GoogleSignIn.getClient(
            context,
            GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constant.GOOGLE_OAUTH_KEY)
                .requestEmail()
                .build()
        )
    }

    fun getLoginUser(): User? = firebaseAuth.currentUser?.let {
        User(
            id = it.uid,
            username = it.uid,
            displayName = it.displayName ?: "",
            email = it.email ?: "",
            profilePictureUrl = it.photoUrl.toString(),
            createdAt = System.currentTimeMillis(),
        )
    }

    // TODO: refactor to Flow<Result<Unit>>
    suspend fun loginWithGoogle(accountIdToken: String): Flow<Unit> = flow {
        val credential = GoogleAuthProvider
            .getCredential(accountIdToken, null)

        firebaseAuth
            .signInWithCredential(credential)
            .await()

        emit(Unit)
    }

    suspend fun logOut(context: Context): Flow<Unit> = flow {
        buildGoogleSignInClient(context)
            .signOut()
            .await()

        firebaseAuth.signOut()

        Preferences.userId = ""
        emit(Unit)
    }

}