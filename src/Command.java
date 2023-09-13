import TSim.CommandException;

public interface Command {
    public void execute(int id, int status) throws CommandException;
    
}
