package view;

import java.awt.*;

public class ElementWyboruPoziomu {

    private String nazwaPoziomu;
    private Point polozenie;

    public ElementWyboruPoziomu(String nazwaPoziomu, Point location){
        this.polozenie = location;
        this.nazwaPoziomu = nazwaPoziomu;
    }

    public String getNazwaPoziomu() {
        return nazwaPoziomu;
    }

    public Point getPolozenie() {
        return polozenie;
    }

    public void setPolozenie(Point polozenie) {
        this.polozenie = polozenie;
    }
}
