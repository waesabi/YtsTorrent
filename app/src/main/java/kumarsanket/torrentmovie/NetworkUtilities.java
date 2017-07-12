package kumarsanket.torrentmovie;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by sanketkumar on 27/04/17.
 */

public class NetworkUtilities {

    public static String getResponseFromHttpUrl(String str) throws IOException
    {
        if (str == null) {
            return null;
        }
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        String JsonString = null;

        Log.e("NetworkUtilities", "loadInBackground is Called");

        BufferedReader bufferedReader = null;
        StringBuffer stringBuffer = new StringBuffer();

        Log.v("NetworkUtilities", str);

        try {
            URL url = new URL(str);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            if (inputStream == null) {
                return null;
            }
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + "\n");
            }

            if (stringBuffer.length() == 0) {
                return null;
            }

            JsonString = stringBuffer.toString();

        } catch (IOException e) {
            Log.e("NetworkUtilities", "Problem retrieving the Movie JSON results.", e);
            return null;
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException I) {
                    I.printStackTrace();
                    return null;
                }
            }
        }
        return JsonString;
    }



    public static ArrayList<Movie> extractMovie(String jsonString) throws JSONException
    {
        final String TORRENT_DATA = "data";
        final String MOVIES_ARRAY = "movies";
        final String MOVIES_ID = "id";
        final String MOVIE_URL = "url";
        final String MOVIES_IMDB_CODE = "imdb_code";
        final String MOVIES__TITLE = "title";
        final String MOVIES_SLUG = "slug";
        final String MOVIES_YEAR = "year";
        final String MOVIES_RATING = "rating";
        final String MOVIES_GENRE = "genres";
        final String MOVIES_FULL_DESCRIPTION = "description_full";
        final String MOVIES_TRAILER_CODE = "yt_trailer_code";
        final String MOVIES_BACKGROUND_IMAGE = "background_image";
        final String MOVIES_BACKGROUND_IMAGE_ORIGINAL = "background_image_original";
        final String MOVIES_MEDIUM_IMAGE = "medium_cover_image";
        final String MOVIES_LARGE_IMAGE = "large_cover_image" ;
        final String TORRENT_ARRAY = "torrents" ;
        final String TORRENT_URL = "url" ;
        final String TORRENT_HASH = "hash";
        final String TORRENT_QUALITY = "quality";
        final String TORRENT_SEEDS = "seeds";
        final String TORRENT_PEERS = "peers";
        final String TORRENT_SIZE = "size";


        /**
         *  string variables
         */
        String movie_id,movie_url,movie_imdb_code,movie_title,movie_slug,movie_year,movie_rating;
        String movie_genre,movie_full_description,movie_trailer_code,movie_background_image,movie_background_image_original;
        String movie_large_image,movie_medium_image;
        String torrent_url,torrent_hash,torrent_quality,torrent_seeds,torrent_peers,torrent_size;



        ArrayList<Movie> movieArrayList = new ArrayList<>();

        if(jsonString==null)
        {
            return null;
        }
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONObject jsonDataObject = jsonObject.getJSONObject(TORRENT_DATA);
        JSONArray jsonMovieArray = jsonDataObject.getJSONArray(MOVIES_ARRAY);

        for(int i=0;i<jsonMovieArray.length();i++)
        {
            ArrayList<MovieQuality> movieQualities = new ArrayList<>();
            JSONObject movieJsonObject = jsonMovieArray.getJSONObject(i);
            movie_id = movieJsonObject.getString(MOVIES_ID);
            movie_url = movieJsonObject.getString(MOVIE_URL);
            movie_imdb_code = movieJsonObject.getString(MOVIES_IMDB_CODE);
            movie_title = movieJsonObject.getString(MOVIES__TITLE);
            movie_slug =  movieJsonObject.getString(MOVIES_SLUG);
            movie_year = movieJsonObject.getString(MOVIES_YEAR);
            movie_rating = movieJsonObject.getString(MOVIES_RATING);
            movie_genre = movieJsonObject.getString(MOVIES_GENRE);
            movie_full_description = movieJsonObject.getString(MOVIES_FULL_DESCRIPTION);
            movie_trailer_code = movieJsonObject.getString(MOVIES_TRAILER_CODE);
            movie_background_image = movieJsonObject.getString(MOVIES_BACKGROUND_IMAGE);
            movie_background_image_original = movieJsonObject.getString(MOVIES_BACKGROUND_IMAGE_ORIGINAL);
            movie_large_image = movieJsonObject.getString(MOVIES_LARGE_IMAGE);
            movie_medium_image = movieJsonObject.getString(MOVIES_MEDIUM_IMAGE);
            JSONArray jsonTorrentArray = movieJsonObject.getJSONArray(TORRENT_ARRAY);
            for(int j = 0 ;j<jsonTorrentArray.length();j++)
            {
                JSONObject torrentObject = jsonTorrentArray.getJSONObject(j);
                torrent_url = torrentObject.getString(TORRENT_URL);
                torrent_hash = torrentObject.getString(TORRENT_HASH);
                torrent_quality = torrentObject.getString(TORRENT_QUALITY);
                torrent_seeds = torrentObject.getString(TORRENT_SEEDS);
                torrent_peers = torrentObject.getString(TORRENT_PEERS);
                torrent_size = torrentObject.getString(TORRENT_SIZE);

                MovieQuality movieQuality = new MovieQuality(torrent_url,torrent_hash,torrent_quality,
                        torrent_seeds,torrent_peers,torrent_size);
                movieQualities.add(movieQuality);
            }

            Movie movie = new Movie(movie_id,movie_url,movie_imdb_code,movie_title,movie_slug,movie_year,movie_rating,movie_genre,
                    movie_full_description,movie_trailer_code,movie_background_image,movie_background_image_original,movie_large_image,
                    movie_medium_image,movieQualities);

            movieArrayList.add(movie);

        }


        return movieArrayList ;
    }






    public static ArrayList<Movie> extractSuggestMovie(String jsonString) throws JSONException
    {
        final String TORRENT_DATA = "data";
        final String MOVIES_ARRAY = "movies";
        final String MOVIES_ID = "id";
        final String MOVIE_URL = "url";
        final String MOVIES_IMDB_CODE = "imdb_code";
        final String MOVIES__TITLE = "title";
        final String MOVIES_SLUG = "slug";
        final String MOVIES_YEAR = "year";
        final String MOVIES_RATING = "rating";
        final String MOVIES_GENRE = "genres";
        final String MOVIES_FULL_DESCRIPTION = "description_full";
        final String MOVIES_TRAILER_CODE = "yt_trailer_code";
        final String MOVIES_BACKGROUND_IMAGE = "background_image";
        final String MOVIES_BACKGROUND_IMAGE_ORIGINAL = "background_image_original";
        final String MOVIES_MEDIUM_IMAGE = "medium_cover_image";
        final String MOVIES_LARGE_IMAGE = "large_cover_image" ;
        final String TORRENT_ARRAY = "torrents" ;
        final String TORRENT_URL = "url" ;
        final String TORRENT_HASH = "hash";
        final String TORRENT_QUALITY = "quality";
        final String TORRENT_SEEDS = "seeds";
        final String TORRENT_PEERS = "peers";
        final String TORRENT_SIZE = "size";


        /**
         *  string variables
         */
        String movie_id,movie_url,movie_imdb_code,movie_title,movie_slug,movie_year,movie_rating;
        String movie_genre,movie_full_description,movie_trailer_code,movie_background_image,movie_background_image_original;
        String movie_large_image="",movie_medium_image;
        String torrent_url,torrent_hash,torrent_quality,torrent_seeds,torrent_peers,torrent_size;



        ArrayList<Movie> movieArrayList = new ArrayList<>();

        if(jsonString==null)
        {
            return null;
        }
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONObject jsonDataObject = jsonObject.getJSONObject(TORRENT_DATA);
        JSONArray jsonMovieArray = jsonDataObject.getJSONArray(MOVIES_ARRAY);

        for(int i=0;i<jsonMovieArray.length();i++)
        {
            ArrayList<MovieQuality> movieQualities = new ArrayList<>();
            JSONObject movieJsonObject = jsonMovieArray.getJSONObject(i);
            movie_id = movieJsonObject.getString(MOVIES_ID);
            movie_url = movieJsonObject.getString(MOVIE_URL);
            movie_imdb_code = movieJsonObject.getString(MOVIES_IMDB_CODE);
            movie_title = movieJsonObject.getString(MOVIES__TITLE);
            movie_slug =  movieJsonObject.getString(MOVIES_SLUG);
            movie_year = movieJsonObject.getString(MOVIES_YEAR);
            movie_rating = movieJsonObject.getString(MOVIES_RATING);
            movie_genre = movieJsonObject.getString(MOVIES_GENRE);
            movie_full_description = movieJsonObject.getString(MOVIES_FULL_DESCRIPTION);
            movie_trailer_code = movieJsonObject.getString(MOVIES_TRAILER_CODE);
            movie_background_image = movieJsonObject.getString(MOVIES_BACKGROUND_IMAGE);
            movie_background_image_original = movieJsonObject.getString(MOVIES_BACKGROUND_IMAGE_ORIGINAL);
            movie_medium_image = movieJsonObject.getString(MOVIES_MEDIUM_IMAGE);
            JSONArray jsonTorrentArray = movieJsonObject.getJSONArray(TORRENT_ARRAY);
            for(int j = 0 ;j<jsonTorrentArray.length();j++)
            {
                JSONObject torrentObject = jsonTorrentArray.getJSONObject(j);
                torrent_url = torrentObject.getString(TORRENT_URL);
                torrent_hash = torrentObject.getString(TORRENT_HASH);
                torrent_quality = torrentObject.getString(TORRENT_QUALITY);
                torrent_seeds = torrentObject.getString(TORRENT_SEEDS);
                torrent_peers = torrentObject.getString(TORRENT_PEERS);
                torrent_size = torrentObject.getString(TORRENT_SIZE);

                MovieQuality movieQuality = new MovieQuality(torrent_url,torrent_hash,torrent_quality,
                        torrent_seeds,torrent_peers,torrent_size);
                movieQualities.add(movieQuality);
            }

            Movie movie = new Movie(movie_id,movie_url,movie_imdb_code,movie_title,movie_slug,movie_year,movie_rating,movie_genre,
                    movie_full_description,movie_trailer_code,movie_background_image,movie_background_image_original,movie_large_image,
                    movie_medium_image,movieQualities);

            movieArrayList.add(movie);

        }


        return movieArrayList ;
    }
}
