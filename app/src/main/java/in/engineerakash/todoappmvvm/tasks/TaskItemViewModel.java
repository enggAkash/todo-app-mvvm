package in.engineerakash.todoappmvvm.tasks;

import android.content.Context;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

import in.engineerakash.todoappmvvm.TaskViewModel;
import in.engineerakash.todoappmvvm.data.source.TasksRepository;

/**
 * Listens to user action from the list item in ({@link TasksFragment}) and redirects them to
 * the Fragment's actions listener.
 */
public class TaskItemViewModel extends TaskViewModel {

    // This navigator is wrapper in a WeakReference to avoid leaks because it has references to
    // an activity.There is no StraightForward way to clear it for each item in a list adapter.
    @Nullable
    private WeakReference<TaskItemNavigator> mNavigator;

    public TaskItemViewModel(Context context, TasksRepository tasksRepository) {
        super(context, tasksRepository);
    }

    public void setNavigator(@Nullable TaskItemNavigator navigator) {
        mNavigator = new WeakReference<>(navigator);
    }

    /**
     * Called by the Data Binding library when the row is clicked.
     */
    public void taskClicked() {
        String taskId = getTaskId();
        if (taskId == null) {
            // Clicked happened before task was loaded, no-op.
            return;
        }
        if (mNavigator != null && mNavigator.get() != null)
            mNavigator.get().openTaskDetails(taskId);
    }

}
