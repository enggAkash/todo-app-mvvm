package in.engineerakash.todoappmvvm.statistics;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import in.engineerakash.todoappmvvm.R;

/**
 * Show Statistic for tasks.
 */
public class StatisticsActivity extends AppCompatActivity {

    public static final String STATS_VIEWMODEL_TAG = "STATS_VIEWMODEL_TAG";

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        setupToolbar();

        setupNavigationDrawer();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setupNavigationDrawer() {
        mDrawerLayout = findViewById(R.id.drawable_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimary);
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null)
            setupDrawerContent(navigationView);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.list_navigation_menu_item:
                        NavUtils.navigateUpFromSameTask(StatisticsActivity.this);
                        break;
                    case R.id.statistics_navigation_menu_item:
                        // Do nothing, we are on that screen
                        break;
                }
                // Close the navigation drawer, when navigation item is selected
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                return true;
            }
        });
    }

}
