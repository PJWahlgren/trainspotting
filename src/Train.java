import TSim.CommandException;
import TSim.SensorEvent;
import TSim.TSimInterface;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Train{
    enum Direction{
        UP,
        DOWN
    }
    TSimInterface tsi = TSimInterface.getInstance();
    private final PropertyChangeSupport support;
    private final int id;
    private int speed;
    private Direction direction;
    private SensorEvent event;
    public Train(int id){
        this.id = id;
        if (id == 1)
            direction = Direction.DOWN;
        else
            direction = Direction.UP;
        support = new PropertyChangeSupport(this);
        Thread t = new Thread(()->{
            while (true) {
                try {
                    SensorEvent ev = tsi.getSensor(id);
                    support.firePropertyChange(Integer.toString(id), this.event, ev);
                    event = ev;
                } catch (CommandException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        t.start();
    }

    /**
     * A way to add observers to each train
     * @param pcl Observer, in this case the input expects TrainSensor.
     */
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void setSpeed(int speed) throws CommandException {
        this.speed = speed;
        tsi.setSpeed(id,speed);
    }

    /**
     * This sets the train speed to 0. Note that it does not change the speed attribute!
     * @throws CommandException
     */
    public void stop() throws CommandException {
        tsi.setSpeed(id,0);
        System.out.println("Train " + id + " stopping");
    }

    /**
     * Resumes the train at whatever speed it was set before it was stopped.
     * @throws CommandException
     */
    public void resume() throws CommandException{
        tsi.setSpeed(id,speed);
        System.out.println("Train " + id + " resuming");
    }

    /**
     * Inverts the speed of the train and therefore the direction.
     * @throws CommandException If the speed was inverted before it was stopped.
     */
    public void invertDirection() throws CommandException {
        speed = -speed;
        tsi.setSpeed(id,speed);
        if (direction == Direction.UP)
            direction = Direction.DOWN;
        else
            direction = Direction.UP;
    }

    public SensorEvent getEvent() {
        return event;
    }

    public int getId() {
        return id;
    }
    /**
     * Returns the speed of the train.
     * @return int
     */
    public int getSpeed() {
        return speed;
    }
    /**
     * Get directions
     * @return Train.Direction
     */
    public Direction getDirection() {
        return direction;
    }
    /**
     * True if it's going down.
     * @return boolean
     */
    public boolean isGoingDown(){
        return direction == Direction.DOWN;
    }
    /**
     * True if it's going up.
     * @return boolean
     */
    public boolean isGoingUp(){
        return direction == Direction.UP;
    }
}
