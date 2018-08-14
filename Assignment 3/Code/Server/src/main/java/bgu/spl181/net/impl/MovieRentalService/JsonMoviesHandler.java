package bgu.spl181.net.impl.MovieRentalService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.io.*;
import java.util.*;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class JsonMoviesHandler {
    private Movies movies;
    private ReadWriteLock locker;
    private String movieFilePath;
    private Reader reader;
    private Gson gson =  new GsonBuilder().registerTypeAdapter(Integer.class,
            (JsonSerializer<Integer>)(integer, type, jsonSerializationContext) ->
                    new JsonPrimitive(integer.toString())).setPrettyPrinting().create();


    public JsonMoviesHandler(String movieFilePath){
        this.movies = new Movies();
        this.locker = new ReentrantReadWriteLock();//ReadWriteLock is the interface while ReentrantReadWriteLock is the implementation
        this.movieFilePath = movieFilePath;
        try {
            this.reader = new FileReader(movieFilePath);
        }
        catch (FileNotFoundException ex){}
        movies = gson.fromJson(reader , movies.getClass());
        if(movies == null){
            movies = new Movies();
        }
    }

    public void updateJson(){
        locker.writeLock().lock();
        try(FileWriter writer = new FileWriter(movieFilePath)){
            gson.toJson(movies, Movies.class, writer);
        }
        catch (IOException ex){}
        finally{//finally makes sure that the the commands below will be execute no matter what exception is thrown.
            locker.writeLock().unlock();
        }
    }

    public MovieTemplate getMovieTemplate(String movieName){
        locker.readLock().lock();
        MovieTemplate ans = movies.getMovie(movieName);
        locker.readLock().unlock();
        return ans;
    }

    public void Block(){
        locker.writeLock().lock();
    }

    public void UnBlock(){
        locker.writeLock().unlock();
    }

    public boolean isMovieExists(String movieName) {
        locker.readLock().lock();
        boolean ans = movies.containsMovieByName(movieName);
        locker.readLock().unlock();
        return ans;
    }

    public void addMovie (String movieName, int amount, int price, ArrayList<String> bannedCountries){
        locker.writeLock().lock();
            ArrayList<Integer> IDarrayList = new ArrayList<Integer>(); //list that contains all the IDs of the movies
            for (MovieTemplate movie :movies.getMovies()) {
                Integer convertedID = Integer.parseInt(movie.getId());//convert to int
                IDarrayList.add(convertedID);
            }
            Integer maxID;
            try{
                maxID = (Collections.max(IDarrayList)) + 1;//finds max and adds one
            }catch (NoSuchElementException ex){
                maxID = 1;
            }
            String movieID = String.valueOf(maxID);// convert back to string
            MovieTemplate movieTemplate = new MovieTemplate(movieID, movieName, price, bannedCountries, amount, amount);
            movies.addMovie(movieTemplate);
            updateJson();
        locker.writeLock().unlock();
    }
    public void removeMovie(String movieName){
        locker.writeLock().lock();
        movies.removeMovie(movieName);
        updateJson();
        locker.writeLock().unlock();
    }
    public boolean isPossibleToRemoveMovie(String movieName){
        return movies.isPossibleToRemoveMovie(movieName);
    }
    public void increaseAvilbleAmountByOne(String movieName){
        locker.writeLock().lock();
            movies.getMovie(movieName).increaseAvailableAmount();
            updateJson();
        locker.writeLock().unlock();
    }

    public void reduceAvilbleAmountByOne(String movieName){
        locker.writeLock().lock();
            movies.getMovie(movieName).reduceAvailableAmount();
            updateJson();
        locker.writeLock().unlock();
    }

    public boolean isBannedCountry(String movieName, String countryName){
        for (String country : getMovieTemplate(movieName).getBannedCountries()) {
            if(country.equalsIgnoreCase(countryName)){
                return true;
            }
        }
        return false;
    }

    public String movieTemplateToString(String movieName)  {
        locker.readLock().lock();
        MovieTemplate movie = getMovieTemplate(movieName);
        String name = movie.getName();
        int availableCopy = movie.getAvailableAmount();
        int price = movie.getPrice();
        List<String> bannedCountriesList = movie.getBannedCountries();
        String countriesString ="";
        if(!bannedCountriesList.isEmpty()){
            for (String country:bannedCountriesList){
                countriesString =countriesString + " \"" + country + "\"";
            }
        }
        //should look like that by the orders: <”movie name”> <No. copies left> <price> <”united states”,"israel"…>
        String ans = "\"" + name+ "\" " + availableCopy + " " + price + countriesString;
        locker.readLock().unlock();
        return ans;
    }

    public String movieListToString() {
        //should look like that :  "The Godfather" "The Pursuit Of Happiness" "The Notebook" "JusticeLeague"
        String moviesString = "";
        for (MovieTemplate movie :movies.getMovies()) {
            moviesString = moviesString + "\"" + movie.getName() + "\" ";
        }
        moviesString = moviesString.substring(0, moviesString.length()-1);
        return moviesString;
    }

    public int getNumberOfAvailableAmount(String movieName){
        return getMovieTemplate(movieName).getAvailableAmount();
    }


    public int getPrice(String movieName){
        return getMovieTemplate(movieName).getPrice();
    }
    public void setPrice(String movieName, int newPrice){
        getMovieTemplate(movieName).setPrice(newPrice);
        updateJson();
    }
    public String getMovieId(String movieName){
        return getMovieTemplate(movieName).getId();
    }

    public Movies getMovies(){
        return this.movies;
    }
}
