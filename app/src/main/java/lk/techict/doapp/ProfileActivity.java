package lk.techict.doapp;

import android.app.Dialog;
import android.content.Intent;
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

        // 2. Load and display data from database
        displayUserData();

        // 3. Setup Bottom Navigation logic
        setupNavigation();

        // 4. Trigger the custom Sign Out Dialog
        btnSignOut.setOnClickListener(v -> showSignOutDialog());

        // 5. Trigger the Edit Profile Dialog
        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
    }

    private void showEditProfileDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_edit_profile);

        // Make dialog background transparent to respect rounded corners
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        EditText etEditUsername = dialog.findViewById(R.id.etEditUsername);
        EditText etEditEmail = dialog.findViewById(R.id.etEditEmail);
        MaterialButton btnCancel = dialog.findViewById(R.id.btnCancelEdit);
        MaterialButton btnSave = dialog.findViewById(R.id.btnSaveChanges);

        // Pre-fill with existing data
        String currentName = etUsername.getText().toString();
        String currentEmail = etEmail.getText().toString();
        etEditUsername.setText(currentName);
        etEditEmail.setText(currentEmail);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String newName = etEditUsername.getText().toString().trim();
            String newEmail = etEditEmail.getText().toString().trim();

            if (newName.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Update the user record in SQLite
                boolean isUpdated = DB.updateUserData(currentName, newName, newEmail);
                if (isUpdated) {
                    Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                    displayUserData(); // Refresh the main screen fields
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private void showSignOutDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_signout);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Using MaterialButton to support custom attributes like stroke and icons
        MaterialButton btnCancel = dialog.findViewById(R.id.btnCancel);
        MaterialButton btnConfirmSignOut = dialog.findViewById(R.id.btnConfirmSignOut);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirmSignOut.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            // Clear activity stack so user can't go back after signing out
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        dialog.show();
    }

    private void setupNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_profile);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_dev) {
                // Navigate to the newly created Developer Info screen
                startActivity(new Intent(this, DeveloperInfoActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return id == R.id.nav_profile;
        });
    }

    private void displayUserData() {
        Cursor cursor = DB.getUserData();
        if (cursor != null && cursor.moveToFirst()) {
            try {
                // Column 0: Username, Column 1: Email
                etUsername.setText(cursor.getString(0));
                etEmail.setText(cursor.getString(1));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        } else {
            Toast.makeText(this, "No profile found.", Toast.LENGTH_LONG).show();
        }
    }
}