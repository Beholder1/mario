package view;

import java.io.Serializable;

public class Rekord implements Serializable {
    private String poziom;
    private int rekord;

    public Rekord(String poziom, int rekord)
    {
        this.poziom = poziom;
        this.rekord = rekord;
    }

    public int getRekord(){
        return rekord;
    }

    @Override
    public String toString() {
        return poziom + ": " + rekord;
    }
}
