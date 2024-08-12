package AIRobot.devices;

import AIRobot.commands.*;
import AIRobot.commands.*;
import org.usb4java.DeviceHandle;

import static java.lang.Thread.sleep;

public class Motor {
    public byte getMotor() {
        return motor;
    }

    public void setMotor(byte motor) {
        this.motor = motor;
    }

    private byte motor;
    private DeviceHandle handle;

    private double power;    // Power level (+CW, -CCW)
    private Module module;
    public Motor(byte mtr, DeviceHandle hndl, Module module){
        motor = mtr;
        handle = hndl;
        this.module = module;
    }
    public void setPower(double pwr) throws UnsupportedCommandException, InterruptedException {
        //Motor power command
        Command p1 = new MotorPowerCommand(pwr, (byte)motor);
        p1.commandExecute(handle);
        //sleep(75);
        //Motor channel enable
        Command p2 = new MotorChannelEnable(true, motor);
        p2.commandExecute(handle);
    }

    public int getPower() throws UnsupportedCommandException, InterruptedException {
        Command p1 = new GetMotorPowerCommand(motor, module);
        Command cmd = p1.commandExecute(handle);
        sleep(75);
        MotorPowerResponse rsp = (MotorPowerResponse) cmd.getResponse();
        int pwr = rsp.getPower();
        return pwr;
    }
}
