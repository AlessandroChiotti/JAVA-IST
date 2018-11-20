package ist.java.data;

import java.io.IOException;
import java.util.List;

public interface Readable {
    AbstractPost readOne(); //returns latest tweet
    List<AbstractPost> readAll() throws IOException; //returns all tweets
    List<AbstractPost> readOwnPost() throws IOException; //returns only E from the same author

}
