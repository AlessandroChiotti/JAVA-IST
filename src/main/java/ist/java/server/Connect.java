package ist.java.server;

import java.io.*;
import java.net.Socket;
import ist.java.request.*;
import ist.java.data.*;
import java.util.*;

class Connect extends Thread
{
	// dichiarazione delle variabili socket e dei buffer
	Socket clientSocket;
    Object object;
    private Blog blog;

	public Connect(Socket clientSocket, Blog blog)
	{
        this.clientSocket = clientSocket;
        this.blog = blog;
	}

	public void run()
	{
		try(// Channel to send objects
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            // Channel to receive objects
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());){
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
                    System.out.println("Problem receiving or sending objects from/to client "
                            + clientSocket.getLocalSocketAddress());
                    System.exit(1);
                } catch (EOFException exc) {
                    System.out.println("Client " + clientSocket.getLocalSocketAddress() + " closed the connection");

                } catch (IOException exc) {
                    System.out.println("I/O Exception interacting with the client " + clientSocket.getLocalSocketAddress());

                }
		}
		catch (ObjectStreamException exc) {
            System.out.println("Problem receiving or sending objects from/to client "
                    + clientSocket.getLocalSocketAddress());
            System.exit(1);
        } catch (IOException exc) {
            System.out.println("I/O Exception interacting with the client");
            System.exit(1);
        }
        
	}
}