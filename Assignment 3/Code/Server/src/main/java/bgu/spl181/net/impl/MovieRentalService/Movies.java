package bgu.spl181.net.impl.MovieRentalService;

import java.util.ArrayList;
import java.util.List;

public class Movies {
    private List<MovieTemplate> movies = new ArrayList<>();

    public boolean containsMovieByName(String movieName) {
        for (MovieTemplate movie:movies) {
            if(movie.getName().equals(movieName)){
                return true;
            }
        }
        return false;
    }

    public List<String> getBannedCountries(String movieName){
        return getMovie(movieName).getBannedCountries();

    }
    public MovieTemplate getMovie(String movieName) {
        for (MovieTemplate movie:movies) {
            if(movie.getName().equals(movieName)){
                return movie;
            }
        }
        return null;
    }

    public void addMovie(MovieTemplate movieTemplate) {
        movies.add(movieTemplate);
    }

    public boolean isPossibleToRemoveMovie(String movieName){
        int totalAmount= this.getMovie(movieName).getTotalAmount();
        int availableAmount  = this.getMovie(movieName).getAvailableAmount();
        boolean isMovieExists =this.containsMovieByName(movieName);
        return isMovieExists && (availableAmount == totalAmount);
    }
    public void removeMovie(String movieName){
        if(isPossibleToRemoveMovie(movieName)){
            movies.remove(this.getMovie(movieName));
        }
    }
    public List<MovieTemplate> getMovies() {
        return movies;
    }
}
