package obiekty.blok;

import manager.GameEngine;
import obiekty.ObiektGry;
import obiekty.nagroda.Nagroda;

import java.awt.image.BufferedImage;

public abstract class Blok extends ObiektGry {

    private boolean czyZniszczalny;
    private boolean czyPusty;

    public Blok(double x, double y, BufferedImage styl){
        super(x, y, styl);
        setWymiar(50, 50);
    }

    public void setCzyZniszczalny(boolean czyZniszczalny) {
        this.czyZniszczalny = czyZniszczalny;
    }

    public void setCzyPusty(boolean czyPusty) {
        this.czyPusty = czyPusty;
    }

    public Nagroda odkryj(GameEngine silnik){ return null;}
}
