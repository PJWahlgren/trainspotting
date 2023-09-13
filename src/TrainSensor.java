import TSim.CommandException;
import TSim.SensorEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class TrainSensor implements PropertyChangeListener {
    private final int xPos;
    private final int yPos;
    private Command command;

    public TrainSensor(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }
    public TrainSensor(int xPos, int yPos, Command command){
        this(xPos,yPos);
        this.command = command;
    }
    public boolean isSensor(SensorEvent event){
        return xPos == event.getXpos() && yPos == event.getYpos();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SensorEvent event = (SensorEvent) evt.getNewValue();
        if (isSensor(event)
                && command != null
                && event.getStatus() == SensorEvent.ACTIVE) {
            //System.out.println("@Sensor " + evt.getNewValue());
            Thread t = new Thread(() -> {
                try {
                    command.execute(event.getTrainId(), event.getStatus());
                } catch (CommandException e) {
                    throw new RuntimeException(e);
                }
            });
            t.start();
        }
    }

    public void setCommand(Command command) {
        this.command = command;
    }
}
