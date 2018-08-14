package bgu.spl181.net.impl.MovieRentalService;

import bgu.spl181.net.impl.UserService.JsonUserHandler;
import bgu.spl181.net.impl.UserService.USTBProtocolShardData;

public class MRSProtocolShardData extends USTBProtocolShardData {
    private JsonMoviesHandler moviesHandler;

    public MRSProtocolShardData(JsonUserHandler usersHandler, JsonMoviesHandler moviesHandler) {
        super(usersHandler);
        this.moviesHandler = moviesHandler;
    }

    /**
     * getter for JsonMoviesHandler
     * @return JsonMoviesHandler
     */
    public JsonMoviesHandler getMovieHandler() {
        return moviesHandler;
    }

    public Movies getMovies(){
        return moviesHandler.getMovies();
    }

}
