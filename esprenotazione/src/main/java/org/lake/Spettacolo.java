package org.lake;

public class Spettacolo {

    private static final int MAX_SEATS = 30;

    private int available = MAX_SEATS;


    
    public synchronized int getSeats() {
        return this.available;
    }


    public synchronized String book(int q) {
        if (q <= 0) {
            return "ERRORE;La quantità deve essere positiva";
        }
        if (q > available) {
            return "ERRORE;Posti insufficienti;Disponibili: " + available;
        }
        available -= q;
        return "OK;Prenotazione effettuata;Disponibili: " + available;
    }



    public synchronized String cancel(int q) {
        if (q <= 0) {
            return "ERRORE;La quantità deve essere positiva";
        }
        int prenotati = MAX_SEATS - available;
        if (q > prenotati) {
            return "ERRORE;Annullamento non consentito;Disponibili: " + available;
        }
        available += q;
        return "OK;Annullamento effettuato;Disponibili: " + available;
    }
}