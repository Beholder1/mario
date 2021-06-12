package manager;

import model.GameObject;
import model.Map;
import model.blok.Blok;
import model.blok.Cegla;
import model.postac.Przeciwnik;
import model.postac.Rario;
import model.nagroda.PrzedmiotSpecjalny;
import model.nagroda.Moneta;
import model.nagroda.Nagroda;
import view.ImageLoader;

import java.awt.*;
import java.util.ArrayList;

public class MapManager {

    private Map map;

    public MapManager() {}

    public void updateLocations() {
        if (map == null)
            return;

        map.updateLocations();
    }

    public void resetCurrentMap(GameEngine engine) {
        Rario rario = getMario();
        rario.zresetujPolozenie();
        engine.resetCamera();
        createMap(engine.getImageLoader(), map.getPath());
        map.setMario(rario);
    }

    public boolean createMap(ImageLoader loader, String path) {
        MapCreator mapCreator = new MapCreator(loader);
        map = mapCreator.createMap("/maps/" + path, 400);

        return map != null;
    }

    public void acquirePoints(int point) {
        map.getMario().zyskajPunkty(point);
    }

    public Rario getMario() {
        return map.getMario();
    }


    public boolean isGameOver() {
        return getMario().getPozostaleSerca() == 0;
    }

    public int getScore() {
        return getMario().getPunkty();
    }

    public int getRemainingLives() {
        return getMario().getPozostaleSerca();
    }

    public void drawMap(Graphics2D g2) {
        map.drawMap(g2);
    }

    public int passMission() {
        if(getMario().getX() >= map.getEndPoint().getX() && !map.getEndPoint().isTouched()){
            map.getEndPoint().setTouched(true);
            int height = (int)getMario().getY();
            return height * 2;
        }
        else
            return -1;
    }

    public boolean endLevel(){
        return getMario().getX() >= map.getEndPoint().getX() + 320;
    }

    public void checkCollisions(GameEngine engine) {
        if (map == null) {
            return;
        }

        checkBottomCollisions(engine);
        checkTopCollisions(engine);
        checkMarioHorizontalCollision(engine);
        checkEnemyCollisions();
        checkPrizeCollision();
        checkPrizeContact(engine);
    }

    private void checkBottomCollisions(GameEngine engine) {
        Rario rario = getMario();
        ArrayList<Blok> bricks = map.getAllBricks();
        ArrayList<Przeciwnik> enemies = map.getEnemies();
        ArrayList<GameObject> toBeRemoved = new ArrayList<>();

        Rectangle marioBottomBounds = rario.getBottomBounds();

        if (!rario.isJumping())
            rario.setFalling(true);

        for (Blok brick : bricks) {
            Rectangle brickTopBounds = brick.getTopBounds();
            if (marioBottomBounds.intersects(brickTopBounds)) {
                rario.setY(brick.getY() - rario.getDimension().height + 1);
                rario.setFalling(false);
                rario.setVelY(0);
            }
        }

        for (Przeciwnik przeciwnik : enemies) {
            Rectangle enemyTopBounds = przeciwnik.getTopBounds();
            if (marioBottomBounds.intersects(enemyTopBounds)) {
                rario.zyskajPunkty(100);
                toBeRemoved.add(przeciwnik);
            }
        }

        if (rario.getY() + rario.getDimension().height >= map.getBottomBorder()) {
            rario.setY(map.getBottomBorder() - rario.getDimension().height);
            rario.setFalling(false);
            rario.setVelY(0);
        }

        removeObjects(toBeRemoved);
    }

    private void checkTopCollisions(GameEngine engine) {
        Rario rario = getMario();
        ArrayList<Blok> bricks = map.getAllBricks();

        Rectangle marioTopBounds = rario.getTopBounds();
        for (Blok brick : bricks) {
            Rectangle brickBottomBounds = brick.getBottomBounds();
            if (marioTopBounds.intersects(brickBottomBounds)) {
                rario.setVelY(0);
                rario.setY(brick.getY() + brick.getDimension().height);
                Nagroda nagroda = brick.odkryj(engine);
                if(nagroda != null)
                    map.addRevealedPrize(nagroda);
            }
        }
    }

    private void checkMarioHorizontalCollision(GameEngine engine){
        Rario rario = getMario();
        ArrayList<Blok> bricks = map.getAllBricks();
        ArrayList<Przeciwnik> enemies = map.getEnemies();
        ArrayList<GameObject> toBeRemoved = new ArrayList<>();

        boolean marioDies = false;
        boolean toRight = rario.getWPrawo();

        Rectangle marioBounds = toRight ? rario.getRightBounds() : rario.getLeftBounds();

        for (Blok brick : bricks) {
            Rectangle brickBounds = !toRight ? brick.getRightBounds() : brick.getLeftBounds();
            if (marioBounds.intersects(brickBounds)) {
                rario.setVelX(0);
                if(toRight)
                    rario.setX(brick.getX() - rario.getDimension().width);
                else
                    rario.setX(brick.getX() + brick.getDimension().width);
            }
        }

        for(Przeciwnik przeciwnik : enemies){
            Rectangle enemyBounds = !toRight ? przeciwnik.getRightBounds() : przeciwnik.getLeftBounds();
            if (marioBounds.intersects(enemyBounds)) {
                marioDies = rario.dotknieciePrzeciwnika(engine);
                toBeRemoved.add(przeciwnik);
            }
        }
        removeObjects(toBeRemoved);


        if (rario.getX() <= engine.getCameraLocation().getX() && rario.getVelX() < 0) {
            rario.setVelX(0);
            rario.setX(engine.getCameraLocation().getX());
        }

        if(marioDies) {
            resetCurrentMap(engine);
        }
    }

