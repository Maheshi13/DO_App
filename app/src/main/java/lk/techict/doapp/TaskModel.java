package lk.techict.doapp;

public class TaskModel {
    private String name;
    private String desc;
    private String date;
    private String time;

    // Constructor to initialize the data
    public TaskModel(String name, String desc, String date, String time) {
        this.name = name;
        this.desc = desc;
        this.date = date;
        this.time = time;
    }

    // Getters so the Adapter can read the values
    public String getName() { return name; }
    public String getDesc() { return desc; }
    public String getDate() { return date; }
    public String getTime() { return time; }
}