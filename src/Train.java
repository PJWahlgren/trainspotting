import TSim.CommandException;
import TSim.SensorEvent;
import TSim.TSimInterface;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Train{
    TSimInterface tsi = TSimInterface.getInstance();
    private final PropertyChangeSupport support;
    private final int id;
    private int speed;
    private SensorEvent event;
    public Train(int id){
        this.id = id;
        support = new PropertyChangeSupport(this);
        Thread t = new Thread(()->{
            while (true) {
                try {
                    support.firePropertyChange(Integer.toString(id), this.event, tsi.getSensor(id));
                } catch (CommandException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        t.start();
    }
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void setSpeed(int speed) throws CommandException {
        this.speed = speed;
        tsi.setSpeed(id,speed);
    }
    public void stop() throws CommandException {
        tsi.setSpeed(id,0);
    }
    public void resume() throws CommandException{
        tsi.setSpeed(id,speed);
    }
    public void invertDirection() throws CommandException {
        speed = -speed;
        tsi.setSpeed(id,speed);
    }

    public int getId() {
        return id;
    }

    public int getSpeed() {
        return speed;
    }
}
