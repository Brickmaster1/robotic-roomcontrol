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

package RobotControl.devices;

import RobotControl.commands.*;
import org.usb4java.DeviceHandle;

import static java.lang.Thread.sleep;

public class Motor {
    public byte getMotor() {
        return motor;
    }

    public void setMotor(byte motor) {
        this.motor = motor;
    }

    private byte motor;
    private DeviceHandle handle;

    private double power;    // Power level (+CW, -CCW)
    private Module module;
    public Motor(byte mtr, DeviceHandle hndl, Module module) {
        motor = mtr;
        handle = hndl;
        this.module = module;
    }
    public void setPower(double pwr) throws UnsupportedCommandException, InterruptedException {
        //Motor power command
        Command p1 = new MotorPowerCommand(pwr, (byte)motor);
        p1.commandExecute(handle);
        //sleep(75);
        //Motor channel enable
        Command p2 = new MotorChannelEnable(true, motor);
        p2.commandExecute(handle);
    }

    public int getPower() throws UnsupportedCommandException, InterruptedException {
        Command p1 = new GetMotorPowerCommand(motor, module);
        Command cmd = p1.commandExecute(handle);
        sleep(75);
        MotorPowerResponse rsp = (MotorPowerResponse) cmd.getResponse();
        int pwr = rsp.getPower();
        return pwr;
    }
}
