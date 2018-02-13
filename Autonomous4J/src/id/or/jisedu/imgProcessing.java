package id.or.jisedu;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class imgProcessing {
  
  public static void main(String[] args) throws Exception {
    BufferedImage img = null;
    File input = new File("/Users/32350/Desktop/image.png");
    File output = new File("/Users/32350/Desktop/imageOutput.png");
    try {
      img = ImageIO.read(input);
    } catch (IOException ignored) {
    }
    img = printAllRed(img);
    try {
      ImageIO.write(img, "png", output);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private static BufferedImage printAllRed(BufferedImage image) {
    Color white = new Color(255, 255, 255);
    Color bleck = new Color(0, 0, 0);
    float totx = 0;
    float toty = 0;
    int counter = 0;
    float avgx;
    float avgy;
    
    BufferedImage bufferedImage = new BufferedImage(58, 33, BufferedImage.TYPE_INT_RGB);
    
    for (int x = 0; x < image.getWidth(); x++) {
      for (int y = 0; y < image.getHeight(); y++) {
        int clr = image.getRGB(x, y);
        final float r = (clr & 0x00ff0000) >> 16;
        if (r > 128) {
          bufferedImage.setRGB(x, y, white.getRGB());
          totx += x;
          toty += y;
          counter++;
        } else if (r >= 120) {
          bufferedImage.setRGB(x, y, bleck.getRGB());
        }
      }
    }
    
    if (counter >= 1) {
      avgx = totx / counter;
      avgy = toty / counter;
    } else {
      avgx = image.getWidth() / 2;
      avgy = image.getHeight() / 2;
    }
    bufferedImage.setRGB((int) avgx, (int) avgy, (new Color(255, 0, 0)).getRGB());
    
    
    return bufferedImage;
  }
  
  
}
