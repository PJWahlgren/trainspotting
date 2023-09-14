import TSim.*;

import java.util.ArrayList;
import java.util.List;

public class Lab1 {
  Train[] trains;
  BinarySemaphore semCrossing = new BinarySemaphore();
  BinarySemaphore semC = new BinarySemaphore();
  //Semaphore semGoingOutOfDorE = new BinarySemaphore();
  BinarySemaphore semF = new BinarySemaphore();
  BinarySemaphore semDorE = new BinarySemaphore();
  BinarySemaphore semAorB = new BinarySemaphore();
  BinarySemaphore semGorH = new BinarySemaphore();

  //There may only be one train waiting. If there are more, then we have an issue!
  Train waitingTrain;


  public Lab1(int speed1, int speed2) {
    List<TrainSensor> sensors = new ArrayList<>();
    Train train1 = new Train(1);
    Train train2 = new Train(2);
    //Added null so indexing becomes more intuitive.
    trains = new Train[]{null, train1, train2};

    TrainSensor a1 = new TrainSensor(14,3);
    TrainSensor a2 = new TrainSensor(6,6);
    TrainSensor a3 = new TrainSensor(11,7);
    TrainSensor a4 = new TrainSensor(15,7);

    TrainSensor b1 = new TrainSensor(14,5);
    TrainSensor b2 = new TrainSensor(8 ,5);
    TrainSensor b3 = new TrainSensor(10,8);
    TrainSensor b4 = new TrainSensor(15,8);

    TrainSensor c1 = new TrainSensor(16,9);

    TrainSensor d1 = new TrainSensor(5,9);
    TrainSensor d2 = new TrainSensor(14,9);

    TrainSensor e1 = new TrainSensor(5,10);
    TrainSensor e2 = new TrainSensor(14,10);

    TrainSensor f1 = new TrainSensor(3,9);

    TrainSensor g1 = new TrainSensor(4,11);
    TrainSensor g2 = new TrainSensor(14,11);

    TrainSensor h1 = new TrainSensor(3,12);
    TrainSensor h2 = new TrainSensor(14,13);

    sensors.add(a1);
    sensors.add(a2);
    sensors.add(a3);
    sensors.add(a4);

    sensors.add(b1);
    sensors.add(b2);
    sensors.add(b3);
    sensors.add(b4);

    sensors.add(c1);

    sensors.add(d1);
    sensors.add(d2);

    sensors.add(e1);
    sensors.add(e2);

    sensors.add(f1);

    sensors.add(g1);
    sensors.add(g2);

    sensors.add(h1);
    sensors.add(h2);

    semCrossing.addTrainSensor(a2);
    semCrossing.addTrainSensor(a3);
    semCrossing.addTrainSensor(b2);
    semCrossing.addTrainSensor(b3);

    semC.addTrainSensor(a4);
    semC.addTrainSensor(b4);
    semC.addTrainSensor(d2);
    semC.addTrainSensor(e2);

    semDorE.addTrainSensor(c1);
    semDorE.addTrainSensor(f1);

    semF.addTrainSensor(d1);
    semF.addTrainSensor(e1);
    semF.addTrainSensor(g1);
    semF.addTrainSensor(h1);
    semAorB.tryAcquire();
    semGorH.tryAcquire();
    try {
      TrackSwitch s1 = new TrackSwitch(17,7);
      TrackSwitch s2 = new TrackSwitch(15,9);
      TrackSwitch s3 = new TrackSwitch(4,9);
      TrackSwitch s4 = new TrackSwitch(3,11);

      train1.setSpeed(speed1);
      train2.setSpeed(speed2);
      for (TrainSensor sensor : sensors){
        train1.addPropertyChangeListener(sensor);
        train2.addPropertyChangeListener(sensor);
      }
      a1.setCommandOnActive((evt) -> {
        stopwaitrevert(evt.getTrainId(), train1.getSpeed() < 0,train2.getSpeed() > 0);
      });
      a2.setCommandOnActive((evt) -> {
        if (getTrain(evt).isGoingUp())
          semCrossing.releaseThenNotifyAllSensors();
        else
        if (!semCrossing.tryAcquire(evt))
          getTrain(evt).stop();
        else
          getTrain(evt).resume();
      });
      a3.setCommandOnActive((evt) -> {
        if (getTrain(evt).isGoingUp())
          if (!semCrossing.tryAcquire(evt))
            getTrain(evt).stop();
          else
            getTrain(evt).resume();
      });
      a3.setCommandOnInactive(evt -> {
        if (getTrain(evt).isGoingDown())
          semCrossing.releaseThenNotifyAllSensors();
      });
      a4.setCommandOnActive((evt) -> {
        if (getTrain(evt).isGoingDown())
          if (semC.tryAcquire(evt)){
            getTrain(evt).resume();
            s1.switchRight();
          }
          else
            getTrain(evt).stop();
      });
      a4.setCommandOnInactive(evt -> {
        if (getTrain(evt).isGoingUp())
          semC.releaseThenNotifyAllSensors();
        else
          semAorB.releaseThenNotifyAllSensors();
      });

      b1.setCommandOnActive((evt) -> {
        stopwaitrevert(evt.getTrainId(), train1.getSpeed() < 0,train2.getSpeed() > 0);
      });
      b2.setCommandOnActive((evt) -> {
        if (getTrain(evt).isGoingUp())
          semCrossing.releaseThenNotifyAllSensors();
        else
          if (!semCrossing.tryAcquire(evt))
            getTrain(evt).stop();
      });
      b3.setCommandOnActive((evt) -> {
        if (getTrain(evt).isGoingDown())
          semCrossing.releaseThenNotifyAllSensors();
        else
          if (!semCrossing.tryAcquire(evt))
            getTrain(evt).stop();
          else
            getTrain(evt).resume();
      });
      b4.setCommandOnActive((evt) -> {
        if (getTrain(evt).isGoingDown())
          if (semC.tryAcquire(evt)){
            s1.switchLeft();
            getTrain(evt).resume();
          }
          else
            getTrain(evt).stop();
      });
      b4.setCommandOnInactive(evt -> {
        if (getTrain(evt).isGoingUp())
          semC.releaseThenNotifyAllSensors();
      });

      c1.setCommandOnActive((evt) -> {
        if (getTrain(evt).isGoingDown()) {
          //System.out.println("semDorE activated: " + semDorE);
          if (semDorE.tryAcquire())
            s2.switchRight();
          else
            s2.switchLeft();
        }else{
          if (semAorB.tryAcquire())
            s1.switchRight();
          else
            s1.switchLeft();
        }
      });
      c1.setCommandOnInactive(evt -> {
      });

      d1.setCommandOnActive((evt) -> {
        if (getTrain(evt).isGoingDown()) {
          //System.out.println("semF activated: " + semF);
          if (!semF.tryAcquire(evt))
            getTrain(evt).stop();
          else{
            s3.switchLeft();
            getTrain(evt).resume();
          }
        }
      });
      d1.setCommandOnInactive(evt -> {
        if (getTrain(evt).isGoingUp())
          semF.releaseThenNotifyAllSensors();
        else
          semDorE.releaseThenNotifyAllSensors();
        //System.out.println("semF released: " + semF);
      });
      d2.setCommandOnActive((evt) -> {
        if (getTrain(evt).isGoingUp()){
          if (!semC.tryAcquire(evt))
            getTrain(evt).stop();
          else {
            s2.switchRight();
            getTrain(evt).resume();
          }
        }
      });
      d2.setCommandOnInactive(evt -> {
        if (getTrain(evt).isGoingDown())
          semC.releaseThenNotifyAllSensors();
        else
          semDorE.releaseThenNotifyAllSensors();
      });

      e1.setCommandOnActive((evt) -> {
        //System.out.println("semF released: " + semF);
        if (getTrain(evt).isGoingDown())
          if (!semF.tryAcquire(evt))
            getTrain(evt).stop();
          else{
            s3.switchRight();
            getTrain(evt).resume();
          }
      });
      e1.setCommandOnInactive(evt -> {
        if (getTrain(evt).isGoingUp())
          semF.releaseThenNotifyAllSensors();
      });

      e2.setCommandOnActive((evt) -> {
        if (getTrain(evt).isGoingUp()){
          if (!semC.tryAcquire(evt))
            getTrain(evt).stop();
          else {
            s2.switchLeft();
            getTrain(evt).resume();
          }
        }
      });
      e2.setCommandOnInactive(evt -> {
        if (getTrain(evt).isGoingDown())
          semC.releaseThenNotifyAllSensors();
      });

      f1.setCommandOnActive((evt) -> {
        //System.out.println("semDorE activated: " + semDorE);
        if (getTrain(evt).isGoingUp()){
          if (semDorE.tryAcquire())
            s3.switchLeft();
          else
            s3.switchRight();
        }
      });
      f1.setCommandOnInactive(evt -> {
        if (getTrain(evt).isGoingDown()) {
          //System.out.println("semDorE released: " + semDorE);
          if (semGorH.tryAcquire())
            s4.switchLeft();
          else
            s4.switchRight();
        }
      });

      g1.setCommandOnActive((evt) -> {
        if (getTrain(evt).isGoingUp()) {
          if (!semF.tryAcquire(evt))
            getTrain(evt).stop();
          else {
            s4.switchLeft();
            getTrain(evt).resume();
            //System.out.println("semF activated: " + semF);
          }
        }
      });
      g1.setCommandOnInactive(evt -> {
        if (getTrain(evt).isGoingDown())
          semF.releaseThenNotifyAllSensors();
        else
          semGorH.releaseThenNotifyAllSensors();

      });
      g2.setCommandOnActive((evt) -> {
        stopwaitrevert(evt.getTrainId(),train1.getSpeed() > 0,train2.getSpeed() < 0);
      });

      h1.setCommandOnActive((evt) -> {
        if (getTrain(evt).isGoingUp())
          if (!semF.tryAcquire(evt))
            getTrain(evt).stop();
          else{
            s4.switchRight();
            getTrain(evt).resume();
          }
      });
      h1.setCommandOnInactive(evt -> {
        if (getTrain(evt).isGoingDown())
          semF.releaseThenNotifyAllSensors();
      });
      h2.setCommandOnActive((evt) -> {
        stopwaitrevert(evt.getTrainId(),train1.getSpeed() > 0,train2.getSpeed() < 0);
      });
    }
    catch (CommandException e) {
      e.printStackTrace();    // or only e.getMessage() for the error
      System.exit(1);
    }
  }

  /**
   * It stops, wait, and then revert the train's direction. In that order. The conditions
   * are there so the train don't stop right away when they go but only when it
   * makes sense
   * @param id                 Train id
   * @param conditionForTrain1 Condition for Train 1.
   * @param conditionForTrain2 Condition for Train 2.
   */
  private void stopwaitrevert(int id, boolean conditionForTrain1, boolean conditionForTrain2){
    boolean condition;
    if (id == 1){
      condition = conditionForTrain1;
    }
    else{
      condition = conditionForTrain2;
    }
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
  private Train getTrain(SensorEvent event){
    return trains[event.getTrainId()];
  }
}
