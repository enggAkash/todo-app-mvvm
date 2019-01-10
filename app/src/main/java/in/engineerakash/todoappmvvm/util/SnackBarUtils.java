package in.engineerakash.todoappmvvm.util;

import android.support.design.widget.Snackbar;
import android.view.View;

public class SnackBarUtils {

    /**
     * Default SnackBar length is {@link Snackbar#LENGTH_LONG}
     */
    public static void showSnackBar(View v, String snackBarText) {
        showSnackBar(v, snackBarText, Snackbar.LENGTH_LONG);
    }

    public static void showSnackBar(View v, String snackBarText, int length) {
        if (v == null || snackBarText == null)
            return;

        Snackbar.make(v, snackBarText, length).show();

    }

}
