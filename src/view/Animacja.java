package view;

import java.awt.image.BufferedImage;

public class Animacja {

    private int indeks = 0, licznik = 0;
    private BufferedImage[] leweKlatki, praweKlatki;
    private BufferedImage obecnaKlatka;

    public Animacja(BufferedImage[] leweKlatki, BufferedImage[] praweKlatki){
        this.leweKlatki = leweKlatki;
        this.praweKlatki = praweKlatki;

        obecnaKlatka = praweKlatki[1];
    }

    public BufferedImage animuj(boolean czyWPrawo){
        licznik++;
        BufferedImage[] klatki = czyWPrawo ? praweKlatki : leweKlatki;

        if(licznik > 5){
            if(indeks + 3 > klatki.length)
                indeks = 0;

            obecnaKlatka = klatki[indeks+2];
            indeks++;
            licznik = 0;
        }

        return obecnaKlatka;
    }

    public BufferedImage[] getLeweKlatki() {
        return leweKlatki;
    }

    public BufferedImage[] getPraweKlatki() {
        return praweKlatki;
    }

}
