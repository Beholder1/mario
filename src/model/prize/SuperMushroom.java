package model.prize;

import manager.GameEngine;
import model.postac.Rario;
import model.postac.RarioDuzy;
import view.Animation;
import view.ImageLoader;

import java.awt.image.BufferedImage;

public class SuperMushroom extends BoostItem{

    public SuperMushroom(double x, double y, BufferedImage style) {
        super(x, y, style);
        setPoint(125);
    }

    @Override
    public void onTouch(Rario rario, GameEngine engine) {
        rario.zyskajPunkty(getPoint());

        ImageLoader imageLoader = new ImageLoader();

        if(!rario.getRarioDuzy().getCzyDuzy()){
            BufferedImage[] leftFrames = imageLoader.getLeftFrames(RarioDuzy.DUZY);
            BufferedImage[] rightFrames = imageLoader.getRightFrames(RarioDuzy.DUZY);

            Animation animation = new Animation(leftFrames, rightFrames);
            RarioDuzy newForm = new RarioDuzy(animation, true);
            rario.setRarioDuzy(newForm);
            rario.setDimension(72, 72);
        }
    }
}
