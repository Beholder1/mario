package view;

import java.awt.*;
import java.util.ArrayList;

public class MapSelection {

    private ArrayList<String> poziomy = new ArrayList<>();
    private MapSelectionItem[] mapSelectionItems;

    public MapSelection(){
        getMaps();
        this.mapSelectionItems = createItems(this.poziomy);
    }

    public void draw(Graphics g){
        g.setColor(Color.BLACK);
        g.fillRect(0,0, 1280, 720);

        if(mapSelectionItems == null){
            System.out.println(1);
            return;
        }

        String tytul = "Wybierz poziom";
        int x_location = (1280 - g.getFontMetrics().stringWidth(tytul))/2;
        g.setColor(Color.YELLOW);
        g.drawString(tytul, x_location, 100);

        for(MapSelectionItem item : mapSelectionItems){
            g.setColor(Color.WHITE);
            int width = g.getFontMetrics().stringWidth(item.getName().split("[.]")[0]);
            item.setLocation( new Point((1280-width)/2, item.getLocation().y));
            g.drawString(item.getName().split("[.]")[0], item.getLocation().x, item.getLocation().y);
        }
    }

    private void getMaps(){
        poziomy.add("Poziom 1.png");
        poziomy.add("Poziom 2.png");
        poziomy.add("Poziom 3.png");
        poziomy.add("Poziom 4.png");
        poziomy.add("Poziom 5.png");
    }

    private MapSelectionItem[] createItems(ArrayList<String> maps){
        if(maps == null)
            return null;

        int defaultGridSize = 100;
        MapSelectionItem[] items = new MapSelectionItem[maps.size()];
        for (int i = 0; i < items.length; i++) {
            Point location = new Point(0, (i+1)*defaultGridSize+150);
            items[i] = new MapSelectionItem(maps.get(i), location);
        }

        return items;
    }

    public String selectMap(int index){
        if(index < mapSelectionItems.length && index > -1)
            return mapSelectionItems[index].getName();
        return null;
    }

    public int changeSelectedMap(int index, boolean up) {
        if(up){
            if(index <= 0)
                return mapSelectionItems.length - 1;
            else
                return index - 1;
        }
        else{
            if(index >= mapSelectionItems.length - 1)
                return 0;
            else
                return index + 1;
        }
    }
}
