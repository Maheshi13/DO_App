package lk.techict.doapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {
    // Added 'email' to the variable list
    EditText username, email, password, repassword;
    Button signup;
    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize UI components
        username = findViewById(R.id.etUsername_Signup);
        email = findViewById(R.id.etEmail_Signup); // Make sure this ID exists in activity_signup.xml
        password = findViewById(R.id.etPassword_Signup);
        repassword = findViewById(R.id.etConfirmPassword_Signup);
        signup = findViewById(R.id.btnSignup);
        DB = new DBHelper(this);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String emailText = email.getText().toString(); // Capture email input
                String pass = password.getText().toString();
                String repass = repassword.getText().toString();

                // 1. Validation: Check if all fields (including email) are filled
                if(user.equals("") || emailText.equals("") || pass.equals("") || repass.equals("")) {
                    Toast.makeText(SignupActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // 2. Validation: Check if passwords match
                    if(pass.equals(repass)) {
                        Boolean checkuser = DB.checkUsername(user);
                        if(!checkuser) {

                            // FIXED: Passing three arguments to match DBHelper (user, emailText, pass)
                            Boolean insert = DB.insertData(user, emailText, pass);

                            if(insert) {
                                Toast.makeText(SignupActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish(); // Close signup screen so back button doesn't return here
                            } else {
                                Toast.makeText(SignupActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SignupActivity.this, "User already exists!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}