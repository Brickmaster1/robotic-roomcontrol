package AIRobot.commands;

import AIRobot.util.Datagram;
import AIRobot.util.Response;

import java.nio.ByteBuffer;

public class MotorPowerResponse extends Response {
    //----------------------------------------------------------------------------------------------
    // State
    //----------------------------------------------------------------------------------------------

    private static final int cbPayload = 2;

    private short power = 0;    // signed!

    //----------------------------------------------------------------------------------------------
    // Construction
    //----------------------------------------------------------------------------------------------


    //----------------------------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------------------------

    public int getPower()
    {
        return this.power;
    }

    //----------------------------------------------------------------------------------------------
    // Operations
    //----------------------------------------------------------------------------------------------


    @Override
    public int getCommandNumber() {
        return 0;
    }

    public byte[] toPayloadByteArray()
    {
        ByteBuffer buffer = ByteBuffer.allocate(cbPayload).order(Datagram.LYNX_ENDIAN);
        buffer.putShort(this.power);
        return buffer.array();
    }

    public void fromPayloadByteArray(byte[] rgb)
    {
        ByteBuffer buffer = ByteBuffer.wrap(rgb).order(Datagram.LYNX_ENDIAN);
        this.power = buffer.getShort();
    }
}
