package lk.techict.doapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_TASK = 1;
    private ArrayList<Object> itemList; // This list contains both Strings and TaskModels

    public TaskAdapter(ArrayList<Object> itemList) {
        this.itemList = itemList;
    }

    @Override
    public int getItemViewType(int position) {
        // If the item is a String, it's a Date Header. Otherwise, it's a Task.
        if (itemList.get(position) instanceof String) {
            return TYPE_HEADER;
        }
        return TYPE_TASK;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
            return new TaskViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.tvDateHeader.setText((String) itemList.get(position));
        } else {
            TaskModel task = (TaskModel) itemList.get(position);
            TaskViewHolder taskHolder = (TaskViewHolder) holder;

            taskHolder.tvDate.setText(task.getDate());
            taskHolder.tvTime.setText(task.getTime());
            taskHolder.tvName.setText(task.getName());
            taskHolder.tvDesc.setText(task.getDesc());
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // ViewHolder for the Date Labels (e.g., March 25)
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvDateHeader;
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateHeader = itemView.findViewById(R.id.tvDateHeader);
        }
    }

    // ViewHolder for the Task Cards
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc, tvDate, tvTime;
        CheckBox cbDone;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvShowName);
            tvDesc = itemView.findViewById(R.id.tvShowDesc);
            tvDate = itemView.findViewById(R.id.tvShowDate);
            tvTime = itemView.findViewById(R.id.tvShowTime);
            cbDone = itemView.findViewById(R.id.cbDone);
        }
    }
}