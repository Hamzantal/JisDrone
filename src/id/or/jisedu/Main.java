package id.or.jisedu;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Main extends Application {

  public Main() throws IOException {
  }

  public static void main(String[] args) {
    launch(args);
  }

  // Sprites
  private Circle circle;
  private final Drone DRONE = new Drone(0);
  private List<Cloud> clouds = new ArrayList<>();
  private List<ImageView> tiles = new ArrayList<>();

  //Key-press related
  private boolean pressed = false;
  private boolean toLeft;
  private boolean toRight;

  //Drone Movement
  private boolean hover = false;
  private boolean flip;
  private double gravity = 1;
  private float roll = 0;
  private int flipIncr = -100;
  private double changeY = 5;

  //Base
  private double width = 1280;
  private double height = 720;
  private Random r = new Random();
  private AnchorPane pane = new AnchorPane();
  private BufferedImage ground = ImageIO.read(ClassLoader.getSystemResource("id/or/jisedu/res/ground.png"));
  private Scene scene = new Scene(pane, width, height + ground.getHeight());

  public void start(Stage stage) throws Exception {
    setupClouds();
    pane.setStyle("-fx-background-color: #DCFFFF");
    setupGround();
    setupStage(stage);
    new AnimationTimer() {
      private int update = 0;

      public void handle(long currentTime) {
        DRONE.update();
        if (!flip && !hover) {
          if (toLeft) roll -= 1.25;
          else if (toRight) roll += 1.25;
          if (roll >= 45) roll = 50;
          else if (roll <= -45) roll = -50;
          updateY();
          updateRoll();
        } else if (hover && flipIncr == -100) hover();
        else flip();
        DRONE.setCenterY(circle.getCenterY());
        DRONE.setCenterX(circle.getCenterX());
        update++;
        if (update == 5) {
          updateClouds();
          update = 0;
        }
        sleep();
      }
    }.start();
  }

  private void setupStage(Stage stage) {
    circle = new Circle();
    circle.setCenterX(scene.getWidth() / 2);
    circle.setCenterY(scene.getHeight() * .9);
    circle.setRadius(DRONE.getHeight() / 2);
    pane.getChildren().addAll(circle, DRONE);
    stage.setScene(scene);
    stage.show();
    DRONE.setCenterX(scene.getWidth() / 2);
    DRONE.setCenterY(scene.getHeight() * 0.9);
    stage.setOnCloseRequest(e -> System.exit(0));
    scene.setOnKeyPressed(e -> {
      if (e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.W || e.getCode() == KeyCode.UP) pressed = true;
      if (e.getCode() == KeyCode.A || e.getCode() == KeyCode.LEFT) toLeft = true;
      if (e.getCode() == KeyCode.D || e.getCode() == KeyCode.RIGHT) toRight = true;
      if (e.getCode() == KeyCode.H) hover = !hover;
      if (e.getCode() == KeyCode.F && !flip) flipIncr = -15;
      if (e.getCode() == KeyCode.F && !flip) flip = true;
      if (e.getCode() == KeyCode.ESCAPE) System.exit(0);
    });
    scene.setOnKeyReleased(e -> {
      if (e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.W || e.getCode() == KeyCode.UP) pressed = false;
      if (e.getCode() == KeyCode.A || e.getCode() == KeyCode.LEFT) toLeft = false;
      if (e.getCode() == KeyCode.D || e.getCode() == KeyCode.RIGHT) toRight = false;
    });
    stage.widthProperty().addListener((obs, oldval, newval) -> width = scene.getWidth());
    stage.heightProperty().addListener((obs, oldval, newval) -> {
      height = newval.doubleValue();
      updateGround();
    });
    circle.setVisible(false);
  }

  private void setupGround() {
    for (int i = 0; i < 200; i++) {
      ImageView view = new ImageView(SwingFXUtils.toFXImage(ground, null));
      pane.getChildren().add(view);
      view.setY(scene.getHeight() - ground.getHeight());
      view.setX(i * 16);
      tiles.add(view);
    }
  }

  private void updateGround() {
    for (ImageView tile : tiles) {
      height = scene.getHeight() - 16;
      tile.setY(scene.getHeight() - ground.getHeight());
      if (circle.getCenterY() + circle.getRadius() > height) circle.setCenterY(scene.getHeight() * .9);

    }
  }

  private void setupClouds() {
    for (int i = 0; i < 5; i++) {
      Cloud cloud = new Cloud();
      clouds.add(cloud);
      pane.getChildren().add(cloud);
      cloud.setCenterX(256 * i + r.nextInt(100) + 100d);
      cloud.setCenterY(r.nextInt(500) + 20d);
    }
  }

  private void updateClouds() {
    for (Cloud cloud : clouds) {
      cloud.setX(cloud.getX() + r.nextDouble());
      if (cloud.getX() > width) {
        cloud.setX(1);
        cloud.setY(r.nextInt(500) + 20);
      }
    }
  }

  private void hover() {
    if (circle.getCenterX() + (DRONE.getWidth() / 2) >= width || circle.getCenterX() - (DRONE.getWidth() / 2) <= 0)
      roll *= -.3d;
    if (roll > 0.5) roll -= 1f;
    else if (roll < -0.5) roll += 1f;
    if (Math.abs(roll) <= .5) roll = 0;
    DRONE.setRotate(roll);
    if (circle.getCenterX() + (DRONE.getWidth() / 2) > width) circle.setCenterX(width - (DRONE.getWidth() / 2) - 5);
    if (circle.getCenterX() - (DRONE.getWidth() / 2) < 0) circle.setCenterX(5 + (DRONE.getWidth() / 2));
    if (circle.getCenterY() + circle.getRadius() + changeY >= height) {
      changeY *= -.3;
      roll *= 0.2;
    } else {
      if (changeY > 0) changeY -= gravity;
      else if (changeY < 0) changeY += gravity;
      if (Math.abs(changeY) <= 0.5) changeY = 0;
      circle.setCenterY(changeY + circle.getCenterY());
    }
    circle.setCenterX(circle.getCenterX() + roll);

  }

  private void flip() {
    if (flipIncr >= 0) {
      if (flipIncr == 0) changeY = -15;
      DRONE.setRotate(roll + flipIncr);
      flipIncr = flipIncr + 10;
      if (flipIncr < 180) {
        if (changeY > 0) changeY -= gravity;
        else if (changeY < 0) changeY += gravity;
        circle.setCenterY(changeY + circle.getCenterY());
      } else {
        changeY += gravity;
        circle.setCenterY(circle.getCenterY() + changeY);
      }
      if (circle.getCenterY() + circle.getRadius() + changeY >= height) {
        DRONE.setRotate(roll);
        flipIncr = -100;
        circle.setCenterY(height * .9);
        flip = false;
      }
      if (flipIncr >= 360) {
        DRONE.setRotate(roll);
        flipIncr = -100;
        flip = false;
      }
      return;
    }
    changeY -= 3;
    if (changeY < -15) changeY = -15;
    circle.setCenterY(circle.getCenterY() + changeY);
    if (circle.getCenterY() + circle.getRadius() + changeY >= height) {
      changeY *= -.3;
    }
    if (circle.getCenterX() + (DRONE.getWidth() / 2) >= width || circle.getCenterX() - (DRONE.getWidth() / 2) <= 0)
      roll *= -.3d;

    if (roll > 0.02) roll -= 3f;
    else if (roll < -0.02) roll += 3f;
    DRONE.setRotate(roll);
    if (circle.getCenterX() + (DRONE.getWidth() / 2) > width) circle.setCenterX(width - (DRONE.getWidth() / 2));
    if (circle.getCenterX() - (DRONE.getWidth() / 2) < 0) circle.setCenterX(0 + (DRONE.getWidth() / 2));
    circle.setCenterX(circle.getCenterX() + roll);
    flipIncr++;

  }

  private void updateY() {
    if (!pressed) {
      if (circle.getCenterY() + circle.getRadius() + changeY >= height) {
        changeY *= -.3;
        roll *= 0.2;
      } else changeY += gravity;
      circle.setCenterY(circle.getCenterY() + changeY);
    } else {
      if (circle.getCenterY() + circle.getRadius() + changeY >= height) {
        changeY *= -0.3;
        roll *= 0.2;
      }
      circle.setCenterY(circle.getCenterY() + (changeY -= 1));
    }
  }

  private void updateRoll() {
    if (circle.getCenterX() + (DRONE.getWidth() / 2) >= width || circle.getCenterX() - (DRONE.getWidth() / 2) <= 0)
      roll *= -.3d;
    circle.setCenterX(circle.getCenterX() + roll);
    if (!toRight && !toLeft) {
      if (roll > 0.02) roll -= 0.5f;
      else if (roll < -0.02) roll += 0.5f;
    }
    if (circle.getCenterX() + (DRONE.getWidth() / 2) > width) circle.setCenterX(width - (DRONE.getWidth() / 2));
    if (circle.getCenterX() - (DRONE.getWidth() / 2) < 0) circle.setCenterX(0 + (DRONE.getWidth() / 2));
    DRONE.setRotate(roll);
  }

  private void sleep() {
    try {
      Thread.sleep((long) 18);
    } catch (Exception ignored) {

    }
  }

}
