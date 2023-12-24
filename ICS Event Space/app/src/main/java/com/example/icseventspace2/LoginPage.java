package com.example.icseventspace2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.FirestoreClient;
public class LoginPage extends AppCompatActivity {
    private TextInputEditText username_field, password_field;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String username, for_change_pass, current_user, password;
    private boolean account_found;
    private TextView login_text, logInToContinue_text;
    private AppCompatButton login_button;
    private TextInputLayout username_layout, password_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        username_field = findViewById(R.id.username);
        password_field = findViewById(R.id.password);
        login_text = findViewById(R.id.login_text);
        logInToContinue_text = findViewById(R.id.log_in_to_continue);
        login_button = findViewById(R.id.login_button);
        username_layout = findViewById(R.id.username_input);
        password_layout = findViewById(R.id.password_input);

        try {
            Intent intent = getIntent();
            for_change_pass = intent.getStringExtra("change_pass");
            current_user = intent.getStringExtra("current_user");

            if (for_change_pass.equals("true")) {
                login_text.setText("Change\nPassword");
                login_text.setTextSize(30);
                logInToContinue_text.setVisibility(View.GONE);
                username_layout.setHint("Old Password");
                username_field.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                username_layout.setPasswordVisibilityToggleEnabled(true);
                password_layout.setHint("New Password");
                login_button.setText("SAVE");
            }
        } catch (Exception e) {}
    }

    public void backToHome(View view) {
        Intent intent = new Intent(LoginPage.this, MainActivity.class);
        if (for_change_pass.equals("true")) {
            intent.putExtra("current_user", current_user);
            intent.putExtra("logged_in", "true");
        }
        finish();
        startActivity(intent);
    }

    public void log_in(View view) {
        account_found = false;
        username = username_field.getText().toString();
        password = password_field.getText().toString();

        AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this ,R.style.MyAlertDialogTheme));
        myAlertBuilder.setTitle("Change password?");

        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            Toast.makeText(this, "Please enter your credentials", Toast.LENGTH_SHORT).show();
        }
        else {
            // if change pass
            if (login_button.getText().equals("SAVE")) {
                db.collection("Admins").document(current_user).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            if (documentSnapshot.getString("password").equals(username)) {
                                myAlertBuilder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        db.collection("Admins").document(current_user).update("password", password);
                                        Toast.makeText(LoginPage.this, "Password changed successfully ", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });

                                myAlertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(getApplicationContext(), "Password Change Canceled", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                myAlertBuilder.show();
                            }
                            else {
                                Toast.makeText(LoginPage.this, "Incorrect Old Password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
            // if log in
            else {
                db.collection("Admins")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String docId = document.getId();
                                        String docUsername = document.getString("username");
                                        String docPassword = document.getString("password");
                                        if (docId.equals(username) && docUsername.equals(username) && docPassword.equals(password)) {
                                            account_found = true;
                                            break;
                                        }
                                    }
                                }
                                if (account_found) {
                                        Toast.makeText(LoginPage.this, "Log in successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LoginPage.this, MainActivity.class);
                                        intent.putExtra("logged_in", "true");
                                        intent.putExtra("current_user", username);
                                        startActivity(intent);
                                        finish();
                                }
                                else {
                                    Toast.makeText(LoginPage.this, "Incorrect credentials", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }
}