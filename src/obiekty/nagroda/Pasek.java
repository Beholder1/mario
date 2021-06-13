package obiekty.nagroda;

import obiekty.postac.Rario;
import obiekty.postac.RarioDuzy;
import view.Animation;
import view.ImageLoader;

import java.awt.image.BufferedImage;

public class Pasek extends PrzedmiotSpecjalny {

    public Pasek(double x, double y, BufferedImage styl) {
        super(x, y, styl);
        setPunkt(125);
    }

    @Override
    public void przyDotknieciu(Rario rario) {
        rario.zyskajPunkty(getPunkt());

        ImageLoader obraz = new ImageLoader();

        if(!rario.getRarioDuzy().getCzyDuzy()){
            BufferedImage[] leweKlatki = obraz.getLeftFrames(RarioDuzy.DUZY);
            BufferedImage[] praweKlatki = obraz.getRightFrames(RarioDuzy.DUZY);

            Animation animation = new Animation(leweKlatki, praweKlatki);
            RarioDuzy nowyRario = new RarioDuzy(animation, true);
            rario.setRarioDuzy(nowyRario);
            rario.setWymiar(72, 72);
        }
    }
}
