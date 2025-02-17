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

import RobotControl.util.Datagram;
import RobotControl.util.Response;

import java.nio.ByteBuffer;

public class MotorPowerResponse extends Response {
    //----------------------------------------------------------------------------------------------
    // State
    //----------------------------------------------------------------------------------------------

    private static final int cbPayload = 2;

    private short power = 0;    // signed!

    //----------------------------------------------------------------------------------------------
    // Construction
    //----------------------------------------------------------------------------------------------


    //----------------------------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------------------------

    public int getPower()
    {
        return this.power;
    }

    //----------------------------------------------------------------------------------------------
    // Operations
    //----------------------------------------------------------------------------------------------


    @Override
    public int getCommandNumber() {
        return 0;
    }

    public byte[] toPayloadByteArray()
    {
        ByteBuffer buffer = ByteBuffer.allocate(cbPayload).order(Datagram.LYNX_ENDIAN);
        buffer.putShort(this.power);
        return buffer.array();
    }

    public void fromPayloadByteArray(byte[] rgb)
    {
        ByteBuffer buffer = ByteBuffer.wrap(rgb).order(Datagram.LYNX_ENDIAN);
        this.power = buffer.getShort();
    }
}
