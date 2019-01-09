package in.engineerakash.todoappmvvm.taskdetail;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import in.engineerakash.todoappmvvm.R;

public class TaskDetailActivity extends AppCompatActivity {

    public static final int DELETE_RESULT_OK = RESULT_FIRST_USER + 2;

    public static final int EDIT_RESULT_OK = RESULT_FIRST_USER + 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
    }
}
