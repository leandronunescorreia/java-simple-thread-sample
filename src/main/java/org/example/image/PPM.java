package org.example.image;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class PPM extends Simple {

    public PPM(int width, int height, int bitDepth) {
        super(width, height, bitDepth);
    }

    public PPM(String filename) throws IOException{
        super(0, 0, 0);

        load(filename);
    }

    public void load(String filename) throws IOException {
        try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(filename))) {
            // Read magic number (e.g., "P6")
            String magic = readToken(is);
            if (!magic.equals("P6")) {
                throw new IOException("Unsupported format: " + magic);
            }

            // Read width, height, maxVal
            int width = Integer.parseInt(readToken(is));
            int height = Integer.parseInt(readToken(is));
            int maxVal = Integer.parseInt(readToken(is));
            if (maxVal != 255) {
                throw new IOException("Only maxVal=255 is supported.");
            }

            int bitDepth = 24;
            this.width = width;
            this.height = height;
            this.bitDepth = bitDepth;

            // Read raw pixel data
            int totalBytes = width * height * 3;
            this.pixels = new byte[totalBytes];
            int bytesRead = is.readNBytes(this.pixels, 0, totalBytes);

            if (bytesRead != totalBytes) {
                throw new IOException("Unexpected EOF when reading pixel data");
            }
        }
    }

    private String readToken(InputStream is) throws IOException {
        ByteArrayOutputStream token = new ByteArrayOutputStream();
        int b;
        // Skip whitespace
        while ((b = is.read()) != -1 && Character.isWhitespace(b)) {}
        // Read token
        while (b != -1 && !Character.isWhitespace(b)) {
            token.write(b);
            b = is.read();
        }
        return token.toString(StandardCharsets.US_ASCII);
    }


    public void save(String filename) throws IOException {
        try (OutputStream os = new FileOutputStream(filename)) {
            String header = String.format("P6\n%d %d\n255\n", getWidth(), getHeight());
            os.write(header.getBytes(StandardCharsets.US_ASCII));
            os.write(getPixels());
        }
    }
}

