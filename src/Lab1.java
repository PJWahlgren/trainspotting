import TSim.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Lab1 {
  Train[] trains;
  Semaphore sem1 = new Semaphore(1);
  Semaphore sem2 = new Semaphore(1);
  Semaphore sem3 = new Semaphore(1);
  Semaphore sem4 = new Semaphore(1);
  Semaphore semDorE = new Semaphore(1);
  Semaphore semAorB = new Semaphore(1);
  Semaphore SemGorH = new Semaphore(1);

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

    TrainSensor c1 = new TrainSensor(18,7);
    TrainSensor c2 = new TrainSensor(16,9);

    TrainSensor d1 = new TrainSensor(7,9);
    TrainSensor d2 = new TrainSensor(12,9);

    TrainSensor e1 = new TrainSensor(6,10);
    TrainSensor e2 = new TrainSensor(13,10);

    TrainSensor f1 = new TrainSensor(1,10);

    TrainSensor g1 = new TrainSensor(6,11);
    TrainSensor g2 = new TrainSensor(14,11);

    TrainSensor h1 = new TrainSensor(4,13);
    TrainSensor h2 = new TrainSensor(14,13);

    sensors.add(a1);
    sensors.add(a2);
    sensors.add(a3);
    sensors.add(a4);

    sensors.add(b1);
    sensors.add(b2);
    sensors.add(b3);
    sensors.add(b4);

    sensors.add(c2);

    sensors.add(d1);
    sensors.add(d2);

    sensors.add(e1);
    sensors.add(e2);

    sensors.add(f1);

    sensors.add(g1);
    sensors.add(g2);

    sensors.add(h1);
    sensors.add(h2);

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
      a1.setCommand((id, status) -> {
        stopwaitrevert(id,train1.getSpeed() < 0,train2.getSpeed() > 0);
      });
      a2.setCommand((id, status) -> {
        if (trains[id].isGoingUp()){
          sem1.release();
          notifyTrainWaiting(sem1);
        }
        else
          activateSemaphore(id, Train.Direction.DOWN,sem1);
      });
      a3.setCommand((id, status) -> {
        if (trains[id].isGoingDown()){
          sem1.release();
          notifyTrainWaiting(sem1);
        }else
          activateSemaphore(id, Train.Direction.UP,sem1);
      });
      a4.setCommand((id, status) -> {
        if (trains[id].isGoingUp()){
          s1.invertDirection();
          sem2.release();
          notifyTrainWaiting(sem2);
        }
        else{
          if (isSemaphoreAvailable(sem2))
            s1.switchRight();
          activateSemaphore(id, Train.Direction.DOWN, sem2);
        }
      });

      b1.setCommand((id, status) -> {
        stopwaitrevert(id,train1.getSpeed() < 0,train2.getSpeed() > 0);
      });
      b2.setCommand((id, status) -> {
        if (trains[id].isGoingUp()){
          sem1.release();
          notifyTrainWaiting(sem1);
        }
        else
          activateSemaphore(id, Train.Direction.DOWN,sem1);
      });
      b3.setCommand((id, status) -> {
        if (trains[id].isGoingDown()){
          sem1.release();
          notifyTrainWaiting(sem1);
        }else
          activateSemaphore(id, Train.Direction.UP,sem1);
      });
      b4.setCommand((id, status) -> {
        if (trains[id].isGoingUp()){
          s1.invertDirection();
          sem2.release();
          notifyTrainWaiting(sem2);
        }
        else {
          if (isSemaphoreAvailable(sem2))
            s1.switchLeft();
          activateSemaphore(id, Train.Direction.DOWN, sem2);
        }

      });
      c1.setCommand((id, status) -> {
        if (trains[id].isGoingUp()){
          sem3.release();
          if (isSemaphoreAvailable(semAorB))
            s1.switchRight();
          else
            s1.switchLeft();
          notifyTrainWaiting(sem3);
        }
      });
      c2.setCommand((id, status) -> {
        if (trains[id].isGoingDown()){
          if (semDorE.tryAcquire())
            s2.switchRight();
          else
            s2.switchLeft();
          s1.invertDirection();
          sem2.release();
          notifyTrainWaiting(sem2);
        }else
          if (semDorE.availablePermits() < 1)
            semDorE.release();
      });

      d1.setCommand((id, status) -> {
        if (trains[id].isGoingDown()){
          if (isSemaphoreAvailable(sem3))
            s3.switchLeft();
          activateSemaphore(id, Train.Direction.DOWN, sem3);
        }else {
          sem4.release();
          s4.invertDirection();
          System.out.println(sem4);
          notifyTrainWaiting(sem4);
        }
      });
      d2.setCommand((id, status) -> {
        if (trains[id].isGoingUp()){
          if (sem2.availablePermits() == 1)
            s2.switchRight();
          activateSemaphore(id, Train.Direction.UP, sem2);
        }
      });

      e1.setCommand((id, status) -> {
        if (trains[id].isGoingDown()){
          if (sem3.availablePermits() == 1)
            s3.switchRight();
          activateSemaphore(id, Train.Direction.DOWN, sem3);
        }else {
          sem4.release();
          s4.invertDirection();
          System.out.println(sem4);
          notifyTrainWaiting(sem4);
        }
      });
      e2.setCommand((id, status) -> {
        if (trains[id].isGoingUp()){
          if (sem2.availablePermits() == 1)
            s2.switchLeft();
          activateSemaphore(id, Train.Direction.UP, sem2);
        }
      });

      f1.setCommand((id, status) -> {
        if (trains[id].isGoingDown()){
          activateSemaphore(id, Train.Direction.DOWN, sem4);
          System.out.println(sem4);
          s3.invertDirection();
          if (semDorE.availablePermits() < 1)
            semDorE.release();
        }else{
          if (semDorE.tryAcquire())
            s3.switchLeft();
          else
            s3.switchRight();
        }
      });

      g1.setCommand((id, status) -> {
        if (trains[id].isGoingUp()){
          System.out.println("sem4 " + isSemaphoreAvailable(sem4));
          if (isSemaphoreAvailable(sem4))
            s4.switchLeft();
          activateSemaphore(id, Train.Direction.UP, sem4);
        }else{
          sem3.release();
          notifyTrainWaiting(sem3);
          sem4.release();
          System.out.println(sem4);
          s4.invertDirection();
          notifyTrainWaiting(sem4);
        }
      });
      g2.setCommand((id, status) -> {
        stopwaitrevert(id,train1.getSpeed() > 0,train2.getSpeed() < 0);
      });

      h1.setCommand((id, status) -> {
        if (trains[id].isGoingUp()){
          if (isSemaphoreAvailable(sem4))
            s4.switchRight();
          activateSemaphore(id, Train.Direction.UP, sem4);
        }else{
          sem4.release();
          notifyTrainWaiting(sem4);
          sem3.release();
          notifyTrainWaiting(sem3);
          System.out.println(sem4);
          s4.invertDirection();
        }
      });
      h2.setCommand((id, status) -> {
        stopwaitrevert(id,train1.getSpeed() > 0,train2.getSpeed() < 0);
      });
    }
    catch (CommandException e) {
      e.printStackTrace();    // or only e.getMessage() for the error
      System.exit(1);
    }
  }

  private void stopwaitrevert(int id,boolean condition0, boolean condition1){
    boolean condition;
    if (id == 1){
      condition = condition0;
    }
    else{
      condition = condition1;
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
  private void activateSemaphore(int id, Train.Direction direction, Semaphore sem){
    if (trains[id].getDirection() == direction){
      if (!sem.tryAcquire()){
        System.out.println("Train " + id + " could not acquire");
        waitingTrain = trains[id];
        try{
          waitingTrain.stop();
        } catch (CommandException e) {
          throw new RuntimeException(e);
        }
      }
      else
        System.out.println("Train " + id + " says rock on!");
    }
  }
  private void notifyTrainWaiting(Semaphore sem){
    if (waitingTrain != null){
      System.out.println("Notifiying train " + waitingTrain.getId());
      if (sem.tryAcquire()){
        System.out.println("Train " + waitingTrain.getId() + " acquired");
        try{
          System.out.println("Resuming sema" + sem);
          waitingTrain.resume();
        } catch (CommandException e) {
          throw new RuntimeException(e);
        }
        waitingTrain = null;
      }
    }
  }
  private boolean isSemaphoreAvailable(Semaphore sem){
    return sem.availablePermits() == 1;
  }
}
