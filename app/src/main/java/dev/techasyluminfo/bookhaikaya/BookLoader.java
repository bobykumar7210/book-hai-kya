package dev.techasyluminfo.bookhaikaya;

import android.content.Context;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.List;

public class BookLoader extends AsyncTaskLoader<List<Book>> {
    public static final String Log_TAG=BookLoader.class.getName();
    private  String mUrl;
    private Context mContext;
    private int mfind;
    public BookLoader(@NonNull Context context,String url,int find) {
        super(context);
        mfind=find;
        mContext=context;
        mUrl=url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<Book> loadInBackground() {

        if (mUrl == null) {
            return null;
        }
        List<Book> bookList;
        if(mfind==1) {
            Log.i(Log_TAG, "loadInBackground:MainActivity ");
            // Perform the network request, parse the response, and extract a list of earthquakes.
            bookList = BookUtility.fetchBookData(mUrl);
            return bookList;
        }
        else{
            Log.i(Log_TAG, "loadInBackground:DetailActivity "+mfind);
            bookList=BookUtility.fetchSingleBook(mUrl);
            return bookList;
        }

    }


}
