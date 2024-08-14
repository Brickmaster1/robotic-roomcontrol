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

package AIRobot.HttpServer;

import AIRobot.devices.Module;
import AIRobot.DeviceUtil.PowerSetting;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.opencv.videoio.VideoCapture;
import org.usb4java.DeviceHandle;

import java.io.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import static java.lang.Thread.sleep;

public class EchoPostHandler implements HttpHandler {
    static public PowerSetting psg = new PowerSetting(true, false, false);
    private DeviceHandle handle;
    private Module module;
    private BlockingQueue bq;

    public static VideoCapture camera = null;
    public EchoPostHandler(BlockingQueue bqueue) {
        this.bq = bqueue;

    }

    public DeviceHandle getHandle() {
        return handle;
    }

    public void setHandle(DeviceHandle handle) {
        this.handle = handle;
    }

    public Module getModule() {
        return module;
    }

    private int deg = 125;

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


    @Override

    public void handle(HttpExchange he) throws IOException {
        // parse request
        Map<String, Object> parameters = new HashMap<String, Object>();
        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        //parseQuery(query, parameters);
        //deg = deg - 10;
        try {
            //double rot = (int)parameters.get("degree");
            PowerSetting ps = this.getCommand(query);
            if (ps != null)
                 bq.put(ps);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // send response
        String response = "";
        for (String key : parameters.keySet()) {
            response += key + " = " + parameters.get(key) + "\n";
        }
        he.sendResponseHeaders(200, response.length());
        OutputStream os = he.getResponseBody();
        os.write(response.toString().getBytes());
        os.close();
    }

    public double convertDegreesToRot(int deg){
        double rot = 0;
//            int deg = (int)parameters.get("degree");
        if (deg == 90) {
            rot = 0;
        } else if (deg < 90) {
            //rot = -1 * (1.0 - (deg / 90.0));
            rot = (1.0 - (deg / 90.0));
        } else {
            //rot = (1.0 - ((180 - deg) / 90.0));
            rot = -1*(1.0 - ((180 - deg) / 90.0));
        }
        return rot;
    }

    public PowerSetting getCommand(String query){
        PowerSetting ps = new PowerSetting();
        //2 Modes
        // STOP = Joystick mode
        // AUTO = Self driving mode
        //psg is a global static variable to maintain previous state
        ps.setSTOP(psg.isSTOP());
        ps.setAUTO(psg.isAUTO());
        ps.setTRAINING(psg.isTRAINING());
        String pairs[] = query.split("[=]");
        if (pairs != null) {
            if (pairs[0].equals("STOP")) {
                ps.setSTOP(true);
                ps.setAUTO(false);
                psg.setSTOP(true);
                psg.setAUTO(false);
                ps.setTRAINING(false);
                psg.setTRAINING(false);
                return ps;
            }else if (pairs[0].equals("AUTO")) {
                ps.setAUTO(true);
                ps.setSTOP(false);
                psg.setAUTO(true);
                psg.setSTOP(false);
                ps.setTRAINING(false);
                psg.setTRAINING(false);
                return ps;
            } else if (pairs[0].equals("TRAINING")) {
                initializeCamera();
                ps.setAUTO(false);
                ps.setSTOP(true);
                psg.setAUTO(false);
                psg.setSTOP(true);
                ps.setTRAINING(true);
                psg.setTRAINING(true);
                return ps;
            }
            else if(pairs[0].equals("dir") && ps.isSTOP()) {
                //ps.setDirection(convertDegreesToRot(Integer.parseInt(pairs[1])));
                ps.setDirection(Integer.parseInt(pairs[1]));
                System.out.println("in echoPost setDirection  Integer.parseInt(pairs[1]) " + Integer.parseInt(pairs[1]));
                return ps;
            }
            else if(pairs[0].equals("ato") && ps.isAUTO()) {
                //ps.setDirection(convertDegreesToRot(Integer.parseInt(pairs[1])));
                ps.setAutoDirection(Integer.parseInt(pairs[1]));
                return ps;
            }
            else{
                //Default to zero degrees to stop
                //ps.setDirection(0);
                //return ps;
                //return null, so it is not added to the queue
                return null;
            }
        }
        //0 degrees is to stop, so default to that, previously it was 90
        //ps.setDirection(0);
        //returning null so it is not added to the queueu
        return null;
    }

    private void initializeCamera(){
        // Create a VideoCapture object to capture video from the default camera (usually 0)
        if(camera == null) {
            camera = new VideoCapture(0);
        }

        if (!camera.isOpened()) {
            System.out.println("Error: Camera is not available!");
            return;
        }
    }

    public static void parseQuery(String query, Map<String,
            Object> parameters) throws UnsupportedEncodingException {

        if (query != null) {
            String pairs[] = query.split("[&]");
            for (String pair : pairs) {
                String param[] = pair.split("[=]");
                String key = null;
                String value = null;
                if (param.length > 0) {
                    key = URLDecoder.decode(param[0],
                            System.getProperty("file.encoding"));
                }
                if (param.length > 1) {
                    value = URLDecoder.decode(param[1],
                            System.getProperty("file.encoding"));
                }
                if (parameters.containsKey(key)) {
                    Object obj = parameters.get(key);
                    if (obj instanceof List<?>) {
                        List<String> values = (List<String>) obj;
                        values.add(value);

                    } else if (obj instanceof String) {
                        List<String> values = new ArrayList<String>();
                        values.add((String) obj);
                        values.add(value);
                        parameters.put(key, values);
                    }
                } else {
                    parameters.put(key, value);
                }
            }
        }
    }
}
