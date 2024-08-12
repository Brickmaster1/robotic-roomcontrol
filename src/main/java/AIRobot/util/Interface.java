package AIRobot.util;

import AIRobot.commands.Command;

import java.util.HashMap;
import java.util.Map;

public class Interface {
    public static final int ERRONEOUS_COMMAND_NUMBER = 0;

    public static final String dekaInterfaceName = "DEKA";
    private String                                                  interfaceName;
    private Integer                                                 baseCommandNumber;
    private Class<? extends Command>[]                 commands;
    private Map<Class<? extends Command>, Integer> commandIndices;
    private Map<Class<? extends Response>, Integer>    responseIndices;
    private boolean                                                 wasNacked;
    public Interface(String interfaceName, Class<? extends Command>... commands)
    {
        this.baseCommandNumber = ERRONEOUS_COMMAND_NUMBER;
        this.interfaceName     = interfaceName;
        this.commands          = commands;
        this.commandIndices    = new HashMap<Class<? extends Command>, Integer>();
        //this.responseIndices   = new HashMap<Class<? extends Response>, Integer>();
        this.wasNacked         = false;
//        for (int i = 0; i < this.commands.length; i++)
//        {
//            Class<? extends Command> commandClass = this.commands[i];
//
//            if (commandClass == null)
//                continue;   // filler
//
//            // Remember the index of this command
//            this.commandIndices.put(commandClass, i);
//            try {
//                // Find the corresponding response
//                Class<? extends Response> responseClass = (Class<? extends Response>)Command.getResponseClass(commandClass);
//
//                // Remember the same index for the response
//                this.responseIndices.put(responseClass, i);
//
//                // Note which response goes with which command
//                //LynxModule.correlateResponse(commandClass, responseClass);
//            }
//            catch (Exception ignore)
//            {
//                // Probably doesn't have a response
//            }
//        }
    }
}
