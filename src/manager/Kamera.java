package manager;

public class Kamera {

    private double x, y;
    private int liczbaKlatek;

    public Kamera(){
        this.x = 0;
        this.y = 0;
        this.liczbaKlatek = 60;
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

    public void moveCam(double ileX, double ileY){
        if(liczbaKlatek > 0){
            int direction = (liczbaKlatek%2 == 0)? 1 : -1;
            x = x + 4 * direction;
            liczbaKlatek--;
        }
        else{
            x = x + ileX;
            y = y + ileY;
        }
    }
}
