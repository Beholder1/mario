package manager;

import obiekty.ObiektGry;
import obiekty.Poziom;
import obiekty.blok.Blok;
import obiekty.blok.Cegla;
import obiekty.postac.Przeciwnik;
import obiekty.postac.Rario;
import obiekty.nagroda.PrzedmiotSpecjalny;
import obiekty.nagroda.Moneta;
import obiekty.nagroda.Nagroda;
import view.ZaladowanieObrazu;

import java.awt.*;
import java.util.ArrayList;

public class ZarzadzaniePoziomem {

    private Poziom poziom;

    public ZarzadzaniePoziomem() {}

    public void zmienPolozenia() {
        if (poziom == null)
            return;
        poziom.zmienPolozenia();
    }

    public void zresetujPoziom(SilnikGry silnik) {
        Rario rario = getRario();
        rario.zresetujPolozenie();
        silnik.resetKamery();
        stworzPoziom(silnik.getZaladowanieObrazu(), poziom.getSciezka());
        poziom.setRario(rario);
    }

    public boolean stworzPoziom(ZaladowanieObrazu obraz, String sciezka) {
        GeneratorMapy generatorMapy = new GeneratorMapy(obraz);
        poziom = generatorMapy.utworzPoziom("/poziomy/" + sciezka);

        return poziom != null;
    }

    public void zyskajPunkty(int point) {
        poziom.getRario().zyskajPunkty(point);
    }

    public Rario getRario() {
        return poziom.getRario();
    }

    public boolean czyPrzegrano() {
        return getRario().getPozostaleSerca() == 0;
    }

    public int getPunkty() {
        return getRario().getPunkty();
    }

    public int getPozostaleSerca() {
        return getRario().getPozostaleSerca();
    }

    public void wyswietlPoziom(Graphics2D grafika) {
        poziom.wyswietlPoziom(grafika);
    }

    public int wygrana() {
        if(getRario().getX() >= poziom.getKoniec().getX() && !poziom.getKoniec().getCzyDotknieta()){
            poziom.getKoniec().setCzyDotknieta(true);
            int wysokosc = (int)getRario().getY();
            return wysokosc * 2;
        }
        else
            return -1;
    }

    public boolean koniecPoziomu(){
        return getRario().getX() >= poziom.getKoniec().getX() + 320;
    }

    public void sprawdzKolizje(SilnikGry silnik) {
        if (poziom == null) {
            return;
        }

        sprawdzDolneKolizje();
        sprawdzGorneKolizje(silnik);
        sprawdzBoczneKolizjeRario(silnik);
        sprawdzKolizjePrzeciwnika();
        sprawdzKolizjeNagrody();
        sprawdzDotkniecieNagrody();
    }

    private void sprawdzDolneKolizje() {
        Rario rario = getRario();
        ArrayList<Blok> cegly = poziom.getWszystkieCegly();
        ArrayList<Przeciwnik> przeciwnicy = poziom.getPrzeciwnicy();
        ArrayList<ObiektGry> doUsuniecia = new ArrayList<>();

        Rectangle dolnaGranicaRario = rario.dolnaGranica();

        if (!rario.getCzySkacze())
            rario.setCzySpada(true);

        for (Blok cegla : cegly) {
            Rectangle brickTopBounds = cegla.gornaGranica();
            if (dolnaGranicaRario.intersects(brickTopBounds)) {
                rario.setY(cegla.getY() - rario.getWymiar().height + 1);
                rario.setCzySpada(false);
                rario.setPredkoscWY(0);
            }
        }

        for (Przeciwnik przeciwnik : przeciwnicy) {
            Rectangle gornaGranicaPrzeciwnika = przeciwnik.gornaGranica();
            if (dolnaGranicaRario.intersects(gornaGranicaPrzeciwnika)) {
                rario.zyskajPunkty(100);
                doUsuniecia.add(przeciwnik);
            }
        }

        if (rario.getY() + rario.getWymiar().height >= poziom.getDolnaGranica()) {
            rario.setY(poziom.getDolnaGranica() - rario.getWymiar().height);
            rario.setCzySpada(false);
            rario.setPredkoscWY(0);
        }

        usunObiektyGry(doUsuniecia);
    }

