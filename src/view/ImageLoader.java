package view;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageLoader {

    private BufferedImage marioForms;

    public ImageLoader(){
        marioForms = loadImage("/rario.png");
    }

    public BufferedImage loadImage(String path){
        BufferedImage imageToReturn = null;

        try {
            imageToReturn = ImageIO.read(getClass().getResource("/media" + path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageToReturn;
    }

    public BufferedImage getSubImage(BufferedImage image, int col, int row, int w, int h){
        return image.getSubimage(col, row, w, h);
    }

    public BufferedImage[] getLeftFrames(int marioForm){
        BufferedImage[] leftFrames = new BufferedImage[5];
        int col = 0;
        int width = 50, height = 50;

        if(marioForm == 1) {
            col = 100;
            width = 72;
            height = 72;
        }
        for(int i = 0; i < 5; i++){
            leftFrames[i] = marioForms.getSubimage(col, (i)*height, width, height);
        }
        return leftFrames;
    }

    public BufferedImage[] getRightFrames(int marioForm){
        BufferedImage[] rightFrames = new BufferedImage[5];
        int col = 50;
        int width = 50, height = 50;

        if(marioForm == 1) {
            col = 172;
            width = 72;
            height = 72;
        }
        for(int i = 0; i < 5; i++){
            rightFrames[i] = marioForms.getSubimage(col, (i)*height, width, height);
        }
        return rightFrames;
    }
}
