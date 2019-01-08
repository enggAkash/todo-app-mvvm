package in.engineerakash.todoappmvvm;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;

import in.engineerakash.todoappmvvm.data.Task;
import in.engineerakash.todoappmvvm.data.source.TasksDataSource;

/**
 * Abstract class for View Models that expose a single {@link Task}.
 */
public abstract class TaskViewModel extends BaseObservable implements TasksDataSource.GetTaskCallback {

    public final ObservableField<String> snackBarText = new ObservableField<>();

    public final ObservableField<String> title = new ObservableField<>();

    public final ObservableField<String> description = new ObservableField<>();

    public final ObservableField<Task> mTaskObservable = new ObservableField<>();



}
