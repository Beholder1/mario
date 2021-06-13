package obiekty.blok;

import java.awt.image.BufferedImage;

public class NiezniszczalnaCegla extends Blok{

    public NiezniszczalnaCegla(double x, double y, BufferedImage styl){
        super(x, y, styl);
        setCzyZniszczalny(false);
        setCzyPusty(true);
    }

}
