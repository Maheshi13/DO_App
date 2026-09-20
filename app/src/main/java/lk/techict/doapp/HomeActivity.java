package lk.techict.doapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TaskAdapter adapter;
    DBHelper DB;
    ArrayList<Object> consolidatedList;
    TextView tvDate;
    ImageView ivProfile;
    FloatingActionButton fab;
    BottomNavigationView bottomNav;

    String loggedInUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 1. Initialize views
        tvDate = findViewById(R.id.tvDate);
        ivProfile = findViewById(R.id.ivProfile);
        fab = findViewById(R.id.fabAddTask);
        bottomNav = findViewById(R.id.bottomNavigation);
        recyclerView = findViewById(R.id.rvTasks);

        DB = new DBHelper(this);
        consolidatedList = new ArrayList<>();

        // 2. Get logged-in username
        SharedPreferences preferences =
                getSharedPreferences("UserSession", MODE_PRIVATE);

        loggedInUsername = preferences.getString("username", null);

        if (loggedInUsername == null) {

            startActivity(new Intent(this, LoginActivity.class));
            finish();

            return;
        }

        // 3. Setup RecyclerView
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new TaskAdapter(consolidatedList);
        recyclerView.setAdapter(adapter);

        // 4. Set today's date
        SimpleDateFormat sdf =
                new SimpleDateFormat("MMMM dd", Locale.getDefault());

        String currentDate =
                "- " + sdf.format(Calendar.getInstance().getTime());

        tvDate.setText(currentDate);

        // 5. Profile icon navigation
        ivProfile.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeActivity.this,
                    ProfileActivity.class
            );

            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        // 6. Bottom navigation
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_profile) {

                startActivity(
                        new Intent(this, ProfileActivity.class)
                );

                overridePendingTransition(0, 0);
                finish();

                return true;

            } else if (id == R.id.nav_dev) {

                startActivity(
                        new Intent(this, DeveloperInfoActivity.class)
                );

                overridePendingTransition(0, 0);
                finish();

                return true;
            }

            return id == R.id.nav_home;
        });

        // 7. Add task button
        fab.setOnClickListener(v -> {

            startActivity(
                    new Intent(HomeActivity.this, AddTaskActivity.class)
            );
        });

        // 8. Load tasks
        loadTasksFromDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (DB != null && loggedInUsername != null) {
            loadTasksFromDatabase();
        }
    }

    private void loadTasksFromDatabase() {

        try {

            // Get only this user's tasks
            Cursor cursor = DB.getTasks(loggedInUsername);

            consolidatedList.clear();

            String lastDate = "";

            SimpleDateFormat sdf =
                    new SimpleDateFormat("MMMM dd", Locale.getDefault());

            String today =
                    sdf.format(Calendar.getInstance().getTime());

            if (cursor != null) {

                while (cursor.moveToNext()) {

                    // Returned columns:
                    // 0 = ID
                    // 1 = Name
                    // 2 = Description
                    // 3 = Date
                    // 4 = Time

                    String taskName = cursor.getString(1);
                    String taskDesc = cursor.getString(2);
                    String taskDate = cursor.getString(3);
                    String taskTime = cursor.getString(4);

                    if (!taskDate.equals(lastDate)) {

                        if (!taskDate.equals(today)) {
                            consolidatedList.add(taskDate);
                        }

                        lastDate = taskDate;
                    }

                    consolidatedList.add(
                            new TaskModel(
                                    taskName,
                                    taskDesc,
                                    taskDate,
                                    taskTime
                            )
                    );
                }

                cursor.close();
            }

            adapter.notifyDataSetChanged();

        } catch (Exception e) {

            e.printStackTrace();

            Toast.makeText(
                    this,
                    "Error loading tasks",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
