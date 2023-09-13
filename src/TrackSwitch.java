import TSim.CommandException;
import TSim.SensorEvent;
import TSim.TSimInterface;
public class TrackSwitch{
    TSimInterface tsi = TSimInterface.getInstance();
    int xPos;
    int yPos;
    int direction;

    public TrackSwitch(int xPos, int yPos) throws CommandException {
        this.xPos = xPos;
        this.yPos = yPos;
        switchLeft();
    }

    /**
     * Turn this switch left.
     * @throws CommandException
     */
    public void switchLeft() throws CommandException {
        direction = TSimInterface.SWITCH_LEFT;
        tsi.setSwitch(xPos,yPos,direction);
        //System.out.println("Switch left");
    }

    /**
     * Turn this switch right.
     * @throws CommandException
     */
    public void switchRight() throws CommandException{
        direction = TSimInterface.SWITCH_RIGHT;
        tsi.setSwitch(xPos,yPos,direction);
        //System.out.println("Switch right");

    }
}
