/*
Copyright (c) 2024 Aditya Mogli
Copyright (C) 2011 Klaus Reimer, k@ailis.de
Copyright (C) 2013 Luca Longinotti, l@longi.li

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

package AIRobot;

import AIRobot.DeviceUtil.MotorControl;
import AIRobot.devices.Module;
import AIRobot.devices.Motor;
import AIRobot.commands.UnsupportedCommandException;
import AIRobot.usb.UsbInterface;
import AIRobot.util.*;

import org.usb4java.DeviceHandle;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

//import static AIRobot.usb.UsbInterface.controlTransfer;
//import static AIRobot.usb.UsbInterface.pingInitialContact;
import static java.lang.System.exit;
import static java.lang.Thread.sleep;

//driver class for controlling the robot, initializes the device and starts the threads
public class Driver {
    //hashmap to convert degree to x and y values
    public static HashMap<Integer, FloatPair> DegreeMap = new HashMap<>();
    //this is to initialize the camera using openCV
//    public static VideoCapture camera;
    //Vendor and product id of REV EXPANSION HUB
    private static final short VENDOR_ID = 0x403;
    private static final short PRODUCT_ID = 0x6015;


    //Interface of the REV EXPANSION HUB
    private static final byte INTERFACE = 0x0;

    //Input endpoint of the REV EXPANSION HUB
    public static final byte IN_ENDPOINT = (byte) 0x81;


    //Output endpoint of the REV EXPANSION HUB
    public static final byte OUT_ENDPOINT = 0x2;

    /** The communication timeout in milliseconds. */
    public static final int TIMEOUT = Integer.MAX_VALUE;

    public DeviceHandle getHandle() {
        return handle;
    }

    public void setHandle(DeviceHandle handle) {
        this.handle = handle;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }


    //private MotorControl motorControl;

    private DeviceHandle handle;
    private Module module;

    //initialize the device
    public void initialization(DeviceHandle handle) throws InterruptedException {
        ByteBuffer buffer = null;
        //resetDevice
        UsbInterface.controlTransfer(handle, (byte) 0x00, 0, 0, buffer);
        sleep(75);
        //setConfig
        UsbInterface.controlTransfer(handle, (byte) 0x01, 0, 0, buffer);
        sleep(75);
        //setAddress
        UsbInterface.controlTransfer(handle, (byte) 0x02, 0, 0, buffer);
        sleep(75);
        //setBaudrate
        UsbInterface.controlTransfer(handle, (byte) 0x03, 16390, 1, buffer);
        sleep(75);
        //setChar
        UsbInterface.controlTransfer(handle, (byte) 0x04, 8, 1, buffer);
        sleep(75);
        //setLatency
        UsbInterface.controlTransfer(handle, (byte)0x09, 1, 1, buffer);
        sleep(75);
        //setBit
        UsbInterface.controlTransfer(handle, (byte)0x0b, 8243, 1, buffer);
        sleep(75);
        //setBit1
        UsbInterface.controlTransfer(handle, (byte)0x0b, 8242, 1, buffer);
        sleep(75);
        //setBit2
        UsbInterface.controlTransfer(handle, (byte)0x0b, 8243, 1, buffer);
        sleep(75);
    }


    //main function that initializes the device and starts the threads
    public void driverFunction(BlockingQueue bq) throws UnsupportedCommandException, InterruptedException {
      //initialize the device
      int result = LibUsb.init(null);
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to initialize libusb", result);
        }

        // Open test device (REV EXPANSION HUB)
        this.handle = LibUsb.openDeviceWithVidPid(null, VENDOR_ID,
                PRODUCT_ID);
        if (handle == null)
        {
            System.err.println("Test device not found.");
            exit(1);
        }

        //Need to do this because of RPI, as it claims the expansion hub inteface
        if (LibUsb.kernelDriverActive(handle, INTERFACE) == 1) {
            result = LibUsb.detachKernelDriver(handle, INTERFACE);
            if (result != LibUsb.SUCCESS) {
                throw new LibUsbException("Unable to detach kernel driver", result);
            }
        }

        // Claim the ADB interface
        result = LibUsb.claimInterface(handle, INTERFACE);
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to claim interface", result);
        }

        //Initialize the Robot Controller
        initialization(handle);

        //Creating a module to represent the hub
        this.module = new Module();
        //Start reading from the USB port
        UsbInterface usbi = new UsbInterface(handle);

        ReadBuffer rb = new ReadBuffer();

        //Pinging the Robot controller
        UsbInterface.pingInitialContact(handle);
        sleep(75);
        //Read from the usb port
        BulkPacketInWorker bpiw = new BulkPacketInWorker(usbi, rb);
        Thread t1 =new Thread(bpiw);   // Using the constructor Thread(Runnable r)
        t1.start();

        ContinuousPing cp = new ContinuousPing(handle);
        Thread t2 =new Thread(cp);
        t2.start();

        //ProcessBulkPacketIn this reads from availablebuffer and puts in readbuffer
        ReadBufferWorker rbw = new ReadBufferWorker(rb);
        Thread t3 =new Thread(rbw);   // Using the constructor Thread(Runnable r)
        t3.start();

        //Reads from the read buffers and converts to datagram
        ReadBufferToDatagram rbtd = new ReadBufferToDatagram(handle, rb, module);
        Thread t4 =new Thread(rbtd);   // Using the constructor Thread(Runnable r)
        t4.start();

        //Power the motor
        Motor fr = new Motor((byte)0, handle, module);
        fr.setPower(0.5);
        int pwr = fr.getPower();
        //System.out.println("Power " + pwr + " motor " + fr.getMotor());
        sleep(200);
        fr.setPower((short)0);
        MotorControl motorControl = new MotorControl(handle, module, bq);
        Thread t5 =new Thread(motorControl);
        t5.start();

        // Release only after all threads are complete
        CountDownLatch latch = new CountDownLatch(2);
            latch.await();

        result = LibUsb.releaseInterface(handle, INTERFACE);
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to release interface", result);
        }

        // Reattach the kernel driver if it was detached
        if (LibUsb.kernelDriverActive(handle, INTERFACE) == 0) {
            result = LibUsb.attachKernelDriver(handle, INTERFACE);
            if (result != LibUsb.SUCCESS) {
                System.err.println("Warning: Unable to reattach kernel driver");
            }
        }
        // Close the device
        LibUsb.close(handle);

        // Deinitialize the libusb context
        LibUsb.exit(null);
    }
}