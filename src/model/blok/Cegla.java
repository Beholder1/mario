package model.blok;

import manager.GameEngine;
import manager.MapManager;
import model.nagroda.Nagroda;
import view.Animation;
import view.ImageLoader;

import java.awt.image.BufferedImage;

public class Cegla extends Blok {

    private Animation animacja;
    private boolean niszczenie;
    private int klatki;

    public Cegla(double x, double y, BufferedImage styl){
        super(x, y, styl);
        setCzyZniszczalny(true);
        setCzyPusty(true);

        ustawAnimacje();
        niszczenie = false;
        klatki = animacja.getLeftFrames().length;
    }

    private void ustawAnimacje(){
        ImageLoader obraz = new ImageLoader();
        BufferedImage[] leweKlatki = obraz.getBrickFrames();

        animacja = new Animation(leweKlatki, leweKlatki);
    }

    @Override
    public Nagroda odkryj(GameEngine silnik){
        MapManager menedzer = silnik.getMapManager();
        if(!menedzer.getMario().isSuper())
            return null;

        niszczenie = true;
        menedzer.addRevealedBrick(this);

        double x = getX() - 27, y = getY() - 27;
        setLocation(x, y);

        return null;
    }

    public int getFrames(){
        return klatki;
    }

    public void animuj(){
        if(niszczenie){
            setStyle(animacja.animate(3, true));
            klatki--;
        }
    }
}
