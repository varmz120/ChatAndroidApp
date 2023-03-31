package com.example.loginpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    private Button Student;
    private Button TA;
    private Button Professor;
    private Button Register;
    private Button Back;
    private String Username;
    private String Password;
    private String confirmPassword;
    private TextView roleView;
    String selectedRole = "";

    private static Integer userid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Student = (Button) findViewById(R.id.student);
        TA = (Button) findViewById(R.id.ta);
        Professor = (Button) findViewById(R.id.prof);
        Register = (Button) findViewById(R.id.register);
        roleView = (TextView) findViewById(R.id.role);
        Back = (Button) findViewById(R.id.back);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();


        Student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedRole = "Student";
                roleView.setText(selectedRole);
            }
        });

        TA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedRole = "TA";
                roleView.setText(selectedRole);
            }
        });

        Professor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedRole = "Professor";
                roleView.setText(selectedRole);
            }
        });

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Username = ((EditText) findViewById(R.id.newusername)).getText().toString();
                Password = ((EditText) findViewById(R.id.newpassword)).getText().toString();
                confirmPassword = ((EditText) findViewById(R.id.confirmpassword)).getText().toString();
                if (selectedRole.equals("")){
                    Toast.makeText(Register.this,"Registration failed, please select a role",Toast.LENGTH_LONG).show();
                } else if (Password.equalsIgnoreCase(confirmPassword) && !Password.equalsIgnoreCase("")) {
                    mAuth.createUserWithEmailAndPassword(Username, Password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Get the user ID
                                        String userId = mAuth.getCurrentUser().getUid();

                                        // Store additional user information in the Firebase Realtime Database
                                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                        mDatabase.child("users").child(userId).child("username").setValue(Username);
                                        mDatabase.child("users").child(userId).child("role").setValue(roleView.getText().toString());

                                        // Show success message
                                        Toast.makeText(Register.this, "Successful Registration. Press back to return to home page", Toast.LENGTH_LONG).show();
                                    } else {
                                        // Show error message
                                        Toast.makeText(Register.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        Log.d("TAG",task.getException().getMessage());
                                    }
                                }
                            });

                } else
                {
                    Toast.makeText(Register.this,"Password fields are empty or do not match", Toast.LENGTH_LONG).show();
                }
                ((EditText) findViewById(R.id.newusername)).setText("");
                ((EditText) findViewById(R.id.newpassword)).setText("");
                ((EditText) findViewById(R.id.confirmpassword)).setText("");
            }
        });
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this,MainActivity.class);
                startActivity(intent);
            }
        });

}}

