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

package AIRobot;

import AIRobot.HttpServer.EchoGetHandler;
import AIRobot.HttpServer.EchoPostHandler;
import AIRobot.HttpServer.RootHandler;
import AIRobot.commands.UnsupportedCommandException;
import com.sun.net.httpserver.HttpServer;
import nu.pattern.OpenCV;
import org.opencv.core.Core;
import org.opencv.osgi.OpenCVInterface;
import org.opencv.osgi.OpenCVNativeLoader;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;


import static java.lang.System.exit;

//DriverServer class is the main class that starts the HTTP server and the driver function
public class DriverServer {

    //The port number to listen for incoming requests.
    int port = 9000;

    static {
        System.out.println("Loading library: " + Core.NATIVE_LIBRARY_NAME);
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        OpenCV.loadShared();

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
    public static void main(String args[]) throws IOException, UnsupportedCommandException, InterruptedException {
        //Queue size of 100
        BlockingQueue bq = new ArrayBlockingQueue<>(100);
        DriverServer ds = new DriverServer();
        ds.startHTTPServer(bq);
        Driver driver = new Driver();
        try {
            driver.driverFunction(bq);
        }catch (Exception e){
            System.err.println("Error " + e.getMessage());
        } finally {
            exit(1);
        }
        //eph.setMotorControl(driver.getMotorControl());
    }
}
