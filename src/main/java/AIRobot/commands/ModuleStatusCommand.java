package AIRobot.commands;

public class ModuleStatusCommand extends Command {
    private boolean clearStatusAfterResponse;

    public ModuleStatusCommand(){
        super();
        //Get Module Status command
        this.clearStatusAfterResponse = true;
        this.commandNumber = CommandClasses.MODULE_STATUS_CMD;
    }

    public byte[] toPayloadData(){
        //for Get Module status Command
        return new byte[] { this.clearStatusAfterResponse ? (byte)1 : (byte)0 };

    }
}
