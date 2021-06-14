package view;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ZaladowanieObrazu {

    private BufferedImage formyRario;

    public ZaladowanieObrazu(){
        formyRario = zaladujObraz("/rario.png");
    }

    public BufferedImage zaladujObraz(String path){
        BufferedImage obrazDoZwrocenia = null;

        try {
            obrazDoZwrocenia = ImageIO.read(getClass().getResource("/grafika" + path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return obrazDoZwrocenia;
    }

    public BufferedImage getMniejszyObraz(BufferedImage image, int col, int row, int w, int h){
        return image.getSubimage(col, row, w, h);
    }

    public BufferedImage[] getLeweKlatki(int formaRario){
        BufferedImage[] leweKlatki = new BufferedImage[5];
        int kolumna = 0;
        int szerokosc = 50, wysokosc = 50;

        if(formaRario == 1) {
            kolumna = 100;
            szerokosc = 72;
            wysokosc = 72;
        }
        for(int i = 0; i < 5; i++){
            leweKlatki[i] = formyRario.getSubimage(kolumna, (i)*wysokosc, szerokosc, wysokosc);
        }
        return leweKlatki;
    }

    public BufferedImage[] getPraweKlatki(int marioForm){
        BufferedImage[] praweKlatki = new BufferedImage[5];
        int kolumna = 50;
        int szerokosc = 50, wysokosc = 50;

        if(marioForm == 1) {
            kolumna = 172;
            szerokosc = 72;
            wysokosc = 72;
        }
        for(int i = 0; i < 5; i++){
            praweKlatki[i] = formyRario.getSubimage(kolumna, (i)*wysokosc, szerokosc, wysokosc);
        }
        return praweKlatki;
    }
}
