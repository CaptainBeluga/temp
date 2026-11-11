package org.lake;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final Spettacolo spettacolo;

    public ClientHandler(Socket socket, Spettacolo spettacolo) {
        this.socket = socket;
        this.spettacolo = spettacolo;
    }

    @Override
    public void run() {

        String clientId = socket.getInetAddress().getHostAddress()
                + ":" + socket.getPort();

        System.out.printf("[%s] Sessione aperta%n", clientId);

        try (
                Socket s = this.socket;
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8),
                        true);
        ) {
            String msg;
            while ((msg = in.readLine()) != null) {

                String richiesta = msg.trim();

                System.out.printf("[%s] RICHIESTA: %s%n", clientId, richiesta);

                String risposta;

                // Riga vuota
                if (richiesta.isEmpty()) {
                    risposta = "ERRORE;Sintassi non valida";
                    out.println(risposta);
                    System.out.printf("[%s] RISPOSTA: %s%n", clientId, risposta);
                    continue;
                }

                String[] data = richiesta.split("\\s+");
                String comando = data[0];

                switch (comando) {

                    case "DISPONIBILI":
                        if (data.length != 1) {
                            risposta = "ERRORE;Sintassi non valida";
                        } else {
                            risposta = "OK;Posti disponibili: " + spettacolo.getSeats();
                        }
                        break;

                    case "PRENOTA": {
                        if (data.length != 2) {
                            risposta = "ERRORE;Sintassi non valida";
                            break;
                        }
                        Integer q = parseQuantita(data[1], out, clientId);
                        if (q == null) continue; // parseQuantita ha già risposto
                        risposta = spettacolo.book(q);
                        break;
                    }

                    case "ANNULLA": {
                        if (data.length != 2) {
                            risposta = "ERRORE;Sintassi non valida";
                            break;
                        }
                        Integer q = parseQuantita(data[1], out, clientId);
                        if (q == null) continue; // parseQuantita ha già risposto
                        risposta = spettacolo.cancel(q);
                        break;
                    }

                    case "ESCI":
                        if (data.length != 1) {
                            risposta = "ERRORE;Sintassi non valida";
                        } else {
                            risposta = "OK;Connessione in chiusura";
                            out.println(risposta);
                            System.out.printf("[%s] RISPOSTA: %s%n", clientId, risposta);
                            System.out.printf("[%s] Sessione chiusa su richiesta del client%n", clientId);
                            return;
                        }
                        break;

                    default:
                        // include comandi sconosciuti E comandi non maiuscoli (es. "prenota")
                        risposta = "ERRORE;Comando sconosciuto";
                        break;
                }

                out.println(risposta);
                System.out.printf("[%s] RISPOSTA: %s%n", clientId, risposta);
            }

            System.out.printf("[%s] Sessione chiusa (client disconnesso)%n", clientId);

        } catch (Exception e) {
            System.err.printf("[%s] Errore nella gestione del client: %s%n",
                    clientId, e.getMessage());
        }
    }


    
    private Integer parseQuantita(String testo, PrintWriter out, String clientId) {
        try {
            return Integer.parseInt(testo);
        } catch (NumberFormatException e) {
            String risposta = "ERRORE;Quantità non valida";
            out.println(risposta);
            System.out.printf("[%s] RISPOSTA: %s%n", clientId, risposta);
            return null;
        }
    }
}