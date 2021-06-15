package dev.techasyluminfo.bookhaikaya;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.github.ybq.android.spinkit.style.Wave;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {
    public static final String TAG = MainActivity.class.getName();
    public static final String API_BOOK_KEY="AIzaSyDUiHq6rQlWqFe970tHECMyElhXldsWgio";
    public static final String API_KEY_PARAMETER="key";

    public static final String API_SAVE_STATE_KEY = "api_url";
    public static final int LOADER_ID = 1;
    private static final String FILTER_PARAMETER = "filter";
    String searchText = "";
    public String API_URL = "";
    public static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes";
    public static final String QUERY_PARAMETER="q";
    public static final String START_INDEX_PARAMETER = "startIndex";
    public static final String MAXRESULT_PARAMETER = "maxResults";
    public static final String ORDERBY_PARAMETER = "orderBy";
    int startIndex = 0, maxResult = 20;
    public static final String DEFAULT_SEARCH_KEYWORD = "a";

    BookAdapter bookAdapter;
    List<Book> bookList = new ArrayList<>();
    ListView bookListView;
    private ProgressBar progressBarSpinner;
    private TextView emptySateTextView;

    LoaderManager loaderManager;
    private SearchView searchView;
    ProgressBar progressBarSpinkitLoadingMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppSinglton.setDefaultpreference(this);


        //setiing loading more progressbar
        progressBarSpinkitLoadingMore = findViewById(R.id.lodingMoreSpinner);
        Sprite sprite = new ThreeBounce();
        progressBarSpinkitLoadingMore.setIndeterminateDrawable(sprite);

        progressBarSpinner =  findViewById(R.id.loadingSpinner);
        Sprite sprite2 = new ThreeBounce();
        progressBarSpinner.setIndeterminateDrawable(sprite2);
        progressBarSpinner.setVisibility(View.VISIBLE);

        emptySateTextView = findViewById(R.id.emptyStateTextView);

        searchView = findViewById(R.id.searchView1);
        searchView.onActionViewExpanded();

        bookListView = findViewById(R.id.bookListView);
        bookListView.setEmptyView(emptySateTextView);

        bookAdapter = new BookAdapter(MainActivity.this, bookList);
        bookListView.setAdapter(bookAdapter);


        loaderManager = LoaderManager.getInstance(this);
        //  loaderManager.initLoader(1, null, this);


        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String searchpreference = sharedPrefs.getString(getString(R.string.edit_preference_default_search_key)
                , getString(R.string.edit_preference_search_default_value));

        if (!searchpreference.equals(getString(R.string.edit_preference_search_default_value))) {
            searchView.setQuery(searchpreference, true);
        }


        API_URL = createApiUrl();
        loaderManager.initLoader(LOADER_ID, null, MainActivity.this);


        searchListener();

        // TODO pagination cose is here
        onScrollListenerWithPagination();


    }



    private void onScrollListenerWithPagination() {
        bookListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (!view.canScrollList(View.SCROLL_AXIS_VERTICAL) && scrollState == SCROLL_STATE_IDLE) {
                    //When List reaches bottom and the list isn't moving (is idle)
                   // Toast.makeText(MainActivity.this, R.string.toast_item_reach, Toast.LENGTH_SHORT).show();
                    // Toast.makeText(MainActivity.this, "Loading more", Toast.LENGTH_SHORT).show();

                    progressBarSpinkitLoadingMore.setVisibility(View.VISIBLE);
                    if (loaderManager.hasRunningLoaders()) {
                        // Toast.makeText(MainActivity.this, "Loading more", Toast.LENGTH_SHORT).show();


                    } else {
                        startIndex = startIndex + maxResult;

                        loaderManager.destroyLoader(LOADER_ID);
                        if (!(BookUtility.totalItem == 0)) {
                            if (BookUtility.totalItem < startIndex) {
                                progressBarSpinner.setVisibility(View.INVISIBLE);
                                progressBarSpinkitLoadingMore.setVisibility(View.INVISIBLE);
                                Toast.makeText(MainActivity.this, "No more data", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        //Toast.makeText(MainActivity.this, "startIndex" + startIndex, Toast.LENGTH_SHORT).show();
                        API_URL = createApiUrl();
                        Log.i(TAG, "loading more data: " + API_URL);
                        loaderManager.initLoader(LOADER_ID, null, MainActivity.this);

                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    //searchView Query Litener
    private void searchListener() {

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.onActionViewExpanded();
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub

                //Toast.makeText(activity, String.valueOf(hasFocus),Toast.LENGTH_SHORT).show();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub
                startIndex = 0;
                maxResult = 20;
                bookListView.setVisibility(View.VISIBLE);
                searchingStart(query.trim());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String searchTxt) {

                if (searchTxt.length() > 2) {
                    startIndex = 0;
                    maxResult = 20;
                    bookListView.setVisibility(View.VISIBLE);
                    searchingStart(searchTxt.trim());
                    return true;
                } else {

                    bookListView.setVisibility(View.INVISIBLE);

                }


                return false;
            }

        });
    }



    //searching start from here
    private void searchingStart(String searchTxt) {
        emptySateTextView.setText("");

        progressBarSpinner.setVisibility(View.VISIBLE);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit().putString(getString(R.string.edit_preference_default_search_key), searchTxt);
        editor.apply();

        API_URL = createApiUrl();

        if (loaderManager.getLoader(LOADER_ID) != null) {
            loaderManager.restartLoader(LOADER_ID, null, MainActivity.this);
        } else {
            loaderManager.initLoader(LOADER_ID, null, MainActivity.this);
        }
    }

    // Build API URl method
    private String createApiUrl() {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String searchPreference = sharedPrefs.getString(
                getString(R.string.edit_preference_default_search_key),
                getString(R.string.edit_preference_search_default_value));
        String orderByPreference = sharedPrefs.getString(
                getString(R.string.list_preference_orderBy_key),
                getString(R.string.settings_order_by_relevence_value)
        );
        String filterbypreference=sharedPrefs.getString(getString(R.string.list_preference_filter_key)
        ,getString(R.string.setting_filter_noen_value));

        //String queryString = MessageFormat.format(BASE_URL, searchPreference);
        Uri baseUri = Uri.parse(BASE_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter(QUERY_PARAMETER,searchPreference);
        uriBuilder.appendQueryParameter(START_INDEX_PARAMETER, String.valueOf(startIndex));
        uriBuilder.appendQueryParameter(MAXRESULT_PARAMETER, String.valueOf(maxResult));
        if(!filterbypreference.equals(getString(R.string.setting_filter_noen_value))){
            uriBuilder.appendQueryParameter(FILTER_PARAMETER, filterbypreference);
        }
        uriBuilder.appendQueryParameter(ORDERBY_PARAMETER, orderByPreference);
        uriBuilder.appendQueryParameter(API_KEY_PARAMETER,API_BOOK_KEY);

        Log.i(TAG, "createApiUrl: " +uriBuilder.toString());
        return uriBuilder.toString();
    }





    @NonNull
    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {


        return new BookLoader(this, API_URL);

    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Book>> loader, List<Book> data) {
        progressBarSpinner.setVisibility(View.GONE);
        progressBarSpinkitLoadingMore.setVisibility(View.INVISIBLE);
        if (startIndex >= 20) {

        } else {
            bookAdapter.clear();
        }

        if (!isNetworkConnected()) {
            if (startIndex >= 20) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            } else {
                bookAdapter.clear();
                emptySateTextView.setText("No internet connection");
            }
            progressBarSpinner.setVisibility(View.GONE);

            return;
        }
        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {


            bookList.addAll(data);
            bookAdapter.notifyDataSetChanged();

        } else emptySateTextView.setText("No Book Available");

    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Book>> loader) {
        // TODO pagination must code
        if (startIndex >= 20) {

        } else {
            bookAdapter.clear();
        }
    }

    //Check internet connection
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("you");

        } catch (Exception e) {
            return false;
        }
    }

    //disable return button for app
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }


    public void filterSideBtn(View view) {

        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
    }
}