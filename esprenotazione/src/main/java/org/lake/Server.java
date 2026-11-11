package org.lake;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {

        int port = 12345;


        Spettacolo spettacolo = new Spettacolo();

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.printf("Server in ascolto su localhost:%d%n", port);
            System.out.println("Capienza iniziale: " + spettacolo.getSeats() + " posti");

            while (true) {
                Socket socket = serverSocket.accept();

                System.out.printf("Nuovo client connesso da %s:%d%n",
                        socket.getInetAddress().getHostAddress(),
                        socket.getPort());

                        
                new Thread(new ClientHandler(socket, spettacolo)).start();
            }

        } catch (IOException e) {
            System.err.println("Errore sul server: " + e.getMessage());
        }
    }
}