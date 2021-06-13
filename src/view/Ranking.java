package view;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Ranking {

    private ArrayList<Integer> rekordy = new ArrayList<>();
    private ArrayList<String> poziomy = new ArrayList<>();
    private ElementWyboruPoziomu[] ElementyWyboruPoziomu;

    public Ranking(){
        rekordy.add(0);
        rekordy.add(0);
        rekordy.add(0);
        rekordy.add(0);
        rekordy.add(0);
        dodajPoziomy();
        this.ElementyWyboruPoziomu = stworzElementy(this.poziomy);

    }

    public void wyswietl(Graphics grafika){
        grafika.setColor(Color.BLACK);
        grafika.fillRect(0,0, 1280, 720);

        if(ElementyWyboruPoziomu == null){
            System.out.println(1);
            return;
        }

        String tytul = "Najlepsze wyniki";
        int polozenieWX = (1280 - grafika.getFontMetrics().stringWidth(tytul))/2;
        grafika.setColor(Color.YELLOW);
        grafika.drawString(tytul, polozenieWX, 100);
        int licznik = 0;
        for(ElementWyboruPoziomu element : ElementyWyboruPoziomu){
            grafika.setColor(Color.WHITE);
            int szerokosc = grafika.getFontMetrics().stringWidth(element.getNazwaPoziomu().split("[.]")[0]);
            element.setPolozenie( new Point((1280-szerokosc)/2, element.getPolozenie().y));
            grafika.drawString(element.getNazwaPoziomu().split("[.]")[0] + ": " + rekordy.get(licznik), element.getPolozenie().x - 70, element.getPolozenie().y);
            licznik++;
        }
    }
    public void dodajRekord(int rekord, int poziom){
        rekordy.set(poziom, rekord);
    }

    public int getRekord(int poziom){
        return rekordy.get(poziom);
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
