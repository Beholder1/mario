package obiekty.nagroda;

import obiekty.ObiektGry;
import obiekty.postac.Rario;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class PrzedmiotSpecjalny extends ObiektGry implements Nagroda {

    private boolean czyOdkryty = false;
    private int punkt;

    public PrzedmiotSpecjalny(double x, double y, BufferedImage styl) {
        super(x, y, styl);
        setWymiar(50, 50);
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
            grafika.drawImage(getStyl(), (int)getX(), (int)getY(), null);
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
