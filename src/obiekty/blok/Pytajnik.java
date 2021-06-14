package obiekty.blok;

import zarzadzanie.SilnikGry;
import obiekty.nagroda.Nagroda;

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
    public Nagroda odkryj(SilnikGry silnik){
        BufferedImage nowyStyl = silnik.getZaladowanieObrazu().zaladujObraz("/obiekty.png");
        nowyStyl = silnik.getZaladowanieObrazu().getMniejszyObraz(nowyStyl, 0, 50, 50, 50);

        if(nagroda != null){
            nagroda.odkryj();
        }

        setCzyPusty(true);
        setStyl(nowyStyl);

        Nagroda zwrocNagrode = this.nagroda;
        this.nagroda = null;
        return zwrocNagrode;
    }
}
