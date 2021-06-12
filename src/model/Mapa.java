package model;

import model.blok.Blok;
import model.blok.Cegla;
import model.postac.Przeciwnik;
import model.postac.Rario;
import model.nagroda.PrzedmiotSpecjalny;
import model.nagroda.Moneta;
import model.nagroda.Nagroda;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

public class Mapa {

    private Rario rario;
    private ArrayList<Blok> cegly = new ArrayList<>();
    private ArrayList<Przeciwnik> przeciwnicy = new ArrayList<>();
    private ArrayList<Blok> niezniszczalneCegly = new ArrayList<>();
    private ArrayList<Nagroda> nagrody = new ArrayList<>();
    private ArrayList<Blok> odkryteCegly = new ArrayList<>();
    private Flaga koniec;
    private BufferedImage tlo;
    private double dolnaGranica = 720 - 70;
    private String sciezka;


    public Mapa(BufferedImage tlo) {
        this.tlo = tlo;
    }

    public Rario getRario() {
        return rario;
    }

    public void setRario(Rario rario) {
        this.rario = rario;
    }

    public ArrayList<Przeciwnik> getPrzeciwnicy() {
        return przeciwnicy;
    }

    public ArrayList<Nagroda> getNagrody() {
        return nagrody;
    }

    public ArrayList<Blok> getWszystkieCegly() {
        ArrayList<Blok> wszystkieCegly = new ArrayList<>();
        wszystkieCegly.addAll(cegly);
        wszystkieCegly.addAll(niezniszczalneCegly);

        return wszystkieCegly;
    }

    public void dodajCegle(Blok cegla) {
        this.cegly.add(cegla);
    }

    public void dodajNiezniszczalnaCegle(Blok niezniszczalnaCegla) {
        this.niezniszczalneCegly.add(niezniszczalnaCegla);
    }

    public void dodajPrzeciwnika(Przeciwnik przeciwnik) {
        this.przeciwnicy.add(przeciwnik);
    }

    public void drawMap(Graphics2D g2){
        wyswietlTlo(g2);
        wyswietlNagrody(g2);
        wyswietlCegly(g2);
        wyswietlPrzeciwnikow(g2);
        wyswietlRario(g2);
        koniec.wyswietl(g2);
    }

    private void wyswietlNagrody(Graphics2D grafika) {
        for(Nagroda nagroda : nagrody){
            if(nagroda instanceof Moneta){
                ((Moneta) nagroda).wyswietl(grafika);
            }
            else if(nagroda instanceof PrzedmiotSpecjalny){
                ((PrzedmiotSpecjalny) nagroda).wyswietl(grafika);
            }
        }
    }

    private void wyswietlTlo(Graphics2D grafika){
        grafika.drawImage(tlo, 0, 0, null);
    }

    private void wyswietlCegly(Graphics2D grafika) {
        for(Blok cegla : cegly){
            if(cegla != null)
                cegla.wyswietl(grafika);
        }
        for(Blok niezniszczalnaCegla : niezniszczalneCegly){
            niezniszczalnaCegla.wyswietl(grafika);
        }
    }

    private void wyswietlPrzeciwnikow(Graphics2D grafika) {
        for(Przeciwnik przeciwnik : przeciwnicy){
            if(przeciwnik != null)
                przeciwnik.wyswietl(grafika);
        }
    }

    private void wyswietlRario(Graphics2D grafika) {
        rario.wyswietl(grafika);
    }

    public void zmienPolozenie() {
        rario.zmienPolozenie();
        for(Przeciwnik przeciwnik : przeciwnicy){
            przeciwnik.zmienPolozenie();
        }

        for(Iterator<Nagroda> iteratorNagrod = nagrody.iterator(); iteratorNagrod.hasNext();){
            Nagroda nagroda = iteratorNagrod.next();
            if(nagroda instanceof Moneta){
                ((Moneta) nagroda).zmienPolozenie();
                if(((Moneta) nagroda).getGranicaOdkrycia() > ((Moneta) nagroda).getY()){
                    iteratorNagrod.remove();
                }
            }
            else if(nagroda instanceof PrzedmiotSpecjalny){
                ((PrzedmiotSpecjalny) nagroda).zmienPolozenie();
            }
        }

        for(Iterator<Blok> iteratorCegiel = odkryteCegly.iterator(); iteratorCegiel.hasNext();){
            Cegla cegla = (Cegla)iteratorCegiel.next();
            cegly.remove(cegla);
            iteratorCegiel.remove();
        }

        koniec.zmienPolozenie();
    }

    public double getDolnaGranica() {
        return dolnaGranica;
    }

    public void dodajNagrode(Nagroda nagroda) {
        nagrody.add(nagroda);
    }

    public void setKoniec(Flaga koniec) {
        this.koniec = koniec;
    }

    public Flaga getKoniec() {
        return koniec;
    }

    public void dodajOdkrytaCegle(Cegla cegla) {
        odkryteCegly.add(cegla);
    }

    public void usunPrzeciwnika(Przeciwnik przeciwnik) {
        przeciwnicy.remove(przeciwnik);
    }

    public void usunNagrode(Nagroda nagroda) {
        nagrody.remove(nagroda);
    }

    public String getSciezka() {
        return sciezka;
    }

    public void setSciezka(String sciezka) {
        this.sciezka = sciezka;
    }
}
