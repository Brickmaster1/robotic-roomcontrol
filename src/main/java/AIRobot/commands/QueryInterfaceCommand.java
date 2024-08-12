package AIRobot.commands;

import java.nio.charset.Charset;

public class QueryInterfaceCommand extends Command {
    public QueryInterfaceCommand(){
        super();
//        query interface command
        this.commandNumber = CommandClasses.QUERY_INTERFACE_CMD;
    }

    public byte[] toPayloadData(){
        //for QueryInteface
        String intf = "DEKA\0";
        return intf.getBytes(Charset.forName("UTF-8"));
    }
}
