package lk.techict.doapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AddTaskActivity extends AppCompatActivity {
    EditText name, desc, day, month, year, time;
    Button createBtn;
    ImageView btnBack;
    BottomNavigationView bottomNav; // Added this
    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // 1. Initialize Views
        name = findViewById(R.id.etTaskName);
        desc = findViewById(R.id.etDescription);
        day = findViewById(R.id.etDay);
        month = findViewById(R.id.etMonth);
        year = findViewById(R.id.etYear);
        time = findViewById(R.id.etTime);
        createBtn = findViewById(R.id.btnCreateTask);
        btnBack = findViewById(R.id.btnBack);
        bottomNav = findViewById(R.id.bottomNavigation); // Initialize Bottom Nav

        DB = new DBHelper(this);

        // 2. Back button logic
        btnBack.setOnClickListener(v -> finish());

        // 3. BOTTOM NAVIGATION LOGIC (The missing part)
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // If they click home, just finish this activity to go back
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_dev) {
                startActivity(new Intent(this, DeveloperInfoActivity.class));
                finish();
                return true;
            }
            return false;
        });

        // 4. Create Task Logic
        createBtn.setOnClickListener(v -> {
            String taskName = name.getText().toString().trim();
            String taskDesc = desc.getText().toString().trim();
            String d = day.getText().toString().trim();
            String m = month.getText().toString().trim();
            String taskTime = time.getText().toString().trim();

            if(taskName.isEmpty() || taskTime.isEmpty() || d.isEmpty() || m.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    int monthInt = Integer.parseInt(m);
                    String[] monthNames = {"January", "February", "March", "April", "May", "June",
                            "July", "August", "September", "October", "November", "December"};

                    if (monthInt >= 1 && monthInt <= 12) {
                        String monthWord = monthNames[monthInt - 1];
                        String taskDate = monthWord + " " + d;

                        boolean inserted = DB.insertTask(taskName, taskDesc, taskDate, taskTime);
                        if(inserted) {
                            Toast.makeText(this, "Task Added Successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Database Error", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Enter a valid month (1-12)", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Month and Day must be numbers", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}



