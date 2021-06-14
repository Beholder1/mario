package obiekty.postac;

import view.Animacja;
import view.ZaladowanieObrazu;

import java.awt.image.BufferedImage;

public class RarioForma {

    public static final int MALY = 0, DUZY = 1;

    private Animacja animacja;
    private boolean czyDuzy;

    public RarioForma(Animacja animacja, boolean czyDuzy){
        this.animacja = animacja;
        this.czyDuzy = czyDuzy;
    }

    public BufferedImage getCurrentStyle(boolean czyWPrawo, boolean ruszX, boolean ruszY){

        BufferedImage styl;

        if(ruszY && czyWPrawo){
            styl = animacja.getPraweKlatki()[0];
        }
        else if(ruszY){
            styl = animacja.getLeweKlatki()[0];
        }
        else if(ruszX){
            styl = animacja.animuj(czyWPrawo);
        }
        else {
            if(czyWPrawo){
                styl = animacja.getPraweKlatki()[1];
            }
            else
                styl = animacja.getLeweKlatki()[1];
        }

        return styl;
    }

    public RarioForma dotknieciePrzeciwnika(ZaladowanieObrazu obraz) {
        BufferedImage[] leweKlatki = obraz.getLeweKlatki(0);
        BufferedImage[] praweKlatki = obraz.getPraweKlatki(0);

        Animacja animacja1 = new Animacja(leweKlatki, praweKlatki);

        return new RarioForma(animacja1, false);
    }

    public boolean getCzyDuzy() {
        return czyDuzy;
    }


}
