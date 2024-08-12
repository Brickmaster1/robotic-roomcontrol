package AIRobot.devices;

import AIRobot.commands.Command;

import java.util.concurrent.ConcurrentHashMap;

public class Module {
    //protected final ConcurrentHashMap<Integer,MessageClassAndCtor> commandClasses;
    protected ConcurrentHashMap<Integer, Command> unfinishedCommands;

//    protected static Map<Integer,MessageClassAndCtor> standardMessages = new HashMap<Integer, MessageClassAndCtor>();    // command number -> class
//    protected static Map<Class<? extends Command>,MessageClassAndCtor>  responseClasses = new HashMap<Class<? extends Command>, MessageClassAndCtor>();

//    protected static class MessageClassAndCtor
//    {
//        public Class<? extends LynxMessage> clazz;
//        public Constructor<? extends LynxMessage> ctor;

//        public void assignCtor() throws NoSuchMethodException
//        {
//            try {
//                this.ctor = this.clazz.getConstructor(LynxModule.class);
//            }
//            catch (NoSuchMethodException ignored)
//            {
//                try {
//                    this.ctor = this.clazz.getConstructor(LynxModuleIntf.class);
//                }
//                catch (NoSuchMethodException e)
//                {
//                    this.ctor = null;
//                }
//            }
//        }
//    }
    public Module(){
        this.unfinishedCommands = new ConcurrentHashMap<Integer, Command>();
    }

//    public Module(ConcurrentHashMap<Integer, MessageClassAndCtor> commandClasses, ConcurrentHashMap<Integer, Response> unfinishedClasses, ConcurrentHashMap<Integer, Response> unfinishedCommands) {
//        this.unfinishedCommands = unfinishedCommands;
//        this.commandClasses = commandClasses;
//
//    }

    public void addToUnfinishedCommands(Command cmd){
        unfinishedCommands.put(cmd.getCommandNumber(), cmd);
    }
    public void removeToUnfinishedCommand(Command cmd){
        unfinishedCommands.remove(cmd.getCommandNumber());
    }
    public Command getUnfinishedCommand(int cmdnbr){
        Command cmd = unfinishedCommands.get(cmdnbr);
        //System.out.println("Cmd number  in module = " + cmdnbr);
        if(cmd == null){
            //System.out.println("cmd is null for cmd number " + cmdnbr);
        }
        return cmd;
    }

    public int getUnfinishCommandLength(){
        return unfinishedCommands.size();
    }
    public void createCommands(){

    }
}
