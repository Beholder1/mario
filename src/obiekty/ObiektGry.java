package obiekty;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class ObiektGry {

    private double x, y;
    private double predkoscWX, predkoscWY;
    private Dimension wymiar;
    private BufferedImage styl;
    private double grawitacja;
    private boolean czySpada, czySkacze;

    public ObiektGry(double x, double y, BufferedImage styl){
        setPolozenie(x, y);
        setStyl(styl);

        if(styl != null){
            setWymiar(styl.getWidth(), styl.getHeight());
        }

        predkoscWX = 0;
        predkoscWY = 0;
        grawitacja = 0.38;
        czySkacze = false;
        czySpada = true;
    }

    public void wyswietl(Graphics grafika) {
        BufferedImage styl = getStyl();

        if(styl != null){
            grafika.drawImage(styl, (int)x, (int)y, null);
        }
    }

    public void zmienPolozenie() {
        if(czySkacze && predkoscWY <= 0){
            czySkacze = false;
            czySpada = true;
        }
        else if(czySkacze){
            predkoscWY = predkoscWY - grawitacja;
            y = y - predkoscWY;
        }

        if(czySpada){
            y = y + predkoscWY;
            predkoscWY = predkoscWY + grawitacja;
        }

        x = x + predkoscWX;
    }

    public void setPolozenie(double x, double y) {
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

    public void setWymiar(int szerokosc, int wysokosc){ this.wymiar =  new Dimension(szerokosc, wysokosc); }

    public BufferedImage getStyl() {
        return styl;
    }

    public void setStyl(BufferedImage styl) {
        this.styl = styl;
    }

    public double getPredkoscWX() {
        return predkoscWX;
    }

    public void setPredkoscWX(double predkoscWX) {
        this.predkoscWX = predkoscWX;
    }

    public double getPredkoscWY() {
        return predkoscWY;
    }

    public void setPredkoscWY(double predkoscWY) {
        this.predkoscWY = predkoscWY;
    }

    public Rectangle gornaGranica(){
        return new Rectangle((int)x+wymiar.width/6, (int)y, 2*wymiar.width/3, wymiar.height/2);
    }

    public Rectangle dolnaGranica(){
        return new Rectangle((int)x+wymiar.width/6, (int)y + wymiar.height/2, 2*wymiar.width/3, wymiar.height/2);
    }

    public Rectangle lewaGranica(){
        return new Rectangle((int)x, (int)y + wymiar.height/4, wymiar.width/4, wymiar.height/2);
    }

    public Rectangle prawaGranica(){
        return new Rectangle((int)x + 3*wymiar.width/4, (int)y + wymiar.height/4, wymiar.width/4, wymiar.height/2);
    }

    public Rectangle getGranice(){
        return new Rectangle((int)x, (int)y, wymiar.width, wymiar.height);
    }

    public boolean getCzySpada() {
        return czySpada;
    }

    public void setCzySpada(boolean czySpada) {
        this.czySpada = czySpada;
    }

    public boolean getCzySkacze() {
        return czySkacze;
    }

    public void setCzySkacze(boolean czySkacze) {
        this.czySkacze = czySkacze;
    }
}
