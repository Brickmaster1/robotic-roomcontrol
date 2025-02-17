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

package RobotControl.commands;

public class CommandClasses {
    //This is the list of command classes used in this project, you can add more as needed
    public static final int SET_POWER_CMD = 0x100F;
    public static final int GET_POWER_CMD = 0x1010;
    public static final int MODULE_STATUS_CMD = 0x7f03;
    public static final int MOTOR_CHANNEL_ENABLE_CMD = 0x100A;
    public static final int PING_CMD = 0x7f04;
    public static final int QUERY_INTERFACE_CMD = 0x7f07;

    public static final int COMMAND_NUMBER_ACK  = 0x7f01;
}
