package utils;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.test.ludovicofabbri.radioshake.R;

/**
 * Created by ludovicofabbri on 13/10/16.
 */

public class Utils {


    /**
     * return an error message toast
     * @param context
     * @param message
     * @param duration
     */
    public static Toast createErrorToast(Context context, String message, int duration) {

        Toast toast = Toast.makeText(context, message, duration);
        View view = toast.getView();
        view.setPadding(16,16,16,16);
        view.setMinimumWidth(400);
        view.setBackgroundResource(R.drawable.toast_error_bg);

        return toast;
    }




    /**
     * return an ok message toast
     * @param context
     * @param message
     * @param duration
     */
    public static Toast createOkToast(Context context, String message, int duration) {

        Toast toast = Toast.makeText(context, message, duration);
        View view = toast.getView();
        view.setPadding(16,16,16,16);
        view.setMinimumWidth(400);
        view.setBackgroundResource(R.drawable.toast_ok_bg);

        return toast;
    }





}
