package manager;

import obiekty.ObiektGry;
import obiekty.Poziom;
import obiekty.blok.Blok;
import obiekty.blok.Cegla;
import obiekty.postac.Przeciwnik;
import obiekty.postac.Rario;
import obiekty.nagroda.PrzedmiotSpecjalny;
import obiekty.nagroda.Moneta;
import obiekty.nagroda.Nagroda;
import view.ImageLoader;

import java.awt.*;
import java.util.ArrayList;

public class MapManager {

    private Poziom poziom;

    public MapManager() {}

    public void updateLocations() {
        if (poziom == null)
            return;

        poziom.zmienPolozenie();
    }

    public void resetCurrentMap(GameEngine engine) {
        Rario rario = getMario();
        rario.zresetujPolozenie();
        engine.resetCamera();
        createMap(engine.getImageLoader(), poziom.getSciezka());
        poziom.setRario(rario);
    }

    public boolean createMap(ImageLoader loader, String path) {
        GeneratorMapy generatorMapy = new GeneratorMapy(loader);
        poziom = generatorMapy.utworzPoziom("/poziomy/" + path);

        return poziom != null;
    }

    public void acquirePoints(int point) {
        poziom.getRario().zyskajPunkty(point);
    }

    public Rario getMario() {
        return poziom.getRario();
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
        poziom.drawMap(g2);
    }

    public int passMission() {
        if(getMario().getX() >= poziom.getKoniec().getX() && !poziom.getKoniec().getCzyDotknieta()){
            poziom.getKoniec().setCzyDotknieta(true);
            int height = (int)getMario().getY();
            return height * 2;
        }
        else
            return -1;
    }

    public boolean endLevel(){
        return getMario().getX() >= poziom.getKoniec().getX() + 320;
    }

    public void checkCollisions(GameEngine engine) {
        if (poziom == null) {
            return;
        }

        checkBottomCollisions();
        checkTopCollisions(engine);
        checkMarioHorizontalCollision(engine);
        checkEnemyCollisions();
        checkPrizeCollision();
        checkPrizeContact();
    }

    private void checkBottomCollisions() {
        Rario rario = getMario();
        ArrayList<Blok> bricks = poziom.getWszystkieCegly();
        ArrayList<Przeciwnik> enemies = poziom.getPrzeciwnicy();
        ArrayList<ObiektGry> toBeRemoved = new ArrayList<>();

        Rectangle marioBottomBounds = rario.dolnaGranica();

        if (!rario.getCzySkacze())
            rario.setCzySpada(true);

        for (Blok brick : bricks) {
            Rectangle brickTopBounds = brick.gornaGranica();
            if (marioBottomBounds.intersects(brickTopBounds)) {
                rario.setY(brick.getY() - rario.getWymiar().height + 1);
                rario.setCzySpada(false);
                rario.setPredkoscWY(0);
            }
        }

        for (Przeciwnik przeciwnik : enemies) {
            Rectangle enemyTopBounds = przeciwnik.gornaGranica();
            if (marioBottomBounds.intersects(enemyTopBounds)) {
                rario.zyskajPunkty(100);
                toBeRemoved.add(przeciwnik);
            }
        }

        if (rario.getY() + rario.getWymiar().height >= poziom.getDolnaGranica()) {
            rario.setY(poziom.getDolnaGranica() - rario.getWymiar().height);
            rario.setCzySpada(false);
            rario.setPredkoscWY(0);
        }

        removeObjects(toBeRemoved);
    }

    private void checkTopCollisions(GameEngine engine) {
        Rario rario = getMario();
        ArrayList<Blok> bricks = poziom.getWszystkieCegly();

        Rectangle marioTopBounds = rario.gornaGranica();
        for (Blok brick : bricks) {
            Rectangle brickBottomBounds = brick.dolnaGranica();
            if (marioTopBounds.intersects(brickBottomBounds)) {
                rario.setPredkoscWY(0);
                rario.setY(brick.getY() + brick.getWymiar().height);
                Nagroda nagroda = brick.odkryj(engine);
                if(nagroda != null)
                    poziom.dodajNagrode(nagroda);
            }
        }
    }

    private void checkMarioHorizontalCollision(GameEngine engine){
        Rario rario = getMario();
        ArrayList<Blok> bricks = poziom.getWszystkieCegly();
        ArrayList<Przeciwnik> enemies = poziom.getPrzeciwnicy();
        ArrayList<ObiektGry> toBeRemoved = new ArrayList<>();

        boolean marioDies = false;
        boolean toRight = rario.getWPrawo();

        Rectangle marioBounds = toRight ? rario.prawaGranica() : rario.lewaGranica();

        for (Blok brick : bricks) {
            Rectangle brickBounds = !toRight ? brick.prawaGranica() : brick.lewaGranica();
            if (marioBounds.intersects(brickBounds)) {
                rario.setPredkoscWX(0);
                if(toRight)
                    rario.setX(brick.getX() - rario.getWymiar().width);
                else
                    rario.setX(brick.getX() + brick.getWymiar().width);
            }
        }

        for(Przeciwnik przeciwnik : enemies){
            Rectangle enemyBounds = !toRight ? przeciwnik.prawaGranica() : przeciwnik.lewaGranica();
            if (marioBounds.intersects(enemyBounds)) {
                marioDies = rario.dotknieciePrzeciwnika(engine);
                toBeRemoved.add(przeciwnik);
            }
        }
        removeObjects(toBeRemoved);


        if (rario.getX() <= engine.getCameraLocation().getX() && rario.getPredkoscWX() < 0) {
            rario.setPredkoscWX(0);
            rario.setX(engine.getCameraLocation().getX());
        }

        if(marioDies) {
            resetCurrentMap(engine);
        }
    }

