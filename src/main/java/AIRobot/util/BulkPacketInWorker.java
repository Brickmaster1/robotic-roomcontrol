package AIRobot.util;

import AIRobot.usb.UsbInterface;
import org.usb4java.DeviceHandle;

import static AIRobot.usb.UsbInterface.MODEM_STATUS_SIZE;


public class BulkPacketInWorker implements Runnable {
    //This class is meant to read from the USB port and write to a readBuffer
    public static final int ENDPOINT_BUFFER_SIZE = 64;
    UsbInterface usb;
    DeviceHandle handle;
    ReadBuffer readbuffer;
    public BulkPacketInWorker(UsbInterface usbi, ReadBuffer rb){

        usb = usbi;
        readbuffer = rb;
    }


    @Override
    public void run() {
        try{
            do {
                BulkPacketBufferIn packetBuffer = readbuffer.acquireWritableInputBuffer();
                byte[] b = packetBuffer.array();
                b = usb.read(usb.getHandle(), ENDPOINT_BUFFER_SIZE );
                packetBuffer.setByteBuffer(b);
                int cbRead = b.length;
                if (cbRead > 0)
                {
                    // Got some data : pass it along to our (lower-priority) processor
                    packetBuffer.setCurrentLength(cbRead);
                    readbuffer.releaseReadableBuffer(packetBuffer);
                    if (cbRead <= MODEM_STATUS_SIZE)
                    {
                        //noteTrivialInput();
                    }
                }
                else
                {
                    // No data received, so put buffer back into the pool
                    packetBuffer.setCurrentLength(0);       // be consistent, helps debugging
                    readbuffer.releaseWritableInputBuffer(packetBuffer);

                    // Log any errors
                    if (cbRead < 0)
                    {
                        System.out.println("bulkTransfer() error: " +  cbRead);
                    }
                    else
                        System.out.println("bulkTransfer() error: ");
                }
                //System.out.println("bulkPacketInWorker byte[] " + Arrays.toString(b));
            }while (!Thread.interrupted());
                throw new InterruptedException();
        } catch (InterruptedException e) {
            this.readbuffer.purgeInputData();
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
