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

import AIRobot.devices.Module;
import AIRobot.usb.UsbInterface;
import AIRobot.util.Datagram;
import AIRobot.util.Message;
import org.usb4java.DeviceHandle;

import java.util.Arrays;

public class Command extends Message {
    protected int commandNumber = 0;
    private int destModuleAddress = 0;
    static private int messageNumber = 0;
    private int referenceNumber = 0;
    private int packetId = 0;
    private byte[] payloadData;

    public boolean isResponseExpected() {
        return isResponseExpected;
    }

    public void setResponseExpected(boolean responseExpected) {
        isResponseExpected = responseExpected;
    }

    protected boolean isResponseExpected;

    protected Module module;

    public Command() {
        this.destModuleAddress = 0x02;
        this.referenceNumber = 0;
        this.packetId = this.commandNumber;
        //this.payloadData = payloadData;
        this.isResponseExpected = false;
    }

    public int getCommandNumber() {
        return commandNumber;
    }

    @Override
    public byte[] toPayloadByteArray() {
        return new byte[0];
    }

    @Override
    public void fromPayloadByteArray(byte[] rgb) {

    }

    public void setCommandNumber(int commandNumber) {
        this.commandNumber = commandNumber;
    }

    public int getDestModuleAddress() {
        return destModuleAddress;
    }

    public void setDestinationModuleAddress(int destModuleAddress) {
        this.destModuleAddress = destModuleAddress;
    }

    public int getMessageNumber() {
        messageNumber++;
        return messageNumber;
    }

    public void setMessageNumber(int messageNumber) {
        this.messageNumber = messageNumber;
    }

    public int getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(int referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public int getPacketId() {
        return packetId;
    }

    public void setPacketId(int packetId) {
        this.packetId = packetId;
    }

    public byte[] getPayloadData() {
        return payloadData;
    }

    public void setPayloadData(byte[] payloadData) {
        this.payloadData = payloadData;
    }

    public byte[] toPayloadData(){
        return null;
    }

    public byte[] createDatagram() throws UnsupportedCommandException {
        Datagram dg = new Datagram(this);
        //System.out.println("CreateDatagram " + dg.getCommandNumber());
        byte[] b1 = dg.toByteArray();
        return b1;
    }
    private Command waitForResponse(){
        // Set the desired duration for the loop in milliseconds
        long durationInMillis = 1000; // 1 seconds
        // Record the start time
        long startTime = System.currentTimeMillis();
        if(isResponseExpected) {
            while (System.currentTimeMillis() - startTime < durationInMillis) {
                Command cmd = module.getUnfinishedCommand(this.commandNumber);
                if (cmd != null) {
                    //module.removeToUnfinishedCommand(this);
                    return cmd;
                }
            }
        }
        //return null if response is not received in a second
        return null;
    }

    public Command commandExecute(DeviceHandle handle) throws UnsupportedCommandException {
        byte[] b1 = createDatagram();
        //Need to add to unfinished command to handle the response
        if(isResponseExpected){
            module.addToUnfinishedCommands(this);
            //System.out.println("in addtoUnifinishedCommands " + module.getUnfinishCommandLength());
        }
        UsbInterface.write(handle, b1);
        //System.out.println(Arrays.toString(b1));
        return waitForResponse();
    }
}