    private void sprawdzGorneKolizje(SilnikGry silnik) {
        Rario rario = getRario();
        ArrayList<Blok> cegly = poziom.getWszystkieCegly();

        Rectangle gornaGranicaRario = rario.gornaGranica();
        for (Blok cegla : cegly) {
            Rectangle dolnaGranicaCegly = cegla.dolnaGranica();
            if (gornaGranicaRario.intersects(dolnaGranicaCegly)) {
                rario.setPredkoscWY(0);
                rario.setY(cegla.getY() + cegla.getWymiar().height);
                Nagroda nagroda = cegla.odkryj(silnik);
                if(nagroda != null)
                    poziom.dodajNagrode(nagroda);
            }
        }
    }

    private void sprawdzBoczneKolizjeRario(SilnikGry engine){
        Rario rario = getRario();
        ArrayList<Blok> cegly = poziom.getWszystkieCegly();
        ArrayList<Przeciwnik> przeciwnicy = poziom.getPrzeciwnicy();
        ArrayList<ObiektGry> doUsuniecia = new ArrayList<>();

        boolean smierc = false;
        boolean czyWPrawo = rario.getWPrawo();

        Rectangle prawaGranicaRario = rario.prawaGranica();
        Rectangle lewaGranicaRario = rario.lewaGranica();

        for (Blok cegla : cegly) {
            Rectangle graniceCegly = !czyWPrawo ? cegla.prawaGranica() : cegla.lewaGranica();
            if (prawaGranicaRario.intersects(graniceCegly)||lewaGranicaRario.intersects(graniceCegly)) {
                rario.setPredkoscWX(0);
                if(czyWPrawo)
                    rario.setX(cegla.getX() - rario.getWymiar().width);
                else
                    rario.setX(cegla.getX() + cegla.getWymiar().width);
            }
        }

        for(Przeciwnik przeciwnik : przeciwnicy){
            Rectangle granicePrzeciwnika = !czyWPrawo ? przeciwnik.prawaGranica() : przeciwnik.lewaGranica();
            if (prawaGranicaRario.intersects(granicePrzeciwnika)||lewaGranicaRario.intersects(granicePrzeciwnika)) {
                smierc = rario.dotknieciePrzeciwnika(engine);
                doUsuniecia.add(przeciwnik);
            }
        }
        usunObiektyGry(doUsuniecia);


        if (rario.getX() <= engine.getPozycjaKamery().getX() && rario.getPredkoscWX() < 0) {
            rario.setPredkoscWX(0);
            rario.setX(engine.getPozycjaKamery().getX());
        }

        if(smierc) {
            zresetujPoziom(engine);
        }
    }

    private void sprawdzKolizjePrzeciwnika() {
        ArrayList<Blok> cegly = poziom.getWszystkieCegly();
        ArrayList<Przeciwnik> przeciwnicy = poziom.getPrzeciwnicy();

        for (Przeciwnik przeciwnik : przeciwnicy) {
            boolean czyStoiNaCegle = false;

            for (Blok cegla : cegly) {
                Rectangle granicePrzeciwnika = przeciwnik.lewaGranica();
                Rectangle graniceCegly = cegla.prawaGranica();

                Rectangle dolnaGranicaPrzeciwnika = przeciwnik.dolnaGranica();
                Rectangle gornaGranicaCegly = cegla.gornaGranica();

                if (przeciwnik.getPredkoscWX() > 0) {
                    granicePrzeciwnika = przeciwnik.prawaGranica();
                    graniceCegly = cegla.lewaGranica();
                }

                if (granicePrzeciwnika.intersects(graniceCegly)) {
                    przeciwnik.setPredkoscWX(-przeciwnik.getPredkoscWX());
                }

                if (dolnaGranicaPrzeciwnika.intersects(gornaGranicaCegly)){
                    przeciwnik.setCzySpada(false);
                    przeciwnik.setPredkoscWY(0);
                    przeciwnik.setY(cegla.getY()- przeciwnik.getWymiar().height);
                    czyStoiNaCegle = true;
                }
            }

            if(przeciwnik.getY() + przeciwnik.getWymiar().height > poziom.getDolnaGranica()){
                przeciwnik.setCzySpada(false);
                przeciwnik.setPredkoscWY(0);
                przeciwnik.setY(poziom.getDolnaGranica()- przeciwnik.getWymiar().height);
            }

            if (!czyStoiNaCegle && przeciwnik.getY() < poziom.getDolnaGranica()){
                przeciwnik.setCzySpada(true);
            }
        }
    }

