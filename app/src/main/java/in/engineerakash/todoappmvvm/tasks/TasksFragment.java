package in.engineerakash.todoappmvvm.tasks;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.engineerakash.todoappmvvm.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TasksFragment extends Fragment {

    private TasksViewModel mTasksViewModel;

    public TasksFragment() {
        // Required empty public constructor
    }

    public static TasksFragment newInstance() {

        Bundle args = new Bundle();

        TasksFragment fragment = new TasksFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    public void setViewModel(TasksViewModel viewModel) {
        mTasksViewModel = viewModel;
    }
}
