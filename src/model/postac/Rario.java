package model.postac;

import manager.Camera;
import manager.GameEngine;
import view.Animation;
import model.GameObject;
import view.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Rario extends GameObject{

    private int PozostaleSerca;
    private int punkty;
    private RarioDuzy rarioDuzy;
    private boolean wPrawo = true;

    public Rario(double x, double y){
        super(x, y, null);
        setDimension(50,50);

        PozostaleSerca = 3;
        punkty = 0;

        ImageLoader obraz = new ImageLoader();
        BufferedImage[] leweKlatki = obraz.getLeftFrames(RarioDuzy.MALY);
        BufferedImage[] praweKlatki = obraz.getRightFrames(RarioDuzy.MALY);

        Animation animacja = new Animation(leweKlatki, praweKlatki);
        rarioDuzy = new RarioDuzy(animacja, false);
        setStyle(rarioDuzy.getCurrentStyle(wPrawo, false, false));
    }

    @Override
    public void wyswietl(Graphics g){
        boolean ruszX = (getVelX() != 0);
        boolean ruszY = (getVelY() != 0);

        setStyle(rarioDuzy.getCurrentStyle(wPrawo, ruszX, ruszY));

        super.wyswietl(g);
    }

    public void skok() {
        if(!isJumping() && !isFalling()){
            setJumping(true);
            setVelY(11);
        }
    }

    public void rusz(boolean wPrawo, Camera kamera) {
        if(wPrawo){
            setVelX(5);
        }
        else if(kamera.getX() < getX()){
            setVelX(-5);
        }

        this.wPrawo = wPrawo;
    }

    public boolean dotknieciePrzeciwnika(GameEngine silnik){

        if(!rarioDuzy.getCzyDuzy()){
            PozostaleSerca--;
            return true;
        }
        else{
            silnik.shakeCamera();
            rarioDuzy = rarioDuzy.dotknieciePrzeciwnika(silnik.getImageLoader());
            setDimension(50, 50);
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
        setVelX(0);
        setVelY(0);
        setX(50);
        setJumping(false);
        setFalling(true);
    }
}
