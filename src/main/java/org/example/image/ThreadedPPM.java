package org.example.image;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ThreadedPPM extends PPM {
    public ThreadedPPM(int width, int height, int bitDepth) {
        super(width, height, bitDepth);
    }

    public ThreadedPPM(String filename, int numberOfThreads, boolean runSync) throws IOException {
        super(0, 0, 0);

        long headerSize = this.readImageHeaderInfo(filename);
        this.pixels = new byte[width * height * (bitDepth / 8)];

        long dataSize = new java.io.File(filename).length() - headerSize;
        long chunkSize = dataSize / numberOfThreads;
        long remainder = dataSize % numberOfThreads;

        List<Thread> threads = new ArrayList<>(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            long start = headerSize + (i * chunkSize);
            long length = chunkSize + (i == numberOfThreads - 1 ? remainder : 0);
            long pixelOffset = i * chunkSize;
            threads.add(new Thread(() -> {
                try {
                    multiThreadLoad(filename, start, length, pixelOffset, runSync);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new IOException("Thread interrupted", e);
            }
        }
    }

    public long readImageHeaderInfo(String filename) throws IOException {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(filename))) {
            long bytesRead = 0;

            String magic = readToken(bufferedInputStream);
            bytesRead += magic.length() + 1;
            if (!magic.equals("P6")) {
                throw new IOException("Unsupported format: " + magic);
            }

            int width = Integer.parseInt(readToken(bufferedInputStream));
            bytesRead += String.valueOf(width).length() + 1;

            int height = Integer.parseInt(readToken(bufferedInputStream));
            bytesRead += String.valueOf(height).length() + 1;

            int maxVal = Integer.parseInt(readToken(bufferedInputStream));
            bytesRead += String.valueOf(maxVal).length() + 1;

            if (maxVal != 255) {
                throw new IOException("Only maxVal=255 is supported.");
            }

            this.width = width;
            this.height = height;
            this.bitDepth = 24;

            return bytesRead;
        }
    }


    public void multiThreadLoad(String filename, long start, long length, long pixelOffset, boolean runSync) throws IOException {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(filename))) {
            // Skip to correct byte position in file
            long skipped = bufferedInputStream.skip(start);
            if (skipped != start) {
                throw new IOException("Failed to skip to start position.");
            }

            byte[] buffer = new byte[(int) length];
            int read = bufferedInputStream.read(buffer);
            if (read != length) {
                throw new IOException("Unexpected EOF or read error.");
            }

            if(runSync) {
                synchronized (this) {
                    System.arraycopy(buffer, 0, this.pixels, (int) pixelOffset, (int) length);
                }
            } else {
                System.arraycopy(buffer, 0, this.pixels, (int) pixelOffset, (int) length);
            }
        }
    }
}
