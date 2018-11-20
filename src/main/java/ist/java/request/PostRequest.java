package ist.java.request;
import ist.java.data.*;
import java.util.Date;

public class PostRequest extends AbstractPost {
    public PostRequest(String name, Date timestamp){
        this.author = name;
        this.timestamp = timestamp;
    }
}