    private void checkEnemyCollisions() {
        ArrayList<Blok> bricks = map.getAllBricks();
        ArrayList<Przeciwnik> enemies = map.getEnemies();

        for (Przeciwnik przeciwnik : enemies) {
            boolean standsOnBrick = false;

            for (Blok brick : bricks) {
                Rectangle enemyBounds = przeciwnik.getLeftBounds();
                Rectangle brickBounds = brick.getRightBounds();

                Rectangle enemyBottomBounds = przeciwnik.getBottomBounds();
                Rectangle brickTopBounds = brick.getTopBounds();

                if (przeciwnik.getVelX() > 0) {
                    enemyBounds = przeciwnik.getRightBounds();
                    brickBounds = brick.getLeftBounds();
                }

                if (enemyBounds.intersects(brickBounds)) {
                    przeciwnik.setVelX(-przeciwnik.getVelX());
                }

                if (enemyBottomBounds.intersects(brickTopBounds)){
                    przeciwnik.setFalling(false);
                    przeciwnik.setVelY(0);
                    przeciwnik.setY(brick.getY()- przeciwnik.getDimension().height);
                    standsOnBrick = true;
                }
            }

            if(przeciwnik.getY() + przeciwnik.getDimension().height > map.getBottomBorder()){
                przeciwnik.setFalling(false);
                przeciwnik.setVelY(0);
                przeciwnik.setY(map.getBottomBorder()- przeciwnik.getDimension().height);
            }

            if (!standsOnBrick && przeciwnik.getY() < map.getBottomBorder()){
                przeciwnik.setFalling(true);
            }
        }
    }

    private void checkPrizeCollision() {
        ArrayList<Nagroda> nagrody = map.getRevealedPrizes();
        ArrayList<Blok> bricks = map.getAllBricks();

        for (Nagroda nagroda : nagrody) {
            if (nagroda instanceof PrzedmiotSpecjalny) {
                PrzedmiotSpecjalny boost = (PrzedmiotSpecjalny) nagroda;
                Rectangle prizeBottomBounds = boost.getBottomBounds();
                Rectangle prizeRightBounds = boost.getRightBounds();
                Rectangle prizeLeftBounds = boost.getLeftBounds();
                boost.setFalling(true);

                for (Blok brick : bricks) {
                    Rectangle brickBounds;

                    if (boost.isFalling()) {
                        brickBounds = brick.getTopBounds();

                        if (brickBounds.intersects(prizeBottomBounds)) {
                            boost.setFalling(false);
                            boost.setVelY(0);
                            boost.setY(brick.getY() - boost.getDimension().height + 1);
                            if (boost.getVelX() == 0)
                                boost.setVelX(2);
                        }
                    }

                    if (boost.getVelX() > 0) {
                        brickBounds = brick.getLeftBounds();

                        if (brickBounds.intersects(prizeRightBounds)) {
                            boost.setVelX(-boost.getVelX());
                        }
                    } else if (boost.getVelX() < 0) {
                        brickBounds = brick.getRightBounds();

                        if (brickBounds.intersects(prizeLeftBounds)) {
                            boost.setVelX(-boost.getVelX());
                        }
                    }
                }

                if (boost.getY() + boost.getDimension().height > map.getBottomBorder()) {
                    boost.setFalling(false);
                    boost.setVelY(0);
                    boost.setY(map.getBottomBorder() - boost.getDimension().height);
                    if (boost.getVelX() == 0)
                        boost.setVelX(2);
                }

            }
        }
    }

    private void checkPrizeContact(GameEngine engine) {
        ArrayList<Nagroda> nagrody = map.getRevealedPrizes();
        ArrayList<GameObject> toBeRemoved = new ArrayList<>();

        Rectangle marioBounds = getMario().getGranice();
        for(Nagroda nagroda : nagrody){
            Rectangle prizeBounds = nagroda.getGranice();
            if (prizeBounds.intersects(marioBounds)) {
                nagroda.przyDotknieciu(getMario());
                toBeRemoved.add((GameObject) nagroda);
            } else if(nagroda instanceof Moneta){
                nagroda.przyDotknieciu(getMario());
            }
        }

        removeObjects(toBeRemoved);
    }

    private void removeObjects(ArrayList<GameObject> list){
        if(list == null)
            return;

        for(GameObject object : list){
            if(object instanceof Przeciwnik){
                map.removeEnemy((Przeciwnik)object);
            }
            else if(object instanceof Moneta || object instanceof PrzedmiotSpecjalny){
                map.removePrize((Nagroda)object);
            }
        }
    }

    public void addRevealedBrick(Cegla ordinaryBrick) {
        map.addRevealedBrick(ordinaryBrick);
    }

    public void updateTime(){
        if(map != null)
            map.updateTime(1);
    }
}
