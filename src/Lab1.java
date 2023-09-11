import TSim.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Lab1 {
  Train[] trains;
  TSimInterface tsi = TSimInterface.getInstance();

  public Lab1(int speed1, int speed2) {
    TrackSwitch[] switches = new TrackSwitch[4];
    List<TrainSensor> sensors = new ArrayList<>();
    Semaphore sem = new Semaphore(1);
    Train train1 = new Train(1);
    Train train2 = new Train(2);
    trains = new Train[]{null, train1, train2};

    TrainSensor a1 = new TrainSensor(14,3);
    TrainSensor a2 = new TrainSensor(6,6);
    TrainSensor a3 = new TrainSensor(10,7);

    TrainSensor b1 = new TrainSensor(14,5);
    TrainSensor b2 = new TrainSensor(8 ,6);
    TrainSensor b3 = new TrainSensor(10,8);

    TrainSensor c1 = new TrainSensor(16,9);

    TrainSensor d1 = new TrainSensor(10,9);

    TrainSensor e1 = new TrainSensor(10,10);

    TrainSensor f1 = new TrainSensor(1,9);

    TrainSensor g1 = new TrainSensor(14,11);

    TrainSensor h1 = new TrainSensor(14,13);

    sensors.add(a1);
    sensors.add(a2);
    sensors.add(a3);

    sensors.add(b1);
    sensors.add(b2);
    sensors.add(b3);

    sensors.add(c1);
    sensors.add(d1);
    sensors.add(e1);
    sensors.add(f1);
    sensors.add(g1);

    sensors.add(h1);

    try {
      TrackSwitch s1 = new TrackSwitch(17,7);
      TrackSwitch s2 = new TrackSwitch(15,9);
      TrackSwitch s3 = new TrackSwitch(4,9);
      TrackSwitch s4 = new TrackSwitch(3,11);

      train1.setSpeed(0);
      train2.setSpeed(speed2);
      for (TrainSensor sensor : sensors){
        train1.addPropertyChangeListener(sensor);
        train2.addPropertyChangeListener(sensor);
      }
      a1.setCommand(id -> {
        stopwaitrevert(id,train1.getSpeed() < 0,train2.getSpeed() > 0);
      });
      b1.setCommand(id -> {
        stopwaitrevert(id,train1.getSpeed() < 0,train2.getSpeed() > 0);
      });
      g1.setCommand(id -> {
        stopwaitrevert(id,train1.getSpeed() > 0,train2.getSpeed() < 0);
      });
      h1.setCommand(id -> {
        stopwaitrevert(id,train1.getSpeed() > 0,train2.getSpeed() < 0);
      });


      f1.setCommand(id -> {
        s3.switchRight();
        s2.switchLeft();
      });
    }
    catch (CommandException e) {
      e.printStackTrace();    // or only e.getMessage() for the error
      System.exit(1);
    }
  }

  public void stopwaitrevert(int id,boolean condition0, boolean condition1){
    boolean condition;
    if (id == 1)
      condition = condition0;
    else
      condition = condition1;
    try{
      if (condition){
        trains[id].stop();
        Thread.sleep(2000);
        trains[id].invertDirection();
      }

    } catch (CommandException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
