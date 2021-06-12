package model.hero;

import view.Animation;
import view.ImageLoader;

import java.awt.image.BufferedImage;

public class MarioForm {

    public static final int SMALL = 0, SUPER = 1;

    private Animation animation;
    private boolean isSuper;

    public MarioForm(Animation animation, boolean isSuper){
        this.animation = animation;
        this.isSuper = isSuper;
    }

    public BufferedImage getCurrentStyle(boolean toRight, boolean movingInX, boolean movingInY){

        BufferedImage style;

        if(movingInY && toRight){
            style = animation.getRightFrames()[0];
        }
        else if(movingInY){
            style = animation.getLeftFrames()[0];
        }
        else if(movingInX){
            style = animation.animate(5, toRight);
        }
        else {
            if(toRight){
                style = animation.getRightFrames()[1];
            }
            else
                style = animation.getLeftFrames()[1];
        }

        return style;
    }

    public MarioForm onTouchEnemy(ImageLoader imageLoader) {
        BufferedImage[] leftFrames = imageLoader.getLeftFrames(0);
        BufferedImage[] rightFrames= imageLoader.getRightFrames(0);

        Animation newAnimation = new Animation(leftFrames, rightFrames);

        return new MarioForm(newAnimation, false);
    }


    public boolean isSuper() {
        return isSuper;
    }

    public void setSuper(boolean aSuper) {
        isSuper = aSuper;
    }
}