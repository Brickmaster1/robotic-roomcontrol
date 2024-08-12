package AIRobot.commands;

import AIRobot.util.Datagram;

import java.nio.ByteBuffer;



public class MotorPowerCommand extends Command {
    public final int cbPayload = 3;
    private byte motor;
    private double power;


    private byte enabled;


    public final static double apiPowerLast  =  1.0;
    public final static double apiPowerFirst = -1.0;

    public final static int lapiPowerLast  =  32767;
    public final static int lapiPowerFirst = -lapiPowerLast;
    public MotorPowerCommand(double pwr, byte mtr){
        super();
//      Motor Power command
        this.motor = mtr;
        this.power = pwr;
        //SetMotorPowerCommand = 4096 + 15
        this.commandNumber = CommandClasses.SET_POWER_CMD;
    }

    public byte[] toPayloadData(){
        //for setMotorCommand
        double pwr = scale(power, apiPowerFirst, apiPowerLast, lapiPowerFirst, lapiPowerLast);
        ByteBuffer buffer = ByteBuffer.allocate(cbPayload).order(Datagram.LYNX_ENDIAN);
        buffer.put(this.motor);
        //buffer.putShort(this.power);
        int ipwr = (int)pwr;
        short spwr = (short)ipwr;
        buffer.putShort(spwr);
        return buffer.array();
    }

    /**
     * Scale a number in the range of x1 to x2, to the range of y1 to y2
     * @param n number to scale
     * @param x1 lower bound range of n
     * @param x2 upper bound range of n
     * @param y1 lower bound of scale
     * @param y2 upper bound of scale
     * @return a double scaled to a value between y1 and y2, inclusive
     */
    public static double scale(double n, double x1, double x2, double y1, double y2) {
        double a = (y1-y2)/(x1-x2);
        double b = y1 - x1*(y1-y2)/(x1-x2);
        return a*n+b;
    }
}
