package com.urahamat01.myapplicationtour.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.urahamat01.myapplicationtour.R;

public class LogInActivity extends AppCompatActivity {

    private TextView registationTv, forgotPasswordTv;
    private EditText emailEt, passwordEt;
    private Button signinBtn;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog loadinbar;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialize();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            Intent i = new Intent(LogInActivity.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);

        } else {

            signinBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String email = emailEt.getText().toString();
                    String password = passwordEt.getText().toString();

                    if(!validate(email,password)){
                        return;
                    }else {
                        loadinbar.setTitle("SignIn");
                        loadinbar.setMessage("Signing In");
                        loadinbar.show();
                        loadinbar.setCanceledOnTouchOutside(true);
                        signInWithEmailAndPassword(email, password);
                    }
                }
            });

            registationTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
                    startActivity(intent);
                }
            });

            forgotPasswordTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
                    LayoutInflater layoutInflater = LayoutInflater.from(LogInActivity.this);
                    View view = layoutInflater.inflate(R.layout.alartdialog_fotget_password, null);

                    builder.setView(view);
                    final Dialog dialog = builder.create();
                    dialog.show();

                    final EditText email = view.findViewById(R.id.fotgetAlartDialogEmailEtId);
                    TextView sendActin = view.findViewById(R.id.fotgetpassAlartDialogSendTvId);
                    TextView cancel = view.findViewById(R.id.fotgetpassAlartDialogCancelTvId);

                    final String forgetEmail = email.getText().toString();

                    sendActin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (email.getText().toString().trim().matches(emailPattern)) {

                                String emailAddress = email.getText().toString();

                                firebaseAuth.sendPasswordResetEmail(emailAddress)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(LogInActivity.this, "Email Sent", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(LogInActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });


                            } else {
                                Toast.makeText(LogInActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                            }

                            dialog.dismiss();
                        }
                    });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            });
        }
    }

    private void signInWithEmailAndPassword(String email, String password) {

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    loadinbar.dismiss();
                    Toast.makeText(LogInActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LogInActivity.this, SplashScreenActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else {

                    Toast.makeText(LogInActivity.this, "Wrong Email or password", Toast.LENGTH_SHORT).show();
                    loadinbar.dismiss();
                }
            }
        });

    }

    private boolean validate(String email, String password) {

            if (email.isEmpty()) {
                emailEt.setError("Please enter a email");
                return false;
            } else if (!email.matches(emailPattern)) {
                emailEt.setError("Please enter a valid email");
                return false;
            } else if (password.isEmpty()) {
                passwordEt.setError("Please enter a password");
                return false;
            } else if (password.length() < 6) {
                passwordEt.setError("Password must be at least 6 character");
                return false;
            }
            return true;
    }

    private void initialize() {
        registationTv = findViewById(R.id.SignUpTvId);
        emailEt = findViewById(R.id.signInEmailEtId);
        passwordEt = findViewById(R.id.signInPasswordEtId);
        signinBtn = findViewById(R.id.signInBtnId);
        firebaseAuth = FirebaseAuth.getInstance();
        forgotPasswordTv = findViewById(R.id.forgotpassTvid);
        loadinbar = new ProgressDialog(this);
    }

}
