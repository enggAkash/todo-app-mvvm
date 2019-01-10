package in.engineerakash.todoappmvvm;

import android.databinding.BindingAdapter;
import android.support.v4.widget.SwipeRefreshLayout;

import in.engineerakash.todoappmvvm.tasks.TasksViewModel;

public class SwipeRefreshLayoutDataBinding {


    /**
     * Reload the data when pull-to-refresh is triggered.
     * <p>
     * Creates the {@code android:onRefresh} for a {@link SwipeRefreshLayout}.
     */
    @BindingAdapter("android:onRefresh")
    public static void setSwipeREfreshLayoutOnRefreshListener(ScrollChildSwipeRefreshLayout view,
                                                              final TasksViewModel viewModel) {
        view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.loadTasks(true);
            }
        });
    }

}