    private void sprawdzKolizjeNagrody() {
        ArrayList<Nagroda> nagrody = poziom.getNagrody();
        ArrayList<Blok> cegly = poziom.getWszystkieCegly();

        for (Nagroda nagroda : nagrody) {
            if (nagroda instanceof PrzedmiotSpecjalny) {
                PrzedmiotSpecjalny przedmiotSpecjalny = (PrzedmiotSpecjalny) nagroda;
                Rectangle dolnaGranicaNagrody = przedmiotSpecjalny.dolnaGranica();
                Rectangle prawaGranicaNagrody = przedmiotSpecjalny.prawaGranica();
                Rectangle lewaGranicaNagrody = przedmiotSpecjalny.lewaGranica();
                przedmiotSpecjalny.setCzySpada(true);

                for (Blok cegla : cegly) {
                    Rectangle graniceCegly;

                    if (przedmiotSpecjalny.getCzySpada()) {
                        graniceCegly = cegla.gornaGranica();

                        if (graniceCegly.intersects(dolnaGranicaNagrody)) {
                            przedmiotSpecjalny.setCzySpada(false);
                            przedmiotSpecjalny.setPredkoscWY(0);
                            przedmiotSpecjalny.setY(cegla.getY() - przedmiotSpecjalny.getWymiar().height + 1);
                            if (przedmiotSpecjalny.getPredkoscWX() == 0)
                                przedmiotSpecjalny.setPredkoscWX(2);
                        }
                    }

                    if (przedmiotSpecjalny.getPredkoscWX() > 0) {
                        graniceCegly = cegla.lewaGranica();

                        if (graniceCegly.intersects(prawaGranicaNagrody)) {
                            przedmiotSpecjalny.setPredkoscWX(-przedmiotSpecjalny.getPredkoscWX());
                        }
                    } else if (przedmiotSpecjalny.getPredkoscWX() < 0) {
                        graniceCegly = cegla.prawaGranica();

                        if (graniceCegly.intersects(lewaGranicaNagrody)) {
                            przedmiotSpecjalny.setPredkoscWX(-przedmiotSpecjalny.getPredkoscWX());
                        }
                    }
                }

                if (przedmiotSpecjalny.getY() + przedmiotSpecjalny.getWymiar().height > poziom.getDolnaGranica()) {
                    przedmiotSpecjalny.setCzySpada(false);
                    przedmiotSpecjalny.setPredkoscWY(0);
                    przedmiotSpecjalny.setY(poziom.getDolnaGranica() - przedmiotSpecjalny.getWymiar().height);
                    if (przedmiotSpecjalny.getPredkoscWX() == 0)
                        przedmiotSpecjalny.setPredkoscWX(2);
                }

            }
        }
    }

    private void sprawdzDotkniecieNagrody() {
        ArrayList<Nagroda> nagrody = poziom.getNagrody();
        ArrayList<ObiektGry> doUsuniecia = new ArrayList<>();

        Rectangle graniceRario = getRario().getGranice();
        for(Nagroda nagroda : nagrody){
            Rectangle graniceNagrody = nagroda.getGranice();
            if (graniceNagrody.intersects(graniceRario)) {
                nagroda.przyDotknieciu(getRario());
                doUsuniecia.add((ObiektGry) nagroda);
            } else if(nagroda instanceof Moneta){
                nagroda.przyDotknieciu(getRario());
            }
        }

        usunObiektyGry(doUsuniecia);
    }

    private void usunObiektyGry(ArrayList<ObiektGry> list){
        if(list == null)
            return;

        for(ObiektGry object : list){
            if(object instanceof Przeciwnik){
                poziom.usunPrzeciwnika((Przeciwnik)object);
            }
            else if(object instanceof Moneta || object instanceof PrzedmiotSpecjalny){
                poziom.usunNagrode((Nagroda)object);
            }
        }
    }

    public void odkrycieCegly(Cegla cegla) {
        poziom.dodajOdkrytaCegle(cegla);
    }

}
