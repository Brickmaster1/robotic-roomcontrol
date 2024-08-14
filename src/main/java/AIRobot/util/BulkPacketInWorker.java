/*
Copyright (c) 2017 Robert Atkinson

All rights reserved.

Derived in part from information in various resources, including FTDI, the
Android Linux implementation, FreeBsc, UsbSerial, and others.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package AIRobot.util;

import AIRobot.usb.UsbInterface;
import org.usb4java.DeviceHandle;

import static AIRobot.usb.UsbInterface.MODEM_STATUS_SIZE;


public class BulkPacketInWorker implements Runnable {
    //This class is meant to read from the USB port and write to a readBuffer
    public static final int ENDPOINT_BUFFER_SIZE = 64;
    UsbInterface usb;
    DeviceHandle handle;
    ReadBuffer readbuffer;
    public BulkPacketInWorker(UsbInterface usbi, ReadBuffer rb){

        usb = usbi;
        readbuffer = rb;
    }


    @Override
    public void run() {
        try{
            do {
                BulkPacketBufferIn packetBuffer = readbuffer.acquireWritableInputBuffer();
                byte[] b = packetBuffer.array();
                b = usb.read(usb.getHandle(), ENDPOINT_BUFFER_SIZE );
                packetBuffer.setByteBuffer(b);
                int cbRead = b.length;
                if (cbRead > 0)
                {
                    // Got some data : pass it along to our (lower-priority) processor
                    packetBuffer.setCurrentLength(cbRead);
                    readbuffer.releaseReadableBuffer(packetBuffer);
                    if (cbRead <= MODEM_STATUS_SIZE)
                    {
                        //noteTrivialInput();
                    }
                }
                else
                {
                    // No data received, so put buffer back into the pool
                    packetBuffer.setCurrentLength(0);       // be consistent, helps debugging
                    readbuffer.releaseWritableInputBuffer(packetBuffer);

                    // Log any errors
                    if (cbRead < 0)
                    {
                        System.out.println("bulkTransfer() error: " +  cbRead);
                    }
                    else
                        System.out.println("bulkTransfer() error: ");
                }
                //System.out.println("bulkPacketInWorker byte[] " + Arrays.toString(b));
            }while (!Thread.interrupted());
                throw new InterruptedException();
        } catch (InterruptedException e) {
            this.readbuffer.purgeInputData();
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
