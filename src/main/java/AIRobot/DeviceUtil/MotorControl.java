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

package AIRobot.DeviceUtil;

import AIRobot.commands.UnsupportedCommandException;
import AIRobot.devices.Module;
import AIRobot.devices.Motor;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.usb4java.DeviceHandle;



import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import java.util.concurrent.BlockingQueue;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

//import static AIRobot.Driver.camera;
//
//import static AIRobot.Driver.camera;
import static AIRobot.HttpServer.EchoPostHandler.camera;
import static java.lang.Thread.sleep;

public class MotorControl implements Runnable {
    private DeviceHandle handle;
    private Module module;

    private Motor FL0;
    private Motor BL1;
    private Motor BR2;
    private Motor FR3;

    private final ExecutorService executor = Executors.newFixedThreadPool(4); // Adjust the pool size as needed

//    public double getPwr() {
//        return pwr;
//    }
//
//    public void setPwr(double pwr) {
//        this.pwr = pwr;
//    }

    //private double pwr;
    private BlockingQueue bq;

    public MotorControl(DeviceHandle hndl, Module mdl, BlockingQueue bqueue) {
        this.handle = hndl;
        this.module = mdl;
        this.bq = bqueue;
        FL0 = new Motor((byte) 0, handle, module);
        BL1 = new Motor((byte) 1, handle, module);
        BR2 = new Motor((byte) 2, handle, module);
        FR3 = new Motor((byte) 3, handle, module);
    }

    public void run() {
        System.out.println("thread is running...");
        Mat frame = new Mat();
        int frameNumber = 0;
        //Boolean STOP = false;
        try {
            while (true) {
                PowerSetting ps = (PowerSetting) bq.take();
                if (ps == null)
                    continue;
                System.out.println("PowerSetting = " + ps.getDirection());
                if (ps.isSTOP()) {
                    double rot_x = ps.getDirection();
                    System.out.println("IN STOP MotorControl power = " + ps.getDirection() + " power setting AUTO " + ps.isAUTO());
                    robotControlHM(rot_x);
                    //setMotorPowerToZero();
                    if(ps.isTRAINING()) {
                        //if degree of angle is 0, then do not take a snapshot
//                        if(rot_x == 0)
//                            continue;
                        if (camera.read(frame)) {
                            String filename = generateFilename(rot_x);
                            executor.submit(() -> saveImage(rot_x, frame));
                        } else {
                            System.out.println("Error: Unable to capture frame!");
                        }
                    }
                    continue;
                } else if (ps.isAUTO()) {
                    double rot_x = ps.getAutoDirection();
                    System.out.println("IN AUTO MotorControl power = " + ps.getAutoDirection() + " power setting AUTO " + ps.isAUTO());
                    robotControlHM(rot_x);
                    //setMotorPowerToZero();
                    continue;
//                } else if (ps.isTRAINING()) {
//                    double rot_x = ps.getDirection();
//                    if(ps.isSTOP()){
//                        if(rot_x == 0)
//                            continue;
//                        if (camera.read(frame)) {
//                            String filename = generateFilename(rot_x);
//                            executor.submit(() -> saveImage(rot_x, frame));
//                        } else {
//                            System.out.println("Error: Unable to capture frame!");
//                            break;
//                        }
//                    }
                } else {
                    //set motor power to zero if mode is neither stop nor auto
                    System.out.println("in setting to Zero ");
                    setMotorPowerToZero();
                }
                if (ps.isAUTO())
                    System.out.println("MotorControl power = " + ps.getAutoDirection() + " power setting AUTO " + ps.isAUTO());
                else
                    System.out.println("MotorControl power = " + ps.getDirection() + " power setting AUTO " + ps.isAUTO());
                //following code is for training purposes
                //if degree of angle is 0, then do not take a snapshot
            }
        } catch (InterruptedException | UnsupportedCommandException e) {
            throw new RuntimeException(e);
        }
    }

