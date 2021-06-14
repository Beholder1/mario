package obiekty.blok;

import zarzadzanie.SilnikGry;
import zarzadzanie.ZarzadzaniePoziomem;
import obiekty.nagroda.Nagroda;

import java.awt.image.BufferedImage;

public class Cegla extends Blok {

    public Cegla(double x, double y, BufferedImage styl){
        super(x, y, styl);
        setCzyZniszczalny(true);
        setCzyPusty(true);

    }

    @Override
    public Nagroda odkryj(SilnikGry silnik){
        ZarzadzaniePoziomem menedzer = silnik.getZarzadzaniePoziomem();
        if(!menedzer.getRario().czyDuzy())
            return null;
        menedzer.odkrycieCegly(this);
        return null;
    }
}
