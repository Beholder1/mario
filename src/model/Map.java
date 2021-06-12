package model;

import model.blok.Blok;
import model.blok.Cegla;
import model.postac.Przeciwnik;
import model.postac.Rario;
import model.nagroda.PrzedmiotSpecjalny;
import model.nagroda.Moneta;
import model.nagroda.Nagroda;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

public class Map {

    private double remainingTime;
    private Rario rario;
    private ArrayList<Blok> bricks = new ArrayList<>();
    private ArrayList<Przeciwnik> enemies = new ArrayList<>();
    private ArrayList<Blok> groundBricks = new ArrayList<>();
    private ArrayList<Nagroda> revealedPrizes = new ArrayList<>();
    private ArrayList<Blok> revealedBricks = new ArrayList<>();
    private EndFlag endPoint;
    private BufferedImage backgroundImage;
    private double bottomBorder = 720 - 70;
    private String path;


    public Map(double remainingTime, BufferedImage backgroundImage) {
        this.backgroundImage = backgroundImage;
        this.remainingTime = remainingTime;
    }


    public Rario getMario() {
        return rario;
    }

    public void setMario(Rario rario) {
        this.rario = rario;
    }

    public ArrayList<Przeciwnik> getEnemies() {
        return enemies;
    }

    public ArrayList<Nagroda> getRevealedPrizes() {
        return revealedPrizes;
    }

    public ArrayList<Blok> getAllBricks() {
        ArrayList<Blok> allBricks = new ArrayList<>();

        allBricks.addAll(bricks);
        allBricks.addAll(groundBricks);

        return allBricks;
    }

    public void addBrick(Blok brick) {
        this.bricks.add(brick);
    }

    public void addGroundBrick(Blok brick) {
        this.groundBricks.add(brick);
    }

    public void addEnemy(Przeciwnik przeciwnik) {
        this.enemies.add(przeciwnik);
    }

    public void drawMap(Graphics2D g2){
        drawBackground(g2);
        drawPrizes(g2);
        drawBricks(g2);
        drawEnemies(g2);
        drawMario(g2);
        endPoint.wyswietl(g2);
    }


    private void drawPrizes(Graphics2D g2) {
        for(Nagroda nagroda : revealedPrizes){
            if(nagroda instanceof Moneta){
                ((Moneta) nagroda).wyswietl(g2);
            }
            else if(nagroda instanceof PrzedmiotSpecjalny){
                ((PrzedmiotSpecjalny) nagroda).wyswietl(g2);
            }
        }
    }

    private void drawBackground(Graphics2D g2){
        g2.drawImage(backgroundImage, 0, 0, null);
    }

    private void drawBricks(Graphics2D g2) {
        for(Blok brick : bricks){
            if(brick != null)
                brick.wyswietl(g2);
        }

        for(Blok brick : groundBricks){
            brick.wyswietl(g2);
        }
    }

    private void drawEnemies(Graphics2D g2) {
        for(Przeciwnik przeciwnik : enemies){
            if(przeciwnik != null)
                przeciwnik.wyswietl(g2);
        }
    }

    private void drawMario(Graphics2D g2) {
        rario.wyswietl(g2);
    }

    public void updateLocations() {
        rario.zmienPolozenie();
        for(Przeciwnik przeciwnik : enemies){
            przeciwnik.zmienPolozenie();
        }

        for(Iterator<Nagroda> prizeIterator = revealedPrizes.iterator(); prizeIterator.hasNext();){
            Nagroda nagroda = prizeIterator.next();
            if(nagroda instanceof Moneta){
                ((Moneta) nagroda).zmienPolozenie();
                if(((Moneta) nagroda).getGranicaOdkrycia() > ((Moneta) nagroda).getY()){
                    prizeIterator.remove();
                }
            }
            else if(nagroda instanceof PrzedmiotSpecjalny){
                ((PrzedmiotSpecjalny) nagroda).zmienPolozenie();
            }
        }

        for(Iterator<Blok> brickIterator = revealedBricks.iterator(); brickIterator.hasNext();){
            Cegla brick = (Cegla)brickIterator.next();
            bricks.remove(brick);
            brickIterator.remove();
        }

        endPoint.zmienPolozenie();
    }

    public double getBottomBorder() {
        return bottomBorder;
    }

    public void addRevealedPrize(Nagroda nagroda) {
        revealedPrizes.add(nagroda);
    }


    public void setEndPoint(EndFlag endPoint) {
        this.endPoint = endPoint;
    }

    public EndFlag getEndPoint() {
        return endPoint;
    }

    public void addRevealedBrick(Cegla ordinaryBrick) {
        revealedBricks.add(ordinaryBrick);
    }

    public void removeEnemy(Przeciwnik object) {
        enemies.remove(object);
    }

    public void removePrize(Nagroda object) {
        revealedPrizes.remove(object);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void updateTime(double passed){
        remainingTime = remainingTime - passed;
    }
}
