package com.promact.akansh.irecall;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.SignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import android.Manifest;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    private static final int RC_SIGN_IN = 1;
    private String idToken, email, name;
    private Uri photoUri;
    private static final String TAG = "MainActivity";
    private static final int REQUEST_PERMISSIONS = 20;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onStart() {
        super.onStart();

        /*user = mAuth.getCurrentUser();
        Log.i("IRecall user", "user: " + user);*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        if (SaveSharedPref.getToken(MainActivity.this).length()==0){
            //This code is for configuring the sign-in in order to request the user's name and email;
            GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

            //The next step is to build a GoogleApiClient
            final GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, options).build();
            com.google.android.gms.common.SignInButton signInButton = (com.google.android.gms.common.SignInButton) findViewById(R.id.loginWithGoogle);

            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    + ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    + ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    + ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale
                        (MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                        (MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                        (MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                        (MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Snackbar.make(findViewById(android.R.id.content),
                            "Please Grant all permissions",
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ActivityCompat.requestPermissions
                                            (MainActivity.this,
                                            new String[]
                                                    {Manifest.permission.READ_EXTERNAL_STORAGE,
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                                    Manifest.permission.ACCESS_FINE_LOCATION},
                                                    REQUEST_PERMISSIONS);
                                }
                            }).show();
                } else {
                    ActivityCompat.requestPermissions
                            (MainActivity.this, new String[]
                            {
                             Manifest.permission.READ_EXTERNAL_STORAGE,
                             Manifest.permission.WRITE_EXTERNAL_STORAGE,
                             Manifest.permission.ACCESS_COARSE_LOCATION,
                             Manifest.permission.ACCESS_FINE_LOCATION
                            },
                             REQUEST_PERMISSIONS);
                }
            } else {
                signInButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                        startActivityForResult(signInIntent, RC_SIGN_IN);
                    }
                });
            }
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

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        user = mAuth.getCurrentUser();
        Log.i("IRecall user", "user: " + user);
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
            firebaseAuthWithGoogle(account);

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
            intent.putExtra("userId", user.getUid());
            SaveSharedPref.setPrefs(getApplicationContext(), idToken, name, email, photoUri.toString(), user.getUid());

            startActivity(intent);
        }

        else{
            Toast.makeText(getApplicationContext(), "Login was unsuccessful", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.i("IRecall firebase auth: ", "-----------------" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.i("IRecall user: ", "=============== completed===============");
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.i("IRecall user: ", "users: " + user);
                        } else {
                            Toast.makeText(MainActivity.this, "Authentication unsuccessful",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                });
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
