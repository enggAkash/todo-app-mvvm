package in.engineerakash.todoappmvvm.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import java.util.UUID;

/**
 * Immutable model class for Task.
 */
@Entity(tableName = "tasks")
public class Task {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "entryid")
    private final String mId;

    @Nullable
    @ColumnInfo(name = "title")
    private final String mTitle;

    @Nullable
    @ColumnInfo(name = "description")
    private final String mDescription;

    @ColumnInfo(name = "completed")
    private final boolean mCompleted;

    /**
     * Use this constructor to create new active Task
     *
     * @param title       title of the task
     * @param description description of the task
     */
    public Task(String title, String description) {
        this(title, description, UUID.randomUUID().toString(), false);
    }

    /**
     * Use this constructor to create an active Task if the task already has an id (copy of
     * another Task)
     *
     * @param title       title of the task
     * @param description description of the task
     * @param id          id of the task
     */
    public Task(String title, String description, String id) {
        this(title, description, id, false);
    }

    /**
     * Use this constructor to create new completed Task
     *
     * @param title       title of the task
     * @param description description of the task
     * @param completed   true if the task is complete, false if it's active
     */
    public Task(String title, String description, boolean completed) {
        this(title, description, UUID.randomUUID().toString(), completed);
    }

    /**
     * Use this constructor to specify a completed Task if the task already has an id ( copy of
     * another task)
     *
     * @param title       title of the task
     * @param description description of the task
     * @param id          id of the task
     * @param completed   tru if task is completed, false if task is active
     */
    public Task(@Nullable String title, @Nullable String description, @NonNull String id, boolean completed) {
        mId = id;
        mTitle = title;
        mDescription = description;
        mCompleted = completed;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @Nullable
    public String getTitle() {
        return mTitle;
    }

    @Nullable
    public String getTitleForList() {
        if (!Strings.isNullOrEmpty(mTitle))
            return mTitle;
        else
            return mDescription;
    }

    @Nullable
    public String getDescription() {
        return mDescription;
    }

    public boolean isCompleted() {
        return mCompleted;
    }

    public boolean isActive() {
        return !mCompleted;
    }

    public boolean isEmpty() {
        return Strings.isNullOrEmpty(mTitle) &&
                Strings.isNullOrEmpty(mDescription);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        Task task = (Task) obj;
        return Objects.equal(mId, task.getId()) &&
                Objects.equal(mTitle, task.getTitle()) &&
                Objects.equal(mDescription, task.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mId, mTitle, mDescription);
    }

    @Override
    public String toString() {
        return "Task with title " + mTitle;
    }
}
