package model;

import java.awt.image.BufferedImage;

public class Flaga extends GameObject{

    private boolean czyDotknieta = false;

    public Flaga(double x, double y, BufferedImage styl) {
        super(x, y, styl);
    }

    @Override
    public void zmienPolozenie() {
        if(czyDotknieta){
            if(getY() + getDimension().getHeight() >= 576){
                setFalling(false);
                setVelY(0);
                setY(576 - getDimension().getHeight());
            }
            super.zmienPolozenie();
        }
    }

    public boolean getCzyDotknieta() {
        return czyDotknieta;
    }

    public void setCzyDotknieta(boolean czyDotknieta) {
        this.czyDotknieta = czyDotknieta;
    }
}
