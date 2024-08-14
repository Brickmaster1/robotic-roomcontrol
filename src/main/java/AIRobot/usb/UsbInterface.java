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

package AIRobot.usb;

import AIRobot.commands.*;
import AIRobot.commands.*;
import AIRobot.util.ElapsedTime;
import org.usb4java.BufferUtils;
import org.usb4java.DeviceHandle;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;

import static java.lang.Thread.sleep;

public class UsbInterface {

    //Vendor and product id of REV EXPANSION HUB
    private static final short VENDOR_ID = 0x403;
    private static final short PRODUCT_ID = 0x6015;


    /** The ADB interface number of the Samsung Galaxy Nexus. */
    //private static final byte INTERFACE = 1;
    private static final byte INTERFACE = 0x0;

    /** The ADB input endpoint of the Samsung Galaxy Nexus. */
    //private static final byte IN_ENDPOINT = (byte) 0x83;
    public static final byte IN_ENDPOINT = (byte) 0x81;


    /** The ADB output endpoint of the Samsung Galaxy Nexus. */
    //private static final byte OUT_ENDPOINT = 0x03;
    public static final byte OUT_ENDPOINT = 0x2;

    /** The communication timeout in milliseconds. */
    //    private static final int TIMEOUT = 5000;
    public static final int TIMEOUT = Integer.MAX_VALUE;

    public static final byte MODEM_STATUS_SIZE = 2;

    private DeviceHandle handle;
    public UsbInterface(DeviceHandle hdl){
        handle = hdl;
    }

    public DeviceHandle getHandle(){
        return handle;
    }

    public static void pingInitialContact(final DeviceHandle handle) throws UnsupportedCommandException, InterruptedException {
        ElapsedTime duration = new ElapsedTime();
        double msInitialContact = 5;
        while (duration.milliseconds() < msInitialContact) {
            Command p = new PingCommand();
            byte[] b1 = p.createDatagram();
            write(handle, b1);
            //System.out.println(Arrays.toString(b1));
            //sleep(2);
        }
        sleep(75);
        Command q = new QueryInterfaceCommand();
        byte[] b2 = q.createDatagram();
        write(handle, b2);
        sleep(75);
        ElapsedTime duration1 = new ElapsedTime();
        //while (duration1.milliseconds() < 50) {
        Command p = new PingCommand();
        byte[] b1 = p.createDatagram();
        write(handle, b1);
        sleep(75);
        //}
        Command f = new FtdiResetCommand(true);
        byte[] b3 = f.createDatagram();
        write(handle, b3);
    }

    /**
     * Writes some data to the device.
     *
     * @param handle
     *            The device handle.
     * @param data
     *            The data to send to the device.
     */
    public static void write(DeviceHandle handle, byte[] data)
    {
        ByteBuffer buffer = BufferUtils.allocateByteBuffer(data.length);
        buffer.put(data);
        IntBuffer transferred = BufferUtils.allocateIntBuffer();
        int result = LibUsb.bulkTransfer(handle, OUT_ENDPOINT, buffer,
                transferred, TIMEOUT);
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to send data", result);
        }
        //System.out.println(transferred.get() + " bytes sent to device");
    }

    /**
     * Reads some data from the device.
     *
     * @param handle
     *            The device handle.
     *            The number of bytes to read from the device.
     * @return The read data.
     */
    public static byte[] read(DeviceHandle handle, int size)
    {
        ByteBuffer buffer = BufferUtils.allocateByteBuffer(size).order(
                ByteOrder.LITTLE_ENDIAN);
        IntBuffer transferred = BufferUtils.allocateIntBuffer();
        int result = LibUsb.bulkTransfer(handle, IN_ENDPOINT, buffer,
                transferred, TIMEOUT);
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to read data", result);
        }
        //System.out.println(transferred.get() + " bytes read from device");
        //byte[] b = new byte[buffer.capacity()];
        byte[] b = new byte[transferred.get()];
        //byte[] b = buffer.array();
        buffer.get(b);
        return b;
    }
    public static void controlTransfer(DeviceHandle handle, byte req, int wVal, int idx, ByteBuffer buffer) {
        int result;
        ByteBuffer data = BufferUtils.allocateByteBuffer(0);
        result = LibUsb.controlTransfer(handle, (byte) (LibUsb.REQUEST_TYPE_VENDOR | LibUsb.ENDPOINT_OUT), req, (short) wVal,
                (short) idx, data, 0);
        if (result == LibUsb.SUCCESS) {
            System.out.println("USB device control transferred successfully " + result + " " + req + " " + wVal);
        } else {
            throw new LibUsbException("Unable to control transfer USB device", result);
        }
    }
}
