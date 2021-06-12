package model.prize;

import manager.GameEngine;
import model.postac.Rario;

import java.awt.*;

public interface Prize {

    int getPoint();

    void reveal();

    Rectangle getBounds();

    void onTouch(Rario rario, GameEngine engine);

}
