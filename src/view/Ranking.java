package view;

import java.awt.*;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import java.io.*;
import java.io.Serializable;

public class Ranking implements Serializable {
    private ArrayList<String> poziomy = new ArrayList<>();
    private ElementWyboruPoziomu[] ElementyWyboruPoziomu;

    public Ranking() {
    }
    public ArrayList<Rekord> read(String fileName) throws IOException, ClassNotFoundException {
        ArrayList<Rekord> tmpArrayList = new ArrayList<>();
        File file = new File(fileName);

        if(file.exists() && file.length() != 0) {
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            tmpArrayList = (ArrayList<Rekord>) ois.readObject();
            ois.close();
        }

        return tmpArrayList;
    }
    public void wyswietl(Graphics grafika) throws IOException, ClassNotFoundException {
        grafika.setColor(Color.BLACK);
        grafika.fillRect(0,0, 1280, 720);

        if(ElementyWyboruPoziomu == null){
            System.out.println(1);
            return;
        }
        ArrayList<Rekord> rekordy = read(".\\rekordy.dat");
        String tytul = "Najlepsze wyniki";
        int polozenieWX = (1280 - grafika.getFontMetrics().stringWidth(tytul))/2;
        grafika.setColor(Color.YELLOW);
        grafika.drawString(tytul, polozenieWX, 100);
        int licznik = 0;
        for(ElementWyboruPoziomu element : ElementyWyboruPoziomu){
            grafika.setColor(Color.WHITE);
            int szerokosc = grafika.getFontMetrics().stringWidth(element.getNazwaPoziomu().split("[.]")[0]);
            element.setPolozenie( new Point((1280-szerokosc)/2, element.getPolozenie().y));
            grafika.drawString(rekordy.get(licznik).toString(), element.getPolozenie().x - 70, element.getPolozenie().y);
            licznik++;
        }
    }

}
