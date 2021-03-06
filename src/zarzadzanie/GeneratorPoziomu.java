package zarzadzanie;

import obiekty.Flaga;
import obiekty.blok.*;
import obiekty.nagroda.*;
import view.ZaladowanieObrazu;
import obiekty.Poziom;
import obiekty.postac.Przeciwnik;
import obiekty.postac.Rario;

import java.awt.*;
import java.awt.image.BufferedImage;

class GeneratorPoziomu {

    private ZaladowanieObrazu obraz;
    private BufferedImage tlo;
    private BufferedImage pasek, serce, moneta;
    private BufferedImage cegla, pytajnik, niezniszczalnaCegla, rura;
    private BufferedImage przeciwnikLewy, przeciwnikPrawy, flaga;

    GeneratorPoziomu(ZaladowanieObrazu obraz) {

        this.obraz = obraz;
        BufferedImage obiekty = obraz.zaladujObraz("/obiekty.png");

        this.tlo = obraz.zaladujObraz("/tlo.png");
        this.pasek = obraz.getMniejszyObraz(obiekty, 100, 150, 50, 50);
        this.serce= obraz.getMniejszyObraz(obiekty, 50, 150, 50, 50);
        this.moneta = obraz.getMniejszyObraz(obiekty, 0, 150, 50, 50);
        this.cegla = obraz.getMniejszyObraz(obiekty, 0, 0, 50, 50);
        this.pytajnik = obraz.getMniejszyObraz(obiekty, 50, 0, 50, 50);
        this.niezniszczalnaCegla = obraz.getMniejszyObraz(obiekty, 50, 50, 50, 50);
        this.rura = obraz.getMniejszyObraz(obiekty, 100, 0, 100, 100);
        this.przeciwnikLewy = obraz.getMniejszyObraz(obiekty, 0, 100, 50, 50);
        this.przeciwnikPrawy = obraz.getMniejszyObraz(obiekty, 50, 100, 50, 50);
        this.flaga = obraz.getMniejszyObraz(obiekty, 100, 100, 50, 50);

    }

    Poziom utworzPoziom(String sciezkaPoziomu) {
        BufferedImage rozstawieniePoziomu = obraz.zaladujObraz(sciezkaPoziomu);

        if (rozstawieniePoziomu == null) {
            System.out.println("Niepoprawna sciezka do pliku");
            return null;
        }

        Poziom poziom = new Poziom(tlo);
        String[] sciezki = sciezkaPoziomu.split("/");
        poziom.setSciezka(sciezki[sciezki.length-1]);

        int rarioPiksel = new Color(160, 160, 160).getRGB();
        int ceglaPiksel = new Color(0, 0, 255).getRGB();
        int pytajnikPiksel = new Color(255, 255, 0).getRGB();
        int niezniszczalnaCeglaPiksel = new Color(255, 0, 0).getRGB();
        int ruraPiksel = new Color(0, 255, 0).getRGB();
        int przeciwnikPiksel = new Color(0, 255, 255).getRGB();
        int flagaPiksel = new Color(160, 0, 160).getRGB();

        for (int x = 0; x < rozstawieniePoziomu.getWidth(); x++) {
            for (int y = 0; y < rozstawieniePoziomu.getHeight(); y++) {

                int piksel = rozstawieniePoziomu.getRGB(x, y);
                int odstepX = x*50;
                int odstepY = y*50;

                if (piksel == ceglaPiksel) {
                    Blok cegla = new Cegla(odstepX, odstepY, this.cegla);
                    poziom.dodajCegle(cegla);
                }
                else if (piksel == pytajnikPiksel) {
                    Nagroda nagroda = wygenerujNagrode(odstepX, odstepY);
                    Blok cegla = new Pytajnik(odstepX, odstepY, this.pytajnik, nagroda);
                    poziom.dodajCegle(cegla);
                }
                else if (piksel == ruraPiksel) {
                    Blok niezniszczalnaCegla = new Rura(odstepX, odstepY, this.rura);
                    poziom.dodajNiezniszczalnaCegle(niezniszczalnaCegla);
                }
                else if (piksel == niezniszczalnaCeglaPiksel) {
                    Blok niezniszczalnaCegla = new NiezniszczalnaCegla(odstepX, odstepY, this.niezniszczalnaCegla);
                    poziom.dodajNiezniszczalnaCegle(niezniszczalnaCegla);
                }
                else if (piksel == przeciwnikPiksel) {
                    Przeciwnik przeciwnik = new Przeciwnik(odstepX, odstepY, this.przeciwnikLewy);
                    przeciwnik.setPrawyObraz(przeciwnikPrawy);
                    poziom.dodajPrzeciwnika(przeciwnik);
                }
                else if (piksel == rarioPiksel) {
                    Rario rario = new Rario(odstepX, odstepY);
                    poziom.setRario(rario);
                }
                else if(piksel == flagaPiksel){
                    Flaga flaga= new Flaga(odstepX+24, odstepY, this.flaga);
                    poziom.koniec(flaga);
                }
            }
        }
        return poziom;
    }

    private Nagroda wygenerujNagrode(double x, double y){
        Nagroda nagroda;
        int liczba = (int)(Math.random() * 10);

        if(liczba == 0){
            nagroda = new Pasek(x, y, this.pasek);
        }
        else if(liczba == 1){
            nagroda = new Serce(x, y, this.serce);
        }
        else{
            nagroda = new Moneta(x, y, this.moneta, 50);
        }

        return nagroda;
    }


}
