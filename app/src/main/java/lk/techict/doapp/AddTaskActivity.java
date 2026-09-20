package lk.techict.doapp;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {

    EditText name, desc, day, month, year, time;
    Button createBtn;
    ImageView btnBack;
    BottomNavigationView bottomNav;
    DBHelper DB;

    String loggedInUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // 1. Initialize views
        name = findViewById(R.id.etTaskName);
        desc = findViewById(R.id.etDescription);
        day = findViewById(R.id.etDay);
        month = findViewById(R.id.etMonth);
        year = findViewById(R.id.etYear);
        time = findViewById(R.id.etTime);
        createBtn = findViewById(R.id.btnCreateTask);
        btnBack = findViewById(R.id.btnBack);
        bottomNav = findViewById(R.id.bottomNavigation);

        DB = new DBHelper(this);

        // 2. Get the currently logged-in username
        SharedPreferences preferences =
                getSharedPreferences("UserSession", MODE_PRIVATE);

        loggedInUsername = preferences.getString("username", null);

        if (loggedInUsername == null) {

            Toast.makeText(
                    this,
                    "Please sign in again",
                    Toast.LENGTH_SHORT
            ).show();

            startActivity(new Intent(this, LoginActivity.class));
            finish();

            return;
        }

        // 3. Fill today's date
        Calendar today = Calendar.getInstance();

        day.setText(String.format(
                Locale.getDefault(),
                "%02d",
                today.get(Calendar.DAY_OF_MONTH)
        ));

        month.setText(String.format(
                Locale.getDefault(),
                "%02d",
                today.get(Calendar.MONTH) + 1
        ));

        year.setText(String.valueOf(
                today.get(Calendar.YEAR)
        ));

        // 4. Fill current time
        SimpleDateFormat timeFormat =
                new SimpleDateFormat("hh:mm a", Locale.getDefault());

        time.setText(timeFormat.format(today.getTime()));

        // 5. Time picker
        time.setFocusable(false);
        time.setClickable(true);

        time.setOnClickListener(v -> {

            Calendar selectedTime = Calendar.getInstance();

            TimePickerDialog timePickerDialog =
                    new TimePickerDialog(
                            AddTaskActivity.this,
                            (view, hourOfDay, minute) -> {

                                Calendar selected = Calendar.getInstance();

                                selected.set(
                                        Calendar.HOUR_OF_DAY,
                                        hourOfDay
                                );

                                selected.set(
                                        Calendar.MINUTE,
                                        minute
                                );

                                String formattedTime =
                                        new SimpleDateFormat(
                                                "hh:mm a",
                                                Locale.getDefault()
                                        ).format(selected.getTime());

                                time.setText(formattedTime);
                            },
                            selectedTime.get(Calendar.HOUR_OF_DAY),
                            selectedTime.get(Calendar.MINUTE),
                            false
                    );

            timePickerDialog.show();
        });

        // 6. Back button
        btnBack.setOnClickListener(v -> finish());

        // 7. Bottom navigation
        bottomNav.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {

                finish();
                return true;

            } else if (id == R.id.nav_profile) {

                startActivity(new Intent(this, ProfileActivity.class));
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

            return false;
        });

        // 8. Create task
        createBtn.setOnClickListener(v -> {

            String taskName = name.getText().toString().trim();
            String taskDesc = desc.getText().toString().trim();
            String d = day.getText().toString().trim();
            String m = month.getText().toString().trim();
            String y = year.getText().toString().trim();
            String taskTime = time.getText().toString().trim();

            if (taskName.isEmpty() ||
                    taskTime.isEmpty() ||
                    d.isEmpty() ||
                    m.isEmpty() ||
                    y.isEmpty()) {

                Toast.makeText(
                        this,
                        "Please fill in all required fields",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            try {

                int dayInt = Integer.parseInt(d);
                int monthInt = Integer.parseInt(m);
                int yearInt = Integer.parseInt(y);

                if (d.length() != 2 || m.length() != 2) {

                    Toast.makeText(
                            this,
                            "Day and Month must contain 2 digits",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                if (y.length() != 4) {

                    Toast.makeText(
                            this,
                            "Year must contain 4 digits",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                // Validate date
                Calendar calendar = Calendar.getInstance();
                calendar.clear();
                calendar.setLenient(false);
                calendar.set(yearInt, monthInt - 1, dayInt);

                calendar.getTime();

                // Prevent past dates
                Calendar todayDate = Calendar.getInstance();

                todayDate.set(Calendar.HOUR_OF_DAY, 0);
                todayDate.set(Calendar.MINUTE, 0);
                todayDate.set(Calendar.SECOND, 0);
                todayDate.set(Calendar.MILLISECOND, 0);

                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                if (calendar.before(todayDate)) {

                    Toast.makeText(
                            this,
                            "You cannot add a task for a past date",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                String[] monthNames = {
                        "January", "February", "March",
                        "April", "May", "June",
                        "July", "August", "September",
                        "October", "November", "December"
                };

                String monthWord = monthNames[monthInt - 1];

                String taskDate = monthWord + " " + d;

                // 9. Save the task with the logged-in username
                boolean inserted = DB.insertTask(
                        loggedInUsername,
                        taskName,
                        taskDesc,
                        taskDate,
                        taskTime
                );

                if (inserted) {

                    Toast.makeText(
                            this,
                            "Task Added Successfully!",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();

                } else {

                    Toast.makeText(
                            this,
                            "Database Error",
                            Toast.LENGTH_SHORT
                    ).show();
                }

            } catch (NumberFormatException e) {

                Toast.makeText(
                        this,
                        "Day, Month and Year must be numbers",
                        Toast.LENGTH_SHORT
                ).show();

            } catch (IllegalArgumentException e) {

                Toast.makeText(
                        this,
                        "Please enter a valid date",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}
