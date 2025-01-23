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
3. Neither the name of Aditya Mogli nor the names of its contributors
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

import RobotControl.devices.Module;
import RobotControl.usb.UsbInterface;
import RobotControl.util.Datagram;
import RobotControl.util.Message;
import org.usb4java.DeviceHandle;


// Represents a command that can be sent to the USB module
public class Command extends Message {
    protected int commandNumber = 0;
    private int destModuleAddress = 0;
    static private int messageNumber = 0;
    private int referenceNumber = 0;
    private int packetId = 0;
    private byte[] payloadData;
    protected boolean isResponseExpected;
    protected Module module;

    // Creates a command with default values
    public Command() {
        this.destModuleAddress = 0x02; // Default module address
        this.referenceNumber = 0; // Default reference number
        this.packetId = this.commandNumber; // Packet ID set to command number
        this.isResponseExpected = false; // By default, no response is expected
    }

    // Returns the command number
    public int getCommandNumber() {
        return commandNumber;
    }

    // Converts the command to a payload byte array
    @Override
    public byte[] toPayloadByteArray() {
        return new byte[0];
    }

    // Populates the command from a given payload byte array
    @Override
    public void fromPayloadByteArray(byte[] rgb) {

    }

    // Sets the command number
    public void setCommandNumber(int commandNumber) {
        this.commandNumber = commandNumber;
    }

    // Returns the destination module address
    public int getDestModuleAddress() {
        return destModuleAddress;
    }

    // Sets the destination module address
    public void setDestinationModuleAddress(int destModuleAddress) {
        this.destModuleAddress = destModuleAddress;
    }

    // Returns the current message number and increments it
    public int getMessageNumber() {
        messageNumber++;
        return messageNumber;
    }

    // Sets the message number (this method appears unused and might be redundant)
    public void setMessageNumber(int messageNumber) {
        this.messageNumber = messageNumber;
    }

    // Returns the reference number
    public int getReferenceNumber() {
        return referenceNumber;
    }

    // Sets the reference number
    public void setReferenceNumber(int referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    // Returns the packet ID
    public int getPacketId() {
        return packetId;
    }

    // Sets the packet ID
    public void setPacketId(int packetId) {
        this.packetId = packetId;
    }

    // Returns the payload data
    public byte[] getPayloadData() {
        return payloadData;
    }

    // Sets the payload data
    public void setPayloadData(byte[] payloadData) {
        this.payloadData = payloadData;
    }

    // Converts the command to payload data (currently returns null)
    public byte[] toPayloadData(){
        return null;
    }

    // Creates a datagram from the command and returns it as a byte array
    public byte[] createDatagram() throws UnsupportedCommandException {
        Datagram dg = new Datagram(this); // Create a datagram with the command
        byte[] b1 = dg.toByteArray(); // Convert the datagram to a byte array
        return b1;
    }

    // Waits for a response to the command for a set duration
    private Command waitForResponse(){
        // Set the desired duration for the loop in milliseconds
        long durationInMillis = 1000; // 1 second

        // Record the start time
        long startTime = System.currentTimeMillis();

        // If the command expects a response, wait for it
        if(isResponseExpected) {
            while (System.currentTimeMillis() - startTime < durationInMillis) {
                // Check if there's an unfinished command matching the command number
                Command cmd = module.getUnfinishedCommand(this.commandNumber);
                if (cmd != null) {
                    return cmd; // Return the command if a response is found
                }
            }
        }
        // Return null if no response is received within the duration
        return null;
    }

    // Executes the command by sending it via USB and waits for a response if expected
    public Command commandExecute(DeviceHandle handle) throws UnsupportedCommandException {
        byte[] b1 = createDatagram(); // Create a datagram for the command

        // If a response is expected, add the command to the list of unfinished commands
        if(isResponseExpected){
            module.addToUnfinishedCommands(this);
        }

        // Write the datagram to the USB interface
        UsbInterface.write(handle, b1);

        // Wait for a response if expected and return the response command
        return waitForResponse();
    }
}