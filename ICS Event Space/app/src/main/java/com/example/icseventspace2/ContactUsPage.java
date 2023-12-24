package com.example.icseventspace2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class ContactUsPage extends AppCompatActivity {

    private TextInputEditText subject_field, message_field;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us_page);

        subject_field = findViewById(R.id.subject);
        message_field = findViewById(R.id.message);
    }

    public void backToHome(View view) {
        startActivity(new Intent(ContactUsPage.this, MainActivity.class));
        finish();
    }

    public void send_message(View view) {
        String subject = subject_field.getText().toString();
        String message = message_field.getText().toString();
        String[] recipient =  {"iscscunofficial@gmail.com"};

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, recipient);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        intent.setType("message/rfc882");
        if (message.trim().isEmpty()) {
            Toast.makeText(this, "Please type your message", Toast.LENGTH_SHORT).show();
        }
        else{
            startActivityForResult(Intent.createChooser(intent, "Choose your email client"), 1);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        subject_field.setText("");
        message_field.setText("");
    }

}