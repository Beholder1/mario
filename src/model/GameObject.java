package model;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class GameObject {

    private double x, y;
    private double velX, velY;
    private Dimension wymiar;
    private BufferedImage styl;
    private double gravityAcc;
    private boolean falling, jumping;

    public GameObject(double x, double y, BufferedImage style){
        setLocation(x, y);
        setStyl(style);

        if(style != null){
            setDimension(style.getWidth(), style.getHeight());
        }

        setVelX(0);
        setVelY(0);
        setGravityAcc(0.38);
        jumping = false;
        falling = true;
    }

    public void wyswietl(Graphics g) {
        BufferedImage style = getStyl();

        if(style != null){
            g.drawImage(style, (int)x, (int)y, null);
        }
    }

    public void zmienPolozenie() {
        if(jumping && velY <= 0){
            jumping = false;
            falling = true;
        }
        else if(jumping){
            velY = velY - gravityAcc;
            y = y - velY;
        }

        if(falling){
            y = y + velY;
            velY = velY + gravityAcc;
        }

        x = x + velX;
    }

    public void setLocation(double x, double y) {
        setX(x);
        setY(y);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Dimension getWymiar(){
        return wymiar;
    }

    public void setDimension(int width, int height){ this.wymiar =  new Dimension(width, height); }

    public BufferedImage getStyl() {
        return styl;
    }

    public void setStyl(BufferedImage styl) {
        this.styl = styl;
    }

    public double getVelX() {
        return velX;
    }

    public void setVelX(double velX) {
        this.velX = velX;
    }

    public double getVelY() {
        return velY;
    }

    public void setVelY(double velY) {
        this.velY = velY;
    }

    public void setGravityAcc(double gravityAcc) {
        this.gravityAcc = gravityAcc;
    }

    public Rectangle getTopBounds(){
        return new Rectangle((int)x+wymiar.width/6, (int)y, 2*wymiar.width/3, wymiar.height/2);
    }

    public Rectangle getBottomBounds(){
        return new Rectangle((int)x+wymiar.width/6, (int)y + wymiar.height/2, 2*wymiar.width/3, wymiar.height/2);
    }

    public Rectangle getLeftBounds(){
        return new Rectangle((int)x, (int)y + wymiar.height/4, wymiar.width/4, wymiar.height/2);
    }

    public Rectangle getRightBounds(){
        return new Rectangle((int)x + 3*wymiar.width/4, (int)y + wymiar.height/4, wymiar.width/4, wymiar.height/2);
    }

    public Rectangle getGranice(){
        return new Rectangle((int)x, (int)y, wymiar.width, wymiar.height);
    }

    public boolean isFalling() {
        return falling;
    }

    public void setFalling(boolean falling) {
        this.falling = falling;
    }

    public boolean isJumping() {
        return jumping;
    }

    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }
}
