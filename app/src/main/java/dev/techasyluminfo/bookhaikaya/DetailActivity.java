package dev.techasyluminfo.bookhaikaya;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.app.ShareCompat;
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
import java.util.Currency;
import java.util.List;

import dev.techasyluminfo.bookhaikaya.databinding.ActivityDetailBinding;

import static dev.techasyluminfo.bookhaikaya.BookUtility.TEMP_IMAGE;
import static dev.techasyluminfo.bookhaikaya.MainActivity.*;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {
    private static final String LOG_TAG = "DetailActivity";
    private static final String SALE_FREE = "FREE";
    private static final String NOT_FOR_SALE = "NOT_FOR_SALE";
    private static final String FOR_SALE = "FOR_SALE";
    public static final String DETAIL_URL = "detail_url";
    String selfUrl;
    private ActivityDetailBinding binding;
    List<Book> book = new ArrayList<>();
    public static final int LOADER_ID = 34;
    LoaderManager loaderManager;
    int buyBtnKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        buyBtnKey = binding.buyBookBtn.getId();

        Sprite sprite = new ThreeBounce();
        binding.lodingdetailSpinner.setIndeterminateDrawable(sprite);
        binding.lodingdetailSpinner.setVisibility(View.VISIBLE);
        getDataFromIntent();
        loaderManager = LoaderManager.getInstance(this);
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
        } else {
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
            onClickBtn(currentBook);
        } else {
            // TODO set empty view
            binding.lodingdetailSpinner.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
        }

    }

    private void setDataToUI(Book currentBook) {
        String thumbnailUrl = currentBook.getThumnailUrl();
        if (thumbnailUrl.equals(TEMP_IMAGE)) {
            binding.bookThumbnailImgView.setImageResource(R.drawable.temp_thumbnail);
        } else {
            Picasso.get().load(thumbnailUrl).resize(150,250).into(binding.bookThumbnailImgView);
        }

        binding.bookTitleTxtView.setText(currentBook.getBookTitle());
        binding.titleInAboutTextView.setText(currentBook.getBookTitle());
        String description = currentBook.getBookDesc();
        String des = Html.fromHtml(description).toString();
        binding.bookDescriptionTxtView.setText(des);

        String authorString = currentBook.getAuthorName();
        binding.authorWithTitleTxtView.setText(authorString);
        binding.datePublishedTextView.setText(currentBook.getDatepublished());
        binding.lodingdetailSpinner.setVisibility(View.INVISIBLE);
        binding.pageCountTxtView.setText(String.valueOf(currentBook.getPageCount()));
        binding.printTypeTxtView.setText(currentBook.getPrintType());

        String publisher=currentBook.getPublisher();
        binding.publisherTextView.setText(publisher);
        binding.publisherWithAuthorTxtView.setText(publisher);

        String saleability = currentBook.getSaleability();
        binding.saleInfoTxtView.setText(saleability);
        //TODO pdf downloder
        binding.PdfTextView.setText("No info");


        if (saleability.equals(SALE_FREE)) {
            binding.buyBookBtn.setText("Free Book");
            binding.buyBookBtn.setTag(buyBtnKey, SALE_FREE);

        } else if (saleability.equals(NOT_FOR_SALE)) {
            binding.buyBookBtn.setText("Not For Sale");
            binding.buyBookBtn.setTag(buyBtnKey, NOT_FOR_SALE);


        } else {
            binding.buyBookBtn.setText("Buy Book");
            binding.buyBookBtn.setTag(buyBtnKey, FOR_SALE);

        }
        binding.lodingdetailSpinner.setVisibility(View.INVISIBLE);


    }

    private void onClickBtn(final Book currentBook) {
       //setBtnView(currentBook);

        binding.buyBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!loaderManager.hasRunningLoaders()) {
                    String tag = binding.buyBookBtn.getTag(buyBtnKey).toString();
                    if (tag.equals(NOT_FOR_SALE)) {
                        Toast.makeText(DetailActivity.this, "Not for Sale", Toast.LENGTH_SHORT).show();
                    } else {

                        String bookBuylink = currentBook.getBuylink();
                        if (!bookBuylink.equals("")) {
                            openWebView(bookBuylink);

                        } else {
                            Toast.makeText(DetailActivity.this, "Sorry Buy option not available this Book right now " + tag, Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    Toast.makeText(DetailActivity.this, "Data is loading", Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.viewSampleBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String webReaderLink = currentBook.getWebReaderLink();
                if (!webReaderLink.equals("")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webReaderLink));
                    startActivity(intent);
                } else {
                    Toast.makeText(DetailActivity.this, "Sorry No View Available", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openWebView(String link) {
        Intent intent = new Intent(DetailActivity.this, WebActivity.class);
        intent.putExtra(DETAIL_URL, link);
        startActivity(intent);
    }

    private void setBtnView(Book currentBook) {
        if (currentBook.getWebReaderLink().equals("")) {
            binding.viewSampleBookBtn.setTextColor(getResources().getColor(android.R.color.darker_gray));
        } else {
            binding.viewSampleBookBtn.setTextColor(getResources().getColor(android.R.color.black));
        }
        if (currentBook.getBuylink().equals("")) {
            binding.buyBookBtn.setTextColor(getResources().getColor(android.R.color.darker_gray));
        } else {
            binding.buyBookBtn.setTextColor(getResources().getColor(android.R.color.black));

        }
    }


    @Override
    public void onLoaderReset(@NonNull Loader<List<Book>> loader) {
        book.clear();

    }
    // TODO Problem retrieving the Book JSON results.
    //    java.net.SocketTimeoutException: timeout
}