package dev.techasyluminfo.bookhaikaya;

import android.content.Context;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.List;

public class BookLoader extends AsyncTaskLoader<List<Book>> {
    public static final String Log_TAG=BookLoader.class.getName();
    private  String mUrl;
    public BookLoader(@NonNull Context context,String url) {
        super(context);
        mUrl=url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<Book> loadInBackground() {
        Log.i(Log_TAG, "loadInBackground: ");
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<Book> bookList = BookUtility.fetchBookData(mUrl);
        return  bookList;
    }


}
