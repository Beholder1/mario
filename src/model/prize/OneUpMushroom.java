package model.prize;

import manager.GameEngine;
import model.postac.Rario;

import java.awt.image.BufferedImage;

public class OneUpMushroom extends BoostItem{

    public OneUpMushroom(double x, double y, BufferedImage style) {
        super(x, y, style);
        setPoint(200);
    }

    @Override
    public void onTouch(Rario rario, GameEngine engine) {
        rario.zyskajPunkty(getPoint());
        rario.setPozostaleSerca(rario.getPozostaleSerca() + 1);
    }
}
