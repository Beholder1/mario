package view;

import manager.GameEngine;
import manager.StatusGry;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class UIManager extends JPanel{

    private GameEngine silnik;
    private Font font;
    private BufferedImage menu, tworcy, sterowanie, przegrana;
    private BufferedImage serce;
    private BufferedImage wybor;
    private MapSelection mapSelection;

    public UIManager(GameEngine silnik, int szerokosc, int wysokosc) {
        setPreferredSize(new Dimension(szerokosc, wysokosc));
        setMaximumSize(new Dimension(szerokosc, wysokosc));
        setMinimumSize(new Dimension(szerokosc, wysokosc));

        this.silnik = silnik;
        ImageLoader obraz = silnik.getImageLoader();

        mapSelection = new MapSelection();

        this.serce = obraz.loadImage("/serce.png");
        this.wybor = obraz.loadImage("/wybor.png");
        this.menu = obraz.loadImage("/menu.png");
        this.sterowanie = obraz.loadImage("/sterowanie.png");
        this.tworcy = obraz.loadImage("/tworcy.png");
        this.przegrana = obraz.loadImage("/przegrana.png");

        try {
            InputStream in = getClass().getResourceAsStream("/media/font/font.ttf");
            font = Font.createFont(Font.TRUETYPE_FONT, in);
        } catch (FontFormatException | IOException e) {
            font = new Font("Verdana", Font.PLAIN, 12);
            e.printStackTrace();
        }
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        StatusGry statusGry = silnik.getGameStatus();

        if(statusGry == StatusGry.MENU){
            drawStartScreen(g2);
        }
        else if(statusGry == StatusGry.POZIOMY){
            drawMapSelectionScreen(g2);
        }
        else if(statusGry == StatusGry.TWORCY){
            wyswietlTworcow(g2);
        }
        else if(statusGry == StatusGry.STEROWANIE){
            wyswietlSterowanie(g2);
        }
        else if(statusGry == StatusGry.PRZEGRANA){
            wyswietlPrzegrana(g2);
        }
        else {
            Point camLocation = silnik.getCameraLocation();
            g2.translate(-camLocation.x, -camLocation.y);
            silnik.drawMap(g2);
            g2.translate(camLocation.x, camLocation.y);

            drawPoints(g2);
            drawRemainingLives(g2);

            if(statusGry == StatusGry.ZATRZYMANO){
                drawPauseScreen(g2);
            }
            else if(statusGry == StatusGry.WYGRANA){
                wyswietlWygrana(g2);
            }
        }

        g2.dispose();
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

    private void wyswietlPrzegrana(Graphics2D grafika) {
        grafika.drawImage(przegrana, 0, 0, null);
        grafika.setFont(font.deriveFont(50f));
        grafika.setColor(new Color(130, 48, 48));
        String uzyskanePunkty = "Wynik: " + silnik.getScore();
        int dlugosc = grafika.getFontMetrics().stringWidth(uzyskanePunkty);
        int wysokosc = grafika.getFontMetrics().getHeight();
        grafika.drawString(uzyskanePunkty, (getWidth()-dlugosc)/2, getHeight()-wysokosc*2);
    }

    private void drawPauseScreen(Graphics2D g2) {
        g2.setFont(font.deriveFont(50f));
        g2.setColor(Color.WHITE);
        String displayedStr = "PAUZA";
        int stringLength = g2.getFontMetrics().stringWidth(displayedStr);
        g2.drawString(displayedStr, (getWidth()-stringLength)/2, getHeight()/2);
    }

    private void drawRemainingLives(Graphics2D g2) {
        g2.setFont(font.deriveFont(30f));
        g2.setColor(Color.WHITE);
        String displayedStr = "" + silnik.getRemainingLives();
        g2.drawImage(serce, 50, 10, null);
        g2.drawString(displayedStr, 100, 50);
    }

    private void drawPoints(Graphics2D g2){
        g2.setFont(font.deriveFont(25f));
        g2.setColor(Color.WHITE);
        String displayedStr = "Punkty: " + silnik.getScore();
        g2.drawString(displayedStr, 900, 50);
    }

    private void drawStartScreen(Graphics2D g2){
        int row = silnik.getStartScreenSelection().getLineNumber();
        g2.drawImage(menu, 0, 0, null);
        g2.drawImage(wybor, 350, row * 60 + 445, null);
    }

    private void drawMapSelectionScreen(Graphics2D g2){
        g2.setFont(font.deriveFont(50f));
        g2.setColor(Color.WHITE);
        mapSelection.draw(g2);
        int row = silnik.getSelectedMap();
        int y_location = row*100+253-wybor.getHeight();
        g2.drawImage(wybor, 350, y_location, null);
    }

    public String selectMapViaMouse(Point mouseLocation) {
        return mapSelection.selectMap(mouseLocation);
    }

    public String selectMapViaKeyboard(int index){
        return mapSelection.selectMap(index);
    }

    public int changeSelectedMap(int index, boolean up){
        return mapSelection.changeSelectedMap(index, up);
    }
}