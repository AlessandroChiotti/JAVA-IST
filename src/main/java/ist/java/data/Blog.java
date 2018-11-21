package ist.java.data;

import java.io.*;
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
            try (InputStream fileStream = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileStream);) {
                Object post;
                while (true) {
                    post = in.readObject();
                    tweets.add((AbstractPost) post);
                }
            } catch (EOFException exc) {
                System.out.println("Database correctly read from disk");
            } catch (FileNotFoundException exc) {
                System.out.println("Database" + path + "does not found or cannot be opened for reading.");
            } catch (ObjectStreamException exc) {
                System.out.println("Problem reading data from Database" + path);
            } catch (ClassNotFoundException e) {
                System.out.println("Class of a serialized object cannot be found");
            }
        } else {
            try (OutputStream fileStream = new FileOutputStream(file);
                    ObjectOutputStream out = new ObjectOutputStream(fileStream);) {
            } catch (FileNotFoundException exc) {
                System.out.println("Database '" + path + "' does not found or cannot be opened for reading.");
            }
        }
    }

    @Override
    public void save() throws IOException {
        File file = new File(path);
        try (OutputStream fileStream = new FileOutputStream(file, false);
                ObjectOutputStream out = new ObjectOutputStream(fileStream);) {
            if (!file.exists()) {
                System.out.println("Error saving database, it is corrupted!");
                System.exit(0);
            } else {
                for (AbstractPost p : tweets) {
                    out.writeObject(p);
                }
            }
        } 
        catch (FileNotFoundException exc) {
            System.out.println("Database '" + path + "' does not found or cannot be opened for reading.");
        }
        return;
    }

    @Override
    public AbstractPost readOne() {
        if(!tweets.isEmpty())
            return tweets.get(tweets.size() - 1);
        else
            return null;
    }

    @Override
    public List<AbstractPost> readAll() throws IOException {
        return tweets;
    }

    @Override
    public List<AbstractPost> readOwnPost(String name) throws IOException {
        List<AbstractPost> ownPosts = new LinkedList<>();
        for (AbstractPost p : tweets) {
            if (p.getAuthor().equals(name))
                ownPosts.add(p);
        }
        return ownPosts;
    }

    public void addPost(AbstractPost p) {
        tweets.add(p);
        try {
            this.save();
        } catch (IOException e) {
            System.out.println("Catched problems saving the database");
            e.printStackTrace();
        }
        return;
    }

}