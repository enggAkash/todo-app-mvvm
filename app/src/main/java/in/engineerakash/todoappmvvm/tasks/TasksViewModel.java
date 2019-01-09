package in.engineerakash.todoappmvvm.tasks;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

import in.engineerakash.todoappmvvm.BR;
import in.engineerakash.todoappmvvm.R;
import in.engineerakash.todoappmvvm.addedittask.AddEditTaskActivity;
import in.engineerakash.todoappmvvm.data.Task;
import in.engineerakash.todoappmvvm.data.source.TasksDataSource;
import in.engineerakash.todoappmvvm.data.source.TasksRepository;
import in.engineerakash.todoappmvvm.taskdetail.TaskDetailActivity;

/**
 * Exposes the data to be used in the task list screen
 * <p>
 * {@link BaseObservable} implements a listener registration mechanism which is notified when
 * property changes. This is done by assigning a {@link Bindable} annotation to the property's
 * getter method.
 */
public class TasksViewModel extends BaseObservable {

    // These observable will update the views automatically
    public final ObservableList<Task> items = new ObservableArrayList<>();

    public final ObservableBoolean dataLoading = new ObservableBoolean(false);

    public final ObservableField<String> currentFilteringLabel = new ObservableField<>();

    public final ObservableField<String> noTasksLabel = new ObservableField<>();

    public final ObservableField<Drawable> noTaskIconRes = new ObservableField<>();

    public final ObservableBoolean tasksAddViewVisible = new ObservableBoolean();

    final ObservableField<String> snackBarText = new ObservableField<>();

    private TasksFilterType mCurrentFiltering = TasksFilterType.ALL_TASKS;

    private final TasksRepository mTasksRepository;

    private final ObservableBoolean mIsDataLoadingError = new ObservableBoolean(false);

    private Context mContext;    // To avoid leaks, this must be application context

    private TasksNavigator mNavigator;

    public TasksViewModel(TasksRepository tasksRepository, Context context) {
        this.mTasksRepository = tasksRepository;
        this.mContext = context.getApplicationContext();    // Force use of Application Context

        // Set initial state
        setFiltering(TasksFilterType.ALL_TASKS);
    }

    void setNavigator(TasksNavigator navigator) {
        mNavigator = navigator;
    }

    void onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
        mNavigator = null;
    }

    public void start() {
        loadTasks(false);
    }

    @Bindable
    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void loadTasks(boolean forceUpdate) {
        loadTasks(forceUpdate, true);
    }


    /**
     * Sets the current task filtering type.
     *
     * @param requestType Can be {@link TasksFilterType#ALL_TASKS},
     *                    {@link TasksFilterType#ACTIVE_TASKS}, or
     *                    {@link TasksFilterType#COMPLETE_TASKS}
     */
    private void setFiltering(TasksFilterType requestType) {
        mCurrentFiltering = requestType;

        //Depending on the request type, set the label, icon drawable etc
        switch (requestType) {
            case ALL_TASKS:
                currentFilteringLabel.set(mContext.getString(R.string.label_all));
                noTasksLabel.set(mContext.getString(R.string.no_tasks_all));
                noTaskIconRes.set(mContext.getResources().getDrawable(R.drawable.ic_assignment_turned_in_24dp));
                tasksAddViewVisible.set(true);
                break;
            case ACTIVE_TASKS:
                currentFilteringLabel.set(mContext.getString(R.string.label_all));
                noTasksLabel.set(mContext.getString(R.string.no_tasks_active));
                noTaskIconRes.set(mContext.getResources().getDrawable(R.drawable.ic_check_circle_24dp));
                tasksAddViewVisible.set(false);
                break;
            case COMPLETE_TASKS:
                currentFilteringLabel.set(mContext.getString(R.string.label_completed));
                noTasksLabel.set(mContext.getString(R.string.no_tasks_completed));
                noTaskIconRes.set(mContext.getResources().getDrawable(R.drawable.ic_verified_user_24dp));
                tasksAddViewVisible.set(false);
                break;
        }
    }

    public void clearCompletedTasks() {
        mTasksRepository.cleatCompletedTasks();
        snackBarText.set(mContext.getString(R.string.completed_tasks_cleared));
        loadTasks(false, false);
    }

    public String getSnackBarTet() {
        return snackBarText.get();
    }

    public void addNewTask() {
        if (mNavigator != null)
            mNavigator.addNewTask();
    }

    void handleActivityResult(int requestCode, int resultCode) {
        if (AddEditTaskActivity.REQUEST_CODE == requestCode) {
            switch (resultCode) {
                case TaskDetailActivity.EDIT_RESULT_OK:
                    snackBarText.set(mContext.getString(R.string.successfully_saved_task_message));
                    break;
                case AddEditTaskActivity.ADD_EDIT_RESULT_OK:
                    snackBarText.set(mContext.getString(R.string.successfully_added_task_message));
                    break;
                case TaskDetailActivity.DELETE_RESULT_OK:
                    snackBarText.set(mContext.getString(R.string.successfully_deleted_task_message));
                    break;
            }
        }
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link TasksDataSource}
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private void loadTasks(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI)
            dataLoading.set(true);

        if (forceUpdate)
            mTasksRepository.refreshTasks();


        mTasksRepository.getTasks(new TasksDataSource.LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                List<Task> tasksToShow = new ArrayList<>();

                // This callback may be called twice, once for the cache and once for the
                // loading the data from server API

                // We filter the tasks based on the requestType
                for (Task task : tasks) {
                    switch (mCurrentFiltering) {
                        case ALL_TASKS:
                            tasksToShow.add(task);
                            break;
                        case ACTIVE_TASKS:
                            if (task.isActive()) {
                                tasksToShow.add(task);
                            }
                            break;
                        case COMPLETE_TASKS:
                            if (task.isCompleted()) {
                                tasksToShow.add(task);
                            }
                            break;
                        default:
                            tasksToShow.add(task);
                            break;
                    }
                }

                if (showLoadingUI)
                    dataLoading.set(false);

                mIsDataLoadingError.set(false);

                items.clear();
                items.addAll(tasksToShow);
                notifyPropertyChanged(BR.empty);    //  It's a @Bindable so update manually
            }

            @Override
            public void onDataNotAvailable() {
                mIsDataLoadingError.set(true);
            }
        });

    }
}
