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

import android.os.SystemClock;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.github.ybq.android.spinkit.style.RotatingCircle;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.github.ybq.android.spinkit.style.Wave;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import dev.techasyluminfo.bookhaikaya.databinding.ActivityDetailBinding;

import static dev.techasyluminfo.bookhaikaya.BookUtility.TEMP_IMAGE;
import static dev.techasyluminfo.bookhaikaya.MainActivity.*;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {
    private static final String LOG_TAG = "DetailActivity";
    private static final String SALE_FREE = "FREE";
    private static final String NOT_FOR_SALE = "NOT_FOR_SALE";
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
        Sprite sprite =new ThreeBounce();
        binding.lodingdetailSpinner.setIndeterminateDrawable(sprite);
        binding.lodingdetailSpinner.setVisibility(View.VISIBLE);
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
            binding.lodingdetailSpinner.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "empty view", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private String createUrl(String selfUrl) {
        Uri baseUri = Uri.parse(selfUrl);
        Uri.Builder uriBuilder = baseUri.buildUpon();
       // uriBuilder.appendQueryParameter(API_KEY_PARAMETER, API_BOOK_KEY);
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

        String authorString = currentBook.getAuthorName();
        binding.authorWithTitleTxtView.setText(authorString);
        binding.datePublishedTextView.setText(currentBook.getDatepublished());
        binding.lodingdetailSpinner.setVisibility(View.INVISIBLE);
        binding.pageCountTxtView.setText(String.valueOf(currentBook.getPageCount()));
        binding.printTypeTxtView.setText(currentBook.getPrintType());
        binding.publisherTextView.setText(currentBook.getPublisher());
        String saleability=currentBook.getSaleability();
        binding.saleInfoTxtView.setText(saleability);
        //TODO pdf downloder
        binding.PdfTextView.setText("is available");
        if(saleability.equals(SALE_FREE)){
            binding.buyBookBtn.setText("FREE BOOK");

        }
        else if(saleability.equals(NOT_FOR_SALE)) {
            binding.buyBookBtn.setText("NOT FOR SALE");

        }else binding.buyBookBtn.setText("BUY BOOK");
        
        binding.lodingdetailSpinner.setVisibility(View.INVISIBLE);



    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Book>> loader) {
        book.clear();

    }
    // TODO Problem retrieving the Book JSON results.
    //    java.net.SocketTimeoutException: timeout
}