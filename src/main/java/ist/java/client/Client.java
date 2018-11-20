package ist.java.client;

import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Scanner;

import ist.java.data.*;
import ist.java.request.*;

public class Client {
    public static void main(String... args){
        if (args.length != 2) {
            System.err.println(
                "Usage: .\\gradlew runClient --args '<host name> <port number>'");
            System.exit(0);
        }
        
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        // Start the client
        // socket, connect
        try (Socket clientSocket = new Socket(hostName, portNumber);) {
            String selection;
            Scanner scanner = new Scanner(System.in);
            ObjectOutputStream Objout = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream Objin = new ObjectInputStream(clientSocket.getInputStream());
            Date date = new Date();
            do{
                System.out.println("Select an action:");
                System.out.println("< 1: Write a tweet >");
                System.out.println("< 2: Read all tweet >");
                System.out.println("< x: Exit >");
                selection = scanner.nextLine();
                if(selection.equals("0")){
                    System.out.println("Input error, try to reinsert your choice\n");
                }
                if(selection.equals("1")){      
                    try {
                        System.out.println("Please enter your username:");
                        String name = scanner.nextLine();
                        System.out.println("What's happening? (120 characters)");
                        String tweet = scanner.nextLine();
                        PostSubmission post = new PostSubmission(name, tweet, new Timestamp(date.getTime()));
                        System.out.println();
                        System.out.println(post.toString());
                        System.out.println();
                        Objout.writeObject(post);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                else if(selection.equals("2")){
                    AbstractPost post;
                    System.out.println("Please enter your username:");
                    String name = scanner.nextLine();
                    PostRequest request = new PostRequest(name, new Timestamp(date.getTime()));
                    Objout.writeObject(request);
                    System.out.println("\nSending request...");
                    int n, i;
                    try {
                        System.out.println("Displaying all tweets...\n");
                        n =(int) Objin.readObject();
                        for(i=0; i<n ; i++) {
                            post = (AbstractPost) Objin.readObject();
                            System.out.println(post.toString());
                            System.out.println();
                        }
                        if(i==0){
                            System.out.println("The blog is empty!\n");
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
                }
                else if(selection.equals("x")){
                    System.out.println("Closing blog...");
                }
                else{
                    System.out.println("Wrong slection\n");
                }

            }while(!selection.equals("x"));

            clientSocket.close();
            scanner.close();
        // Implicit closing
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }
    }
}
