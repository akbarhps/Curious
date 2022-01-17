package com.charuniverse.curious.data.repository

import android.content.Context
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.entity.User
import com.charuniverse.curious.util.Constant
import com.charuniverse.curious.util.Preferences
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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
            profilePictureURL = it.photoUrl.toString(),
        )
    }

    suspend fun loginWithGoogle(
        accountIdToken: String
    ): Result<Unit> = withContext(dispatcherContext) {
        return@withContext try {
            val credential = GoogleAuthProvider.getCredential(accountIdToken, null)
            firebaseAuth.signInWithCredential(credential).await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun logOut(context: Context): Result<Unit> = withContext(dispatcherContext) {
        return@withContext try {
            buildGoogleSignInClient(context).signOut().await()
            firebaseAuth.signOut()
            Preferences.userId = ""

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

}