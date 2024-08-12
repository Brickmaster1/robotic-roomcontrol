package AIRobot.commands;

public class PingCommand extends Command{

    public PingCommand(){
        super();
        //ping command
        this.commandNumber = CommandClasses.PING_CMD;
    }

    public byte[] toPayloadData(){
        //for ping
        return new byte[0];
    }
}
