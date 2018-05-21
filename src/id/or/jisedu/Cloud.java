package id.or.jisedu;


import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

class Cloud extends ImageView {
  
  private BufferedImage image;
  
  Cloud() {
    BufferedImage cloud = null;
    try {
      cloud = ImageIO.read(ClassLoader.getSystemResource("id/or/jisedu/res/cloud.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    assert cloud != null;
    this.image = cloud;
    setImage(SwingFXUtils.toFXImage(cloud, null));
  }
  
  private double getHeight() {
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
  
  double getCenterX() {
    return getX() - image.getWidth() / 2;
  }
  
  double getCenterY() {
    return getY() - image.getHeight() / 2;
  }
}
