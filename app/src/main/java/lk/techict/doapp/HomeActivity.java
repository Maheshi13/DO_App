package lk.techict.doapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
    FloatingActionButton fab;
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // --- STEP 1: INITIALIZE ALL VIEWS FIRST (Crucial fix) ---
        tvDate = findViewById(R.id.tvDate);
        fab = findViewById(R.id.fabAddTask);
        bottomNav = findViewById(R.id.bottomNavigation);
        recyclerView = findViewById(R.id.rvTasks);
        DB = new DBHelper(this);
        consolidatedList = new ArrayList<>();

        // --- STEP 2: SETUP RECYCLERVIEW ---
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(consolidatedList);
        recyclerView.setAdapter(adapter);

        // --- STEP 3: SET THE DATE HEADER ---
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd", Locale.getDefault());
        String currentDate = "- " + sdf.format(Calendar.getInstance().getTime());
        tvDate.setText(currentDate);

        // --- STEP 4: NAVIGATION LOGIC (Only once, after initialization) ---
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_dev) {
                startActivity(new Intent(this, DeveloperInfoActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return id == R.id.nav_home;
        });

        // --- STEP 5: FAB LOGIC ---
        fab.setOnClickListener(v -> {
            startActivity(new Intent(this, AddTaskActivity.class));
        });

        // --- STEP 6: LOAD DATA ---
        loadTasksFromDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasksFromDatabase();
    }

    private void loadTasksFromDatabase() {
        try {
            Cursor cursor = DB.getTasks();
            consolidatedList.clear();
            String lastDate = "";

            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd", Locale.getDefault());
            String today = sdf.format(Calendar.getInstance().getTime());

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    // Check column indices. If your DBHelper is standard:
                    // 0=ID, 1=Name, 2=Desc, 3=Date, 4=Time
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
                    consolidatedList.add(new TaskModel(taskName, taskDesc, taskDate, taskTime));
                }
                cursor.close();
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading tasks", Toast.LENGTH_SHORT).show();
        }
    }
}