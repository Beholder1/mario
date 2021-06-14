package manager;

import obiekty.postac.Rario;
import view.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class SilnikGry implements Runnable {

    private final static int SZEROKOSC = 1268, WYSOKOSC = 708;

    private ZarzadzaniePoziomem zarzadzaniePoziomem;
    private InterfejsUzytkownika interfejsUzytkownika;
    private StatusGry statusGry;
    private boolean czyDziala;
    private Kamera kamera;
    private ZaladowanieObrazu zaladowanieObrazu;
    private Thread thread;
    private WyborWMenu wyborWMenu = WyborWMenu.GRAJ;
    private int wybranyPoziom = 0;

    ArrayList<Rekord> rekordy;

    private SilnikGry() throws IOException, ClassNotFoundException {
        rozpoczecie();
    }

    private void rozpoczecie() throws IOException, ClassNotFoundException {
        zaladowanieObrazu = new ZaladowanieObrazu();
        Wejscie wejscie = new Wejscie(this);
        statusGry = StatusGry.MENU;
        kamera = new Kamera();
        interfejsUzytkownika = new InterfejsUzytkownika(this, SZEROKOSC, WYSOKOSC);
        zarzadzaniePoziomem = new ZarzadzaniePoziomem();
        ArrayList<Rekord> listaRekordow = new ArrayList<>();
        File plik = new File(".\\rekordy.dat");
        if(!plik.exists())
        {
            try {
                ObjectOutputStream wy = new ObjectOutputStream(new FileOutputStream(".\\rekordy.dat"));
                listaRekordow.add(new Rekord("Poziom 1", 0));
                listaRekordow.add(new Rekord("Poziom 2", 0));
                listaRekordow.add(new Rekord("Poziom 3", 0));
                listaRekordow.add(new Rekord("Poziom 4", 0));
                listaRekordow.add(new Rekord("Poziom 5", 0));
                wy.writeObject(listaRekordow);
            }
            catch (Exception e) {

            }
        }
        rekordy = wczytajZPliku(".\\rekordy.dat");
        JFrame klatka = new JFrame("Rario");
        klatka.add(interfejsUzytkownika);
        klatka.addKeyListener(wejscie);
        klatka.pack();
        klatka.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        klatka.setResizable(false);
        klatka.setLocationRelativeTo(null);
        klatka.setVisible(true);

        start();
    }

    private synchronized void start() {
        if (czyDziala)
            return;

        czyDziala = true;
        thread = new Thread(this);
        thread.start();
    }

    private void reset(){
        resetKamery();
        setStatusGry(StatusGry.MENU);
    }

    public void resetKamery() {
        kamera = new Kamera();
    }

    public void wybierzPoziom(){
        String sciezka = interfejsUzytkownika.wybierzPoziom(wybranyPoziom);
        if (sciezka != null) {
            stworzPoziom(sciezka);
        }
    }

    public void zmienPoziom(boolean czyWGore){
        wybranyPoziom = interfejsUzytkownika.zmienPoziom(wybranyPoziom, czyWGore);
    }

    private void stworzPoziom(String sciezka) {
        boolean czyZaladowany = zarzadzaniePoziomem.stworzPoziom(zaladowanieObrazu, sciezka);
        if(czyZaladowany){
            setStatusGry(StatusGry.W_TRAKCIE);
        }

        else
            setStatusGry(StatusGry.MENU);
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double liczbaTikow = 60.0;
        double ns = 1000000000 / liczbaTikow;
        double delta = 0;

        while (czyDziala && !thread.isInterrupted()) {

            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                if (statusGry == StatusGry.W_TRAKCIE) {
                    try {
                        petlaGry();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                delta--;
            }
            renderuj();
        }
    }

    private void renderuj() {
        interfejsUzytkownika.repaint();
    }

    private void aktualizujRanking() throws IOException {
        if (rekordy.get(wybranyPoziom).getRekord() < getPunkty()){
            ObjectOutputStream wyjscie = new ObjectOutputStream(new FileOutputStream(".\\rekordy.dat"));
            rekordy.set(wybranyPoziom, new Rekord("Poziom " + (wybranyPoziom +1), getPunkty()));
            wyjscie.writeObject(rekordy);
        }
    }

    private void petlaGry() throws IOException {
        zarzadzaniePoziomem.zmienPolozenia();
        sprawdzKolizje();
        aktualizujKamere();

        if (czyPrzegrano()) {
            aktualizujRanking();
            setStatusGry(StatusGry.PRZEGRANA);
        }

        int missionPassed = getWygrana();
        if(missionPassed > -1){
            zarzadzaniePoziomem.zyskajPunkty(missionPassed);
        } else if(zarzadzaniePoziomem.koniecPoziomu()) {
            aktualizujRanking();
            setStatusGry(StatusGry.WYGRANA);
        }
    }

    private void aktualizujKamere() {
        Rario rario = zarzadzaniePoziomem.getRario();
        double predkoscRarioWX = rario.getPredkoscWX();
        double shiftPrzesuniecia = 0;

        if (predkoscRarioWX > 0 && rario.getX() - 600 > kamera.getX()) {
            shiftPrzesuniecia = predkoscRarioWX;
        }

        kamera.moveCam(shiftPrzesuniecia, 0);
    }

    private void sprawdzKolizje() {
        zarzadzaniePoziomem.sprawdzKolizje(this);
    }

    public void uzyskajWejscie(AkcjeKlawiszy wejscie) {

        if (statusGry == StatusGry.MENU) {
            if (wejscie == AkcjeKlawiszy.WYBIERZ && wyborWMenu == WyborWMenu.GRAJ) {
                startGry();
            } else if (wejscie == AkcjeKlawiszy.WYBIERZ && wyborWMenu == WyborWMenu.TWORCY) {
                setStatusGry(StatusGry.TWORCY);
            } else if (wejscie == AkcjeKlawiszy.WYBIERZ && wyborWMenu == WyborWMenu.STEROWANIE) {
                setStatusGry(StatusGry.STEROWANIE);
            } else if (wejscie == AkcjeKlawiszy.WYBIERZ && wyborWMenu == WyborWMenu.RANKING) {
                setStatusGry(StatusGry.RANKING);
            } else if (wejscie == AkcjeKlawiszy.W_GORE) {
                wybierzOpcje(true);
            } else if (wejscie == AkcjeKlawiszy.W_DOL) {
                wybierzOpcje(false);
            }
        }
        else if(statusGry == StatusGry.POZIOMY){
            if(wejscie == AkcjeKlawiszy.WYBIERZ){
                wybierzPoziom();
            }
            else if(wejscie == AkcjeKlawiszy.W_GORE){
                zmienPoziom(true);
            }
            else if(wejscie == AkcjeKlawiszy.W_DOL){
                zmienPoziom(false);
            }
        } else if (statusGry == StatusGry.W_TRAKCIE) {
            Rario rario = zarzadzaniePoziomem.getRario();
            if (wejscie == AkcjeKlawiszy.SKOK) {
                rario.skok();
            } else if (wejscie == AkcjeKlawiszy.W_PRAWO) {
                rario.rusz(true, kamera);
            } else if (wejscie == AkcjeKlawiszy.W_LEWO) {
                rario.rusz(false, kamera);
            } else if (wejscie == AkcjeKlawiszy.PO_NACISNIECIU) {
                rario.setPredkoscWX(0);
            } else if (wejscie == AkcjeKlawiszy.PAUZA) {
                zapauzuj();
            }
        } else if (statusGry == StatusGry.ZATRZYMANO) {
            if (wejscie == AkcjeKlawiszy.PAUZA) {
                zapauzuj();
            }
        } else if(statusGry == StatusGry.PRZEGRANA && wejscie == AkcjeKlawiszy.POWROT){
            reset();
        } else if(statusGry == StatusGry.WYGRANA && wejscie == AkcjeKlawiszy.POWROT){
            reset();
        }

        if(wejscie == AkcjeKlawiszy.POWROT){
            setStatusGry(StatusGry.MENU);
        }
    }

    private void wybierzOpcje(boolean selectUp) {
        wyborWMenu = wyborWMenu.wybierz(selectUp);
    }

    private void startGry() {
        if (statusGry != StatusGry.PRZEGRANA) {
            setStatusGry(StatusGry.POZIOMY);
        }
    }

    private void zapauzuj() {
        if (statusGry == StatusGry.W_TRAKCIE) {
            setStatusGry(StatusGry.ZATRZYMANO);
        } else if (statusGry == StatusGry.ZATRZYMANO) {
            setStatusGry(StatusGry.W_TRAKCIE);
        }
    }

    private boolean czyPrzegrano() {
        if(statusGry == StatusGry.W_TRAKCIE)
            return zarzadzaniePoziomem.czyPrzegrano();
        return false;
    }

    public ArrayList<Rekord> wczytajZPliku(String nazwaPliku) throws IOException, ClassNotFoundException {
        ArrayList<Rekord> listaRekordow = new ArrayList<>();
        File plik = new File(nazwaPliku);

        if(plik.exists() && plik.length() != 0) {
            FileInputStream strumienWejsciowyPliku = new FileInputStream(nazwaPliku);
            ObjectInputStream strumienWejsciowyObiektu = new ObjectInputStream(strumienWejsciowyPliku);
            listaRekordow = (ArrayList<Rekord>) strumienWejsciowyObiektu.readObject();
            strumienWejsciowyObiektu.close();
        }

        return listaRekordow;
    }

    public ZaladowanieObrazu getZaladowanieObrazu() {
        return zaladowanieObrazu;
    }

    public StatusGry getStatusGry() {
        return statusGry;
    }

    public WyborWMenu getWyborWMenu() {
        return wyborWMenu;
    }

    public void setStatusGry(StatusGry statusGry) {
        this.statusGry = statusGry;
    }

    public int getPunkty() {
        return zarzadzaniePoziomem.getPunkty();
    }

    public int getPozostaleSerca() {
        return zarzadzaniePoziomem.getPozostaleSerca();
    }

    public int getWybranyPoziom() {
        return wybranyPoziom;
    }

    public void wyswietlPoziom(Graphics2D grafika) {
        zarzadzaniePoziomem.wyswietlPoziom(grafika);
    }

    public Point getPozycjaKamery() {
        return new Point((int) kamera.getX(), (int) kamera.getY());
    }

    private int getWygrana(){
        return zarzadzaniePoziomem.wygrana();
    }

    public ZarzadzaniePoziomem getZarzadzaniePoziomem() {
        return zarzadzaniePoziomem;
    }

    public static void main(String... args) throws IOException, ClassNotFoundException {
        new SilnikGry();
    }
}
