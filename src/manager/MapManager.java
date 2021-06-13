package manager;

import model.GameObject;
import model.Mapa;
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

    private Mapa mapa;

    public MapManager() {}

    public void updateLocations() {
        if (mapa == null)
            return;

        mapa.zmienPolozenie();
    }

    public void resetCurrentMap(GameEngine engine) {
        Rario rario = getMario();
        rario.zresetujPolozenie();
        engine.resetCamera();
        createMap(engine.getImageLoader(), mapa.getSciezka());
        mapa.setRario(rario);
    }

    public boolean createMap(ImageLoader loader, String path) {
        MapCreator mapCreator = new MapCreator(loader);
        mapa = mapCreator.createMap("/poziomy/" + path, 400);

        return mapa != null;
    }

    public void acquirePoints(int point) {
        mapa.getRario().zyskajPunkty(point);
    }

    public Rario getMario() {
        return mapa.getRario();
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
        mapa.drawMap(g2);
    }

    public int passMission() {
        if(getMario().getX() >= mapa.getKoniec().getX() && !mapa.getKoniec().getCzyDotknieta()){
            mapa.getKoniec().setCzyDotknieta(true);
            int height = (int)getMario().getY();
            return height * 2;
        }
        else
            return -1;
    }

    public boolean endLevel(){
        return getMario().getX() >= mapa.getKoniec().getX() + 320;
    }

    public void checkCollisions(GameEngine engine) {
        if (mapa == null) {
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
        ArrayList<Blok> bricks = mapa.getWszystkieCegly();
        ArrayList<Przeciwnik> enemies = mapa.getPrzeciwnicy();
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

        if (rario.getY() + rario.getDimension().height >= mapa.getDolnaGranica()) {
            rario.setY(mapa.getDolnaGranica() - rario.getDimension().height);
            rario.setFalling(false);
            rario.setVelY(0);
        }

        removeObjects(toBeRemoved);
    }

    private void checkTopCollisions(GameEngine engine) {
        Rario rario = getMario();
        ArrayList<Blok> bricks = mapa.getWszystkieCegly();

        Rectangle marioTopBounds = rario.getTopBounds();
        for (Blok brick : bricks) {
            Rectangle brickBottomBounds = brick.getBottomBounds();
            if (marioTopBounds.intersects(brickBottomBounds)) {
                rario.setVelY(0);
                rario.setY(brick.getY() + brick.getDimension().height);
                Nagroda nagroda = brick.odkryj(engine);
                if(nagroda != null)
                    mapa.dodajNagrode(nagroda);
            }
        }
    }

    private void checkMarioHorizontalCollision(GameEngine engine){
        Rario rario = getMario();
        ArrayList<Blok> bricks = mapa.getWszystkieCegly();
        ArrayList<Przeciwnik> enemies = mapa.getPrzeciwnicy();
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
        ArrayList<Blok> bricks = mapa.getWszystkieCegly();
        ArrayList<Przeciwnik> enemies = mapa.getPrzeciwnicy();

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

            if(przeciwnik.getY() + przeciwnik.getDimension().height > mapa.getDolnaGranica()){
                przeciwnik.setFalling(false);
                przeciwnik.setVelY(0);
                przeciwnik.setY(mapa.getDolnaGranica()- przeciwnik.getDimension().height);
            }

            if (!standsOnBrick && przeciwnik.getY() < mapa.getDolnaGranica()){
                przeciwnik.setFalling(true);
            }
        }
    }

    private void checkPrizeCollision() {
        ArrayList<Nagroda> nagrody = mapa.getNagrody();
        ArrayList<Blok> bricks = mapa.getWszystkieCegly();

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

                if (boost.getY() + boost.getDimension().height > mapa.getDolnaGranica()) {
                    boost.setFalling(false);
                    boost.setVelY(0);
                    boost.setY(mapa.getDolnaGranica() - boost.getDimension().height);
                    if (boost.getVelX() == 0)
                        boost.setVelX(2);
                }

            }
        }
    }

    private void checkPrizeContact(GameEngine engine) {
        ArrayList<Nagroda> nagrody = mapa.getNagrody();
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
                mapa.usunPrzeciwnika((Przeciwnik)object);
            }
            else if(object instanceof Moneta || object instanceof PrzedmiotSpecjalny){
                mapa.usunNagrode((Nagroda)object);
            }
        }
    }

    public void addRevealedBrick(Cegla ordinaryBrick) {
        mapa.dodajOdkrytaCegle(ordinaryBrick);
    }

}
