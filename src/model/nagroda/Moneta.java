package model.nagroda;

import model.GameObject;
import model.postac.Rario;

import java.awt.*;
import java.awt.image.BufferedImage;


public class Moneta extends GameObject implements Nagroda {

    private int punkt;
    private boolean czyOdkryty, czyOtrzymane = false;
    private int granicaOdkrycia;

    public Moneta(double x, double y, BufferedImage styl, int punkt){
        super(x, y, styl);
        this.punkt = punkt;
        czyOdkryty = false;
        setDimension(30, 42);
        granicaOdkrycia = (int)getY() - getWymiar().height;
    }

    @Override
    public int getPunkt() {
        return punkt;
    }

    @Override
    public void odkryj() {
        czyOdkryty = true;
    }

    @Override
    public void przyDotknieciu(Rario rario) {
        if(!czyOtrzymane){
            czyOtrzymane = true;
            rario.zyskajPunkty(punkt);
        }
    }

    @Override
    public void zmienPolozenie(){
        if(czyOdkryty){
            setY(getY() - 5);
        }
    }

    @Override
    public void wyswietl(Graphics g){
        if(czyOdkryty){
            g.drawImage(getStyl(), (int)getX(), (int)getY(), null);
        }
    }

    public int getGranicaOdkrycia() {
        return granicaOdkrycia;
    }
}
