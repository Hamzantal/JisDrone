package id.or.jisedu;


import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

class Drone extends ImageView {
  
  private BufferedImage image;
  private int type = 1;
  
  Drone(int i) {
    BufferedImage drone = null;
    try {
      drone = ImageIO.read(ClassLoader.getSystemResource("id/or/jisedu/res/drones/drone" + i + ".png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    assert drone != null;
    this.image = drone;
    setImage(SwingFXUtils.toFXImage(drone, null));
  }
  
  private void setImg(int i) {
    BufferedImage drone = null;
    try {
      drone = ImageIO.read(ClassLoader.getSystemResource("id/or/jisedu/res/drones/drone" + i + ".png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    assert drone != null;
    this.image = drone;
    setImage(SwingFXUtils.toFXImage(drone, null));
  }
  
  double getHeight() {
    return image.getHeight();
  }
  
  double getWidth() {
    return image.getWidth();
  }
  
  void setCenterX(Double x) {
    setX(x - getWidth() / 2);
  }
  
  void setCenterY(Double y) {
    setY(y - getHeight() / 2);
  }
  
  void update() {
    switch (type) {
      case 1:
        setImg(type);
        type++;
        break;
      case 2:
        setImg(type);
        type++;
        break;
      case 3:
        setImg(1);
        type++;
        break;
      case 4:
        setImg(0);
        type = 1;
        break;
      default:
        setImg(0);
    }
    
  }
}
