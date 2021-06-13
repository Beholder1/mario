package obiekty.nagroda;

import obiekty.postac.Rario;

import java.awt.*;

public interface Nagroda {

    int getPunkt();

    void odkryj();

    Rectangle getGranice();

    void przyDotknieciu(Rario rario);

}
