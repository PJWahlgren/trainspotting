import TSim.CommandException;
import TSim.SensorEvent;

public interface Command {
    public void execute(SensorEvent evt) throws CommandException;
    
}
