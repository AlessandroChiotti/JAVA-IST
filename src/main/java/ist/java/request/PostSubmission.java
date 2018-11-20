package ist.java.request;
import ist.java.data.*;
import java.util.Date;

public class PostSubmission extends AbstractPost {

	public PostSubmission(String name, String tweet, Date timestamp){
        this.author = name;
        this.tweet = tweet;
        this.timestamp = timestamp;
    }

	public String getAuthor() {
		return author;
	}

}
