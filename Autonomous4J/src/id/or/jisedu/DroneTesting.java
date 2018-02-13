package id.or.jisedu;

import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CommandManager;

import java.util.concurrent.TimeUnit;

public class DroneTesting {
  
  public static void main(String[] args) throws Exception{
    IARDrone drone = new ARDrone();
    drone.start();
    CommandManager cmd = drone.getCommandManager();
    drone.setHorizontalCamera();
    cmd.takeOff();
  
    TimeUnit.SECONDS.sleep(5);
    
    cmd.landing();
    System.exit(0);
    
  }

}
