package com.example.pushupcounter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class GoogleSignInActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_in);

        // Инициализация Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Настройка параметров Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))  // Используйте client_id из Firebase
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Button signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Результат авторизации через Google
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Авторизация через Google прошла успешно, теперь авторизуем через Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Ошибка авторизации через Google
                Log.w("Google Sign-In", "Авторизация через Google провалилась", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<androidx.auth0.firebase.auth.AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<androidx.auth0.firebase.auth.AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Авторизация прошла успешно
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(GoogleSignInActivity.this, "Успешная авторизация: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                            // Переход в MainActivity
                            Intent intent = new Intent(GoogleSignInActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Ошибка авторизации через Firebase
                            Toast.makeText(GoogleSignInActivity.this, "Ошибка авторизации через Firebase", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Пользователь уже авторизован
            Intent intent = new Intent(GoogleSignInActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
