package model.blok;

import java.awt.image.BufferedImage;

public class Rura extends Blok{

    public Rura(double x, double y, BufferedImage styl){
        super(x, y, styl);
        setCzyZniszczalny(false);
        setCzyPusty(true);
        setDimension(100, 100);
    }
}
