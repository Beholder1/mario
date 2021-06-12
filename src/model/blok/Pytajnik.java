package model.blok;

import manager.GameEngine;
import model.prize.Prize;

import java.awt.image.BufferedImage;

public class Pytajnik extends Blok{

    private Prize nagroda;

    public Pytajnik(double x, double y, BufferedImage styl, Prize nagroda) {
        super(x, y, styl);
        setCzyZniszczalny(false);
        setCzyPusty(false);
        this.nagroda = nagroda;
    }

    @Override
    public Prize odkryj(GameEngine silnik){
        BufferedImage nowyStyl = silnik.getImageLoader().loadImage("/sprite1.png");
        nowyStyl = silnik.getImageLoader().getSubImage(nowyStyl, 0, 50, 50, 50);

        if(nagroda != null){
            nagroda.reveal();
        }

        setCzyPusty(true);
        setStyle(nowyStyl);

        Prize zwrocNagrode = this.nagroda;
        this.nagroda = null;
        return zwrocNagrode;
    }

    @Override
    public Prize getPrize(){
        return nagroda;
    }
}
