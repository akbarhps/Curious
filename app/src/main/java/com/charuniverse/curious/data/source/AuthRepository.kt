package com.charuniverse.curious.data.source

import android.content.Context
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.model.User
import com.charuniverse.curious.data.source.in_memory.InMemoryUserDataSource
import com.charuniverse.curious.data.source.remote.MessagingRemoteDataSource
import com.charuniverse.curious.data.source.remote.UserRemoteDataSource
import com.charuniverse.curious.util.Constant
import com.charuniverse.curious.util.Preferences
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val firebaseAuth: FirebaseAuth,
    private val messagingRemoteDataSource: MessagingRemoteDataSource,
    private val userRemoteDataSource: UserRemoteDataSource,
) {

    fun buildGoogleSignInClient(context: Context): GoogleSignInClient = GoogleSignIn.getClient(
        context,
        GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(Constant.GOOGLE_OAUTH_KEY)
            .requestEmail()
            .build()
    )

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

    suspend fun loginWithGoogle(accountIdToken: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            val credential = GoogleAuthProvider
                .getCredential(accountIdToken, null)

            firebaseAuth.signInWithCredential(credential)
                .await()

            val uid = firebaseAuth.uid!!

            var user = userRemoteDataSource.getById(uid)
            if (user == null) {
                user = getLoginUser()!!
                userRemoteDataSource.create(user)
            }

            val token = messagingRemoteDataSource.getToken()
                ?: throw Exception("User token not found")

            userRemoteDataSource.updateFCMToken(uid, token)

            Preferences.userId = uid
            InMemoryUserDataSource.add(user)

            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    suspend fun logOut(context: Context): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            buildGoogleSignInClient(context)
                .signOut()
                .await()

            firebaseAuth.signOut()
            Preferences.userId = ""

            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

}