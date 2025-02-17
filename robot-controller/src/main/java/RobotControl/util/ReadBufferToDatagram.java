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

package RobotControl.util;

import RobotControl.commands.CommandClasses;
import RobotControl.devices.Module;
import RobotControl.commands.Command;
import RobotControl.commands.MotorPowerResponse;
import RobotControl.usb.TimeWindow;
//import com.sun.istack.internal.Nullable;
import org.usb4java.DeviceHandle;

public class ReadBufferToDatagram implements Runnable {
    public final byte[] frameBytes = new byte[]{0x44, 0x4b};
    private boolean stopRequested = false;
    private final byte[] scratch = new byte[2];
    private final byte[] prefix = new byte[4];
    private boolean isSynchronized = false;
    private ReadBuffer readbuffer;
    DeviceHandle handle;
    private Module module;

    public ReadBufferToDatagram(DeviceHandle hdl, ReadBuffer rb, Module module) {
        readbuffer = rb;
        handle = hdl;
        isSynchronized = false;
        this.module = module;
    }

    private byte readSingleByte(DeviceHandle handle, byte[] buffer) throws InterruptedException, Exception {
        byte[] b = readIncomingBytes(buffer, 1, null, handle);
        //System.out.println(Arrays.toString(b));
        return b[0];
    }

    private byte[] readIncomingBytes(byte[] buffer, int cbToRead, TimeWindow timeWindow, DeviceHandle handle) throws InterruptedException, Exception {
        // We specify an essentially infinite read timeout waiting for the next packet to come in
        long msReadTimeout = Integer.MAX_VALUE;

        //buffer = UsbInterface.read(handle, cbToRead);
        int cbread = readbuffer.readBulkInData(buffer, 0, cbToRead, msReadTimeout, null);
        //cbRead.get(buffer);
        if (cbread == cbToRead) {
            return buffer;
            // We got all the data we came for. Just return gracefully
        } else if (cbread == 0) {
            // Couldn't read the data in the time allotted. Because we allot
            // an infinite amount of time, that means that an interrupt occurred, but
            // one that was eaten. Re-signal the interrupt.
            throw new InterruptedException("interrupt during robotUsbDevice.read()");
        } else {
            throw new Exception("error in readIncomingBytes");
        }
    }

    public static byte[] concatenateByteArrays(byte[] first, byte[] second) {
        byte[] concatenated = new byte[first.length + second.length];
        System.arraycopy(first, 0, concatenated, 0, first.length);
        System.arraycopy(second, 0, concatenated, first.length, second.length);
        return concatenated;
    }

    private Datagram PollForIncomingData(DeviceHandle handle) {
        int count = 0;
        stopRequested = false;
        while (!stopRequested) {
            try {
                if (!isSynchronized) {
                    if (readSingleByte(handle, scratch) != frameBytes[0]) {
                        continue;
                    }
                    if (readSingleByte(handle, scratch) != frameBytes[1]) {
                        continue;
                    }
                    readIncomingBytes(scratch, 2, null, handle);
                    System.arraycopy(Datagram.frameBytes, 0, prefix, 0, 2);
                    System.arraycopy(scratch, 0, prefix, 2, 2);
                    // We think we are in sync. Next time, just try the faster path
                    isSynchronized = true;
                } else {
                    // Read the prefix in fewer read calls for better performance
                    readIncomingBytes(prefix, 4, null, handle);

                    // If we're not in sync, then go back to the slow way
                    if (!Datagram.beginsWithFraming(prefix)) {
                        isSynchronized = false;
                        continue;
                    }
                }
                // Compute the packet length, allocate a buffer for the suffix, and read same
                int cbPacketLength = TypeConversion.unsignedShortToInt(TypeConversion.byteArrayToShort(prefix, 2, Datagram.LYNX_ENDIAN));
                int cbSuffix = cbPacketLength - Datagram.cbFrameBytesAndPacketLength;
                byte[] suffix = new byte[cbSuffix];
                TimeWindow payloadTimeWindow = new TimeWindow();
                readIncomingBytes(suffix, cbSuffix, payloadTimeWindow, handle);

                // Parse the message structure of the datagram
                byte[] completePacket = concatenateByteArrays(prefix, suffix);
                Datagram datagram = new Datagram();
                datagram.setPayloadTimeWindow(payloadTimeWindow);
                datagram.fromByteArray(completePacket);
                if (datagram.isChecksumValid()) {
                    return datagram;
                } else {
                    // Invalid checksum. The Lynx specification indicates we are simply to ignore.
                    return null;
                }
            } catch (RobotCoreException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }


    public void onIncomingDatagramReceived(Datagram datagram)
    // We've received a datagram from our module.
    {
        // Reify the incoming command. First, what kind of command is that guy?
        int cmdnbr = datagram.getCommandNumber();
        //System.out.println("Command number received " + cmdnbr);
        Command cmd = module.getUnfinishedCommand(cmdnbr);
        if (cmd==null){
            //System.out.println("Returning null for command " + cmdnbr + " len " + module.getUnfinishCommandLength());
            return;
        }
        if(cmd.isResponseExpected()){
            switch(cmdnbr) {
                case CommandClasses.GET_POWER_CMD:
                    //System.out.println("in ReadBufferToDatagram entered command 4112 " + cmdnbr);
                    Response response = new MotorPowerResponse();
                    response.fromPayloadByteArray(datagram.getPayloadData());
                    cmd.onResponseReceived(response);
                    module.removeToUnfinishedCommand(cmd);
                    break;
            }
        }
    }
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            //System.out.println("In polling for incoming data");
            Datagram datagram = PollForIncomingData(handle);
            if (datagram != null) {
                onIncomingDatagramReceived(datagram);
            }
            else{
                //System.out.println("Datagram is null");
            }
        }
    }
}
