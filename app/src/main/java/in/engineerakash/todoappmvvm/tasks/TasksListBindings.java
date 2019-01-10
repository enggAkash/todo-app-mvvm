package in.engineerakash.todoappmvvm.tasks;

import android.databinding.BindingAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

import in.engineerakash.todoappmvvm.data.Task;

/**
 * Contains {@link BindingAdapter}s for the {@link Task} list.
 */
public class TasksListBindings {

    @SuppressWarnings("unchecked")
    @BindingAdapter("app:items")
    public static void setItems(ListView listView, List<Task> items) {
        TasksFragment.TasksAdapter adapter = (TasksFragment.TasksAdapter) listView.getAdapter();
        if (adapter != null)
            adapter.replaceData(items);
    }

}
