package model.nagroda;

import model.postac.Rario;

import java.awt.*;

public interface Nagroda {

    int getPunkt();

    void odkryj();

    Rectangle getGranice();

    void przyDotknieciu(Rario rario);

}
