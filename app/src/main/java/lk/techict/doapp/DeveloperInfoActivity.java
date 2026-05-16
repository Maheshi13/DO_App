package lk.techict.doapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DeveloperInfoActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;
    Button btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_info);

        // Initialize components
        bottomNav = findViewById(R.id.bottomNavigation);
        btnExit = findViewById(R.id.btnDevExit);

        // Highlight the current "Info" tab
        bottomNav.setSelectedItemId(R.id.nav_dev);

        // Exit button logic
        btnExit.setOnClickListener(v -> {
            // Goes back to the previous screen (Home or Profile)
            finish();
        });

        // Bottom Navigation Logic
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return id == R.id.nav_dev;
        });
    }
}