package lk.techict.doapp;

import android.content.Intent;
import android.content.SharedPreferences;
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
    TextView tvSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. Initialize views
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup);

        // 2. Style Signup text
        String text = "No Account? Signup";

        SpannableString ss = new SpannableString(text);

        ForegroundColorSpan colorPurple =
                new ForegroundColorSpan(Color.parseColor("#7F00FF"));

        ss.setSpan(
                colorPurple,
                12,
                18,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        tvSignup.setText(ss);

        // 3. Login button logic
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user = etUsername.getText().toString().trim();
                String pass = etPassword.getText().toString().trim();

                DBHelper DB = new DBHelper(LoginActivity.this);

                if (user.isEmpty() || pass.isEmpty()) {

                    Toast.makeText(
                            LoginActivity.this,
                            "Please enter all fields",
                            Toast.LENGTH_SHORT
                    ).show();

                } else {

                    Boolean checkuserpass =
                            DB.checkUserPassword(user, pass);

                    if (checkuserpass) {

                        // Save the currently logged-in username
                        SharedPreferences preferences =
                                getSharedPreferences(
                                        "UserSession",
                                        MODE_PRIVATE
                                );

                        preferences.edit()
                                .putString("username", user)
                                .apply();

                        Toast.makeText(
                                LoginActivity.this,
                                "Sign in successful",
                                Toast.LENGTH_SHORT
                        ).show();

                        Intent intent = new Intent(
                                LoginActivity.this,
                                HomeActivity.class
                        );

                        intent.addFlags(
                                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                        Intent.FLAG_ACTIVITY_NEW_TASK
                        );

                        startActivity(intent);
                        finish();

                    } else {

                        Toast.makeText(
                                LoginActivity.this,
                                "Invalid username or password",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
            }
        });

        // 4. Signup navigation
        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        LoginActivity.this,
                        SignupActivity.class
                );

                startActivity(intent);
            }
        });
    }
}
