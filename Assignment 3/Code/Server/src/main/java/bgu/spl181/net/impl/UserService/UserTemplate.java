package bgu.spl181.net.impl.UserService;
import bgu.spl181.net.impl.MovieRentalService.UserMovies;

import java.util.*;

public class UserTemplate {
    private String username;
    private String type;
    private String password;
    private String country;
    private ArrayList<UserMovies> movies = new ArrayList<UserMovies>();
    private Integer balance;

    public UserTemplate(String username, String password, String country){
        this.username = username;
        this.type = "normal";
        this.password = password;
        this.country = country;
        this.balance = 0;
    }

    public boolean isUserAdmin(){
        return this.type.equals("admin");
    }
    public String getUsername(){
        return this.username;
    }

    public String getCountry() {
        return country;
    }

    public String getPassword() {
        return password;
    }

    public ArrayList<UserMovies> getMovies() {
        return movies;
    }
    public Integer getBalance(){
        return this.balance;
    }

    public void addToBalance(Integer balance) {
        this.balance = this.balance + balance;
    }

    public void addARentedMovieToUser(String movieId, String movieName, int price) {
        UserMovies newMovie = new UserMovies(movieId , movieName);
        addToBalance(-price);
        this.movies.add(newMovie);
    }

    public void removeFromRentedMovies(String movieId, String movieName) {
        for (UserMovies movie:movies) {
            if(movie.getName().equals(movieName) && movie.getId().equals(movieId)){
                this.movies.remove(movie);
                return;
            }
        }
    }

    public boolean hasMovie(String movieName){
        for (UserMovies movie:movies) {
            if(movie.getName().equals(movieName)){
                return true;
            }
        }
        return false;
    }
}

