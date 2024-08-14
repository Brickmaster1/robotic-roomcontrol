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
