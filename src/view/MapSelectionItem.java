package view;

import java.awt.*;

public class MapSelectionItem {

    private String name;
    private Point location;


    public MapSelectionItem(String map, Point location){
        this.location = location;
        this.name = map;
    }

    public String getName() {
        return name;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }
}
