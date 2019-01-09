package in.engineerakash.todoappmvvm.addedittask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import in.engineerakash.todoappmvvm.R;

public class AddEditTaskActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 1;

    public static final int ADD_EDIT_RESULT_OK = RESULT_FIRST_USER + 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);
    }
}
