package AIRobot.util;

public class ReadBufferWorker implements Runnable{
    private ReadBuffer readBuffer;
    public ReadBufferWorker(ReadBuffer rb){
        readBuffer = rb;
    }
    @Override
    public void run() {
        try {
            do {
                BulkPacketBufferIn bpin = readBuffer.acquireReadableInputBuffer();
                readBuffer.processBulkPacketIn(bpin);
                readBuffer.releaseWritableInputBuffer(bpin);
            }while (!Thread.interrupted());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
