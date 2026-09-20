package lk.techict.doapp;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class ProfileActivity extends AppCompatActivity {

    EditText etUsername, etEmail;
    DBHelper DB;
    BottomNavigationView bottomNav;
    MaterialButton btnSignOut, btnEditProfile;

    String loggedInUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 1. Initialize UI components
        etUsername = findViewById(R.id.etProfileUsername);
        etEmail = findViewById(R.id.etProfileEmail);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnSignOut = findViewById(R.id.btnSignOut);
        bottomNav = findViewById(R.id.bottomNavigation);

        DB = new DBHelper(this);

        // 2. Get the currently logged-in username
        SharedPreferences preferences =
                getSharedPreferences("UserSession", MODE_PRIVATE);

        loggedInUsername = preferences.getString("username", null);

        // Check whether a user is logged in
        if (loggedInUsername == null || loggedInUsername.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please sign in again",
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent = new Intent(
                    ProfileActivity.this,
                    LoginActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();

            return;
        }

        // 3. Load the logged-in user's profile
        displayUserData();

        // 4. Setup bottom navigation
        setupNavigation();

        // 5. Sign-out button
        btnSignOut.setOnClickListener(
                v -> showSignOutDialog()
        );

        // 6. Edit-profile button
        btnEditProfile.setOnClickListener(
                v -> showEditProfileDialog()
        );
    }

    // Display only the logged-in user's data
    private void displayUserData() {

        Cursor cursor = DB.getUserData(loggedInUsername);

        if (cursor != null && cursor.moveToFirst()) {

            try {

                // Column 0 = Username
                // Column 1 = Email

                etUsername.setText(cursor.getString(0));
                etEmail.setText(cursor.getString(1));

            } catch (Exception e) {

                e.printStackTrace();

            } finally {

                cursor.close();
            }

        } else {

            Toast.makeText(
                    this,
                    "No profile found",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    // Edit-profile dialog
    private void showEditProfileDialog() {

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_edit_profile);

        // Make the dialog background transparent
        if (dialog.getWindow() != null) {

            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );
        }

        EditText etEditUsername =
                dialog.findViewById(R.id.etEditUsername);

        EditText etEditEmail =
                dialog.findViewById(R.id.etEditEmail);

        MaterialButton btnCancel =
                dialog.findViewById(R.id.btnCancelEdit);

        MaterialButton btnSave =
                dialog.findViewById(R.id.btnSaveChanges);

        // Get current profile data
        String currentName = loggedInUsername;
        String currentEmail = etEmail.getText().toString();

        // Fill the dialog with current data
        etEditUsername.setText(currentName);
        etEditEmail.setText(currentEmail);

        // Cancel button
        btnCancel.setOnClickListener(
                v -> dialog.dismiss()
        );

        // Save button
        btnSave.setOnClickListener(v -> {

            String newName =
                    etEditUsername.getText().toString().trim();

            String newEmail =
                    etEditEmail.getText().toString().trim();

            if (newName.isEmpty() || newEmail.isEmpty()) {

                Toast.makeText(
                        this,
                        "Please fill all fields",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // Check whether the new username is already used
            if (!newName.equals(currentName)
                    && DB.checkUsername(newName)) {

                Toast.makeText(
                        this,
                        "Username already exists",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // Update the user's information
            boolean isUpdated = DB.updateUserData(
                    currentName,
                    newName,
                    newEmail
            );

            if (isUpdated) {

                // Update the saved logged-in username
                SharedPreferences preferences =
                        getSharedPreferences(
                                "UserSession",
                                MODE_PRIVATE
                        );

                preferences.edit()
                        .putString("username", newName)
                        .apply();

                loggedInUsername = newName;

                Toast.makeText(
                        this,
                        "Profile Updated!",
                        Toast.LENGTH_SHORT
                ).show();

                // Refresh profile information
                displayUserData();

                dialog.dismiss();

            } else {

                Toast.makeText(
                        this,
                        "Update Failed",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        dialog.show();
    }

    // Sign-out confirmation dialog
    private void showSignOutDialog() {

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_signout);

        if (dialog.getWindow() != null) {

            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );
        }

        MaterialButton btnCancel =
                dialog.findViewById(R.id.btnCancel);

        MaterialButton btnConfirmSignOut =
                dialog.findViewById(R.id.btnConfirmSignOut);

        // Cancel sign-out
        btnCancel.setOnClickListener(
                v -> dialog.dismiss()
        );

        // Confirm sign-out
        btnConfirmSignOut.setOnClickListener(v -> {

            dialog.dismiss();

            // Clear the saved user session
            SharedPreferences preferences =
                    getSharedPreferences(
                            "UserSession",
                            MODE_PRIVATE
                    );

            preferences.edit().clear().apply();

            // Navigate to LoginActivity
            Intent intent = new Intent(
                    ProfileActivity.this,
                    LoginActivity.class
            );

            // Prevent going back to the previous screens
            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
        });

        dialog.show();
    }

    // Bottom navigation logic
    private void setupNavigation() {

        bottomNav.setSelectedItemId(R.id.nav_profile);

        bottomNav.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {

                startActivity(
                        new Intent(
                                ProfileActivity.this,
                                HomeActivity.class
                        )
                );

                overridePendingTransition(0, 0);
                finish();

                return true;

            } else if (id == R.id.nav_dev) {

                startActivity(
                        new Intent(
                                ProfileActivity.this,
                                DeveloperInfoActivity.class
                        )
                );

                overridePendingTransition(0, 0);
                finish();

                return true;
            }

            return id == R.id.nav_profile;
        });
    }
}
