package com.example.attends.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.attends.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Singup extends AppCompatActivity {
Button sing;
EditText name;
EditText email;
EditText number;
EditText password;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);
        mAuth = FirebaseAuth.getInstance();
        sing = findViewById(R.id.sing);
       name = findViewById(R.id.name);
        email = findViewById(R.id.email);

        password = findViewById(R.id.password);
      sing.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

              String emaill = email.getText().toString();
              String passwordd = password.getText().toString();
              if (emaill.isEmpty()) {
                  email.setError("please enter email id");
                  email.requestFocus();

              } else if (passwordd.isEmpty()) {
                  password.setError("please enter password ");
                  password.requestFocus();
              } else if (emaill.isEmpty() && passwordd.isEmpty()) {
                  Toast.makeText(Singup.this, "fields are empty", Toast.LENGTH_SHORT).show();
              } else if (!(emaill.isEmpty() && passwordd.isEmpty())) {
                  mAuth.createUserWithEmailAndPassword(emaill, passwordd).addOnCompleteListener(Singup.this, new OnCompleteListener<AuthResult>() {
                      @Override
                      public void onComplete(@NonNull Task<AuthResult> task) {
                          if (!task.isSuccessful()) {
                              Toast.makeText(Singup.this, "Singup unSucuccessful , please Try again", Toast.LENGTH_SHORT).show();
                          }
                          else {
                          Intent intent = new Intent(Singup.this, MainActivity.class);
                          startActivity(intent);
                      }
                      }
                  });
              }
              else {
                  Toast.makeText(Singup.this, "Error Ocurred!", Toast.LENGTH_SHORT).show();
              }
          }
      });

    }
}