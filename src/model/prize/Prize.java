package model.prize;

import manager.GameEngine;
import model.postac.Mario;

import java.awt.*;

public interface Prize {

    int getPoint();

    void reveal();

    Rectangle getBounds();

    void onTouch(Mario mario, GameEngine engine);

}
