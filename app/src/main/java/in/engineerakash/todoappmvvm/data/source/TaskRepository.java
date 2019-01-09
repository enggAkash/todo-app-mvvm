package in.engineerakash.todoappmvvm.data.source;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import in.engineerakash.todoappmvvm.data.Task;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Concrete implementation to load tasks from the data sources into a cache.
 * <p>
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 */
public class TaskRepository implements TasksDataSource {

    private static TaskRepository INSTANCE = null;

    private TasksDataSource mTasksRemoteDataSource;

    private TasksDataSource mTasksLocalDataSource;

    Map<String, Task> mCachedTasks;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested.
     */
    boolean mCacheIsDirty = false;

    // prevent direct instantiation
    private TaskRepository(@NonNull TasksDataSource tasksRemoteDataSource, @NonNull TasksDataSource tasksLocalDataSource) {
        this.mTasksRemoteDataSource = checkNotNull(mTasksRemoteDataSource);
        this.mTasksLocalDataSource = checkNotNull(mTasksLocalDataSource);
    }

    /**
     * Returns single instance of this class, creating if necessary.
     *
     * @param tasksRemoteDataSource the backend(remote) data source
     * @param tasksLocalDataSource  the device storage data source
     * @return the {@link TaskRepository} instance
     */
    public static TaskRepository getInstance(TasksDataSource tasksRemoteDataSource, TasksDataSource tasksLocalDataSource) {
        if (INSTANCE == null)
            INSTANCE = new TaskRepository(tasksRemoteDataSource, tasksLocalDataSource);
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(TasksDataSource, TasksDataSource)} to create new instance
     * next time it's called.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    /**
     * Get tasks from cache, local data source (SQLite) or remote data source, whichever is
     * available first.
     * <p>
     * Note: {@link LoadTasksCallback#onDataNotAvailable()} is fired if all data sources fail
     * to get data.
     */
    @Override
    public void getTasks(@NonNull final LoadTasksCallback callback) {
        checkNotNull(callback);

        // Respond immediately with cache if available and not dirty
        if (mCachedTasks != null && !mCacheIsDirty) {
            callback.onTasksLoaded(new ArrayList<>(mCachedTasks.values()));
            return;
        }

        if (mCacheIsDirty) {
            // If cache is dirty, we need to fetch new data from the network
            getTasksFromRemoteDataSource(callback);

        } else {
            // Query the local storage if available. It not, query the network
            mTasksLocalDataSource.getTasks(new LoadTasksCallback() {
                @Override
                public void onTasksLoaded(List<Task> tasks) {
                    refreshCache(tasks);
                    callback.onTasksLoaded(tasks);
                }

                @Override
                public void onDataNotAvailable() {
                    getTasksFromRemoteDataSource(callback);
                }
            });
        }

    }

    @Override
    public void getTask(@NonNull final String taskId, @NonNull final GetTaskCallback callback) {
        checkNotNull(taskId);
        checkNotNull(callback);

        final Task cachedTask = getTaskWithId(taskId);

        // Respond immediately with cache if available
        if (cachedTask != null) {
            callback.onTaskLoaded(cachedTask);
            return;
        }

        // Load from server/persisted if needed.

        // Is the task is in local data source? It not, query the network
        mTasksLocalDataSource.getTask(taskId, new GetTaskCallback() {

            @Override
            public void onTaskLoaded(Task task) {
                // Do in memory cache update to keep the app UI up to date
                if (mCachedTasks == null) {
                    mCachedTasks = new LinkedHashMap<>();
                }
                mCachedTasks.put(task.getId(), task);
                callback.onTaskLoaded(task);
            }

            @Override
            public void onDataNotAvailable() {
                mTasksRemoteDataSource.getTask(taskId, new GetTaskCallback() {
                    @Override
                    public void onTaskLoaded(Task task) {
                        // Do in memory caching to keep the app ui up to date
                        if (mCachedTasks == null)
                            mCachedTasks = new LinkedHashMap<>();
                        mCachedTasks.put(task.getId(), task);
                        callback.onTaskLoaded(task);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        callback.onDataNotAvailable();
                    }
                });
            }
        });

    }

    @Override
    public void saveTask(@NonNull Task task) {
        checkNotNull(task);

        mTasksRemoteDataSource.saveTask(task);
        mTasksLocalDataSource.saveTask(task);

        // Do in memory cache to keep the app UI up to date
        if (mCachedTasks == null)
            mCachedTasks = new LinkedHashMap<>();

        mCachedTasks.put(task.getId(), task);
    }

    @Override
    public void completeTask(@NonNull Task task) {
        checkNotNull(task);

        mTasksRemoteDataSource.completeTask(task);
        mTasksLocalDataSource.completeTask(task);

        Task completedTask = new Task(task.getTitle(), task.getDescription(), task.getId(), true);

        // Do in memory cache to keep the app UI up to date
        if (mCachedTasks == null)
            mCachedTasks = new LinkedHashMap<>();

        mCachedTasks.put(task.getId(), completedTask);
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        checkNotNull(taskId);
        completeTask(getTaskWithId(taskId));
    }

    @Override
    public void activateTask(@NonNull Task task) {
        checkNotNull(task);

        mTasksRemoteDataSource.activateTask(task);
        mTasksLocalDataSource.activateTask(task);

        Task activeTask = new Task(task.getTitle(), task.getDescription(), task.getId());

        // Do in memory cache to keep the app UI up to date
        if (mCachedTasks == null)
            mCachedTasks = new LinkedHashMap<>();
        mCachedTasks.put(task.getId(), activeTask);
    }

    @Override
    public void activateTask(@NonNull String taskId) {
        checkNotNull(taskId);
        activateTask(getTaskWithId(taskId));
    }

    @Override
    public void cleatCompletedTasks() {
        mTasksRemoteDataSource.cleatCompletedTasks();
        mTasksLocalDataSource.cleatCompletedTasks();

        // Do in memory cache to keep the app UI up to date
        if (mCachedTasks == null)
            mCachedTasks = new LinkedHashMap<>();

        Iterator<Map.Entry<String, Task>> iterator = mCachedTasks.entrySet().iterator();
        if (iterator.hasNext()) {
            Map.Entry<String, Task> entry = iterator.next();
            if (entry.getValue().isCompleted())
                iterator.remove();
        }
    }

    @Override
    public void refreshTasks() {
        mCacheIsDirty = true;
    }

    @Override
    public void deleteAllTasks() {
        mTasksRemoteDataSource.deleteAllTasks();
        mTasksLocalDataSource.deleteAllTasks();

        if (mCachedTasks == null)
            mCachedTasks = new LinkedHashMap<>();
        mCachedTasks.clear();
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        checkNotNull(taskId);

        mTasksRemoteDataSource.deleteTask(taskId);
        mTasksLocalDataSource.deleteTask(taskId);

        mCachedTasks.remove(taskId);
    }

    private void getTasksFromRemoteDataSource(@NonNull final LoadTasksCallback callback) {
        mTasksRemoteDataSource.getTasks(new LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                refreshCache(tasks);
                refreshLocalDataSource(tasks);
                callback.onTasksLoaded(tasks);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshCache(List<Task> tasks) {
        if (mCachedTasks == null)
            mCachedTasks = new LinkedHashMap<>();
        mCachedTasks.clear();
        for (Task task : tasks) {
            mCachedTasks.put(task.getId(), task);
        }
        mCacheIsDirty = false;
    }

    private void refreshLocalDataSource(List<Task> tasks) {
        mTasksLocalDataSource.deleteAllTasks();
        for (Task task : tasks) {
            mTasksLocalDataSource.saveTask(task);
        }
    }

    private Task getTaskWithId(String taskId) {
        checkNotNull(taskId);
        if (mCachedTasks == null || mCachedTasks.isEmpty())
            return null;
        else
            return mCachedTasks.get(taskId);
    }

}
