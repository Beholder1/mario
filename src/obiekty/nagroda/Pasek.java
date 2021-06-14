package obiekty.nagroda;

import obiekty.postac.Rario;
import obiekty.postac.RarioForma;
import view.Animacja;
import view.ZaladowanieObrazu;

import java.awt.image.BufferedImage;

public class Pasek extends PrzedmiotSpecjalny {

    public Pasek(double x, double y, BufferedImage styl) {
        super(x, y, styl);
        setPunkt(125);
    }

    @Override
    public void przyDotknieciu(Rario rario) {
        rario.zyskajPunkty(getPunkt());

        ZaladowanieObrazu obraz = new ZaladowanieObrazu();

        if(!rario.getRarioDuzy().getCzyDuzy()){
            BufferedImage[] leweKlatki = obraz.getLeweKlatki(RarioForma.DUZY);
            BufferedImage[] praweKlatki = obraz.getPraweKlatki(RarioForma.DUZY);

            Animacja animacja = new Animacja(leweKlatki, praweKlatki);
            RarioForma nowyRario = new RarioForma(animacja, true);
            rario.setRarioDuzy(nowyRario);
            rario.setWymiar(72, 72);
        }
    }
}
