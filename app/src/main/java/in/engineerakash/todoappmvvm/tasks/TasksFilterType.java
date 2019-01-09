package in.engineerakash.todoappmvvm.tasks;

/**
 * Used with filter spinner in the tasks list.
 */
public enum TasksFilterType {
    /**
     * Do not filter tasks.
     */
    ALL_TASKS,
    /**
     * Filters only the active (not completed yet) tasks
     */
    ACTIVE_TASKS,
    /**
     * Filters only the completed tasks.
     */
    COMPLETE_TASKS
}
