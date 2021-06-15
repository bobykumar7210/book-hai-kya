package dev.techasyluminfo.bookhaikaya;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class AppSinglton {
    static AppSinglton mInstance = null;


    static int mCount = 0;

    synchronized public static AppSinglton setDefaultpreference(Context context) {
        if (mInstance == null) {
            if (mCount == 0) {
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPrefs.edit().putString(context.getString(R.string.edit_preference_default_search_key),
                        context.getString(R.string.edit_preference_search_default_value));
                editor.apply();
                Toast.makeText(context, "count "+mCount, Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(context, "count "+mCount, Toast.LENGTH_SHORT).show();

            mCount++;
            return mInstance;
        } else return mInstance;
    }
    public static int getmCount(){
        return mCount;
    }


}
