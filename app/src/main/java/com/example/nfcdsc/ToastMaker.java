package com.example.nfcdsc;

import android.content.Context;
import android.widget.Toast;

/**
 *
 * @author Michael Ajuna and Baluku Edgar <michaelajnew@gmail.com, edgarbaluku@gmail.com>
 *
 */
public class ToastMaker {

    /**
     * Displays a Toast notification with a string resource as a parameter
     *
     * @param context
     * @param resId
     */
    public static void toast(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    /**
     * Displays a Toast notification for a short duration with a hardcoded string.
     *
     * @param context
     * @param message
     */
    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}

