package org.lake;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {

        String host = "localhost";
        int porta = 12345;

        try (
                Socket socket = new Socket(host, porta);

                // Input Tastiera
                Scanner keyInput = new Scanner(
                        System.in,
                        StandardCharsets.UTF_8.name());

                // Lettura messaggi dal Server
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream(),
                                StandardCharsets.UTF_8));

                // Invio messaggi al Server
                PrintWriter output = new PrintWriter(
                        new OutputStreamWriter(
                                socket.getOutputStream(),
                                StandardCharsets.UTF_8),
                        true);

        ) {

            System.out.printf("Connesso al Server %s:%d\n",
                    socket.getInetAddress().getHostAddress(),
                    socket.getPort());

            while (true) {

                // Stampa del menu
                System.out.println("\n=== MENU ===");
                System.out.println("1. Consulta i posti disponibili");
                System.out.println("2. Prenota posti");
                System.out.println("3. Annulla posti");
                System.out.println("0. Esci");
                System.out.print("Scelta: ");

                String scelta = keyInput.nextLine().trim();

                String msg;

                switch (scelta) {
                    case "1":
                        // Comando di consultazione
                        msg = "DISPONIBILI";
                        break;

                    case "2":
                        // Chiede la quantità da prenotare
                        System.out.print("Quantità di posti da prenotare: ");
                        String quantitaPrenota = keyInput.nextLine().trim();
                        msg = "PRENOTA " + quantitaPrenota;
                        break;

                    case "3":
                        // Chiede la quantità da annullare
                        System.out.print("Quantità di posti da annullare: ");
                        String quantitaAnnulla = keyInput.nextLine().trim();
                        msg = "ANNULLA " + quantitaAnnulla;
                        break;

                    case "0":
                        // Uscita: invia ESCI, attende risposta e termina
                        output.println("ESCI");
                        String rispostaUscita = input.readLine();
                        if (rispostaUscita != null) {
                            System.out.printf("\n[SERVER]: %s\n", rispostaUscita);
                        }
                        System.out.println("Uscita dal programma.");
                        return;

                    default:
                        // Scelta non prevista: messaggio locale, niente invio
                        System.out.println("Scelta non valida. Riprova.");
                        continue;
                }

                // Invia il comando al Server
                output.println(msg);

                // Riceve la risposta
                String response = input.readLine();

                if (response == null) {
                    System.out.println("Il Server ha chiuso la connessione");
                    break;
                }

                System.out.printf("\n[SERVER]: %s\n", response);
            }

        } catch (java.net.ConnectException e) {
            System.out.println("Impossibile connettersi al server. Verifica che sia avviato.");
        } catch (Exception e) {
            System.out.println("Errore di comunicazione: " + e.getMessage());
        }
    }
}