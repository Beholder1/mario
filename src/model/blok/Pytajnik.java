package model.blok;

import manager.GameEngine;
import model.nagroda.Nagroda;

import java.awt.image.BufferedImage;

public class Pytajnik extends Blok{

    private Nagroda nagroda;

    public Pytajnik(double x, double y, BufferedImage styl, Nagroda nagroda) {
        super(x, y, styl);
        setCzyZniszczalny(false);
        setCzyPusty(false);
        this.nagroda = nagroda;
    }

    @Override
    public Nagroda odkryj(GameEngine silnik){
        BufferedImage nowyStyl = silnik.getImageLoader().loadImage("/obiekty.png");
        nowyStyl = silnik.getImageLoader().getSubImage(nowyStyl, 0, 50, 50, 50);

        if(nagroda != null){
            nagroda.odkryj();
        }

        setCzyPusty(true);
        setStyle(nowyStyl);

        Nagroda zwrocNagrode = this.nagroda;
        this.nagroda = null;
        return zwrocNagrode;
    }
}
