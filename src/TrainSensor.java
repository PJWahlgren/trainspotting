import TSim.CommandException;
import TSim.SensorEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class TrainSensor implements PropertyChangeListener {
    private final int xPos;
    private final int yPos;
    private Command commandOnActive;
    private Command commandOnInactive;

    public TrainSensor(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    /**
     * Checks if the SensorEvent is the same as this sensor. This is how
     * the semaphore tells which sensor it should notify again to the
     * train waiting.
     * @param event
     * @return boolean
     */
    public boolean isSensor(SensorEvent event){
        return xPos == event.getXpos() && yPos == event.getYpos();
    }

    /**
     * Whenever a train passes a sensor it acts as an observer and sends out
     * an SensorEvent
     * @param evt A PropertyChangeEvent object describing the event source
     *          and the property that has changed. All bets are off if it's
     *            anything but a SensorEvent.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        activateSensor((SensorEvent)evt.getNewValue());
    }

    /**
     * Manually activate the command assign to the sensor.
     * @param event
     */
    public void activateSensor(SensorEvent event){
        if (isSensor(event)) {
            //System.out.println("Sensor @(" + event.getXpos() + "," + event.getYpos() + ") " + event.getStatus());
            try {
                if (commandOnActive != null && event.getStatus() == SensorEvent.ACTIVE)
                    commandOnActive.execute(event);
                if (commandOnInactive != null && event.getStatus() == SensorEvent.INACTIVE)
                    commandOnInactive.execute(event);
            } catch (CommandException e) {
                throw new RuntimeException(e);
            }

        }
    }

    /**
     * The way to assign commands to certain sensors. This one is assigned for
     * active sensor input only.
     * @param commandOnActive The command when the signal becomes active.
     */
    public void setCommandOnActive(Command commandOnActive) {
        this.commandOnActive = commandOnActive;
    }
    /**
     * The way to assign commands to certain sensors. This one is assigned for
     * inactive sensor input only.
     * @param commandOnInactive The command when the signal becomes inactive.
     */
    public void setCommandOnInactive(Command commandOnInactive){
        this.commandOnInactive = commandOnInactive;
    }
}
