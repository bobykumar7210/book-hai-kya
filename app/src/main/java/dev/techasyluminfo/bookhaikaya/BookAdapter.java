package dev.techasyluminfo.bookhaikaya;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static dev.techasyluminfo.bookhaikaya.BookUtility.TEMP_IMAGE;

public class BookAdapter extends ArrayAdapter<Book> {
    private Context mContext;
    private List<Book> mBooklst;

    public BookAdapter(@NonNull Context context, @NonNull List<Book> bookList) {
        super(context, 0, bookList);
        this.mContext = context;
        this.mBooklst = bookList;
    }

    public List<Book> getmBooklst() {
        return mBooklst;
    }

    @Override
    public int getCount() {
        return mBooklst.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Book currentBookItem = getItem(position);

        View listItemView = convertView;


        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);

        }


        TextView titleTextView = listItemView.findViewById(R.id.titleTextView);
        TextView authorTextView = listItemView.findViewById(R.id.authorTextView);
        TextView bookDescTextView = listItemView.findViewById(R.id.bookDescTextView);
        ImageView thumnailImageView = listItemView.findViewById(R.id.thumnailImageView);
        TextView publishedDateTextView = listItemView.findViewById(R.id.datePublishedTextView);
        titleTextView.setText(currentBookItem.getBookTitle());
        publishedDateTextView.setText(currentBookItem.getDatepublished());
        authorTextView.setText(currentBookItem.getAuthorName());
        // bookDescTextView.setText(currentBookItem.getBookDesc());
        if (currentBookItem.getThumnailUrl() != null) {
            String thumnailUrl = currentBookItem.getThumnailUrl();
            if (thumnailUrl.equals(TEMP_IMAGE)) {
                thumnailImageView.setImageResource(R.drawable.temp_thumbnail);
            } else {
                // TODO add code for background task to Load image
                Picasso.get()
                        .load(thumnailUrl)
                        .into(thumnailImageView);
            }
        }

        return listItemView;
    }


}
