/*
Copyright (c) 2024 Aditya Mogli

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list
   of conditions, and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice, this list
   of conditions, and the following disclaimer in the documentation and/or
   other materials provided with the distribution.
3. Neither the name of [Your Name or Your Organization] nor the names of its contributors
   may be used to endorse or promote products derived from this software without specific
   prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS LICENSE.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

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
