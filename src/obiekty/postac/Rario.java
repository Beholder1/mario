package obiekty.postac;

import zarzadzanie.Kamera;
import zarzadzanie.SilnikGry;
import view.Animacja;
import obiekty.ObiektGry;
import view.ZaladowanieObrazu;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Rario extends ObiektGry {

    private int PozostaleSerca;
    private int punkty;
    private RarioForma rarioForma;
    private boolean wPrawo = true;

    public Rario(double x, double y){
        super(x, y, null);
        setWymiar(50,50);

        PozostaleSerca = 3;
        punkty = 0;

        ZaladowanieObrazu obraz = new ZaladowanieObrazu();
        BufferedImage[] leweKlatki = obraz.getLeweKlatki(RarioForma.MALY);
        BufferedImage[] praweKlatki = obraz.getPraweKlatki(RarioForma.MALY);

        Animacja animacja = new Animacja(leweKlatki, praweKlatki);
        rarioForma = new RarioForma(animacja, false);
        setStyl(rarioForma.getCurrentStyle(wPrawo, false, false));
    }

    @Override
    public void wyswietl(Graphics g){
        boolean ruszX = (getPredkoscWX() != 0);
        boolean ruszY = (getPredkoscWY() != 0);

        setStyl(rarioForma.getCurrentStyle(wPrawo, ruszX, ruszY));

        super.wyswietl(g);
    }

    public void skok() {
        if(!getCzySkacze() && !getCzySpada()){
            setCzySkacze(true);
            setPredkoscWY(11);
        }
    }

    public void rusz(boolean wPrawo, Kamera kamera) {
        if(wPrawo){
            setPredkoscWX(5);
        }
        else if(kamera.getX() < getX()){
            setPredkoscWX(-5);
        }

        this.wPrawo = wPrawo;
    }

    public boolean dotknieciePrzeciwnika(SilnikGry silnik){

        if(!rarioForma.getCzyDuzy()){
            PozostaleSerca--;
            return true;
        }
        else{
            rarioForma = rarioForma.dotknieciePrzeciwnika(silnik.getZaladowanieObrazu());
            setWymiar(50, 50);
            return false;
        }
    }

    public void zyskajPunkty(int punkty){
        this.punkty = this.punkty + punkty;
    }

    public int getPozostaleSerca() {
        return PozostaleSerca;
    }

    public void setPozostaleSerca(int PozostaleSerca) {
        this.PozostaleSerca = PozostaleSerca;
    }

    public int getPunkty() {
        return punkty;
    }

    public RarioForma getRarioDuzy() {
        return rarioForma;
    }

    public void setRarioDuzy(RarioForma rarioForma) {
        this.rarioForma = rarioForma;
    }

    public boolean czyDuzy() {
        return rarioForma.getCzyDuzy();
    }

    public boolean getWPrawo() {
        return wPrawo;
    }

    public void zresetujPolozenie() {
        setPredkoscWX(0);
        setPredkoscWY(0);
        setX(50);
        setCzySkacze(false);
        setCzySpada(true);
    }
}
