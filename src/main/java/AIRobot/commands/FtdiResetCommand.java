package AIRobot.commands;

import AIRobot.util.Datagram;

import java.nio.ByteBuffer;

public class FtdiResetCommand extends Command {

    public final static int cbPayload = 1;

    private byte enabled;

    public FtdiResetCommand(){
        super();
        //FtdiResetCommand = 4096 + 49
        this.commandNumber = 0x1031;
    }

    public FtdiResetCommand( boolean enabled)
    {
        this();
        this.enabled = enabled ? (byte)1 : (byte)0;
    }

//    public byte[] toPayloadData(){
//        //for ping
//        return new byte[0];
//    }

//    public byte[] toPayloadByteArray()
//    {
//        ByteBuffer buffer = ByteBuffer.allocate(cbPayload).order(LynxDatagram.LYNX_ENDIAN);
//        buffer.put(this.enabled);
//        return buffer.array();
//    }
    public byte[] toPayloadData()
    {
        ByteBuffer buffer = ByteBuffer.allocate(cbPayload).order(Datagram.LYNX_ENDIAN);
        buffer.put(this.enabled);
        return buffer.array();
    }
}
