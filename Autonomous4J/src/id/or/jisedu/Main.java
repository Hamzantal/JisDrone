package id.or.jisedu;

import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import id.or.jisedu.ui.RingProgressIndicator;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;


public class Main extends Application {
  private boolean movement = true;
  private final int MAXROT = 25;
  private int noneFound = 0;
  private final IARDrone DRONE = new ARDrone();
  private RingProgressIndicator rpi = new RingProgressIndicator();
  private Label status = new Label("Landed");
  private Label direction = new Label("N/A");
  private Button statusButton = new Button("Operation");
  private boolean takeoff = false;
  
  public static void main(String[] args) {
    launch(args);
  }
  
  public void start(Stage STAGE) throws Exception {
    STAGE.setTitle("Drone Command Manager");
    
    VBox vbox = new VBox(10);
    vbox.setAlignment(Pos.CENTER);
    
    Scene s = new Scene(vbox, 800, 600);
    
    STAGE.setScene(s);
    STAGE.show();
    rpi.setRingWidth(300);
    rpi.makeIndeterminate();
    rpi.setProgress(0);
    vbox.getChildren().addAll(rpi, direction, status, statusButton);
    droneInfo();
    STAGE.setOnCloseRequest(new EventHandler<WindowEvent>() {
      public void handle(WindowEvent we) {
        System.exit(0);
      }
    });
    statusButton.setOnAction(e -> {
      if (!takeoff) {
        DRONE.getCommandManager().takeOff();
        DRONE.setHorizontalCamera();
        DRONE.getVideoManager().addImageListener(this::processImage);
        status.setText("Taken Off");
        statusButton.setText("Land and Stop");
        takeoff = true;
      } else {
        DRONE.landing();
        statusButton.setText("Landed");
        System.exit(0);
      }
    });
    
  }
  
  private void droneInfo() throws Exception {
    DRONE.start();
  }
  
  private void processImage(BufferedImage image) {
    try {
      if (movement) findRed(image);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  private void findRed(BufferedImage rawImage) throws Exception {
    float totalX = 0;
    int whitePxls = 0;
    float bestX;
    
    for (int x = 0; x < rawImage.getWidth(); x++) {
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
    }
    if (whitePxls >= 1) bestX = totalX / whitePxls;
    else bestX = rawImage.getWidth() / 2;
    
    usePxls(rawImage, bestX);
  }
  
  
  private void usePxls(BufferedImage image, float bestX) {
    bestX = bestX - (image.getWidth() / 2);
    final int rotvel = (int) (bestX / (image.getWidth() / 2)) * MAXROT;
    
    try {
      moveDrone(rotvel);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  private void moveDrone(int rotvel) throws Exception {
    movement = false;
    
    if (rotvel == 0) {
      noneFound++;
      DRONE.hover();
      rpi.setProgress(0);
      status.setText("Tracking");
      direction.setText("Hovering");
      TimeUnit.MILLISECONDS.sleep(100);
      movement = true;
      return;
    }
    
    if (rotvel > 0) {
      DRONE.getCommandManager().spinRight(rotvel).doFor(10);
      direction.setText("spinRight");
    } else {
      direction.setText("spinLeft");
      DRONE.getCommandManager().spinLeft(Math.abs(rotvel)).doFor(10);
    }
    
    rpi.setProgress(100 * Math.abs(rotvel) / MAXROT);
    status.setText("Tracking");
    
    
    TimeUnit.MILLISECONDS.sleep(100);
    DRONE.hover();
    movement = true;
  }
  
  
}
