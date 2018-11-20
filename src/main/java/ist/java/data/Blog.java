package ist.java.data;

import java.io.*;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;

public class Blog implements Readable, Writable {
    List<AbstractPost> tweets;
    String path = "database";

    public Blog() {
        tweets = new LinkedList<>();
        try {
            populateFromDisk();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populateFromDisk() throws IOException {
        File file = new File(path);
        if (file.exists()) {
            InputStream fileStream = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fileStream);
            Object post;
            try {
                while (true) {
                    post = in.readObject();
                    tweets.add((AbstractPost) post);
                }
            } catch (SocketTimeoutException exc) {
                // you got the timeout
            } catch (EOFException exc) {
                // end of stream
            } catch (IOException exc) {
                // some other I/O error: print it, log it, etc.
                exc.printStackTrace(); // for example
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            in.close();

        } else {
            OutputStream fileStream = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fileStream);
            out.close();
        }
    }

    @Override
    public void save() throws IOException {
        File file = new File(path);
        OutputStream fileStream = new FileOutputStream(file, false);
        if (!file.exists()) {
            System.out.println("Error saving file, the file corrupted!");
            System.exit(0);
        } 
        else {
            ObjectOutputStream out = new ObjectOutputStream(fileStream);
            for (AbstractPost p : tweets) {
                out.writeObject(p);
            }
            out.close();
        }
        return;
    }

    @Override
    public AbstractPost readOne() {
        return null;
    }

    @Override
    public List<AbstractPost> readAll() throws IOException {
        return tweets;
    }

    @Override
    public List<AbstractPost> readOwnPost() throws IOException {
        return null;
    }

    public void addPost(AbstractPost p) {
        tweets.add(p);
        try {
            this.save();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return;
    }

}
