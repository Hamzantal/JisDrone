package id.or.jisedu;

import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.ATCommand;
import de.yadrone.base.command.CommandManager;
import de.yadrone.base.command.FlightAnimation;

import java.util.concurrent.TimeUnit;

public class DroneTesting {
  
  public static void main(String[] args) throws Exception{
    IARDrone drone = new ARDrone();
    CommandManager cmd = drone.getCommandManager();
    drone.setHorizontalCamera();
    cmd.takeOff();
    cmd.hover();

    TimeUnit.SECONDS.sleep(10);
    
    TimeUnit.SECONDS.sleep(5);
    cmd.hover();
    cmd.landing();
    System.exit(0);
    
  }

}
