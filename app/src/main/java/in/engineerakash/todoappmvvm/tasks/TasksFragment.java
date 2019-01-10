package in.engineerakash.todoappmvvm.tasks;


import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import in.engineerakash.todoappmvvm.R;
import in.engineerakash.todoappmvvm.ScrollChildSwipeRefreshLayout;
import in.engineerakash.todoappmvvm.data.Task;
import in.engineerakash.todoappmvvm.data.source.TasksRepository;
import in.engineerakash.todoappmvvm.databinding.TaskItemBinding;
import in.engineerakash.todoappmvvm.databinding.TasksFragBinding;
import in.engineerakash.todoappmvvm.util.SnackBarUtils;

/**
 * Display a list of grid {@link Task}s . User can choose to view All, Active, Completed Tasks.
 */
public class TasksFragment extends Fragment {

    private TasksViewModel mTasksViewModel;

    private TasksFragBinding mTasksFragBinding;

    private TasksAdapter mListAdapter;

    private Observable.OnPropertyChangedCallback mSnackBarCallback;

    public TasksFragment() {
        // Required empty public constructor
    }

    public static TasksFragment newInstance() {
        return new TasksFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        mTasksViewModel.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mTasksFragBinding = TasksFragBinding.inflate(inflater, container, false);

        mTasksFragBinding.setView(this);

        mTasksFragBinding.setViewmodel(mTasksViewModel);

        setHasOptionsMenu(true);

        View root = mTasksFragBinding.getRoot();

        return root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                mTasksViewModel.clearCompletedTasks();
                break;
            case R.id.menu_filter:
                showFilteringPopUpMenu();
                break;
            case R.id.menu_refresh:
                mTasksViewModel.loadTasks(true);
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu);
    }

    public void setViewModel(TasksViewModel viewModel) {
        mTasksViewModel = viewModel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupSnackBar();

        setupFab();

        setupListAdapter();

        setupRefreshLayout();
    }

    @Override
    public void onDestroy() {
        mListAdapter.onDestroy();
        if (mSnackBarCallback != null) {
            mTasksViewModel.removeOnPropertyChangedCallback(mSnackBarCallback);
        }
        super.onDestroy();
    }

    private void setupSnackBar() {
        mSnackBarCallback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                SnackBarUtils.showSnackBar(getView(), mTasksViewModel.getSnackBarText());
            }
        };
        mTasksViewModel.snackBarText.addOnPropertyChangedCallback(mSnackBarCallback);
    }

    private void showFilteringPopUpMenu() {
        PopupMenu popup = new PopupMenu(getContext(), getActivity().findViewById(R.id.menu_filter));
        popup.getMenuInflater().inflate(R.menu.filter_tasks, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.all:
                        mTasksViewModel.setFiltering(TasksFilterType.ALL_TASKS);
                        break;
                    case R.id.active:
                        mTasksViewModel.setFiltering(TasksFilterType.ACTIVE_TASKS);
                        break;
                    case R.id.completed:
                        mTasksViewModel.setFiltering(TasksFilterType.COMPLETE_TASKS);
                        break;
                    default:
                        mTasksViewModel.setFiltering(TasksFilterType.ALL_TASKS);
                        break;
                }
                mTasksViewModel.loadTasks(false);
                return false;
            }
        });

        popup.show();
    }

    private void setupFab() {
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_add_task);

        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTasksViewModel.addNewTask();
            }
        });
    }

    private void setupListAdapter() {
        ListView listView = mTasksFragBinding.tasksList;

        mListAdapter = new TasksAdapter(
                new ArrayList<Task>(0),
                (TasksActivity) getActivity(),
                null/*TODO Injection.provideTasksRepository(getContext().getApplicationContext())*/,
                mTasksViewModel
        );
        listView.setAdapter(mListAdapter);
    }

    private void setupRefreshLayout() {
        ListView listView = mTasksFragBinding.tasksList;
        ScrollChildSwipeRefreshLayout swipeRefreshLayout = mTasksFragBinding.refreshLayout;

        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );
        // Set the scrolling view in the custom SwipeRefreshLayout
        swipeRefreshLayout.setScrollUpChild(listView);
    }

    public static class TasksAdapter extends BaseAdapter {

        @Nullable
        private TaskItemNavigator mTaskItemNavigator;

        private final TasksViewModel mTasksViewModel;

        private List<Task> mTasks;

        private TasksRepository mTasksRepository;

        public TasksAdapter(List<Task> tasks, TasksActivity taskItemNavigator,
                            TasksRepository tasksRepository, TasksViewModel tasksViewModel) {
            mTasks = tasks;
            mTaskItemNavigator = taskItemNavigator;
            mTasksRepository = tasksRepository;
            mTasksViewModel = tasksViewModel;

            setList(tasks);
        }

        public void onDestroy() {
            mTaskItemNavigator = null;
        }

        public void replaceData(List<Task> tasks) {
            setList(tasks);
        }

        @Override
        public int getCount() {
            return mTasks != null ? mTasks.size() : 0;
        }

        @Override
        public Task getItem(int position) {
            return mTasks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Task task = getItem(position);
            TaskItemBinding binding;

            if (convertView == null) {
                // inflate
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());

                // Create the binding
                binding = TaskItemBinding.inflate(inflater, parent, false);

            } else {
                // Recycling view
                binding = DataBindingUtil.getBinding(convertView);
            }

            final TaskItemViewModel viewModel = new TaskItemViewModel(
                    parent.getContext().getApplicationContext(),
                    mTasksRepository
            );

            viewModel.setNavigator(mTaskItemNavigator);

            binding.setViewmodel(viewModel);

            // To save on PropertyChangeCallbacks, wire the item's snackbar text observable to the
            // fragment's.
            viewModel.snackBarText.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    mTasksViewModel.snackBarText.set(viewModel.getSnackBarText());
                }
            });

            viewModel.setTask(task);

            return binding.getRoot();
        }

        private void setList(List<Task> tasks) {
            mTasks = tasks;
            notifyDataSetChanged();
        }
    }

}
