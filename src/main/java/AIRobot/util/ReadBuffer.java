/*
Copyright (c) 2017 Robert Atkinson

All rights reserved.

Derived in part from information in various resources, including FTDI, the
Android Linux implementation, FreeBsc, UsbSerial, and others.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package AIRobot.util;

import AIRobot.usb.TimeWindow;
//import com.sun.istack.internal.Nullable;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static AIRobot.usb.UsbInterface.MODEM_STATUS_SIZE;
import static AIRobot.usb.UsbInterface.read;


public class ReadBuffer {
    //This Buffer is meant to write data read from USB port

    private boolean open;

    private Deadline mReadDeadline;
    public static final int READ_BUFFER_SIZE = 10;

    public static final int RC_DEVICE_CLOSED            = -1;

    public static final int mEndpointMaxPacketSize = 64;
    private static int cbReadBufferMax         = 16384;
    private final ArrayList<BulkPacketBufferIn> readBuffer;

    private final    CircularByteBuffer                  mCircularBuffer;
    private final    MarkedItemQueue                     mMarkedItemQueue;

    private final    ArrayList<BulkPacketBufferIn>       mAvailableInBuffers;

    private final    int                                 mAvailableInBuffersCapacity;

    private final FtDeviceInfo deviceInfo;
    private volatile Thread                              mReadBulkInDataThread;
    private int msBulkInReadTimeout     = 5000;


    public ReadBuffer() {
        readBuffer = new ArrayList<BulkPacketBufferIn>();
        //this.mCircularBuffer            = new CircularByteBuffer(this.mEndpointMaxPacketSize * 5 /* a guess */, this.mParams.getMaxReadBufferSize());
        this.mCircularBuffer            = new CircularByteBuffer(this.mEndpointMaxPacketSize * 5 /* a guess */, cbReadBufferMax);
        this.mMarkedItemQueue           = new MarkedItemQueue();
        this.mAvailableInBuffers = new ArrayList<BulkPacketBufferIn>();
        //Need to update this
        mAvailableInBuffersCapacity = 10;
        this.deviceInfo = new FtDeviceInfo();
        this.mReadBulkInDataThread = null;
        this.mReadDeadline              = new Deadline(this.msBulkInReadTimeout, TimeUnit.MILLISECONDS);
        this.open = true;
    }

    boolean isOpen(){
        return open;
    }
    void close(){
        open = false;
    }

    public void add(BulkPacketBufferIn buffer) throws Exception {
        if (isSpaceAvailable())
            readBuffer.add(buffer);
        else{
            throw new Exception("No Buffer available");
        }
    }
    public BulkPacketBufferIn remove(){
        //LIFO
        int len = readBuffer.size();
        if(readBuffer.isEmpty())
            return null;
        return readBuffer.remove(len);
    }

    public final static int bb_copy(final ByteBuffer from, final ByteBuffer to) {
        final int len = from.limit();
        return bb_copy(from, 0, to, 0, len);
    }

    public final static int bb_copy(final ByteBuffer from, final int offset1,
                                 final ByteBuffer to, final int offset2, final int len) {
        System.arraycopy(from.array(), offset1, to.array(), offset2, len);
        to.limit(offset2 + len);
        return len;
    }

    private boolean isSpaceAvailable() {
        if (readBuffer.size() < READ_BUFFER_SIZE) {
            return true;
        } else
            return false;
    }
    public void processBulkPacketIn(BulkPacketBufferIn bb) throws InterruptedException {
        if (isOpen() && bb.currentLength > 0) {
            final int cbBuffer = bb.getCurrentLength();
            if (cbBuffer < MODEM_STATUS_SIZE) {
                return;
            }
            synchronized (mCircularBuffer)
            {
                for (;;)
                {
                    //System.out.println("ReadBuffer ProcessBulkPacketin cbBuffer " + cbBuffer);
                    int cbFree   = mCircularBuffer.remainingCapacity();
                    int cbNeeded = cbBuffer - MODEM_STATUS_SIZE;
                    if (cbNeeded <= cbFree)
                        break;

                    // Wait until the state of the buffer changes
                    mCircularBuffer.wait();

                    // Get out of Dodge if things have closed while we were waiting
                    if (!isOpen()) return;
                }
            }
            this.extractReadData(bb);
        }
    }



    public void releaseReadableBuffer(BulkPacketBufferIn packetBuffer)
    {
        synchronized (readBuffer)
        {
            readBuffer.add(packetBuffer); // adds at end
            readBuffer.notifyAll();
        }
    }

    public void purgeInputData()
    {
        synchronized (mCircularBuffer)
        {
            synchronized (readBuffer)
            {
                readBuffer.clear();
            }
            mCircularBuffer.clear();
            mMarkedItemQueue.clear();
        }
    }

    private void extractReadData(BulkPacketBufferIn packetBuffer) throws InterruptedException
    {
        final int cbBuffer = packetBuffer.getCurrentLength();
        if (cbBuffer > 0)
        {
//            verifyInvariants("->extractReadData");
            try {
                short   signalEvents = 0;
                short   signalLineEvents = 0;
                boolean signalRxChar = false;

                final int packetCount = cbBuffer / this.mEndpointMaxPacketSize + (cbBuffer % this.mEndpointMaxPacketSize > 0 ? 1 : 0);
                // RobotLog.dd(TAG, "packetCount=%d cb=%d", packetCount, cbBuffer);

                ByteBuffer byteBuffer = packetBuffer.getByteBuffer();
                int cbExtracted = 0;
                for (int iPacket = 0; iPacket < packetCount; ++iPacket)
                {
                    int ibFirst;
                    int ibMax;
                    if (iPacket == packetCount - 1)
                    {
                        // Last packet : use modem status at start of packet
                        ibFirst = iPacket * this.mEndpointMaxPacketSize;
                        ibMax = cbBuffer;
                        setBufferBounds(byteBuffer, ibFirst, ibMax);
                        //
                        byte b0 = byteBuffer.get(); // Assert.assertTrue(b0 == 0x01, "b0==0x%02x", b0);
                        signalEvents = (short) (this.deviceInfo.modemStatus ^ (short) (b0 & 0xF0));
                        this.deviceInfo.modemStatus = (short) (b0 & 0xF0); // this sign extends, which probably isn't what's desired
                        //
                        byte b1 = byteBuffer.get(); // Assert.assertTrue(b1==0x60 || b1==0x00, "b1==0x%02x", b1);
                        this.deviceInfo.lineStatus = (short) (b1 & 0xFF);  // this sign extends, which probably isn't what's desired
                        //
                        ibFirst += MODEM_STATUS_SIZE;
                        //
                        if (byteBuffer.hasRemaining())
                        {
                            signalLineEvents = (short) (this.deviceInfo.lineStatus & 0x1E);
                        }
                        else
                        {
                            signalLineEvents = 0;
                        }
                    }
                    else
                    {
                        // Not the last packet : ignore modem status at start of packet
                        ibFirst = iPacket * this.mEndpointMaxPacketSize + MODEM_STATUS_SIZE;
                        ibMax = (iPacket + 1) * this.mEndpointMaxPacketSize;
                        setBufferBounds(byteBuffer, ibFirst, ibMax);
                    }

                    //Assert.assertTrue(byteBuffer.remaining() == ibMax - ibFirst);
                    int cbPacket = ibMax - ibFirst;
                    if (cbPacket > 0)
                    {
                        synchronized (mCircularBuffer)
                        {
                            // Remember the bytes in our linear array of bytes
                            cbExtracted += mCircularBuffer.write(byteBuffer);

                            // The first of those was at the start a packet (ie: followed modem status
                            // bytes) while the remainder were not
                            mMarkedItemQueue.addMarkedItem();
                            mMarkedItemQueue.addUnmarkedItems(cbPacket-1);

                            // Remember when these packets came in
                            //mTimestamps.addLast(packetBuffer.getTimestamp(TimeUnit.NANOSECONDS), cbPacket);
                        }
                    }
                }
                if (cbExtracted > 0)
                {
                    signalRxChar = true;
                    wakeReadBulkInData();
                }
                byteBuffer.clear();
                //this.processEventChars(signalRxChar, signalEvents, signalLineEvents);
            }
            finally
            {
//                verifyInvariants("<-extractReadData");
            }
        }
    }

    public BulkPacketBufferIn acquireWritableInputBuffer()
    {
        BulkPacketBufferIn result = null;
        synchronized (mAvailableInBuffers)
        {
            if (!mAvailableInBuffers.isEmpty())
            {
                result = mAvailableInBuffers.remove(mAvailableInBuffers.size()-1); // nit: LIFO for better cache locality
            }
        }
        if (result == null)
        {
            /**
             * The issue of how big of packet buffer to use is surprisingly complicated. The best
             * treatise on this so far located is the FTDI application note entitled:
             *
             *  "AN232B-04 Data Throughput, Latency and Handshaking"
             *
             * To cut to the chase: the best trade-off is to use the endpoint packet size, which is
             * usually (perhaps always?) 64 bytes. If one uses larger than this, if the data comes
             * in at just the most inopportune rate, you can wait multiple frames to get your data,
             * increasing latency perhaps significantly (see Section 3.3, which illustrates how a
             * hypothetical 4KB buffer at a 38.75kbaud could take the full 1.06 before the chip
             * returned data to the driver).
             *
             * Note that FTDI chips seem to have an internal receive buffer of 256 or 512 bytes
             * depending on the model.
             */
            result = new BulkPacketBufferIn(mEndpointMaxPacketSize);
        }
        return result;
    }

    private void wakeReadBulkInData()
    {
        synchronized (this.mCircularBuffer)
        {
            this.mCircularBuffer.notifyAll();
        }
    }

    public BulkPacketBufferIn acquireReadableInputBuffer() throws InterruptedException
    {
        for (;;)
        {
            synchronized (readBuffer)
            {
                if (!readBuffer.isEmpty())
                {
                    return readBuffer.remove(0);
                }
                readBuffer.wait();
            }
        }
    }
    private void setBufferBounds(ByteBuffer buffer, int ibFirst, int ibMax)
    {
        buffer.clear();             // don't assume positions: position <- 0, limit <- capacity
        buffer.position(ibFirst);
        buffer.limit(ibMax);
    }

    public void releaseWritableInputBuffer(BulkPacketBufferIn packetBuffer)
    {
        // Don't retain buffers that have no user data at all

        //if (packetBuffer.getCurrentLength() <= MODEM_STATUS_SIZE || !retainRecentBuffer(packetBuffer))
        if (packetBuffer.getCurrentLength() <= MODEM_STATUS_SIZE)
        {
            offerAvailableBufferIn(packetBuffer);
        }
    }

    protected Deadline getReadDeadline(long msTimeout)
    {
        if (msTimeout == 0)
        {
            msTimeout = this.msBulkInReadTimeout;
        }
        if (mReadDeadline.getDuration(TimeUnit.MILLISECONDS) == msTimeout)
        {
            mReadDeadline.reset();
        }
        else
        {
            mReadDeadline = new Deadline(msTimeout, TimeUnit.MILLISECONDS);
        }
        return mReadDeadline;
    }

    private void offerAvailableBufferIn(BulkPacketBufferIn packetBuffer)
    {
        synchronized (mAvailableInBuffers)
        {
            // keep a few around around so as to reduce GC pressure
            if (mAvailableInBuffers.size() < mAvailableInBuffersCapacity)
            {
                mAvailableInBuffers.add(packetBuffer);
            }
        }
    }

    public int readBulkInData(final byte[] data, final int ibFirst, final int cbToRead, long msTimeout, TimeWindow timeWindow) throws InterruptedException
    {
//        if (mReadBulkInDataInterruptRequested)
//        {
//            throw new InterruptedException("interrupted in readBulkInData()");
//        }
        if (cbToRead > 0 && isOpen())
        {
            mReadBulkInDataThread = Thread.currentThread();
            try {
//                verifyInvariants("->readBulkInData");
                final Deadline readDeadline = getReadDeadline(msTimeout);

                // Loop until we get the amount of data we came for
                while (isOpen())
                {
                    // Stop if we've timed out
                    if (readDeadline.hasExpired())
                    {
                        return 0;
                    }

                    // If we've been poked, then poke our callers
                    if (Thread.interrupted())
                    {
                        throw new InterruptedException("interrupted reading USB data");
                    }

                    // Is there enough data there for us to read?
                    synchronized (mCircularBuffer)
                    {
                        if (mCircularBuffer.size() >= cbToRead)
                        {
                            // Yes, read it
                            int cbRead = mCircularBuffer.read(data, ibFirst, cbToRead);
                            if (cbRead > 0)
                            {
                                mMarkedItemQueue.removeItems(cbRead);
//                                if (timeWindow != null)
//                                {
//                                    timeWindow.setNanosecondsFirst(mTimestamps.getFirst());
//                                    timeWindow.setNanosecondsLast(mTimestamps.removeFirstCount(cbRead));
//                                }
//                                else
//                                {
//                                    mTimestamps.removeFirstCount(cbRead);    // just discard
//                                }

                                mCircularBuffer.notifyAll();
                            }
                            return cbRead;
                        }

                        // Not enough data. Wait for more data to come in. In art, the wait system
                        // complains to the log if you use a non-integer wait interval, so we cap.
                        long msRemaining = Math.min(readDeadline.timeRemaining(TimeUnit.MILLISECONDS), Integer.MAX_VALUE);
                        if (msRemaining > 0)
                        {
                            mCircularBuffer.wait(msRemaining);
                            //mCircularBuffer.wait(10000);
                            //break;
                        }
                    }
                }
                // The device was closed while we were waiting
                return RC_DEVICE_CLOSED;
            }
            finally
            {
                //verifyInvariants("<-readBulkInData");
                mReadBulkInDataThread = null;
            }
        }
        else
        {
            return 0;
        }
    }
}
