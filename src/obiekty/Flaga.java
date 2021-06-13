package obiekty;

import java.awt.image.BufferedImage;

public class Flaga extends ObiektGry {

    private boolean czyDotknieta = false;

    public Flaga(double x, double y, BufferedImage styl) {
        super(x, y, styl);
    }

    @Override
    public void zmienPolozenie() {
        if(czyDotknieta){
            if(getY() + getWymiar().getHeight() >= 576){
                setCzySpada(false);
                setPredkoscWY(0);
                setY(576 - getWymiar().getHeight());
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
