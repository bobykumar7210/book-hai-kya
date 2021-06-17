package dev.techasyluminfo.bookhaikaya;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookUtility {

    private static final String LOG_TAG = "BookUtility";
    public static final String TEMP_IMAGE = "temp";
    public static int totalItem;
    //fetching booklist from json
    public static List<Book> fetchBookData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        List<Book> bookList = extractBookData(jsonResponse);

        // Return the {@link Event}
        return bookList;
    }
    public static List<Book> fetchSingleBook(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
       List<Book>  books = extractSingleBookDetail(jsonResponse);

        // Return the {@link Event}
        return books;
    }




    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                // TODO handling error response in better way
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the Book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    public static List<Book> extractBookData(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }


        List<Book> bookList = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and
            JSONObject rootObject = new JSONObject(jsonResponse);

             totalItem=rootObject.optInt("totalItems");
            if (rootObject.isNull("items")) {
                return null;
            }
            JSONArray featureArray = rootObject.getJSONArray("items");
            for (int k = 0; k < featureArray.length(); k++) {
                JSONObject currentObject = featureArray.getJSONObject(k);
                JSONObject valueObject = currentObject.optJSONObject("volumeInfo");
                //get authors from json
                JSONArray authorArray = valueObject.optJSONArray("authors");

                StringBuilder authors = new StringBuilder();

                if (authorArray == null) {
                    authors.append(" ");
                } else {
                    for (int i = 0; i < authorArray.length(); i++) {
                        String currentString = authorArray.optString(i);
                        authors.append(currentString).append(",");
                    }
                }
                //get thumbnail from json
                String bookThumnailUrl;
                if (valueObject.isNull("imageLinks")) {
                    // TODO change image to defaul thumnail image
                    bookThumnailUrl = TEMP_IMAGE;
                } else {
                    JSONObject imageObject = valueObject.getJSONObject("imageLinks");
                    bookThumnailUrl = imageObject.optString("thumbnail");
                }
                //get title from json
                String boolTitle = valueObject.optString("title");
                //get description from json
                String bookDesc = valueObject.optString("subtitle");
                //get datePublished from json
                String publishedDate;
                if(!valueObject.isNull("publishedDate")){
                    publishedDate=valueObject.optString("publishedDate");
                }
                else publishedDate=" ";

                //getting selflink from json
                String id;
                if(currentObject.isNull("selfLink")){
                    id="";
                }else id=currentObject.optString("selfLink");

                bookList.add(new Book(boolTitle, authors.toString(), bookDesc, bookThumnailUrl,publishedDate,id));
            }

            // build up a list of Earthquake objects with the corresponding data.

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return bookList;
    }
    private static List<Book> extractSingleBookDetail(String jsonResponse)  {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        List<Book> book=new ArrayList<>();
        try {

            JSONObject rootObject=new JSONObject(jsonResponse);
            if(rootObject.isNull("volumeInfo")){
                return null;
            }
            JSONObject valueInfoObject = rootObject.optJSONObject("volumeInfo");

            //get authors from json
            JSONArray authorArray = valueInfoObject.optJSONArray("authors");
            StringBuilder authors = new StringBuilder();

            if (authorArray == null) {
                authors.append(" ");
            } else {
                for (int i = 0; i < authorArray.length(); i++) {
                    String currentString = authorArray.optString(i);
                    authors.append(currentString).append(",");
                }
            }

            //get thumbnail from json
            String bookThumnailUrl;
            if (valueInfoObject.isNull("imageLinks")) {
                // TODO change image to defaul thumnail image
                bookThumnailUrl = TEMP_IMAGE;
            } else {
                JSONObject imageObject = valueInfoObject.getJSONObject("imageLinks");
                bookThumnailUrl = imageObject.optString("thumbnail");
            }

            //get title from json
            String bookTitle = valueInfoObject.optString("title");
            //get description from json
            String bookDesc = valueInfoObject.optString("description");

            //get datePublished from json
            String publishedDate;
            if(!valueInfoObject.isNull("publishedDate")){
                publishedDate=valueInfoObject.optString("publishedDate");
            }else publishedDate="";

            //getting page count
            int pageCount;
            if(!valueInfoObject.isNull("pageCount")){
                pageCount=valueInfoObject.getInt("pageCount");
            }
            else pageCount=0;

            //getting printype
            String printType;
            if(!valueInfoObject.isNull("printType")){
                printType=valueInfoObject.optString("printType");
            }else printType="";

            //getting publisher if available
            String publisher;
            if (!valueInfoObject.isNull("publisher")){
                publisher=valueInfoObject.optString("publisher");
            }
            else publisher="";

            //getting saleInfo object
            JSONObject saleInfoObject=rootObject.optJSONObject("saleInfo");
            String saleability;
            if(!saleInfoObject.isNull("saleability")){
                saleability=saleInfoObject.optString("saleability");
            }else saleability="";

            String buyLink;
            if(!saleInfoObject.isNull("buyLink")){
                buyLink=saleInfoObject.optString("buyLink");
            }else buyLink="";

            //getting accessInfo object
            JSONObject accessInfoObject=rootObject.optJSONObject("accessInfo");
            String webReaderlink;
            if(!accessInfoObject.isNull("webReaderLink")){
                webReaderlink=accessInfoObject.optString("webReaderLink");
            }
            else webReaderlink="";

            book.add(new Book(bookTitle,authors.toString(),bookDesc,bookThumnailUrl,publishedDate
            ,pageCount,printType,publisher,saleability,buyLink,webReaderlink));

            return book;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

}
