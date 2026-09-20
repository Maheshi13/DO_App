package lk.techict.doapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DBNAME = "Login.db";
    private static final int DATABASE_VERSION = 4;

    public DBHelper(Context context) {
        super(context, DBNAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase MyDB) {

        MyDB.execSQL(
                "CREATE TABLE users (" +
                        "username TEXT PRIMARY KEY, " +
                        "email TEXT, " +
                        "password TEXT)"
        );

        MyDB.execSQL(
                "CREATE TABLE tasks (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT, " +
                        "description TEXT, " +
                        "date TEXT, " +
                        "time TEXT, " +
                        "owner TEXT)"
        );
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase MyDB,
            int oldVersion,
            int newVersion
    ) {

        // Add the owner column without deleting existing data
        if (oldVersion < 4) {
            MyDB.execSQL("ALTER TABLE tasks ADD COLUMN owner TEXT");
        }
    }

    // Register a new user
    public Boolean insertData(
            String username,
            String email,
            String password
    ) {

        SQLiteDatabase MyDB = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put("username", username);
        contentValues.put("email", email);
        contentValues.put("password", password);

        long result = MyDB.insert("users", null, contentValues);

        return result != -1;
    }

    // Update user information
    public boolean updateUserData(
            String oldUsername,
            String newUsername,
            String newEmail
    ) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues userValues = new ContentValues();
        userValues.put("username", newUsername);
        userValues.put("email", newEmail);

        long result = db.update(
                "users",
                userValues,
                "username = ?",
                new String[]{oldUsername}
        );

        if (result > 0) {

            // Update the owner of the user's existing tasks
            ContentValues taskValues = new ContentValues();
            taskValues.put("owner", newUsername);

            db.update(
                    "tasks",
                    taskValues,
                    "owner = ?",
                    new String[]{oldUsername}
            );

            return true;
        }

        return false;
    }

    // Get a specific user's profile
    public Cursor getUserData(String username) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT username, email FROM users WHERE username = ?",
                new String[]{username}
        );
    }

    public Boolean checkUsername(String username) {

        SQLiteDatabase MyDB = this.getReadableDatabase();

        Cursor cursor = MyDB.rawQuery(
                "SELECT * FROM users WHERE username = ?",
                new String[]{username}
        );

        boolean exists = cursor.getCount() > 0;

        cursor.close();

        return exists;
    }

    public Boolean checkUserPassword(
            String username,
            String password
    ) {

        SQLiteDatabase MyDB = this.getReadableDatabase();

        Cursor cursor = MyDB.rawQuery(
                "SELECT * FROM users WHERE username = ? AND password = ?",
                new String[]{username, password}
        );

        boolean valid = cursor.getCount() > 0;

        cursor.close();

        return valid;
    }

    // Insert a task belonging to a specific user
    public Boolean insertTask(
            String owner,
            String name,
            String desc,
            String date,
            String time
    ) {

        SQLiteDatabase MyDB = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("owner", owner);
        cv.put("name", name);
        cv.put("description", desc);
        cv.put("date", date);
        cv.put("time", time);

        long result = MyDB.insert("tasks", null, cv);

        return result != -1;
    }

    // Get only the logged-in user's tasks
    public Cursor getTasks(String owner) {

        SQLiteDatabase MyDB = this.getReadableDatabase();

        return MyDB.rawQuery(
                "SELECT id, name, description, date, time " +
                        "FROM tasks WHERE owner = ? ORDER BY id ASC",
                new String[]{owner}
        );
    }
}
