package model.postac;

import view.Animation;
import view.ImageLoader;

import java.awt.image.BufferedImage;

public class RarioDuzy {

    public static final int MALY = 0, DUZY = 1;

    private Animation animacja;
    private boolean czyDuzy;

    public RarioDuzy(Animation animacja, boolean czyDuzy){
        this.animacja = animacja;
        this.czyDuzy = czyDuzy;
    }

    public BufferedImage getCurrentStyle(boolean wPrawo, boolean ruszX, boolean ruszY){

        BufferedImage styl;

        if(ruszY && wPrawo){
            styl = animacja.getRightFrames()[0];
        }
        else if(ruszY){
            styl = animacja.getLeftFrames()[0];
        }
        else if(ruszX){
            styl = animacja.animate(5, wPrawo);
        }
        else {
            if(wPrawo){
                styl = animacja.getRightFrames()[1];
            }
            else
                styl = animacja.getLeftFrames()[1];
        }

        return styl;
    }

    public RarioDuzy dotknieciePrzeciwnika(ImageLoader obraz) {
        BufferedImage[] leweKlatki = obraz.getLeftFrames(0);
        BufferedImage[] praweKlatki = obraz.getRightFrames(0);

        Animation animacja1 = new Animation(leweKlatki, praweKlatki);

        return new RarioDuzy(animacja1, false);
    }

    public boolean getCzyDuzy() {
        return czyDuzy;
    }


}
