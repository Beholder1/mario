package manager;

import model.EndFlag;
import model.blok.*;
import model.prize.*;
import view.ImageLoader;
import model.Map;
import model.enemy.Enemy;
import model.hero.Mario;

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

    Map createMap(String mapPath, double timeLimit) {
        BufferedImage mapImage = imageLoader.loadImage(mapPath);

        if (mapImage == null) {
            System.out.println("Given path is invalid...");
            return null;
        }

        Map createdMap = new Map(timeLimit, backgroundImage);
        String[] paths = mapPath.split("/");
        createdMap.setPath(paths[paths.length-1]);

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
                    createdMap.addBrick(brick);
                }
                else if (currentPixel == surpriseBrick) {
                    Prize prize = generateRandomPrize(xLocation, yLocation);
                    Blok brick = new SurpriseBrick(xLocation, yLocation, this.surpriseBrick, prize);
                    createdMap.addBrick(brick);
                }
                else if (currentPixel == pipe) {
                    Blok brick = new Rura(xLocation, yLocation, this.pipe);
                    createdMap.addGroundBrick(brick);
                }
                else if (currentPixel == groundBrick) {
                    Blok brick = new NiezniszczalnaCegla(xLocation, yLocation, this.groundBrick);
                    createdMap.addGroundBrick(brick);
                }
                else if (currentPixel == goomba) {
                    Enemy enemy = new Enemy(xLocation, yLocation, this.goombaLeft);
                    ((Enemy)enemy).setRightImage(goombaRight);
                    createdMap.addEnemy(enemy);
                }
                else if (currentPixel == mario) {
                    Mario marioObject = new Mario(xLocation, yLocation);
                    createdMap.setMario(marioObject);
                }
                else if(currentPixel == end){
                    EndFlag endPoint= new EndFlag(xLocation+24, yLocation, endFlag);
                    createdMap.setEndPoint(endPoint);
                }
            }
        }

        System.out.println("Map is created..");
        return createdMap;
    }

    private Prize generateRandomPrize(double x, double y){
        Prize generated;
        int random = (int)(Math.random() * 12);

        if(random == 0){
            generated = new SuperMushroom(x, y, this.superMushroom);
        }
        else if(random == 2){
            generated = new OneUpMushroom(x, y, this.oneUpMushroom);
        }
        else{
            generated = new Coin(x, y, this.coin, 50);
        }

        return generated;
    }


}
