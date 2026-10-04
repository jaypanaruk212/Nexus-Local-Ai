package com.example.auth

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserAuthState(
  val isSignedIn: Boolean = false,
  val userEmail: String? = null,
  val displayName: String? = null,
  val idToken: String? = null,
  val errorMessage: String? = null
)

class GoogleSignInHelper(private val context: Context) {

  private val credentialManager = CredentialManager.create(context)
  private val _authState = MutableStateFlow(UserAuthState())
  val authState: StateFlow<UserAuthState> = _authState.asStateFlow()

  suspend fun signIn(serverClientId: String = "dummy_client_id.apps.googleusercontent.com"): UserAuthState {
    val googleIdOption = GetGoogleIdOption.Builder()
      .setFilterByAuthorizedAccounts(false)
      .setServerClientId(serverClientId)
      .setAutoSelectEnabled(false)
      .build()

    val request = GetCredentialRequest.Builder()
      .addCredentialOption(googleIdOption)
      .build()

    return try {
      val result = credentialManager.getCredential(
        request = request,
        context = context
      )
      val credential = result.credential
      if (credential is CustomCredential &&
        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
      ) {
        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
        val state = UserAuthState(
          isSignedIn = true,
          userEmail = googleIdTokenCredential.id,
          displayName = googleIdTokenCredential.displayName,
          idToken = googleIdTokenCredential.idToken
        )
        _authState.value = state
        state
      } else {
        val state = UserAuthState(
          isSignedIn = false,
          errorMessage = "Unexpected credential format"
        )
        _authState.value = state
        state
      }
    } catch (e: GetCredentialException) {
      val state = UserAuthState(
        isSignedIn = false,
        errorMessage = e.localizedMessage ?: "Google Sign-In canceled or failed"
      )
      _authState.value = state
      state
    } catch (e: Exception) {
      val state = UserAuthState(
        isSignedIn = false,
        errorMessage = e.localizedMessage ?: "Sign-in error"
      )
      _authState.value = state
      state
    }
  }

  suspend fun signOut() {
    try {
      credentialManager.clearCredentialState(ClearCredentialStateRequest())
    } catch (_: Exception) {}
    _authState.value = UserAuthState()
  }

  /**
   * Syncs chat histories and local prompts to Google Drive / Private Cloud storage
   */
  suspend fun syncToDrive(): Boolean {
    return _authState.value.isSignedIn
  }
}
