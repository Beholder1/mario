package zarzadzanie;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Wejscie implements KeyListener {

    private SilnikGry silnik;

    Wejscie(SilnikGry silnik) {
        this.silnik = silnik; }

    @Override
    public void keyPressed(KeyEvent akcja) {
        int klawisz = akcja.getKeyCode();
        StatusGry status = silnik.getStatusGry();
        AkcjeKlawiszy obecnaAkcja = AkcjeKlawiszy.BRAK;

        if (klawisz == KeyEvent.VK_UP) {
            if(status == StatusGry.MENU || status == StatusGry.POZIOMY)
                obecnaAkcja = AkcjeKlawiszy.W_GORE;
            else
                obecnaAkcja = AkcjeKlawiszy.SKOK;
        }
        else if(klawisz == KeyEvent.VK_DOWN){
            if(status == StatusGry.MENU || status == StatusGry.POZIOMY)
                obecnaAkcja = AkcjeKlawiszy.W_DOL;
        }
        else if (klawisz == KeyEvent.VK_RIGHT) {
            obecnaAkcja = AkcjeKlawiszy.W_PRAWO;
        }
        else if (klawisz == KeyEvent.VK_LEFT) {
            obecnaAkcja = AkcjeKlawiszy.W_LEWO;
        }
        else if (klawisz == KeyEvent.VK_ENTER) {
            obecnaAkcja = AkcjeKlawiszy.WYBIERZ;
        }
        else if (klawisz == KeyEvent.VK_ESCAPE) {
            if(status == StatusGry.W_TRAKCIE || status == StatusGry.ZATRZYMANO )
                obecnaAkcja = AkcjeKlawiszy.PAUZA;
            else
                obecnaAkcja = AkcjeKlawiszy.POWROT;

        }
        przeslijWejscie(obecnaAkcja);
    }

    @Override
    public void keyReleased(KeyEvent akcja) {
        if(akcja.getKeyCode() == KeyEvent.VK_RIGHT || akcja.getKeyCode() == KeyEvent.VK_LEFT)
            przeslijWejscie(AkcjeKlawiszy.PO_NACISNIECIU);
    }

    private void przeslijWejscie(AkcjeKlawiszy akcja) {
        if(akcja != AkcjeKlawiszy.BRAK)
            silnik.uzyskajWejscie(akcja);
    }

    @Override
    public void keyTyped(KeyEvent a) {}
}
