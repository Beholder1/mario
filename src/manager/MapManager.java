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

    public void zmienPolozenia() {
        if (poziom == null)
            return;
        poziom.zmienPolozenia();
    }

    public void zresetujPoziom(GameEngine silnik) {
        Rario rario = getRario();
        rario.zresetujPolozenie();
        silnik.resetCamera();
        stworzPoziom(silnik.getImageLoader(), poziom.getSciezka());
        poziom.setRario(rario);
    }

    public boolean stworzPoziom(ImageLoader obraz, String sciezka) {
        GeneratorMapy generatorMapy = new GeneratorMapy(obraz);
        poziom = generatorMapy.utworzPoziom("/poziomy/" + sciezka);

        return poziom != null;
    }

    public void zyskajPunkty(int point) {
        poziom.getRario().zyskajPunkty(point);
    }

    public Rario getRario() {
        return poziom.getRario();
    }

    public boolean czyPrzegrano() {
        return getRario().getPozostaleSerca() == 0;
    }

    public int getPunkty() {
        return getRario().getPunkty();
    }

    public int getPozostaleSerca() {
        return getRario().getPozostaleSerca();
    }

    public void wyswietlPoziom(Graphics2D grafika) {
        poziom.wyswietlPoziom(grafika);
    }

    public int wygrana() {
        if(getRario().getX() >= poziom.getKoniec().getX() && !poziom.getKoniec().getCzyDotknieta()){
            poziom.getKoniec().setCzyDotknieta(true);
            int wysokosc = (int)getRario().getY();
            return wysokosc * 2;
        }
        else
            return -1;
    }

    public boolean koniecPoziomu(){
        return getRario().getX() >= poziom.getKoniec().getX() + 320;
    }

    public void checkCollisions(GameEngine silnik) {
        if (poziom == null) {
            return;
        }

        checkBottomCollisions();
        checkTopCollisions(silnik);
        checkMarioHorizontalCollision(silnik);
        checkEnemyCollisions();
        checkPrizeCollision();
        checkPrizeContact();
    }

    private void checkBottomCollisions() {
        Rario rario = getRario();
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
        Rario rario = getRario();
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
        Rario rario = getRario();
        ArrayList<Blok> bricks = poziom.getWszystkieCegly();
        ArrayList<Przeciwnik> enemies = poziom.getPrzeciwnicy();
        ArrayList<ObiektGry> toBeRemoved = new ArrayList<>();

        boolean marioDies = false;
        boolean toRight = rario.getWPrawo();

        Rectangle marioBounds1 = rario.prawaGranica();
        Rectangle marioBounds2 = rario.lewaGranica();

        for (Blok brick : bricks) {
            Rectangle brickBounds = !toRight ? brick.prawaGranica() : brick.lewaGranica();
            if (marioBounds1.intersects(brickBounds)||marioBounds2.intersects(brickBounds)) {
                rario.setPredkoscWX(0);
                if(toRight)
                    rario.setX(brick.getX() - rario.getWymiar().width);
                else
                    rario.setX(brick.getX() + brick.getWymiar().width);
            }
        }

        for(Przeciwnik przeciwnik : enemies){
            Rectangle enemyBounds = !toRight ? przeciwnik.prawaGranica() : przeciwnik.lewaGranica();
            if (marioBounds1.intersects(enemyBounds)||marioBounds2.intersects(enemyBounds)) {
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
            zresetujPoziom(engine);
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

        Rectangle marioBounds = getRario().getGranice();
        for(Nagroda nagroda : nagrody){
            Rectangle prizeBounds = nagroda.getGranice();
            if (prizeBounds.intersects(marioBounds)) {
                nagroda.przyDotknieciu(getRario());
                toBeRemoved.add((ObiektGry) nagroda);
            } else if(nagroda instanceof Moneta){
                nagroda.przyDotknieciu(getRario());
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
