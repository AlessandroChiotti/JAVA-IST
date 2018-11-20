package ist.java.data;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Date;

public abstract class AbstractPost implements Serializable {
    protected String author;
    protected String tweet;
    protected Date timestamp;

    public String toJson() {
        return new Gson().toJson(this);
    }

    @Override
    public String toString(){
        return "timestamp: " + timestamp + "\nauthor: " + '"' + author + '"' + "\ntweet: " + '"' + tweet + '"';
    }

    public String getAuthor(){
        return author;
    }
}
