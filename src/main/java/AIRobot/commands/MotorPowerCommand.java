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

package AIRobot.commands;

import AIRobot.util.Datagram;

import java.nio.ByteBuffer;

public class MotorPowerCommand extends Command {
    public final int cbPayload = 3;
    private byte motor;
    private double power;


    private byte enabled;


    public final static double apiPowerLast  =  1.0;
    public final static double apiPowerFirst = -1.0;

    public final static int lapiPowerLast  =  32767;
    public final static int lapiPowerFirst = -lapiPowerLast;
    public MotorPowerCommand(double pwr, byte mtr){
        super();
//      Motor Power command
        this.motor = mtr;
        this.power = pwr;
        //SetMotorPowerCommand = 4096 + 15
        this.commandNumber = CommandClasses.SET_POWER_CMD;
    }

    public byte[] toPayloadData(){
        //for setMotorCommand
        double pwr = scale(power, apiPowerFirst, apiPowerLast, lapiPowerFirst, lapiPowerLast);
        ByteBuffer buffer = ByteBuffer.allocate(cbPayload).order(Datagram.LYNX_ENDIAN);
        buffer.put(this.motor);
        //buffer.putShort(this.power);
        int ipwr = (int)pwr;
        short spwr = (short)ipwr;
        buffer.putShort(spwr);
        return buffer.array();
    }

    /**
     * Scale a number in the range of x1 to x2, to the range of y1 to y2
     * @param n number to scale
     * @param x1 lower bound range of n
     * @param x2 upper bound range of n
     * @param y1 lower bound of scale
     * @param y2 upper bound of scale
     * @return a double scaled to a value between y1 and y2, inclusive
     */
    public static double scale(double n, double x1, double x2, double y1, double y2) {
        double a = (y1-y2)/(x1-x2);
        double b = y1 - x1*(y1-y2)/(x1-x2);
        return a*n+b;
    }
}
