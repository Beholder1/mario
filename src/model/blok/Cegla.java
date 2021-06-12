package model.blok;

import manager.GameEngine;
import manager.MapManager;
import model.nagroda.Nagroda;

import java.awt.image.BufferedImage;

public class Cegla extends Blok {
    
    private boolean niszczenie;

    public Cegla(double x, double y, BufferedImage styl){
        super(x, y, styl);
        setCzyZniszczalny(true);
        setCzyPusty(true);


        niszczenie = false;

    }

    @Override
    public Nagroda odkryj(GameEngine silnik){
        MapManager menedzer = silnik.getMapManager();
        if(!menedzer.getMario().isSuper())
            return null;

        niszczenie = true;
        menedzer.addRevealedBrick(this);

        double x = getX() - 27, y = getY() - 27;
        setLocation(x, y);

        return null;
    }

}
