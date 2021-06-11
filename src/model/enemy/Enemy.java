package model.enemy;

import model.GameObject;
import java.awt.*;
import java.awt.image.BufferedImage;


public class Enemy extends GameObject{
    private BufferedImage rightImage;
    public Enemy(double x, double y, BufferedImage style) {
        super(x, y, style);
        setFalling(false);
        setJumping(false);
        setVelX(3);
    }
    @Override
    public void draw(Graphics g){
        if(getVelX() > 0){
            g.drawImage(rightImage, (int)getX(), (int)getY(), null);
        }
        else
            super.draw(g);
    }

    public void setRightImage(BufferedImage rightImage) {
        this.rightImage = rightImage;
    }
}
