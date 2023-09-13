import TSim.SensorEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class BinarySemaphore{
    //Holds one event, any more should not be possible unless there are deadlocks!
    SensorEvent queuedEvent;
    List<TrainSensor> sensors;
    Semaphore semaphore = new Semaphore(1);

    /**
     * This class is a wrapper for a semaphore, it acts for the most part just like one
     * except it also stores and communicate with sensors that have been assigned
     * to this Semaphore. It's  also only Binary.
     */
    public BinarySemaphore(){
        this(new ArrayList<>());
    }
    public BinarySemaphore(List<TrainSensor> sensors){
        this.sensors = sensors;
    }

    public void addTrainSensor(TrainSensor sensor){
        sensors.add(sensor);
    }

    /**
     * It releases the semaphore and then activates all sensors assigned to
     * the semaphore. In practice since all sensors check if the SensorEvent
     * sent have come from their own sensor, it'll only tell the sensor which is holding
     * the waiting train.
     */
    public void releaseThenNotifyAllSensors() {
        semaphore.release();
        activateAllSensors();
    }

    /**
     * It activates all sensor assigned to the semaphore.
     * In practice since all sensors check if the SensorEvent
     * sent have come from their own sensor, it'll only tell the sensor which is holding
     * the waiting train.
     */
    private void activateAllSensors(){
        if (queuedEvent != null) {
            for (TrainSensor sensor : sensors)
                sensor.activateSensor(queuedEvent);
            popEvent();
        }
    }

    /**
     * It tries to acquire the semaphore if it can. If it can't then it'll
     * save the SensorEvent from the train that couldn't acquire,
     * and then reactivate it with activeAllSensors().
     * @param event
     * @return boolean
     */
    public boolean tryAcquire(SensorEvent event) {
        boolean succeeded = semaphore.tryAcquire();
        if (!succeeded) {
            queuedEvent = event;
            //System.out.println("Could not acquire.");
        }else
            System.out.println("Success!");
        return succeeded;
    }
    public boolean tryAcquire(){
        return semaphore.tryAcquire();
    }

    /**
     * Pops the SensorEvent from the semaphore.
     * @return SensorEvent
     */
    private SensorEvent popEvent(){
        SensorEvent evt = new SensorEvent(queuedEvent.getTrainId(),
                queuedEvent.getXpos(),
                queuedEvent.getYpos(),
                queuedEvent.getStatus());
        queuedEvent = null;
        return evt;
    }

    /**
     * Uses semaphore's implementation of toString().
     * @return String
     */
    public String toString(){
        return semaphore.toString();
    }
}
