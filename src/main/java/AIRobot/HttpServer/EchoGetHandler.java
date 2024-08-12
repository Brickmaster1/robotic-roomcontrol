package AIRobot.HttpServer;

import AIRobot.devices.Module;
import AIRobot.util.MotorControl;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.usb4java.DeviceHandle;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static AIRobot.HttpServer.EchoPostHandler.parseQuery;
import static AIRobot.HttpServer.EchoPostHandler.psg;

public class EchoGetHandler implements HttpHandler {

    private DeviceHandle handle;
    private Module module;

    public MotorControl getMotorControl() {
        return motorControl;
    }

    public void setMotorControl(MotorControl motorControl) {
        this.motorControl = motorControl;
    }

    private MotorControl motorControl;

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

    @Override

    public void handle(HttpExchange he) throws IOException {
        // parse request
        Map<String, Object> parameters = new HashMap<String, Object>();
        URI requestedUri = he.getRequestURI();
        String query = requestedUri.getRawQuery();
        parseQuery(query, parameters);
        // send response
        String response = "";
        for (String key : parameters.keySet()) {
            if(parameters.get(key).toString().equals("AUTO")) {
                //response += psg.isTRAINING();
            }
            response += key + " = " + parameters.get(key) + "\n";
        }

        he.sendResponseHeaders(200, response.length());
        OutputStream os = he.getResponseBody();
        os.write(response.toString().getBytes());
        os.close();
    }
}