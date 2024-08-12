package AIRobot.commands;

import AIRobot.util.Datagram;


import java.nio.ByteBuffer;

public class MotorChannelEnable extends Command{

    public final static int cbPayload = 2;
    private byte motor;
    private byte enabled;

    public MotorChannelEnable(boolean enbl, byte mtr){
        super();
        //SetMotorChannaleEnableCommand = 4096 + 10
        this.commandNumber = CommandClasses.MOTOR_CHANNEL_ENABLE_CMD;
        this.motor = (byte)mtr;
        this.enabled = enbl ? (byte)1 : (byte)0;
    }

    public byte[] toPayloadData(){
        //for setMotorCommand
        ByteBuffer buffer = ByteBuffer.allocate(cbPayload).order(Datagram.LYNX_ENDIAN);
        buffer.put(this.motor);
        buffer.put(this.enabled);
        return buffer.array();
    }

}
