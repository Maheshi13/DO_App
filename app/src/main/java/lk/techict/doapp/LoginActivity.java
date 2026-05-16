package lk.techict.doapp;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;
    TextView tvSignup; // Declare the TextView here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. Initialize all views
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup); // Initialize the Signup text

        // 2. Apply the purple color to the word "Signup"
        String text = "No Account? Signup";
        SpannableString ss = new SpannableString(text);

        // Define the purple color
        ForegroundColorSpan colorPurple = new ForegroundColorSpan(Color.parseColor("#7F00FF"));

        // Color the word "Signup" (starts at index 12, ends at 18)
        ss.setSpan(colorPurple, 12, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the styled text to your TextView
        tvSignup.setText(ss);

        // 3. Login Button Logic
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = etUsername.getText().toString();
                String pass = etPassword.getText().toString();

                DBHelper DB = new DBHelper(LoginActivity.this);

                if(user.equals("") || pass.equals("")) {
                    Toast.makeText(LoginActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                } else {
                    Boolean checkuserpass = DB.checkUserPassword(user, pass);
                    if(checkuserpass) {
                        Toast.makeText(LoginActivity.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                        // Use LoginActivity.this instead of getApplicationContext() for better stability
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        // Add flags to ensure a clean transition
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
        // 4. Signup Text Click Logic
        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move from Login to Signup screen
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}

