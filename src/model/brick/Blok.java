package model.brick;

import manager.GameEngine;
import model.GameObject;
import model.prize.Prize;

import java.awt.image.BufferedImage;

public abstract class Blok extends GameObject{

    private boolean czyZniszczalny;
    private boolean czyPusty;

    public Blok(double x, double y, BufferedImage styl){
        super(x, y, styl);
        setDimension(50, 50);
    }

    public void setCzyZniszczalny(boolean czyZniszczalny) {
        this.czyZniszczalny = czyZniszczalny;
    }

    public void setCzyPusty(boolean czyPusty) {
        this.czyPusty = czyPusty;
    }

    public Prize odkryj(GameEngine silnik){ return null;}

    public Prize getPrize() {
        return null;
    }
}
