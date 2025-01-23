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

package RobotControl;

import RobotControl.HttpServer.EchoGetHandler;
import RobotControl.HttpServer.EchoPostHandler;
import RobotControl.HttpServer.RootHandler;
import RobotControl.cli.CliOptions;
import RobotControl.commands.UnsupportedCommandException;
import RobotControl.debug.Debugging;
import com.sun.net.httpserver.HttpServer;
//import nu.pattern.OpenCV;
//import org.opencv.core.Core;
import org.usb4java.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;

import static java.lang.System.exit;

//DriverServer class is the main class that starts the HTTP server and the driver function
public class DriverServer {
    DriverServer(int port) {
        this.port = port;
    }

    //The port number to listen for incoming requests.
    private int port;

    static {
//        System.out.println("Loading library: " + Core.NATIVE_LIBRARY_NAME);
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        OpenCV.loadShared();

    }

    //starts the http server and maps handlers to specific urls
    public void startHTTPServer(BlockingQueue bq) throws IOException, UnsupportedCommandException, InterruptedException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        System.out.println("HTTP Server started at " + port);
        server.createContext("/",new RootHandler());
        //server.createContext("/echoHeader",new EchoHeaderHandler());
        EchoGetHandler egh = new EchoGetHandler();
        server.createContext("/echoGet", egh);
        EchoPostHandler eph = new EchoPostHandler(bq);
        server.createContext("/echoPost", eph);
        //100 connections to manage multiple threads
        server.setExecutor(Executors.newFixedThreadPool(100));
        server.start();
    }

    //main function that starts the HTTP server and the driver function
    public static void main(String args[]) throws Exception {
        CliOptions options = new CliOptions(args);
        if(options.debug != null && options.debug.getValue()) {
            Map<String, String> debugInfo = Debugging.getDebuggingInfo();
            if(debugInfo.isEmpty()) {
                System.out.println("Remote debugging environment not detected.");
            } else {
                Debugging.printDebuggingInfo();
//                String address = debugInfo.get("address");
//                if(!address.isEmpty()) {
//                    Debugging.waitForRemoteDebugger(Integer.parseInt(address.substring(address.indexOf(':') + 1)));
//                }
//                System.out.println("Debugger is attached. Proceeding with program execution...");
            }
        }

        //Queue size of 100
        BlockingQueue bq = new ArrayBlockingQueue<>(100);
        DriverServer ds;
        ds = new DriverServer(options.port.getValue());
        ds.startHTTPServer(bq);
        Driver driver = new Driver();

        Context context = new Context();
        int result = LibUsb.init(context);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException("Unable to initialize LibUsb", result);
        }

        DeviceList list = new DeviceList();
        try {
            result = LibUsb.getDeviceList(context, list);
            if (result < 0) {
                throw new LibUsbException("Unable to get device list", result);
            }

            System.out.println("--------------------------------------");
            for (Device device : list) {
                DeviceDescriptor descriptor = new DeviceDescriptor();
                result = LibUsb.getDeviceDescriptor(device, descriptor);
                if (result != LibUsb.SUCCESS) {
                    throw new LibUsbException("Unable to read device descriptor", result);
                }

                System.out.println("Device: " + descriptor.idVendor() + ":" + descriptor.idProduct());
                System.out.println("Manufacturer: " + getDeviceString(device, descriptor.iManufacturer()));
                System.out.println("Product: " + getDeviceString(device, descriptor.iProduct()));
                System.out.println("Serial Number: " + getDeviceString(device, descriptor.iSerialNumber()));
                System.out.println("--------------------------------------");
            }

            //driver.driverFunction(bq);
        } catch (Exception e){
            System.err.println("Error " + e.getMessage());
        } finally {
            LibUsb.freeDeviceList(list, true);
            exit(1);
        }
        //eph.setMotorControl(driver.getMotorControl());
    }

    private static String getDeviceString(Device device, byte index) {
        DeviceHandle handle = new DeviceHandle();
        try {
            int result = LibUsb.open(device, handle);
            if (result != LibUsb.SUCCESS) {
                return "Error: " + LibUsb.errorName(result);
            }

            ByteBuffer buffer = ByteBuffer.allocateDirect(256);
            int length = LibUsb.getStringDescriptor(handle, index, (short) 0, buffer);
            if (length < 0) {
                return "Error: " + LibUsb.errorName(length);
            }

            byte[] bytes = new byte[length];
            buffer.get(bytes, 0, length);
            return new String(bytes, 0, length);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
