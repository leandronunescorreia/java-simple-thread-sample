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

    public ThreadedPPM(String filename, int numberOfThreads) throws IOException {
        super(0, 0, 0);

        var header = this.readImageHeaderInfo(filename);
        this.pixels = new byte[width * height * (bitDepth / 8)];

        long dataSize = (new java.io.File(filename).length() - header);
        long chunkSize = dataSize / numberOfThreads;
        long remainder = dataSize % numberOfThreads;

        //allocate threads
        List<Thread> threads = new ArrayList<>(numberOfThreads);


        for (int i = 0; i < numberOfThreads; i++) {
            long start = (i * chunkSize) + header+1;
            long length = chunkSize + (i == (numberOfThreads - 1) ? remainder: 0);
            threads.add( new Thread(() -> {
                try {
                    multiThreadLoad(filename, start, length);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));
        }

        for (var thread : threads) {
            thread.start();
        }

        // Wait for all threads to finish
        while(true) {
            boolean allDone = true;
            for (var thread : threads) {
                if (thread.isAlive()) {
                    allDone = false;
                    break;
                }
            }
            if (allDone) {
                break;
            }
        }
    }

    public long readImageHeaderInfo(String filename) throws IOException {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(filename))) {
            long bytesRead = 0;

            // Read magic number (e.g., "P6")
            String magic = readToken(bufferedInputStream);
            bytesRead += magic.length() + 1; // +1 for the whitespace or newline
            if (!magic.equals("P6")) {
                throw new IOException("Unsupported format: " + magic);
            }

            // Read width, height, maxVal
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

    public void multiThreadLoad(String filename, long start, long length) throws IOException {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(filename))) {
            // Skip to the start position
            long skipped = bufferedInputStream.skip(start);
            if (skipped != start) {
                throw new IOException("Failed to skip to the start position");
            }

            // Read raw pixel data
            int bytesRead = bufferedInputStream.readNBytes(this.pixels, (int) start, (int) (start+length));

            if (bytesRead != start+length) {
                throw new IOException("Unexpected EOF when reading pixel data");
            }
        }
    }

}

