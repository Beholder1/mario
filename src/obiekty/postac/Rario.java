package obiekty.postac;

import manager.Kamera;
import manager.GameEngine;
import view.Animation;
import obiekty.ObiektGry;
import view.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Rario extends ObiektGry {

    private int PozostaleSerca;
    private int punkty;
    private RarioDuzy rarioDuzy;
    private boolean wPrawo = true;

    public Rario(double x, double y){
        super(x, y, null);
        setWymiar(50,50);

        PozostaleSerca = 3;
        punkty = 0;

        ImageLoader obraz = new ImageLoader();
        BufferedImage[] leweKlatki = obraz.getLeftFrames(RarioDuzy.MALY);
        BufferedImage[] praweKlatki = obraz.getRightFrames(RarioDuzy.MALY);

        Animation animacja = new Animation(leweKlatki, praweKlatki);
        rarioDuzy = new RarioDuzy(animacja, false);
        setStyl(rarioDuzy.getCurrentStyle(wPrawo, false, false));
    }

    @Override
    public void wyswietl(Graphics g){
        boolean ruszX = (getPredkoscWX() != 0);
        boolean ruszY = (getPredkoscWY() != 0);

        setStyl(rarioDuzy.getCurrentStyle(wPrawo, ruszX, ruszY));

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

    public boolean dotknieciePrzeciwnika(GameEngine silnik){

        if(!rarioDuzy.getCzyDuzy()){
            PozostaleSerca--;
            return true;
        }
        else{
            rarioDuzy = rarioDuzy.dotknieciePrzeciwnika(silnik.getImageLoader());
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

    public RarioDuzy getRarioDuzy() {
        return rarioDuzy;
    }

    public void setRarioDuzy(RarioDuzy rarioDuzy) {
        this.rarioDuzy = rarioDuzy;
    }

    public boolean isSuper() {
        return rarioDuzy.getCzyDuzy();
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