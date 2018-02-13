package id.or.jisedu;

import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CommandManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Main extends JFrame {
  
  private BufferedImage image = null;
  JLabel vertvel, rotvel;
  
  public static void main(String[] args) {
    new Main();
  }
  
  private Main() {
    super("YADrone");
    rotvel = new JLabel();
    vertvel = new JLabel();
    vertvel.setText("vertvel");
    rotvel.setText("rotvel");
    add(rotvel);
    add(vertvel);
    rotvel.setAlignmentX(200);
    rotvel.setAlignmentY(200);
    vertvel.setAlignmentX(200);
    vertvel.setAlignmentY(400);
    setSize(640, 360);
    setVisible(true);
    try {
      droneInfo();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public void paint(Graphics g) {
    try {
      if (image != null) {
        image = resize(image);
        g.drawImage(image, 0, 30, image.getWidth(), image.getHeight(), null);
        image = printAllRed(image);
        g.drawImage(image, 60, 30, image.getWidth(), image.getHeight(), null);
      }
    } catch (Exception ignore) {
    }
  }
  
  private IARDrone drone;
  
  private void droneInfo() throws Exception {
    drone = new ARDrone();
    drone.start();
    CommandManager cmd = drone.getCommandManager();
    drone.setHorizontalCamera();
    drone.getVideoManager().addImageListener(newImage -> {
      image = newImage;
      SwingUtilities.invokeLater(this::repaint);
    });
  }
  
  private BufferedImage resize(BufferedImage img) {
    int w = img.getWidth();
    int h = img.getHeight();
    BufferedImage dimg = new BufferedImage(58, 33, img.getType());
    Graphics2D g = dimg.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g.drawImage(img, 0, 0, 58, 33, 0, 0, w, h, null);
    g.dispose();
    return dimg;
  }
  
  private Color white = new Color(255, 255, 255);
  private Color bleck = new Color(0, 0, 0);
  
  private BufferedImage printAllRed(BufferedImage image) throws Exception {
    float totx = 0;
    float toty = 0;
    int counter = 0;
    BufferedImage bufferedImage = new BufferedImage(58, 33, BufferedImage.TYPE_INT_RGB);
    for (int x = 0; x < image.getWidth(); x++) {
      for (int y = 0; y < image.getHeight(); y++) {
        final int clr = image.getRGB(x, y);
        final float r = (clr & 0x00ff0000) >> 16;
        final float g = (clr & 0x0000ff00) >> 8;
        final float b = clr & 0x000000ff;
        if (r >= (g + b) / 2 && r > 116 && g < 72 && b < 72) {
          bufferedImage.setRGB(x, y, white.getRGB());
          totx += x;
          toty += y;
          counter++;
        } else if (r >= 120) {
          bufferedImage.setRGB(x, y, bleck.getRGB());
        }
      }
    }
    float avgx;
    float avgy;
    int maxrot = 70;
    int maxvert = 70;
    int maxhoriz = 20;
    float rotvel;
    float vertvel;
    float horizvel;
    if (counter >= 1) {
      avgx = totx / counter;
      avgy = toty / counter;
    } else {
      avgx = image.getWidth() / 2;
      avgy = image.getHeight() / 2;
    }
    bufferedImage.setRGB((int) avgx, (int) avgy, (new Color(255, 0, 0)).getRGB());
    avgx = avgx - (image.getWidth() / 2);
    avgy = 0 - (avgy - (image.getHeight() / 2));
    rotvel = (avgx / (image.getWidth() / 2)) * maxrot;
    vertvel = (avgy / (image.getHeight() / 2)) * maxvert;
    this.rotvel.setText("rotvel: " + rotvel);
    this.rotvel.setText("vertvel: " + vertvel);
    
    return bufferedImage;
  }
  
  private void moveDrone(int rotvel, int vertvel) {
    if (rotvel != 0) {
      if (rotvel > 0) {
        drone.getCommandManager().spinRight(rotvel);
      } else {
        drone.getCommandManager().spinLeft(Math.abs(rotvel));
      }
    }
    if (vertvel == 0) {
      return;
    }
    if (vertvel > 0) {
      drone.getCommandManager().up(vertvel);
    } else {
      drone.getCommandManager().down(Math.abs(vertvel));
    }
  }
  
  
}
