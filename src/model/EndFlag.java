package model;

import java.awt.image.BufferedImage;

public class EndFlag extends GameObject{

    private boolean touched = false;

    public EndFlag(double x, double y, BufferedImage style) {
        super(x, y, style);
    }

    @Override
    public void zmienPolozenie() {
        if(touched){
            if(getY() + getDimension().getHeight() >= 576){
                setFalling(false);
                setVelY(0);
                setY(576 - getDimension().getHeight());
            }
            super.zmienPolozenie();
        }
    }

    public boolean isTouched() {
        return touched;
    }

    public void setTouched(boolean touched) {
        this.touched = touched;
    }
}
