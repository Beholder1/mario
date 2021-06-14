package obiekty.blok;

import manager.GameEngine;
import manager.ZarzadzaniePoziomem;
import obiekty.nagroda.Nagroda;

import java.awt.image.BufferedImage;

public class Cegla extends Blok {

    public Cegla(double x, double y, BufferedImage styl){
        super(x, y, styl);
        setCzyZniszczalny(true);
        setCzyPusty(true);

    }

    @Override
    public Nagroda odkryj(GameEngine silnik){
        ZarzadzaniePoziomem menedzer = silnik.getMapManager();
        if(!menedzer.getRario().czyDuzy())
            return null;
        menedzer.odkrycieCegly(this);
        return null;
    }
}
