package manager;

import model.Flaga;
import model.blok.*;
import model.nagroda.*;
import view.ImageLoader;
import model.Mapa;
import model.postac.Przeciwnik;
import model.postac.Rario;

import java.awt.*;
import java.awt.image.BufferedImage;

class MapCreator {

    private ImageLoader imageLoader;
    private BufferedImage backgroundImage;
    private BufferedImage superMushroom, oneUpMushroom, coin;
    private BufferedImage ordinaryBrick, surpriseBrick, groundBrick, pipe;
    private BufferedImage goombaLeft, goombaRight, endFlag;

    MapCreator(ImageLoader imageLoader) {

        this.imageLoader = imageLoader;
        BufferedImage sprite = imageLoader.loadImage("/sprite1.png");

        this.backgroundImage = imageLoader.loadImage("/background.png");
        this.superMushroom = imageLoader.getSubImage(sprite, 100, 150, 50, 50);
        this.oneUpMushroom= imageLoader.getSubImage(sprite, 50, 150, 50, 50);
        this.coin = imageLoader.getSubImage(sprite, 0, 150, 50, 50);
        this.ordinaryBrick = imageLoader.getSubImage(sprite, 0, 0, 50, 50);
        this.surpriseBrick = imageLoader.getSubImage(sprite, 50, 0, 50, 50);
        this.groundBrick = imageLoader.getSubImage(sprite, 50, 50, 50, 50);
        this.pipe = imageLoader.getSubImage(sprite, 100, 0, 100, 100);
        this.goombaLeft = imageLoader.getSubImage(sprite, 0, 100, 50, 50);
        this.goombaRight = imageLoader.getSubImage(sprite, 50, 100, 50, 50);
        this.endFlag = imageLoader.getSubImage(sprite, 100, 100, 50, 50);

    }

    Mapa createMap(String mapPath, double timeLimit) {
        BufferedImage mapImage = imageLoader.loadImage(mapPath);

        if (mapImage == null) {
            System.out.println("Given path is invalid...");
            return null;
        }

        Mapa createdMap = new Mapa(backgroundImage);
        String[] paths = mapPath.split("/");
        createdMap.setSciezka(paths[paths.length-1]);

        int pixelMultiplier = 50;

        int mario = new Color(160, 160, 160).getRGB();
        int ordinaryBrick = new Color(0, 0, 255).getRGB();
        int surpriseBrick = new Color(255, 255, 0).getRGB();
        int groundBrick = new Color(255, 0, 0).getRGB();
        int pipe = new Color(0, 255, 0).getRGB();
        int goomba = new Color(0, 255, 255).getRGB();
        int end = new Color(160, 0, 160).getRGB();

        for (int x = 0; x < mapImage.getWidth(); x++) {
            for (int y = 0; y < mapImage.getHeight(); y++) {

                int currentPixel = mapImage.getRGB(x, y);
                int xLocation = x*pixelMultiplier;
                int yLocation = y*pixelMultiplier;

                if (currentPixel == ordinaryBrick) {
                    Blok brick = new Cegla(xLocation, yLocation, this.ordinaryBrick);
                    createdMap.dodajCegle(brick);
                }
                else if (currentPixel == surpriseBrick) {
                    Nagroda nagroda = generateRandomPrize(xLocation, yLocation);
                    Blok brick = new Pytajnik(xLocation, yLocation, this.surpriseBrick, nagroda);
                    createdMap.dodajCegle(brick);
                }
                else if (currentPixel == pipe) {
                    Blok brick = new Rura(xLocation, yLocation, this.pipe);
                    createdMap.dodajNiezniszczalnaCegle(brick);
                }
                else if (currentPixel == groundBrick) {
                    Blok brick = new NiezniszczalnaCegla(xLocation, yLocation, this.groundBrick);
                    createdMap.dodajNiezniszczalnaCegle(brick);
                }
                else if (currentPixel == goomba) {
                    Przeciwnik przeciwnik = new Przeciwnik(xLocation, yLocation, this.goombaLeft);
                    ((Przeciwnik) przeciwnik).setPrawyObraz(goombaRight);
                    createdMap.dodajPrzeciwnika(przeciwnik);
                }
                else if (currentPixel == mario) {
                    Rario rarioObject = new Rario(xLocation, yLocation);
                    createdMap.setRario(rarioObject);
                }
                else if(currentPixel == end){
                    Flaga endPoint= new Flaga(xLocation+24, yLocation, endFlag);
                    createdMap.setKoniec(endPoint);
                }
            }
        }

        System.out.println("Map is created..");
        return createdMap;
    }

    private Nagroda generateRandomPrize(double x, double y){
        Nagroda generated;
        int random = (int)(Math.random() * 12);

        if(random == 0){
            generated = new Pasek(x, y, this.superMushroom);
        }
        else if(random == 2){
            generated = new Serce(x, y, this.oneUpMushroom);
        }
        else{
            generated = new Moneta(x, y, this.coin, 50);
        }

        return generated;
    }


}
