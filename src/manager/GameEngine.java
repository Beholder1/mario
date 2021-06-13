package manager;

import model.postac.Rario;
import view.ImageLoader;
import view.StartScreenSelection;
import view.UIManager;

import javax.swing.*;
import java.awt.*;

public class GameEngine implements Runnable {

    private final static int WIDTH = 1268, HEIGHT = 708;

    private MapManager mapManager;
    private UIManager uiManager;
    private StatusGry statusGry;
    private boolean isRunning;
    private Kamera kamera;
    private ImageLoader imageLoader;
    private Thread thread;
    private StartScreenSelection startScreenSelection = StartScreenSelection.START_GAME;
    private int selectedMap = 0;

    private GameEngine() {
        init();
    }

    private void init() {
        imageLoader = new ImageLoader();
        Wejscie wejscie = new Wejscie(this);
        statusGry = StatusGry.MENU;
        kamera = new Kamera();
        uiManager = new UIManager(this, WIDTH, HEIGHT);
        mapManager = new MapManager();

        JFrame frame = new JFrame("Rario");
        frame.add(uiManager);
        frame.addKeyListener(wejscie);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        start();
    }

    private synchronized void start() {
        if (isRunning)
            return;

        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }

    private void reset(){
        resetCamera();
        setGameStatus(StatusGry.MENU);
    }

    public void resetCamera() {
        kamera = new Kamera();
    }

    public void selectMapViaMouse() {
        String path = uiManager.selectMapViaMouse(uiManager.getMousePosition());
        if (path != null) {
            createMap(path);
        }
    }

    public void selectMapViaKeyboard(){
        String path = uiManager.selectMapViaKeyboard(selectedMap);
        if (path != null) {
            createMap(path);
        }
    }

    public void changeSelectedMap(boolean up){
        selectedMap = uiManager.changeSelectedMap(selectedMap, up);
    }

    private void createMap(String path) {
        boolean loaded = mapManager.createMap(imageLoader, path);
        if(loaded){
            setGameStatus(StatusGry.W_TRAKCIE);
        }

        else
            setGameStatus(StatusGry.MENU);
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();

        while (isRunning && !thread.isInterrupted()) {

            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                if (statusGry == StatusGry.W_TRAKCIE) {
                    gameLoop();
                }
                delta--;
            }
            render();

            if(statusGry != StatusGry.W_TRAKCIE) {
                timer = System.currentTimeMillis();
            }

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
            }
        }
    }

    private void render() {
        uiManager.repaint();
    }

    private void gameLoop() {
        updateLocations();
        checkCollisions();
        updateCamera();

        if (isGameOver()) {
            setGameStatus(StatusGry.PRZEGRANA);
        }

        int missionPassed = passMission();
        if(missionPassed > -1){
            mapManager.acquirePoints(missionPassed);
        } else if(mapManager.endLevel())
            setGameStatus(StatusGry.WYGRANA);
    }

    private void updateCamera() {
        Rario rario = mapManager.getMario();
        double marioVelocityX = rario.getVelX();
        double shiftAmount = 0;

        if (marioVelocityX > 0 && rario.getX() - 600 > kamera.getX()) {
            shiftAmount = marioVelocityX;
        }

        kamera.moveCam(shiftAmount, 0);
    }

    private void updateLocations() {
        mapManager.updateLocations();
    }

    private void checkCollisions() {
        mapManager.checkCollisions(this);
    }

    public void receiveInput(AkcjeKlawiszy input) {

        if (statusGry == StatusGry.MENU) {
            if (input == AkcjeKlawiszy.WYBIERZ && startScreenSelection == StartScreenSelection.START_GAME) {
                startGame();
            } else if (input == AkcjeKlawiszy.WYBIERZ && startScreenSelection == StartScreenSelection.VIEW_ABOUT) {
                setGameStatus(StatusGry.TWORCY);
            } else if (input == AkcjeKlawiszy.WYBIERZ && startScreenSelection == StartScreenSelection.VIEW_HELP) {
                setGameStatus(StatusGry.STEROWANIE);
            } else if (input == AkcjeKlawiszy.W_GORE) {
                selectOption(true);
            } else if (input == AkcjeKlawiszy.W_DOL) {
                selectOption(false);
            }
        }
        else if(statusGry == StatusGry.POZIOMY){
            if(input == AkcjeKlawiszy.WYBIERZ){
                selectMapViaKeyboard();
            }
            else if(input == AkcjeKlawiszy.W_GORE){
                changeSelectedMap(true);
            }
            else if(input == AkcjeKlawiszy.W_DOL){
                changeSelectedMap(false);
            }
        } else if (statusGry == StatusGry.W_TRAKCIE) {
            Rario rario = mapManager.getMario();
            if (input == AkcjeKlawiszy.SKOK) {
                rario.skok();
            } else if (input == AkcjeKlawiszy.W_PRAWO) {
                rario.rusz(true, kamera);
            } else if (input == AkcjeKlawiszy.W_LEWO) {
                rario.rusz(false, kamera);
            } else if (input == AkcjeKlawiszy.PO_NACISNIECIU) {
                rario.setVelX(0);
            } else if (input == AkcjeKlawiszy.PAUZA) {
                pauseGame();
            }
        } else if (statusGry == StatusGry.ZATRZYMANO) {
            if (input == AkcjeKlawiszy.PAUZA) {
                pauseGame();
            }
        } else if(statusGry == StatusGry.PRZEGRANA && input == AkcjeKlawiszy.POWROT){
            reset();
        } else if(statusGry == StatusGry.WYGRANA && input == AkcjeKlawiszy.POWROT){
            reset();
        }

        if(input == AkcjeKlawiszy.POWROT){
            setGameStatus(StatusGry.MENU);
        }
    }

    private void selectOption(boolean selectUp) {
        startScreenSelection = startScreenSelection.select(selectUp);
    }

    private void startGame() {
        if (statusGry != StatusGry.PRZEGRANA) {
            setGameStatus(StatusGry.POZIOMY);
        }
    }

    private void pauseGame() {
        if (statusGry == StatusGry.W_TRAKCIE) {
            setGameStatus(StatusGry.ZATRZYMANO);
        } else if (statusGry == StatusGry.ZATRZYMANO) {
            setGameStatus(StatusGry.W_TRAKCIE);
        }
    }

    private boolean isGameOver() {
        if(statusGry == StatusGry.W_TRAKCIE)
            return mapManager.isGameOver();
        return false;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public StatusGry getGameStatus() {
        return statusGry;
    }

    public StartScreenSelection getStartScreenSelection() {
        return startScreenSelection;
    }

    public void setGameStatus(StatusGry statusGry) {
        this.statusGry = statusGry;
    }

    public int getScore() {
        return mapManager.getScore();
    }

    public int getRemainingLives() {
        return mapManager.getRemainingLives();
    }

    public int getSelectedMap() {
        return selectedMap;
    }

    public void drawMap(Graphics2D g2) {
        mapManager.drawMap(g2);
    }

    public Point getCameraLocation() {
        return new Point((int) kamera.getX(), (int) kamera.getY());
    }

    private int passMission(){
        return mapManager.passMission();
    }

    public MapManager getMapManager() {
        return mapManager;
    }

    public static void main(String... args) {
        new GameEngine();
    }
}
