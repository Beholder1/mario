package model.nagroda;

import model.GameObject;
import model.postac.Rario;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class PrzedmiotSpecjalny extends GameObject implements Nagroda {

    private boolean czyOdkryty = false;
    private int punkt;

    public PrzedmiotSpecjalny(double x, double y, BufferedImage styl) {
        super(x, y, styl);
        setDimension(50, 50);
    }

    public abstract void przyDotknieciu(Rario rario);

    @Override
    public int getPunkt() {
        return punkt;
    }

    @Override
    public void zmienPolozenie(){
        if(czyOdkryty){
            super.zmienPolozenie();
        }
    }

    @Override
    public void wyswietl(Graphics grafika){
        if(czyOdkryty){
            grafika.drawImage(getStyle(), (int)getX(), (int)getY(), null);
        }
    }

    @Override
    public void odkryj(){
        setY(getY()-50);
        czyOdkryty = true;
    }

    public void setPunkt(int punkt) {
        this.punkt = punkt;
    }
}
