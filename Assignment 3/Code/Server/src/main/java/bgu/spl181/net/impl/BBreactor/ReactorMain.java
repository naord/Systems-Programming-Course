package bgu.spl181.net.impl.BBreactor;

import bgu.spl181.net.api.MessageEncoderDecoder;
import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.impl.UserService.MessageEncoderDecoderImp;
import bgu.spl181.net.impl.MovieRentalService.JsonMoviesHandler;
import bgu.spl181.net.impl.MovieRentalService.MRSProtocol;
import bgu.spl181.net.impl.MovieRentalService.MRSProtocolShardData;
import bgu.spl181.net.impl.UserService.JsonUserHandler;
import bgu.spl181.net.srv.Server;

import java.util.function.Supplier;

public class ReactorMain {

    public static void main(String[] args) {
        Integer port = Integer.parseInt(args[0]);
        String path = System.getProperty("user.dir");
        String pathUsers = path + "/Database/Users.json";
        String pathMovies = path + "/Database/Movies.json";
        JsonMoviesHandler moviesHandler = new JsonMoviesHandler(pathMovies);
        JsonUserHandler userHandler = new JsonUserHandler(pathUsers);
        MRSProtocolShardData shardData = new MRSProtocolShardData(userHandler, moviesHandler);
        Supplier<BidiMessagingProtocol<String>> protocolSupplier = ()-> new MRSProtocol(shardData);
        Supplier<MessageEncoderDecoder<String>> encoderDecoderSupplier = ()-> new MessageEncoderDecoderImp();
        Server.reactor(7, port, protocolSupplier, encoderDecoderSupplier).serve();
    }
}
