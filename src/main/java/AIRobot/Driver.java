package AIRobot;

import AIRobot.devices.Module;
import AIRobot.devices.Motor;
import AIRobot.commands.UnsupportedCommandException;
import AIRobot.usb.UsbInterface;
import AIRobot.util.*;
import org.opencv.videoio.VideoCapture;
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

public class Driver {
    //hashmap to convert degree to x and y values
    public static HashMap<Integer, FloatPair> DegreeMap = new HashMap<>();
    //this is to initialize the camera using openCV
//    public static VideoCapture camera;
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

//    public MotorControl getMotorControl() {
//        return motorControl;
//    }
//
//    public void setMotorControl(MotorControl motorControl) {
//        this.motorControl = motorControl;
//    }

    //private MotorControl motorControl;

    private DeviceHandle handle;
    private Module module;
    public void initialization(DeviceHandle handle) throws InterruptedException {
        ByteBuffer buffer = null;
        //resetDevice
        UsbInterface.controlTransfer(handle, (byte) 0x00, 0, 0, buffer);
//        Multi m1=new Multi(handle);
//        Thread t1 =new Thread(m1);   // Using the constructor Thread(Runnable r)
//        t1.start();
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

//    private void initializeCamera(){
//        // Create a VideoCapture object to capture video from the default camera (usually 0)
//        camera = new VideoCapture(0);
//
//        if (!camera.isOpened()) {
//            System.out.println("Error: Camera is not available!");
//            return;
//        }
//    }

    public void initializeDirectionHashmap(){
        // Add some entries to the map
        //Hashmap that converts degrees to x and y values for the 4 motors
        DegreeMap.put(0, new FloatPair(0f, 0f));
        DegreeMap.put(1, new FloatPair(0.9773692821f, 0f));
        DegreeMap.put(2, new FloatPair(1f, -0.05098039f));
        DegreeMap.put(3, new FloatPair(1f, -0.0603921528f));
        DegreeMap.put(4, new FloatPair(1f, -0.0772548976f));
        DegreeMap.put(5, new FloatPair(1f, -0.09019607f));
        DegreeMap.put(6, new FloatPair(1f, -0.1187674993f));
        DegreeMap.put(7, new FloatPair(1f, -0.13115468f));
        DegreeMap.put(8, new FloatPair(1f, -0.1515570865f));
        DegreeMap.put(9, new FloatPair(1f, -0.163921556f));
        DegreeMap.put(10, new FloatPair(1f, -0.17647058f));
        DegreeMap.put(11, new FloatPair(1f, -0.203921555f));
        DegreeMap.put(12, new FloatPair(1f, -0.2180995338f));
        DegreeMap.put(13, new FloatPair(1f, -0.242352926f));
        DegreeMap.put(14, new FloatPair(1f, -0.2582632914f));
        DegreeMap.put(15, new FloatPair(1f, -0.281568616f));
        DegreeMap.put(16, new FloatPair(1f, -0.29411763f));
        DegreeMap.put(17, new FloatPair(1f, -0.315686255f));
        DegreeMap.put(18, new FloatPair(0.9994397757f, -0.3338935357f));
        DegreeMap.put(19, new FloatPair(1f, -0.35686272f));
        DegreeMap.put(20, new FloatPair(1f, -0.3701357254f));
        DegreeMap.put(21, new FloatPair(1f, -0.3986927817f));
        DegreeMap.put(22, new FloatPair(0.9962848284f, -0.4158926484f));
        DegreeMap.put(23, new FloatPair(0.996862746f, -0.433725464f));
        DegreeMap.put(24, new FloatPair(0.9955182029f, -0.45434171f));
        DegreeMap.put(25, new FloatPair(0.996405225f, -0.4728757858f));
        DegreeMap.put(26, new FloatPair(0.9921568667f, -0.4875816667f));
        DegreeMap.put(27, new FloatPair(0.990196075f, -0.51568629f));
        DegreeMap.put(28, new FloatPair(0.9764706f, -0.52941178f));
        DegreeMap.put(29, new FloatPair(0.9764706f, -0.5529412f));
        DegreeMap.put(30, new FloatPair(0.92941177f, -0.5441176688f));
        DegreeMap.put(31, new FloatPair(0.938823546f, -0.58274514f));
        DegreeMap.put(32, new FloatPair(0.9164705955f, -0.582352958f));
        DegreeMap.put(33, new FloatPair(0.899607858f, -0.600000012f));
        DegreeMap.put(34, new FloatPair(0.8445632818f, -0.5800356727f));
        DegreeMap.put(35, new FloatPair(0.8872549063f, -0.6294118038f));
        DegreeMap.put(36, new FloatPair(0.8682353f, -0.639215708f));
        DegreeMap.put(37, new FloatPair(0.8151960856f, -0.6245098244f));
        DegreeMap.put(38, new FloatPair(0.7901960825f, -0.6254902f));
        DegreeMap.put(39, new FloatPair(0.8100218f, -0.6644880322f));
        DegreeMap.put(40, new FloatPair(0.827451f, -0.703921585f));
        DegreeMap.put(41, new FloatPair(0.7967914618f, -0.7062388918f));
        DegreeMap.put(42, new FloatPair(0.792941184f, -0.730196098f));
        DegreeMap.put(43, new FloatPair(0.747450994f, -0.70823531f));
        DegreeMap.put(44, new FloatPair(0.766274524f, -0.749019626f));
        DegreeMap.put(45, new FloatPair(0.7416122139f, -0.7503268122f));
        DegreeMap.put(46, new FloatPair(0.6915032933f, -0.7333333433f));
        DegreeMap.put(47, new FloatPair(0.6990196375f, -0.7617647213f));
        DegreeMap.put(48, new FloatPair(0.6810457667f, -0.76732028f));
        DegreeMap.put(49, new FloatPair(0.6737255f, -0.7882353f));
        DegreeMap.put(50, new FloatPair(0.6784313817f, -0.8222222333f));
        DegreeMap.put(51, new FloatPair(0.6627451f, -0.8352941f));
        DegreeMap.put(52, new FloatPair(0.6104575389f, -0.7995642811f));
        DegreeMap.put(53, new FloatPair(0.6339869544f, -0.8527233244f));
        DegreeMap.put(54, new FloatPair(0.6268907814f, -0.8722689171f));
        DegreeMap.put(55, new FloatPair(0.6058823725f, -0.88823529f));
        DegreeMap.put(56, new FloatPair(0.6068627575f, -0.9068627513f));
        DegreeMap.put(57, new FloatPair(0.5590414278f, -0.8745098111f));
        DegreeMap.put(58, new FloatPair(0.52156865f, -0.8627451f));
        DegreeMap.put(59, new FloatPair(0.5686274775f, -0.9686274525f));
        DegreeMap.put(60, new FloatPair(0.56274511f, -0.984313725f));
        DegreeMap.put(61, new FloatPair(0.502941195f, -0.9264705838f));
        DegreeMap.put(62, new FloatPair(0.507843165f, -0.9725490175f));
        DegreeMap.put(63, new FloatPair(0.4887700818f, -0.9793226364f));
        DegreeMap.put(64, new FloatPair(0.4773109564f, -0.9955182014f));
        DegreeMap.put(65, new FloatPair(0.4483660467f, -0.99477124f));
        DegreeMap.put(66, new FloatPair(0.43977595f, -0.9977591029f));
        DegreeMap.put(67, new FloatPair(0.4140056371f, -0.9988795514f));
        DegreeMap.put(68, new FloatPair(0.394509842f, -1f));
        DegreeMap.put(69, new FloatPair(0.36993468f, -1f));
        DegreeMap.put(70, new FloatPair(0.350588276f, -1f));
        DegreeMap.put(71, new FloatPair(0.3315904522f, -1f));
        DegreeMap.put(72, new FloatPair(0.312941216f, -1f));
        DegreeMap.put(73, new FloatPair(0.2993464533f, -1f));
        DegreeMap.put(74, new FloatPair(0.282352985f, -1f));
        DegreeMap.put(75, new FloatPair(0.26274514f, -1f));
        DegreeMap.put(76, new FloatPair(0.2352941675f, -1f));
        DegreeMap.put(77, new FloatPair(0.2225490675f, -1f));
        DegreeMap.put(78, new FloatPair(0.2052288033f, -1f));
        DegreeMap.put(79, new FloatPair(0.1872549475f, -1f));
        DegreeMap.put(80, new FloatPair(0.1686275f, -1f));
        DegreeMap.put(81, new FloatPair(0.149803974f, -1f));
        DegreeMap.put(82, new FloatPair(0.13725495f, -1f));
        DegreeMap.put(83, new FloatPair(0.115686325f, -1f));
        DegreeMap.put(84, new FloatPair(0.09803927f, -1f));
        DegreeMap.put(85, new FloatPair(0.0776471144f, -1f));
        DegreeMap.put(86, new FloatPair(0.058823586f, -1f));
        DegreeMap.put(87, new FloatPair(0.05098045f, -1f));
        DegreeMap.put(88, new FloatPair(0f, -1f));
        DegreeMap.put(89, new FloatPair(0f, -1f));
        DegreeMap.put(90, new FloatPair(0f, -1f));
        DegreeMap.put(91, new FloatPair(0f, -1f));
        DegreeMap.put(92, new FloatPair(0f, -1f));
        DegreeMap.put(93, new FloatPair(-0.058823526f, -1f));
        DegreeMap.put(94, new FloatPair(-0.076470584f, -1f));
        DegreeMap.put(95, new FloatPair(-0.09019607f, -1f));
        DegreeMap.put(96, new FloatPair(-0.1117647013f, -1f));
        DegreeMap.put(97, new FloatPair(-0.12941176f, -1f));
        DegreeMap.put(98, new FloatPair(-0.15032679f, -1f));
        DegreeMap.put(99, new FloatPair(-0.166666655f, -1f));
        DegreeMap.put(100, new FloatPair(-0.19215685f, -1f));
        DegreeMap.put(101, new FloatPair(-0.206274494f, -1f));
        DegreeMap.put(102, new FloatPair(-0.221568615f, -1f));
        DegreeMap.put(103, new FloatPair(-0.2333333225f, -1f));
        DegreeMap.put(104, new FloatPair(-0.26274508f, -1f));
        DegreeMap.put(105, new FloatPair(-0.28366012f, -1f));
        DegreeMap.put(106, new FloatPair(-0.29411763f, -1f));
        DegreeMap.put(107, new FloatPair(-0.31633985f, -1f));
        DegreeMap.put(108, new FloatPair(-0.3315903911f, -1f));
        DegreeMap.put(109, new FloatPair(-0.3516339733f, -1f));
        DegreeMap.put(110, new FloatPair(-0.376470565f, -1f));
        DegreeMap.put(111, new FloatPair(-0.3939869f, -1f));
        DegreeMap.put(112, new FloatPair(-0.41699344f, -1f));
        DegreeMap.put(113, new FloatPair(-0.4333333108f, -0.999346405f));
        DegreeMap.put(114, new FloatPair(-0.4509803667f, -0.99738562f));
        DegreeMap.put(115, new FloatPair(-0.4723707364f, -0.9971479473f));
        DegreeMap.put(116, new FloatPair(-0.4921568375f, -0.99411765f));
        DegreeMap.put(117, new FloatPair(-0.51111112f, -0.9908496767f));
        DegreeMap.put(118, new FloatPair(-0.5215686567f, -0.9581699333f));
        DegreeMap.put(119, new FloatPair(-0.5271708914f, -0.9372549071f));
        DegreeMap.put(120, new FloatPair(-0.5484593971f, -0.9361344629f));
        DegreeMap.put(121, new FloatPair(-0.5607843433f, -0.915032685f));
        DegreeMap.put(122, new FloatPair(-0.59346407f, -0.9267973883f));
        DegreeMap.put(123, new FloatPair(-0.598431394f, -0.904313732f));
        DegreeMap.put(124, new FloatPair(-0.619607865f, -0.90326798f));
        DegreeMap.put(125, new FloatPair(-0.6190476429f, -0.86890758f));
        DegreeMap.put(126, new FloatPair(-0.6366013333f, -0.8653595f));
        DegreeMap.put(127, new FloatPair(-0.65751636f, -0.8605664467f));
        DegreeMap.put(128, new FloatPair(-0.68333334f, -0.85784315f));
        DegreeMap.put(129, new FloatPair(-0.686274515f, -0.8339869283f));
        DegreeMap.put(130, new FloatPair(-0.7071895567f, -0.8313725667f));
        DegreeMap.put(131, new FloatPair(-0.7154061843f, -0.80840336f));
        DegreeMap.put(132, new FloatPair(-0.7392157f, -0.801960795f));
        DegreeMap.put(133, new FloatPair(-0.7549019688f, -0.7980392263f));
        DegreeMap.put(134, new FloatPair(-0.7751634f, -0.7882353133f));
        DegreeMap.put(135, new FloatPair(-0.7803921683f, -0.76993465f));
        DegreeMap.put(136, new FloatPair(-0.768627455f, -0.72941178f));
        DegreeMap.put(137, new FloatPair(-0.789803928f, -0.73019609f));
        DegreeMap.put(138, new FloatPair(-0.818039226f, -0.722352964f));
        DegreeMap.put(139, new FloatPair(-0.8326797483f, -0.7137255183f));
        DegreeMap.put(140, new FloatPair(-0.8535947833f, -0.70588238f));
        DegreeMap.put(141, new FloatPair(-0.8567320347f, -0.6805228827f));
        DegreeMap.put(142, new FloatPair(-0.8692810667f, -0.6732026333f));
        DegreeMap.put(143, new FloatPair(-0.883921581f, -0.653333348f));
        DegreeMap.put(144, new FloatPair(-0.8913165329f, -0.6425770571f));
        DegreeMap.put(145, new FloatPair(-0.9023965167f, -0.6209150567f));
        DegreeMap.put(146, new FloatPair(-0.912156866f, -0.607843146f));
        DegreeMap.put(147, new FloatPair(-0.9189542533f, -0.5816993667f));
        DegreeMap.put(148, new FloatPair(-0.9450980417f, -0.5803921833f));
        DegreeMap.put(149, new FloatPair(-0.9647058833f, -0.5673202867f));
        DegreeMap.put(150, new FloatPair(-0.9882353f, -0.560784335f));
        DegreeMap.put(151, new FloatPair(-0.96862745f, -0.5276688733f));
        DegreeMap.put(152, new FloatPair(-0.96862745f, -0.50588235f));
        DegreeMap.put(153, new FloatPair(-0.98117648f, -0.485490172f));
        DegreeMap.put(154, new FloatPair(-0.9887955214f, -0.4733893229f));
        DegreeMap.put(155, new FloatPair(-0.9942958927f, -0.4552584409f));
        DegreeMap.put(156, new FloatPair(-1f, -0.4352940933f));
        DegreeMap.put(157, new FloatPair(-0.99607843f, -0.41960782f));
        DegreeMap.put(158, new FloatPair(-1f, -0.398431344f));
        DegreeMap.put(159, new FloatPair(-1f, -0.36862743f));
        DegreeMap.put(160, new FloatPair(-1f, -0.35196077f));
        DegreeMap.put(161, new FloatPair(-1f, -0.3313725275f));
        DegreeMap.put(162, new FloatPair(-1f, -0.3142856943f));
        DegreeMap.put(163, new FloatPair(-1f, -0.29673201f));
        DegreeMap.put(164, new FloatPair(-1f, -0.27843136f));
        DegreeMap.put(165, new FloatPair(-1f, -0.25490195f));
        DegreeMap.put(166, new FloatPair(-1f, -0.24215685f));
        DegreeMap.put(167, new FloatPair(-1f, -0.21568626f));
        DegreeMap.put(168, new FloatPair(-1f, -0.203921555f));
        DegreeMap.put(169, new FloatPair(-1f, -0.18235293f));
        DegreeMap.put(170, new FloatPair(-1f, -0.1607843f));
        DegreeMap.put(171, new FloatPair(-1f, -0.14509803f));
        DegreeMap.put(172, new FloatPair(-1f, -0.12941176f));
        DegreeMap.put(173, new FloatPair(-1f, -0.11633986f));
        DegreeMap.put(174, new FloatPair(-1f, -0.09243696714f));
        DegreeMap.put(175, new FloatPair(-1f, -0.0760784272f));
        DegreeMap.put(176, new FloatPair(-1f, -0.0647058765f));
        DegreeMap.put(177, new FloatPair(-1f, -0.0647058765f));
        DegreeMap.put(178, new FloatPair(-1f, -0.0647058765f));
        DegreeMap.put(179, new FloatPair(-1f, -0.0647058765f));
    }
    public void driverFunction(BlockingQueue bq) throws UnsupportedCommandException, InterruptedException {
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
        //Initialize the hashmap
        initializeDirectionHashmap();
        //Initialize the camera with opencv
        //initializeCamera();
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
//        Motor rr = new Motor((byte)1, handle, module);
//        rr.setPower((short)-1);
//        int pwr1 = rr.getPower();
//        System.out.println("Power " + pwr1 + " motor " + rr.getMotor());
//        sleep(1000);
//        rr.setPower((short)0);
//        Motor rl = new Motor((byte)2, handle, module);
//        rl.setPower((short)-1);
//        sleep(1000);
//        rl.setPower((short)0);
//        Motor fl = new Motor((byte)3, handle, module);
//        fl.setPower((short)-1);
//        sleep(1000);
//        fl.setPower((short)0);
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
    public static void main(String args[]) throws UnsupportedCommandException, InterruptedException {
        //Driver driver = new Driver();
        //driver.driverFunction();
    }
}
