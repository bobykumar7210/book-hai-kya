package dev.techasyluminfo.bookhaikaya;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import dev.techasyluminfo.bookhaikaya.databinding.ActivityDetailBinding;

import static dev.techasyluminfo.bookhaikaya.BookUtility.TEMP_IMAGE;
import static dev.techasyluminfo.bookhaikaya.MainActivity.*;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {
    private static final String LOG_TAG = "DetailActivity";
    String selfUrl;
    private ActivityDetailBinding binding;
    List<Book> book = new ArrayList<>();
    public static final int LOADER_ID = 34;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getDataFromIntent();
        LoaderManager loaderManager = LoaderManager.getInstance(this);
        loaderManager.initLoader(LOADER_ID, null, this);
        // TODO on back pressed data reloding againg-2
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(ID_KEY)) {
            selfUrl = intent.getStringExtra(ID_KEY);
        }
        else{
            // TODO set empty view
            Toast.makeText(this, "empty view", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private String createUrl(String selfUrl) {
        Uri baseUri = Uri.parse(selfUrl);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter(API_KEY_PARAMETER, API_BOOK_KEY);
        Log.i(LOG_TAG, "createUrl: " + uriBuilder.toString());
        return uriBuilder.toString();
    }

    @NonNull
    @Override
    public Loader<List<Book>> onCreateLoader(int id, @Nullable Bundle args) {

        return new BookLoader(this, createUrl(selfUrl), 3);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Book>> loader, List<Book> data) {

        if (data != null && !data.isEmpty()) {
            book = data;
            Book currentBook = book.get(0);
            setDataToUI(currentBook);
        }

    }

    private void setDataToUI(Book currentBook) {
        String thumbnailUrl = currentBook.getThumnailUrl();
        if (thumbnailUrl.equals(TEMP_IMAGE)) {
            binding.bookThumbnailImgView.setImageResource(R.drawable.temp_thumbnail);
        } else {
            Picasso.get().load(thumbnailUrl).into(binding.bookThumbnailImgView);
        }

        binding.bookTitleTxtView.setText(currentBook.getBookTitle());

        String description = currentBook.getBookDesc();
        String des = Html.fromHtml(description).toString();
        binding.bookDescriptionTxtView.setText(des);

        String authorString = "Author : " + currentBook.getAuthorName();
        binding.bookAutherTxtView.setText(authorString);

        binding.datePublishedTextView.setText(currentBook.getDatepublished());

    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Book>> loader) {
        book.clear();

    }
}