package manager;

import obiekty.postac.Rario;
import view.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class GameEngine implements Runnable {

    private final static int WIDTH = 1268, HEIGHT = 708;

    private MapManager mapManager;
    private InterfejsUzytkownika interfejsUzytkownika;
    private StatusGry statusGry;
    private boolean isRunning;
    private Kamera kamera;
    private ImageLoader imageLoader;
    private Thread thread;
    private WyborWMenu wyborWMenu = WyborWMenu.GRAJ;
    private int selectedMap = 0;

    ArrayList<Rekord> rekordy;

    private GameEngine() throws IOException, ClassNotFoundException {
        init();
    }
    public ArrayList<Rekord> read(String fileName) throws IOException, ClassNotFoundException {
        ArrayList<Rekord> tmpArrayList = new ArrayList<>();
        File file = new File(fileName);

        if(file.exists() && file.length() != 0) {
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            tmpArrayList = (ArrayList<Rekord>) ois.readObject();
            ois.close();
        }

        return tmpArrayList;
    }
    private void init() throws IOException, ClassNotFoundException {
        imageLoader = new ImageLoader();
        Wejscie wejscie = new Wejscie(this);
        statusGry = StatusGry.MENU;
        kamera = new Kamera();
        interfejsUzytkownika = new InterfejsUzytkownika(this, WIDTH, HEIGHT);
        mapManager = new MapManager();
        ArrayList<Rekord> tmpArrayList = new ArrayList<>();
        File file = new File(".\\rekordy.dat");
        if(!file.exists())
        {
            try {
                ObjectOutputStream wy = new ObjectOutputStream(new FileOutputStream(".\\rekordy.dat"));
                tmpArrayList.add(new Rekord("Poziom 1", 0));
                tmpArrayList.add(new Rekord("Poziom 2", 0));
                tmpArrayList.add(new Rekord("Poziom 3", 0));
                tmpArrayList.add(new Rekord("Poziom 4", 0));
                tmpArrayList.add(new Rekord("Poziom 5", 0));
                wy.writeObject(tmpArrayList);
            }
            catch (Exception e) {

            }
        }
        rekordy = read(".\\rekordy.dat");
        JFrame frame = new JFrame("Rario");
        frame.add(interfejsUzytkownika);
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

    public void selectMapViaKeyboard(){
        String path = interfejsUzytkownika.wybierzPoziom(selectedMap);
        if (path != null) {
            createMap(path);
        }
    }

    public void changeSelectedMap(boolean up){
        selectedMap = interfejsUzytkownika.zmienPoziom(selectedMap, up);
    }

    private void createMap(String path) {
        boolean loaded = mapManager.stworzPoziom(imageLoader, path);
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

        while (isRunning && !thread.isInterrupted()) {

            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                if (statusGry == StatusGry.W_TRAKCIE) {
                    try {
                        gameLoop();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                delta--;
            }
            render();
        }
    }

    private void render() {
        interfejsUzytkownika.repaint();
    }

    private void aktualizujRanking() throws IOException {
        if (rekordy.get(selectedMap).getRekord() < getScore()){
            ObjectOutputStream wy = new ObjectOutputStream(new FileOutputStream(".\\rekordy.dat"));
            rekordy.set(selectedMap, new Rekord("Poziom " + (selectedMap+1), getScore()));
            wy.writeObject(rekordy);
        }
    }

    private void gameLoop() throws IOException {
        mapManager.zmienPolozenia();
        checkCollisions();
        updateCamera();

        if (isGameOver()) {

            aktualizujRanking();

            setGameStatus(StatusGry.PRZEGRANA);
        }

        int missionPassed = getWygrana();
        if(missionPassed > -1){
            mapManager.zyskajPunkty(missionPassed);
        } else if(mapManager.koniecPoziomu()) {
            aktualizujRanking();
            setGameStatus(StatusGry.WYGRANA);
        }
    }

    private void updateCamera() {
        Rario rario = mapManager.getRario();
        double marioVelocityX = rario.getPredkoscWX();
        double shiftAmount = 0;

        if (marioVelocityX > 0 && rario.getX() - 600 > kamera.getX()) {
            shiftAmount = marioVelocityX;
        }

        kamera.moveCam(shiftAmount, 0);
    }

    private void checkCollisions() {
        mapManager.sprawdzKolizje(this);
    }

    public void receiveInput(AkcjeKlawiszy input) {

        if (statusGry == StatusGry.MENU) {
            if (input == AkcjeKlawiszy.WYBIERZ && wyborWMenu == WyborWMenu.GRAJ) {
                startGame();
            } else if (input == AkcjeKlawiszy.WYBIERZ && wyborWMenu == WyborWMenu.TWORCY) {
                setGameStatus(StatusGry.TWORCY);
            } else if (input == AkcjeKlawiszy.WYBIERZ && wyborWMenu == WyborWMenu.STEROWANIE) {
                setGameStatus(StatusGry.STEROWANIE);
            } else if (input == AkcjeKlawiszy.WYBIERZ && wyborWMenu == WyborWMenu.RANKING) {
                setGameStatus(StatusGry.RANKING);
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
            Rario rario = mapManager.getRario();
            if (input == AkcjeKlawiszy.SKOK) {
                rario.skok();
            } else if (input == AkcjeKlawiszy.W_PRAWO) {
                rario.rusz(true, kamera);
            } else if (input == AkcjeKlawiszy.W_LEWO) {
                rario.rusz(false, kamera);
            } else if (input == AkcjeKlawiszy.PO_NACISNIECIU) {
                rario.setPredkoscWX(0);
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
        wyborWMenu = wyborWMenu.wybierz(selectUp);
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
            return mapManager.czyPrzegrano();
        return false;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public StatusGry getGameStatus() {
        return statusGry;
    }

    public WyborWMenu getStartScreenSelection() {
        return wyborWMenu;
    }

    public void setGameStatus(StatusGry statusGry) {
        this.statusGry = statusGry;
    }

    public int getScore() {
        return mapManager.getPunkty();
    }

    public int getRemainingLives() {
        return mapManager.getPozostaleSerca();
    }

    public int getSelectedMap() {
        return selectedMap;
    }

    public void drawMap(Graphics2D g2) {
        mapManager.wyswietlPoziom(g2);
    }

    public Point getCameraLocation() {
        return new Point((int) kamera.getX(), (int) kamera.getY());
    }

    private int getWygrana(){
        return mapManager.wygrana();
    }

    public MapManager getMapManager() {
        return mapManager;
    }

    public static void main(String... args) throws IOException, ClassNotFoundException {
        new GameEngine();
    }
}
