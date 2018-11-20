package ist.java.client;

import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Scanner;

import ist.java.data.*;
import ist.java.request.*;

public class Client {
    public static void main(String... args) {
        if (args.length != 2) {
            System.err.println("Usage: .\\gradlew runClient --args '<host name> <port number>'");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        // Start the client
        // socket, connect
        try (Socket clientSocket = new Socket(hostName, portNumber);
                ObjectOutputStream Objout = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream Objin = new ObjectInputStream(clientSocket.getInputStream());) {

            String selection;
            Scanner scanner = new Scanner(System.in);
            Date date = new Date();

            do {
                System.out.println("Select an action:");
                System.out.println("< 1: Write a tweet >");
                System.out.println("< 2: Read all tweets >");
                System.out.println("< 3: Read the last tweet >");
                System.out.println("< 4: Read all my tweets >");
                System.out.println("< x: Exit >");
                selection = scanner.nextLine();
                if (selection.equals("x")) {
                    System.out.println("Closing blog...");
                }
                else if (selection.equals("1")) {
                    try {
                        System.out.println("Please enter your username:");
                        String name = scanner.nextLine();
                        System.out.println("What's happening? (120 characters)");
                        String tweet = scanner.nextLine();
                        PostSubmission post = new PostSubmission(name, tweet, new Timestamp(date.getTime()));
                        Objout.writeObject(post);
                        System.out.println();
                        System.out.println(post.toString());
                        System.out.println();
                    } catch(SocketException exc){
                       System.out.println("Server " + hostName + " closed the connection");
                        System.exit(1);
                    } catch (ObjectStreamException exc) {
                        System.out.println("Problem receiving or sending objects from/to server "
                                + clientSocket.getLocalSocketAddress());
                        System.exit(1);
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }

                }
                else if(selection.equals("2") || selection.equals("3") || selection.equals("4")){
                    try {
                        int n, i;
                        AbstractPost post;
                        System.out.println("Please enter your username:");
                        String name = scanner.nextLine();
                        PostRequest request;
                        if (selection.equals("2")) {
                            request = new PostRequest(name, 1, new Timestamp(date.getTime()));
                            Objout.writeObject(request);
                            System.out.println("\nSending request...");
                            System.out.println("Displaying all tweets...\n");
                            n = (int) Objin.readObject();
                            for (i = 0; i < n; i++) {
                                post = (AbstractPost) Objin.readObject();
                                System.out.println(post.toString());
                                System.out.println();
                            }
                            if (i == 0) {
                                System.out.println("The blog is empty!\n");
                            }
                        }
                        else if (selection.equals("3")) {
                            request = new PostRequest(name, 2, new Timestamp(date.getTime()));

                            Objout.writeObject(request);
                            System.out.println("\nSending request...");
                            System.out.println("Displaying the last tweet...\n");
                            n = (int) Objin.readObject();
                            if (n == 1) {
                                post = (AbstractPost) Objin.readObject();
                                System.out.println(post.toString());
                                System.out.println();
                            }
                            if (n == 0) {
                                System.out.println("The blog is empty!\n");
                            }

                        }
                        else if (selection.equals("4")) {
                            request = new PostRequest(name, 3, new Timestamp(date.getTime()));

                            Objout.writeObject(request);
                            System.out.println("\nSending request...");
                            System.out.println("Displaying all your tweets...\n");
                            n = (int) Objin.readObject();
                            for (i = 0; i < n; i++) {
                                post = (AbstractPost) Objin.readObject();
                                System.out.println(post.toString());
                                System.out.println();
                            }
                            if (i == 0) {
                                System.out.println("You didn't write any tweet\n");
                            }
                        }
                    }catch(SocketException exc){
                        System.out.println("Server " + hostName + " closed the connection");
                        System.exit(1);
                    } catch (ClassNotFoundException exc) {
                        System.out.println("Class of a serialized object cannot be found");
                        System.exit(1);
                    } catch (ObjectStreamException exc) {
                        System.out.println("Problem receiving or sending objects from/to server "
                                + clientSocket.getLocalSocketAddress());
                        System.exit(1);
                    } catch (EOFException exc) {
                        System.out.println("Server error: input stream has reached the end");
                        System.exit(1);
                    } catch (IOException exc) {
                        System.out.println("I/O Exception interacting with the server");
                        System.exit(1);
                    }
                }
                else {
                    System.out.println("Wrong slection\n");
                }
            } while (!selection.equals("x"));

            scanner.close();
            // Implicit closing
        } catch (UnknownHostException e) {
            System.err.println("The IP address" + hostName + "of the host could not be determined ");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
    }
}
