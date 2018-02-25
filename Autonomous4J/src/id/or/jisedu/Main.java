package id.or.jisedu;

import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import id.or.jisedu.ui.RingProgressIndicator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Main extends Application {
  private boolean movement = true;
  private final IARDrone DRONE = new ARDrone();
  private RingProgressIndicator rpi;
  private Label status = new Label("Landed");
  private Label direction = new Label("N/A");
  private Button statusButton = new Button("Operation");
  private Label rotVelLabel = new Label("");
  private boolean takeoff = false;
  private ImageView colorView = new ImageView();
  private ImageView trackView = new ImageView();
  private long timeSince = System.currentTimeMillis();
  
  public static void main(String[] args) {
    launch(args);
  }
  
  public void start(Stage STAGE) throws Exception {
    DRONE.start();
    VBox vbox = new VBox(10);
    HBox hbox = new HBox(colorView, trackView);
    STAGE.setTitle("Drone Command Manager");
    vbox.setAlignment(Pos.CENTER);
    hbox.setAlignment(Pos.CENTER);
    Scene s = new Scene(vbox, 1280, 720);
    STAGE.setScene(s);
    STAGE.show();
    rpi = new RingProgressIndicator();
    rpi.setRingWidth(300);
    rpi.makeIndeterminate();
    rpi.setProgress(0);
    rotVelLabel.setFont(new Font("Arial", 30));
    vbox.getChildren().addAll(rpi, direction, status, statusButton, rotVelLabel, hbox);
    STAGE.setOnCloseRequest(we -> System.exit(0));
    statusButton.setOnAction(e -> {
      if (!takeoff) {
        DRONE.getCommandManager().takeOff();
        DRONE.setHorizontalCamera();
        DRONE.getVideoManager().addImageListener((ev) -> Platform.runLater(() -> processImage(ev) ));
        status.setText("Taken Off");
        statusButton.setText("Land and Stop");
        status.setText("Tracking");
        takeoff = true;
      } else {
        DRONE.landing();
        DRONE.stop();
        System.exit(0);
      }
    });
  }
  
  private void processImage(BufferedImage image) {
    colorView.setImage(SwingFXUtils.toFXImage(image, null));
    try {
      if (!(System.currentTimeMillis() - 1000 > timeSince)) return;
      if (movement) {
        timeSince = System.currentTimeMillis();
        findRed(resize(image, 58, 33));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  private BufferedImage resize(BufferedImage img, int width, int length) {
    int w = img.getWidth();
    int h = img.getHeight();
    BufferedImage dimg = new BufferedImage(width, length, img.getType());
    Graphics2D g = dimg.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g.drawImage(img, 0, 0, width, length, 0, 0, w, h, null);
    g.dispose();
    return dimg;
  }
  
  private void findRed(BufferedImage rawImage) throws Exception {
    float totalX = 0;
    int whitePxls = 0;
    float bestX;
    for (int x = 0; x < rawImage.getWidth(); x++)
      for (int y = 0; y < rawImage.getHeight(); y++) {
        final int clr = rawImage.getRGB(x, y);
        final float redPxls = (clr & 0x00ff0000) >> 16;
        final float greenPxls = (clr & 0x0000ff00) >> 8;
        final float bluePxls = clr & 0x000000ff;
        if (redPxls >= (greenPxls + bluePxls) / 2 && redPxls > 116 && greenPxls < 72 && bluePxls < 72) {
          totalX += x;
          whitePxls++;
        }
      }
    
    if (whitePxls >= 1) bestX = totalX / whitePxls;
    else bestX = rawImage.getWidth() / 2;
    
    BufferedImage image = new BufferedImage(58, 33, BufferedImage.TYPE_INT_RGB);
    image.setRGB((int) bestX, rawImage.getHeight() / 2, new Color(255, 0, 0).getRGB());
    trackView.setImage(SwingFXUtils.toFXImage(resize(image, 464, 264), null));
    usePxls(bestX);
  }
  
  
  private void usePxls(float bestX) {
    try {
      moveDrone(((bestX / 58) * 200) - 100);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  private void moveDrone(float rotvel) throws Exception {
    movement = false;
    final int MAXROT = 25;
    
    rotVelLabel.setText(rotvel + "");
    
    if (rotvel == 0) {
      DRONE.hover();
      rpi.setProgress(0);
      direction.setText("Hovering");
      movement = true;
      return;
    }
    
    rpi.setProgress((int) Math.abs(rotvel));
    
    if (rotvel > 0) {
      DRONE.getCommandManager().spinRight(((int) rotvel) / (100 / MAXROT)).doFor(10);
      direction.setText("spinRight");
    } else {
      direction.setText("spinLeft");
      DRONE.getCommandManager().spinLeft(Math.abs(((int) rotvel) / (100 / MAXROT))).doFor(10);
    }
    
    DRONE.hover();
    movement = true;
  }
  
  
}
