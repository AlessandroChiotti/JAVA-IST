package ist.java.server;

import ist.java.data.Blog;
import java.net.*;
import java.io.*;

public class BlogServer {

    private static Blog blog = new Blog();

    public static void main(String... args) throws IOException, ClassNotFoundException {
        if (args.length != 1) {
            System.err.println("Usage: .\\gradlew runServer --args '<port number>'");
            System.exit(0);
        }
        int portNumber = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(portNumber);) {
            System.out.println("Sever is running...");

            while (true) {
                //System.out.println("Waiting for a new client...");
                try {
                    // accept
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client " + clientSocket.getLocalSocketAddress()+ clientSocket.getLocalPort() + " connected\n");
                    Connect newConnection = new Connect(clientSocket, blog);
                    newConnection.start();
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