    private void checkEnemyCollisions() {
        ArrayList<Blok> bricks = poziom.getWszystkieCegly();
        ArrayList<Przeciwnik> enemies = poziom.getPrzeciwnicy();

        for (Przeciwnik przeciwnik : enemies) {
            boolean standsOnBrick = false;

            for (Blok brick : bricks) {
                Rectangle enemyBounds = przeciwnik.lewaGranica();
                Rectangle brickBounds = brick.prawaGranica();

                Rectangle enemyBottomBounds = przeciwnik.dolnaGranica();
                Rectangle brickTopBounds = brick.gornaGranica();

                if (przeciwnik.getPredkoscWX() > 0) {
                    enemyBounds = przeciwnik.prawaGranica();
                    brickBounds = brick.lewaGranica();
                }

                if (enemyBounds.intersects(brickBounds)) {
                    przeciwnik.setPredkoscWX(-przeciwnik.getPredkoscWX());
                }

                if (enemyBottomBounds.intersects(brickTopBounds)){
                    przeciwnik.setCzySpada(false);
                    przeciwnik.setPredkoscWY(0);
                    przeciwnik.setY(brick.getY()- przeciwnik.getWymiar().height);
                    standsOnBrick = true;
                }
            }

            if(przeciwnik.getY() + przeciwnik.getWymiar().height > poziom.getDolnaGranica()){
                przeciwnik.setCzySpada(false);
                przeciwnik.setPredkoscWY(0);
                przeciwnik.setY(poziom.getDolnaGranica()- przeciwnik.getWymiar().height);
            }

            if (!standsOnBrick && przeciwnik.getY() < poziom.getDolnaGranica()){
                przeciwnik.setCzySpada(true);
            }
        }
    }

    private void checkPrizeCollision() {
        ArrayList<Nagroda> nagrody = poziom.getNagrody();
        ArrayList<Blok> bricks = poziom.getWszystkieCegly();

        for (Nagroda nagroda : nagrody) {
            if (nagroda instanceof PrzedmiotSpecjalny) {
                PrzedmiotSpecjalny boost = (PrzedmiotSpecjalny) nagroda;
                Rectangle prizeBottomBounds = boost.dolnaGranica();
                Rectangle prizeRightBounds = boost.prawaGranica();
                Rectangle prizeLeftBounds = boost.lewaGranica();
                boost.setCzySpada(true);

                for (Blok brick : bricks) {
                    Rectangle brickBounds;

                    if (boost.getCzySpada()) {
                        brickBounds = brick.gornaGranica();

                        if (brickBounds.intersects(prizeBottomBounds)) {
                            boost.setCzySpada(false);
                            boost.setPredkoscWY(0);
                            boost.setY(brick.getY() - boost.getWymiar().height + 1);
                            if (boost.getPredkoscWX() == 0)
                                boost.setPredkoscWX(2);
                        }
                    }

                    if (boost.getPredkoscWX() > 0) {
                        brickBounds = brick.lewaGranica();

                        if (brickBounds.intersects(prizeRightBounds)) {
                            boost.setPredkoscWX(-boost.getPredkoscWX());
                        }
                    } else if (boost.getPredkoscWX() < 0) {
                        brickBounds = brick.prawaGranica();

                        if (brickBounds.intersects(prizeLeftBounds)) {
                            boost.setPredkoscWX(-boost.getPredkoscWX());
                        }
                    }
                }

                if (boost.getY() + boost.getWymiar().height > poziom.getDolnaGranica()) {
                    boost.setCzySpada(false);
                    boost.setPredkoscWY(0);
                    boost.setY(poziom.getDolnaGranica() - boost.getWymiar().height);
                    if (boost.getPredkoscWX() == 0)
                        boost.setPredkoscWX(2);
                }

            }
        }
    }

    private void checkPrizeContact() {
        ArrayList<Nagroda> nagrody = poziom.getNagrody();
        ArrayList<ObiektGry> toBeRemoved = new ArrayList<>();

        Rectangle marioBounds = getMario().getGranice();
        for(Nagroda nagroda : nagrody){
            Rectangle prizeBounds = nagroda.getGranice();
            if (prizeBounds.intersects(marioBounds)) {
                nagroda.przyDotknieciu(getMario());
                toBeRemoved.add((ObiektGry) nagroda);
            } else if(nagroda instanceof Moneta){
                nagroda.przyDotknieciu(getMario());
            }
        }

        removeObjects(toBeRemoved);
    }

    private void removeObjects(ArrayList<ObiektGry> list){
        if(list == null)
            return;

        for(ObiektGry object : list){
            if(object instanceof Przeciwnik){
                poziom.usunPrzeciwnika((Przeciwnik)object);
            }
            else if(object instanceof Moneta || object instanceof PrzedmiotSpecjalny){
                poziom.usunNagrode((Nagroda)object);
            }
        }
    }

    public void addRevealedBrick(Cegla ordinaryBrick) {
        poziom.dodajOdkrytaCegle(ordinaryBrick);
    }

}
