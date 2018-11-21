package ist.java.request;
import ist.java.data.*;
import java.util.Date;

public class PostRequest extends AbstractPost {
    protected int type;
    public PostRequest(String name, int type, Date timestamp){
        this.author = name;
        this.type = type;
        this.timestamp = timestamp;
    }

    public int getType(){
        return type;
    }
}
