package model.nagroda;

import model.postac.Rario;

import java.awt.image.BufferedImage;

public class Serce extends PrzedmiotSpecjalny {

    public Serce(double x, double y, BufferedImage styl) {
        super(x, y, styl);
        setPunkt(200);
    }

    @Override
    public void przyDotknieciu(Rario rario) {
        rario.zyskajPunkty(getPunkt());
        rario.setPozostaleSerca(rario.getPozostaleSerca() + 1);
    }
}