    private void selfDriving() throws UnsupportedCommandException, InterruptedException {
        System.out.println("in Self Driving mode - Yahoooo!!!!!! ");
        setMotorPowerToZero();
        Mat frame = new Mat();
        if (camera.read(frame)) {
            Mat processedFrame = imgPreprocess(frame);
            executor.submit(() -> {
                try {
                    driveAfterProcessingImage(processedFrame);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            System.out.println("Error: Unable to capture frame!");
        }
    }

    private void driveAfterProcessingImage(Mat frame) throws IOException {

        //next line should contain the code to send the image to the model
        // and the model will return an angle of attack in degrees
        //double rot_x = modelInference(frame);
        //robotControlHM(rot_x);
        ByteBuffer modelBuffer = ByteBuffer.wrap(Files.readAllBytes(Paths.get("Model/ai-robot.tflite")));

    }


    private String generateFilename(double rot_x) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
        String timestamp = sdf.format(new Date());
        return String.format("./Images/frame_%s_%.0f.png", timestamp, rot_x);
    }

    private void saveImage(double rot_x, Mat frame) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
        String timestamp = sdf.format(new Date());
        String filename = String.format("./Images/frame_%s_%03.0f.png", timestamp, rot_x);
        Imgcodecs.imwrite(filename, frame);
        System.out.println("Saved: " + filename);
    }

    private void setMotorPowerToZero() throws UnsupportedCommandException, InterruptedException {
        FL0.setPower(0);
        BL1.setPower(0);
        BR2.setPower(0);
        FR3.setPower(0);
    }

    //
    private void robotControlHM(double deg) throws UnsupportedCommandException, InterruptedException {
        //FloatPair fp = Driver.DegreeMap.get((int) deg);
        //float x = (float) (0.5 * fp.getFirst());
        //forward y direction is -ve
        //float y = (float) (0.5 * fp.getSecond());
        double y = 0;
        double x = 0;
        if (deg != 0) {
            y = -0.25 * Math.sin(Math.toRadians(deg));
            x = -0.25 * Math.cos(Math.toRadians(deg));
        }
        double denominator = Math.max(Math.abs(y) + Math.abs(x), 1);
        double frontLeftPower = (y + x) / denominator;
        double backLeftPower = (y + x) / denominator;
        double frontRightPower = (y - x) / denominator;
        double backRightPower = (y - x) / denominator;
        System.out.println("deg " + deg + " FL " + frontLeftPower + " BL " + backLeftPower + " FR " + frontRightPower + " BR " + backRightPower);
        FL0.setPower(frontLeftPower);
        BL1.setPower(backLeftPower);
        FR3.setPower(frontRightPower);
        BR2.setPower(backRightPower);

    }

    public Mat imgPreprocess(Mat image) {
        // Remove top half of the image
        int height = image.rows();
        int startY = height / 2;
        Mat croppedImage = new Mat(image, new Rect(0, startY, image.cols(), height - startY));

        // Convert to YUV color space
        Mat yuvImage = new Mat();
        Imgproc.cvtColor(croppedImage, yuvImage, Imgproc.COLOR_RGB2YUV);

        // Apply Gaussian Blur
        Mat blurredImage = new Mat();
        Imgproc.GaussianBlur(yuvImage, blurredImage, new Size(3, 3), 0);

        // Resize the image to (200, 66)
        Mat resizedImage = new Mat();
        Imgproc.resize(blurredImage, resizedImage, new Size(200, 66));
        Mat processedImage = new Mat();
        // Normalize pixel values to the range [0, 1]
        resizedImage.convertTo(processedImage, CvType.CV_32F, 1.0 / 255.0);

        return processedImage;
    }

    private double robotControl(double rot_x) throws UnsupportedCommandException, InterruptedException {
        double tgp_y = 0;
        double tgp_x = 0;
        //double rot_x = 0;
        double rot_y = 0;
        boolean mac1 = false;
        //if going straight dir is 90 degrees
        //if rot_x = 0
        if (rot_x > -0.1 && rot_x < 0.1) {
            tgp_y = 0.5;
            //tgp_y = 0;
        }

        //setMotorPowerToZero();

        if (rot_x > 0.1) {
            //multiplying by 0.3 to make sure it is the maximum
            if (rot_x >= 0.5) {
                FL0.setPower(rot_x * 0.3);
                BL1.setPower(rot_x * 0.3);
                BR2.setPower(rot_x * -0.2);
                FR3.setPower(rot_x * -0.2);
            } else {
                if (rot_x > 0.3)
                    rot_x = 0.3;
                FL0.setPower(rot_x);
                BL1.setPower(rot_x);
                BR2.setPower(-1 * rot_x * 0.9);
                FR3.setPower(-1 * rot_x * 0.9);
            }
        } else if (rot_x < -0.1) {
            if (rot_x <= -0.5) {
                FL0.setPower(rot_x * 0.2);
                BL1.setPower(rot_x * 0.2);
                BR2.setPower(rot_x * -0.3);
                FR3.setPower(rot_x * -0.3);
            } else {
                if (rot_x < -0.3)
                    rot_x = -0.3;
                FL0.setPower(rot_x);
                BL1.setPower(rot_x);
                BR2.setPower(-1 * rot_x * 0.9);
                FR3.setPower(-1 * rot_x * 0.9);
            }
        }
        if (tgp_y > 0) {
            FL0.setPower(0.25);
            BL1.setPower(0.25);
            BR2.setPower(0.25);
            FR3.setPower(0.25);
        } else if (tgp_y < 0) {
            FL0.setPower(0.25 * -1);
            BL1.setPower(0.25 * -1);
            BR2.setPower(0.25 * -1);
            FR3.setPower(0.25 * -1);
        } else if (tgp_y == 0) {
            FL0.setPower(0);
            BL1.setPower(0);
            BR2.setPower(0);
            FR3.setPower(0);
        }

        return rot_x;
    }
}
