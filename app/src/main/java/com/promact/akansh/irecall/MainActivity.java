package com.promact.akansh.irecall;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    private static final int RC_SIGN_IN = 1;
    private String idToken, email, name;
    private Uri photoUri;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (SaveSharedPref.getToken(MainActivity.this).length()==0){
            //This code is for configuring the sign-in in order to request the user's name and email;
            GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

            //The next step is to build a GoogleApiClient
            final GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, options).build();
            com.google.android.gms.common.SignInButton signInButton = (com.google.android.gms.common.SignInButton) findViewById(R.id.loginWithGoogle);

            signInButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
            });
        }
        else{
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    public void handleSignInResult(GoogleSignInResult result){
        Log.d(TAG, "handleSignInResult" + result.isSuccess());

        if (result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            if (account != null){
                idToken = account.getIdToken();
                name = account.getDisplayName();
                email = account.getEmail();
                photoUri = account.getPhotoUrl();
            }

            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.putExtra("idToken", idToken);
            intent.putExtra("name", name);
            intent.putExtra("email", email);
            intent.putExtra("photoUri", photoUri.toString());
            SaveSharedPref.setPrefs(getApplicationContext(), idToken, name, email, photoUri.toString());

            startActivity(intent);
        }

        else{
            Toast.makeText(getApplicationContext(), "Login was unsuccessful", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull  ConnectionResult connectionResult){
        Log.e(TAG, "Connection Failed: " + connectionResult);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        //The first two here are responsible for launching the home page.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//Marks the start of a new task in the history stack
        startActivity(intent);
        finish();
    }
}
