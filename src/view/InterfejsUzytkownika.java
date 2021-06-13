package view;

import manager.GameEngine;
import manager.StatusGry;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class InterfejsUzytkownika extends JPanel{

    private GameEngine silnik;
    private Font font;
    private BufferedImage menu, tworcy, sterowanie, przegrana;
    private BufferedImage serce;
    private BufferedImage wybor;
    private WyborPoziomu wyborPoziomu;

    public InterfejsUzytkownika(GameEngine silnik, int szerokosc, int wysokosc) {
        setPreferredSize(new Dimension(szerokosc, wysokosc));
        setMaximumSize(new Dimension(szerokosc, wysokosc));
        setMinimumSize(new Dimension(szerokosc, wysokosc));

        this.silnik = silnik;
        ImageLoader obraz = silnik.getImageLoader();
        wyborPoziomu = new WyborPoziomu();

        this.serce = obraz.loadImage("/serce.png");
        this.wybor = obraz.loadImage("/wybor.png");
        this.menu = obraz.loadImage("/menu.png");
        this.sterowanie = obraz.loadImage("/sterowanie.png");
        this.tworcy = obraz.loadImage("/tworcy.png");
        this.przegrana = obraz.loadImage("/przegrana.png");

        try {
            InputStream wejscie = getClass().getResourceAsStream("/grafika/font/font.ttf");
            font = Font.createFont(Font.TRUETYPE_FONT, wejscie);
        } catch (FontFormatException | IOException e) {
            font = new Font("Verdana", Font.PLAIN, 12);
            e.printStackTrace();
        }
    }

    @Override
    public void paintComponent(Graphics grafika){
        super.paintComponent(grafika);

        Graphics2D grafika1 = (Graphics2D) grafika.create();
        StatusGry statusGry = silnik.getGameStatus();

        if(statusGry == StatusGry.MENU){
            wyswietlMenu(grafika1);
        }
        else if(statusGry == StatusGry.POZIOMY){
            wyswietlWyborPoziomu(grafika1);
        }
        else if(statusGry == StatusGry.TWORCY){
            wyswietlTworcow(grafika1);
        }
        else if(statusGry == StatusGry.STEROWANIE){
            wyswietlSterowanie(grafika1);
        }
        else if(statusGry == StatusGry.RANKING){
            try {
                wyswietlRanking(grafika1);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        else if(statusGry == StatusGry.PRZEGRANA){
            wyswietlPrzegrana(grafika1);
        }
        else {
            Point kamera = silnik.getCameraLocation();
            grafika1.translate(-kamera.x, -kamera.y);
            silnik.drawMap(grafika1);
            grafika1.translate(kamera.x, kamera.y);

            wyswietlPunkty(grafika1);
            wyswietlPozostaleSerca(grafika1);

            if(statusGry == StatusGry.ZATRZYMANO){
                wyswietlPauze(grafika1);
            }
            else if(statusGry == StatusGry.WYGRANA){
                wyswietlWygrana(grafika1);
            }
        }
        grafika1.dispose();
    }

    private void wyswietlWygrana(Graphics2D grafika) {
        grafika.setFont(font.deriveFont(50f));
        grafika.setColor(Color.WHITE);
        String napis = "Wygrana!";
        int dlugosc = grafika.getFontMetrics().stringWidth(napis);
        grafika.drawString(napis, (getWidth()-dlugosc)/2, getHeight()/2);
    }

    private void wyswietlSterowanie(Graphics2D grafika) {
        grafika.drawImage(sterowanie, 0, 0, null);
    }

    private void wyswietlTworcow(Graphics2D grafika) {
        grafika.drawImage(tworcy, 0, 0, null);
    }

    void wyswietlRanking(Graphics2D grafika) throws IOException, ClassNotFoundException {
        grafika.setFont(font.deriveFont(50f));
        grafika.setColor(Color.WHITE);
        silnik.getRanking().wyswietl(grafika);
    }

    private void wyswietlPrzegrana(Graphics2D grafika) {
        grafika.drawImage(przegrana, 0, 0, null);
        grafika.setFont(font.deriveFont(50f));
        grafika.setColor(new Color(130, 48, 48));
        String uzyskanePunkty = "Wynik: " + silnik.getScore();
        int dlugosc = grafika.getFontMetrics().stringWidth(uzyskanePunkty);
        int wysokosc = grafika.getFontMetrics().getHeight();
        grafika.drawString(uzyskanePunkty, (getWidth()-dlugosc)/2, getHeight()-wysokosc*2);
    }

    private void wyswietlPauze(Graphics2D grafika) {
        grafika.setFont(font.deriveFont(50f));
        grafika.setColor(Color.WHITE);
        String napis = "PAUZA";
        int stringLength = grafika.getFontMetrics().stringWidth(napis);
        grafika.drawString(napis, (getWidth()-stringLength)/2, getHeight()/2);
    }

    private void wyswietlPozostaleSerca(Graphics2D grafika) {
        grafika.setFont(font.deriveFont(30f));
        grafika.setColor(Color.WHITE);
        String pozostaleSerca = "" + silnik.getRemainingLives();
        grafika.drawImage(serce, 50, 10, null);
        grafika.drawString(pozostaleSerca, 100, 50);
    }

    private void wyswietlPunkty(Graphics2D grafika){
        grafika.setFont(font.deriveFont(25f));
        grafika.setColor(Color.WHITE);
        String napis = "Punkty: " + silnik.getScore();
        grafika.drawString(napis, 900, 50);
    }

    private void wyswietlMenu(Graphics2D grafika){
        int wiersz = silnik.getStartScreenSelection().getLineNumber();
        grafika.drawImage(menu, 0, 0, null);
        grafika.drawImage(wybor, 350, wiersz * 60 + 405, null);
    }

    private void wyswietlWyborPoziomu(Graphics2D grafika){
        grafika.setFont(font.deriveFont(50f));
        grafika.setColor(Color.WHITE);
        wyborPoziomu.wyswietl(grafika);
        int wiersz = silnik.getSelectedMap();
        int y = wiersz*100+253-wybor.getHeight();
        grafika.drawImage(wybor, 350, y, null);
    }

    public String wybierzPoziom(int index){
        return wyborPoziomu.wybierzPoziom(index);
    }

    public int zmienPoziom(int index, boolean up){
        return wyborPoziomu.zmienPoziom(index, up);
    }
}