package id.or.jisedu;

import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

enum DroneStatus {
  INFLIGHT, LANDED
}

public class DroneWithFX extends Application {
  private boolean isTracking;
  private boolean followTurn;
  private boolean followHeight;
  private float avgx;
  private float avgy;
  
  private Button bCommand;
  private CheckBox track;
  private CheckBox cfollowTurn;
  private CheckBox cfollowHeight;
  private DroneStatus status = DroneStatus.LANDED;
  private IARDrone drone;
  private ImageView imageView;
  
  private final ScheduledExecutorService WORKER = Executors.newSingleThreadScheduledExecutor();
  private final Color WHITE = new Color(255, 255, 255);
  private final Color BLACK = new Color(0, 0, 0);
  private final Color RED = new Color(255, 0, 0);
  
  public static void main(String[] args) {
    launch(args);
  }
  
  public void start(Stage s) {
    drone = new ARDrone();
    Button forwardButton = new Button("");
    Button backButton = new Button("");
    Button leftButton = new Button("");
    Button rightButton = new Button("");
    forwardButton.setOnMouseClicked(e -> drone.getCommandManager().forward(10));
    forwardButton.setOnMouseReleased(e -> drone.getCommandManager().forward(0));
    backButton.setOnMouseClicked(e -> drone.getCommandManager().backward(10));
    backButton.setOnMouseReleased(e -> drone.getCommandManager().backward(0));
    leftButton.setOnMouseClicked(e -> drone.getCommandManager().goLeft(10));
    leftButton.setOnMouseReleased(e -> drone.getCommandManager().goLeft(0));
    rightButton.setOnMouseClicked(e -> drone.getCommandManager().goRight(10));
    rightButton.setOnMouseReleased(e -> drone.getCommandManager().goRight(0));
    forwardButton.setPrefWidth(30);
    forwardButton.setPrefHeight(30);
    backButton.setPrefWidth(30);
    backButton.setPrefHeight(30);
    leftButton.setPrefWidth(30);
    leftButton.setPrefHeight(30);
    rightButton.setPrefWidth(30);
    rightButton.setPrefHeight(30);
    forwardButton.setLayoutX(100);
    forwardButton.setLayoutY(100);
    leftButton.setLayoutX(60);
    leftButton.setLayoutY(140);
    backButton.setLayoutX(100);
    backButton.setLayoutY(140);
    rightButton.setLayoutX(140);
    rightButton.setLayoutY(140);
    Label label = new Label("Drone Control Panel");
    label.setLayoutX(250);
    label.setFont(new Font(40));
    bCommand = new Button("TakeOff/Land");
    bCommand.setPrefWidth(150);
    bCommand.setLayoutX(325);
    bCommand.setLayoutY(100);
    track = new CheckBox("Enable Tracking");
    track.setLayoutX(335);
    track.setLayoutY(150);
    cfollowTurn = new CheckBox("Turn towards ball");
    cfollowTurn.setLayoutX(335);
    cfollowTurn.setLayoutY(200);
    cfollowHeight = new CheckBox("Stay on plane of ball");
    cfollowHeight.setLayoutX(335);
    cfollowHeight.setLayoutY(250);
    imageView = new ImageView();
    imageView.setLayoutX(335);
    imageView.setLayoutY(300);
    AnchorPane pane = new AnchorPane();
    pane.setStyle("-fx-background-color: seagreen");
    pane.getChildren().addAll(label, bCommand, track, cfollowHeight, cfollowTurn, imageView, leftButton, rightButton, forwardButton, backButton);
    Scene scene = new Scene(pane, 800, 600);
    s.setScene(scene);
    s.show();
    
    bCommand.setOnAction(e -> {
      if (status == DroneStatus.INFLIGHT) {
        status = DroneStatus.LANDED;
        drone.getCommandManager().landing();
        bCommand.setDisable(true);
        track.setDisable(true);
        track.setSelected(false);
        cfollowTurn.setSelected(false);
        cfollowHeight.setSelected(false);
        cfollowTurn.setDisable(true);
        cfollowHeight.setDisable(true);
        forwardButton.setDisable(true);
        backButton.setDisable(true);
        leftButton.setDisable(true);
        rightButton.setDisable(true);
        Runnable task = () -> bCommand.setDisable(false);
        WORKER.schedule(task, 5, TimeUnit.SECONDS);
      } else {
        status = DroneStatus.INFLIGHT;
        bCommand.setDisable(true);
        drone.getCommandManager().takeOff();
        track.setDisable(true);
        track.setSelected(false);
        Runnable task = () -> {
          bCommand.setDisable(false);
          track.setDisable(false);
          forwardButton.setDisable(false);
          backButton.setDisable(false);
          leftButton.setDisable(false);
          rightButton.setDisable(false);
        };
        WORKER.schedule(task, 5, TimeUnit.SECONDS);
      }
    });
    track.setOnAction(e -> {
      
      isTracking = track.isSelected();
      if (!isTracking) {
        cfollowTurn.setSelected(false);
        cfollowHeight.setSelected(false);
        cfollowTurn.setDisable(true);
        cfollowHeight.setDisable(true);
        forwardButton.setDisable(false);
        backButton.setDisable(false);
        leftButton.setDisable(false);
        rightButton.setDisable(false);
      } else {
        forwardButton.setDisable(true);
        backButton.setDisable(true);
        leftButton.setDisable(true);
        rightButton.setDisable(true);
        cfollowTurn.setDisable(false);
        cfollowHeight.setDisable(false);
      }
      
    });
    cfollowHeight.setOnAction(e -> followHeight = cfollowHeight.isSelected());
    cfollowTurn.setOnAction(e -> followTurn = cfollowTurn.isSelected());
    track.setDisable(true);
    cfollowHeight.setDisable(true);
    cfollowTurn.setDisable(true);
    forwardButton.setDisable(true);
    backButton.setDisable(true);
    leftButton.setDisable(true);
    rightButton.setDisable(true);
//    startUp();
  
  }
  
  private void controller(BufferedImage image) {
    Image img = printAllRed(image);
    imageView.setImage(img);
    if (!isTracking) {
      return;
    }
    int maxrot = 70;
    int maxvert = 70;
    int maxhoriz = 20;
    float rotvel;
    float vertvel;
    float horizvel;
    avgx = avgx - (image.getWidth() / 2);
    avgy = 0 - (avgy - (image.getHeight() / 2));
    rotvel = (avgx / (image.getWidth() / 2)) * maxrot;
    vertvel = (avgy / (image.getHeight() / 2)) * maxvert;
    drone.getCommandManager().hover();
    if (followTurn && rotvel != 0) {
    }
  }
  
  
  private void startUp() {
    drone.getVideoManager().addImageListener(this::controller);
  }
  
  private void moveDrone() {
  
  }
  
  
  private Image printAllRed(BufferedImage image) {
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
          bufferedImage.setRGB(x, y, WHITE.getRGB());
          totx += x;
          toty += y;
          counter++;
        } else if (r >= 120) {
          bufferedImage.setRGB(x, y, BLACK.getRGB());
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
    bufferedImage.setRGB((int) avgx, (int) avgy, RED.getRGB());
    return SwingFXUtils.toFXImage(bufferedImage, null);
  }
}
