package obiekty.postac;

import obiekty.ObiektGry;
import java.awt.*;
import java.awt.image.BufferedImage;


public class Przeciwnik extends ObiektGry {
    private BufferedImage prawyObraz;
    public Przeciwnik(double x, double y, BufferedImage styl) {
        super(x, y, styl);
        setCzySpada(false);
        setCzySkacze(false);
        setPredkoscWX(3);
    }
    @Override
    public void wyswietl(Graphics g){
        if(getPredkoscWX() > 0){
            g.drawImage(prawyObraz, (int)getX(), (int)getY(), null);
        }
        else
            super.wyswietl(g);
    }

    public void setPrawyObraz(BufferedImage rightImage) {
        this.prawyObraz = rightImage;
    }
}
