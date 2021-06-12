package manager;

import model.GameObject;
import model.Map;
import model.blok.Blok;
import model.blok.Cegla;
import model.postac.Przeciwnik;
import model.postac.Mario;
import model.prize.BoostItem;
import model.prize.Coin;
import model.prize.Prize;
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
        Mario mario = getMario();
        mario.resetLocation();
        engine.resetCamera();
        createMap(engine.getImageLoader(), map.getPath());
        map.setMario(mario);
    }

    public boolean createMap(ImageLoader loader, String path) {
        MapCreator mapCreator = new MapCreator(loader);
        map = mapCreator.createMap("/maps/" + path, 400);

        return map != null;
    }

    public void acquirePoints(int point) {
        map.getMario().acquirePoints(point);
    }

    public Mario getMario() {
        return map.getMario();
    }


    public boolean isGameOver() {
        return getMario().getRemainingLives() == 0;
    }

    public int getScore() {
        return getMario().getPoints();
    }

    public int getRemainingLives() {
        return getMario().getRemainingLives();
    }

    public int getCoins() {
        return getMario().getCoins();
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
        Mario mario = getMario();
        ArrayList<Blok> bricks = map.getAllBricks();
        ArrayList<Przeciwnik> enemies = map.getEnemies();
        ArrayList<GameObject> toBeRemoved = new ArrayList<>();

        Rectangle marioBottomBounds = mario.getBottomBounds();

        if (!mario.isJumping())
            mario.setFalling(true);

        for (Blok brick : bricks) {
            Rectangle brickTopBounds = brick.getTopBounds();
            if (marioBottomBounds.intersects(brickTopBounds)) {
                mario.setY(brick.getY() - mario.getDimension().height + 1);
                mario.setFalling(false);
                mario.setVelY(0);
            }
        }

        for (Przeciwnik przeciwnik : enemies) {
            Rectangle enemyTopBounds = przeciwnik.getTopBounds();
            if (marioBottomBounds.intersects(enemyTopBounds)) {
                mario.acquirePoints(100);
                toBeRemoved.add(przeciwnik);
            }
        }

        if (mario.getY() + mario.getDimension().height >= map.getBottomBorder()) {
            mario.setY(map.getBottomBorder() - mario.getDimension().height);
            mario.setFalling(false);
            mario.setVelY(0);
        }

        removeObjects(toBeRemoved);
    }

    private void checkTopCollisions(GameEngine engine) {
        Mario mario = getMario();
        ArrayList<Blok> bricks = map.getAllBricks();

        Rectangle marioTopBounds = mario.getTopBounds();
        for (Blok brick : bricks) {
            Rectangle brickBottomBounds = brick.getBottomBounds();
            if (marioTopBounds.intersects(brickBottomBounds)) {
                mario.setVelY(0);
                mario.setY(brick.getY() + brick.getDimension().height);
                Prize prize = brick.odkryj(engine);
                if(prize != null)
                    map.addRevealedPrize(prize);
            }
        }
    }

    private void checkMarioHorizontalCollision(GameEngine engine){
        Mario mario = getMario();
        ArrayList<Blok> bricks = map.getAllBricks();
        ArrayList<Przeciwnik> enemies = map.getEnemies();
        ArrayList<GameObject> toBeRemoved = new ArrayList<>();

        boolean marioDies = false;
        boolean toRight = mario.getToRight();

        Rectangle marioBounds = toRight ? mario.getRightBounds() : mario.getLeftBounds();

        for (Blok brick : bricks) {
            Rectangle brickBounds = !toRight ? brick.getRightBounds() : brick.getLeftBounds();
            if (marioBounds.intersects(brickBounds)) {
                mario.setVelX(0);
                if(toRight)
                    mario.setX(brick.getX() - mario.getDimension().width);
                else
                    mario.setX(brick.getX() + brick.getDimension().width);
            }
        }

        for(Przeciwnik przeciwnik : enemies){
            Rectangle enemyBounds = !toRight ? przeciwnik.getRightBounds() : przeciwnik.getLeftBounds();
            if (marioBounds.intersects(enemyBounds)) {
                marioDies = mario.onTouchEnemy(engine);
                toBeRemoved.add(przeciwnik);
            }
        }
        removeObjects(toBeRemoved);


        if (mario.getX() <= engine.getCameraLocation().getX() && mario.getVelX() < 0) {
            mario.setVelX(0);
            mario.setX(engine.getCameraLocation().getX());
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
        ArrayList<Prize> prizes = map.getRevealedPrizes();
        ArrayList<Blok> bricks = map.getAllBricks();

        for (Prize prize : prizes) {
            if (prize instanceof BoostItem) {
                BoostItem boost = (BoostItem) prize;
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
        ArrayList<Prize> prizes = map.getRevealedPrizes();
        ArrayList<GameObject> toBeRemoved = new ArrayList<>();

        Rectangle marioBounds = getMario().getBounds();
        for(Prize prize : prizes){
            Rectangle prizeBounds = prize.getBounds();
            if (prizeBounds.intersects(marioBounds)) {
                prize.onTouch(getMario(), engine);
                toBeRemoved.add((GameObject) prize);
            } else if(prize instanceof Coin){
                prize.onTouch(getMario(), engine);
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
            else if(object instanceof Coin || object instanceof BoostItem){
                map.removePrize((Prize)object);
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
