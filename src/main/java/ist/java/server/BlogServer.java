package ist.java.server;

import ist.java.data.AbstractPost;
import ist.java.data.Blog;
import ist.java.request.PostRequest;
import ist.java.request.PostSubmission;
import java.util.List;
import java.net.*;
import java.io.*;

public class BlogServer {

    private static Blog blog = new Blog();

    public static void main(String... args) throws IOException, ClassNotFoundException {
        // if (args.length != 1) {
        //     System.err.println("Usage: .\\gradlew runServer --args '<port number>'");
        //     System.exit(0);
        // }
        int portNumber = 4040; //Integer.parseInt(args[0]);
        Object object;

        try (ServerSocket serverSocket = new ServerSocket(portNumber);) {
            System.out.println("Sever is running...");

            while (true) {
                System.out.println("Waiting for a new client...");
                try ( // accept
                        Socket clientSocket = serverSocket.accept();
                        // Channel to send objects
                        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                        // Channel to receive objects
                        ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());) {

                    System.out.println("Client " + clientSocket.getLocalSocketAddress() + " connected\n");

                    try {
                        while (true) {
                            object = in.readObject();
                            if (object instanceof PostRequest) {
                                int type = ((PostRequest) object).getType();
                                if (type == 1) {
                                    System.out.println("All posts requested by client...");
                                    List<AbstractPost> tweets = blog.readAll();
                                    if (!tweets.isEmpty()) {
                                        System.out.println("Sending " + tweets.size() + " posts");
                                        out.writeObject(tweets.size());
                                        for (AbstractPost p : tweets) {
                                            out.writeObject(p);
                                        }
                                    } else {
                                        System.out.println("Sending 0 posts...");
                                        out.writeObject((Object) 0);
                                    }
                                }
                                else if(type == 2){
                                    System.out.println("Last post requested by client...");
                                    AbstractPost tweet = blog.readOne();
                                    if (tweet != null) {
                                        System.out.println("Sending last post");
                                        out.writeObject(1);
                                        out.writeObject(tweet);
                                    } else {
                                        System.out.println("Sending 0 posts...");
                                        out.writeObject((Object) 0);
                                    }
                                }
                                else if (type == 3){
                                    String author = ((PostRequest) object).getAuthor();
                                    System.out.println("All personal posts requested by client " + author + "...");
                                    List<AbstractPost> tweets = blog.readOwnPost(author);
                                    if (!tweets.isEmpty()) {
                                        System.out.println("Sending " + tweets.size() + " posts");
                                        out.writeObject(tweets.size());
                                        for (AbstractPost p : tweets) {
                                            out.writeObject(p);
                                        }
                                    } else {
                                        System.out.println("Sending 0 posts...");
                                        out.writeObject((Object) 0);
                                    }
                                }
                            } else if (object instanceof PostSubmission) {
                                System.out.println("\nAdding a new post by client '"
                                        + ((PostSubmission) object).getAuthor() + "'\n");
                                blog.addPost((PostSubmission) object);
                            }
                        }
                    } catch (ClassNotFoundException exc) {
                        System.out.println("Class of a serialized object cannot be found");
                        System.exit(1);
                    } catch (ObjectStreamException exc) {
                        System.out.println("Problem receiving or sendind objects from/to client "
                                + clientSocket.getLocalSocketAddress());
                        System.exit(1);
                    } catch (EOFException exc) {
                        System.out.println("Client " + clientSocket.getLocalSocketAddress() + " closed the connection");

                    } catch (IOException exc) {
                        System.out.println("I/O Exception interacting with the client " + clientSocket.getLocalSocketAddress());

                    }
                } catch (SocketTimeoutException exc) {
                    exc.printStackTrace();
                } catch (IOException exc) {
                    System.out.println(
                            "Exception caught waiting for a connection or because after the connection the socket is closed, the socket is not connected, or the socket input has been shutdown");
                }
            }
        } catch (IllegalArgumentException exc) {
            System.out.println(
                    "Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            System.out.println(exc.getMessage());
            System.exit(1);
        } catch (IOException exc) {
            System.out.println("I/O Exception opening the socket");
            exc.printStackTrace();
            System.exit(1);
        }
    }

}