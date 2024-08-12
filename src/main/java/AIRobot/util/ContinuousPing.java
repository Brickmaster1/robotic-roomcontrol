package AIRobot.util;

import AIRobot.commands.Command;
import AIRobot.commands.UnsupportedCommandException;
import AIRobot.commands.PingCommand;
import org.usb4java.DeviceHandle;


import static java.lang.Thread.sleep;

public class ContinuousPing implements Runnable{
    private DeviceHandle handle;
    public ContinuousPing(DeviceHandle hndl){
        this.handle = hndl;
    }

    public void run() {
        System.out.println("thread is running...");
        try {
            while(true) {
                ping();
                sleep(2500 - 550);
            }
        } catch (UnsupportedCommandException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void ping() throws UnsupportedCommandException {
        Command p = new PingCommand();
        p.commandExecute(handle);
    }

}