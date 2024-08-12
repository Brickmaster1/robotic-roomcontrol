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

public class DriverServer {

    int port = 9000;

    static {
        System.out.println("Loading library: " + Core.NATIVE_LIBRARY_NAME);
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        OpenCV.loadShared();

    }

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
