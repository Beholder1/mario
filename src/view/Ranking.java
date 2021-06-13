package view;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.io.*;
import java.io.Serializable;

public class Ranking implements Serializable {
    private ArrayList<Rekord> rekordy;
    private ArrayList<String> poziomy = new ArrayList<>();
    private ElementWyboruPoziomu[] ElementyWyboruPoziomu;

    public Ranking() throws IOException, ClassNotFoundException {

        dodajPoziomy();
        this.ElementyWyboruPoziomu = stworzElementy(this.poziomy);

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

    private void dodajPoziomy(){
        poziomy.add("Poziom 1.png");
        poziomy.add("Poziom 2.png");
        poziomy.add("Poziom 3.png");
        poziomy.add("Poziom 4.png");
        poziomy.add("Poziom 5.png");
    }

    private ElementWyboruPoziomu[] stworzElementy(ArrayList<String> poziomy){
        if(poziomy == null)
            return null;

        ElementWyboruPoziomu[] elementy = new ElementWyboruPoziomu[poziomy.size()];
        for (int i = 0; i < elementy.length; i++) {
            Point polozenie = new Point(0, (i+1)*100+150);
            elementy[i] = new ElementWyboruPoziomu(poziomy.get(i), polozenie);
        }

        return elementy;
    }
}
