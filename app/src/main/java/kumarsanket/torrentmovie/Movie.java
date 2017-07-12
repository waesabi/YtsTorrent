package kumarsanket.torrentmovie;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sanketkumar on 27/04/17.
 */

public class Movie implements Serializable {


    public String movie_id;
    public String movie_url;
    public String movie_imdb_code;
    public String movie_name;
    public String movie_slug;
    public String movie_year;
    public String movie_rating;
    public String movie_genre;
    public String movie_full_description;
    public String movie_trailer_code;
    public String movie_background_image;
    public String movie_background_image_original;
    public String medium_image_cover;
    public String large_image_cover;
    public ArrayList<MovieQuality> movieQualities;


    /**
     *  imdb url http://www.imdb.com/title/tt1389762/ where 'tt1389762' is "imdb_code"
     * @param lImageUrl
     * @param mImageUrl
     */

    // add genre
    public Movie(String id,String url , String code,String name,String slug,String year,String rating,String genre,String description,
                 String trailer_code,String background_image,String background_image_original,String lImageUrl,String mImageUrl,
                 ArrayList<MovieQuality> movieQualityArrayList)
    {
        movie_id = id;
        movie_url = url;
        movie_imdb_code = code;
        movie_name = name;
        movie_slug = slug;
        movie_year = year;
        movie_rating = rating;
        movie_genre = genre;
        movie_full_description = description;
        movie_trailer_code = trailer_code;
        movie_background_image= background_image;
        movie_background_image_original = background_image_original;
        medium_image_cover = mImageUrl;
        large_image_cover = lImageUrl;
        movieQualities = movieQualityArrayList;
    }


    public Movie()
    {

    }





    /**
     * -------------------- getter method --------------------------
     * @return
     */

    public String getLarge_image_cover() {
        return large_image_cover;
    }

    public String getMedium_image_cover() {
        return medium_image_cover;
    }

    public String getMovie_background_image() {
        return movie_background_image;
    }

    public String getMovie_background_image_original() {
        return movie_background_image_original;
    }

    public String getMovie_full_description() {
        return movie_full_description;
    }

    public String getMovie_genre() {
        return movie_genre;
    }

    public String getMovie_id() {
        return movie_id;
    }

    public String getMovie_name() {
        return movie_name;
    }

    public String getMovie_rating() {
        return movie_rating;
    }

    public String getMovie_slug() {
        return movie_slug;
    }

    public String getMovie_trailer_code() {
        return movie_trailer_code;
    }

    public String getMovie_url() {
        return movie_url;
    }

    public String getMovie_year() {
        return movie_year;
    }

    public ArrayList<MovieQuality> getMovieQualities() {
        return movieQualities;
    }

    public String getMovie_imdb_code() {
        return movie_imdb_code;
    }

}
