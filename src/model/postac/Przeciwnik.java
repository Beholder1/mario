package model.postac;

import model.GameObject;
import java.awt.*;
import java.awt.image.BufferedImage;


public class Przeciwnik extends GameObject{
    private BufferedImage prawyObraz;
    public Przeciwnik(double x, double y, BufferedImage styl) {
        super(x, y, styl);
        setFalling(false);
        setJumping(false);
        setVelX(3);
    }
    @Override
    public void wyswietl(Graphics g){
        if(getVelX() > 0){
            g.drawImage(prawyObraz, (int)getX(), (int)getY(), null);
        }
        else
            super.wyswietl(g);
    }

    public void setPrawyObraz(BufferedImage rightImage) {
        this.prawyObraz = rightImage;
    }
}
