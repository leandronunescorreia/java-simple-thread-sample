package org.example.image;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
class PPMUnitTest {


    @Test
    @DisplayName("Test PPM image creation and pixel setting")
    void t001() {
        var image = new PPM(2, 2, 24);
        image.setPixels(0, 0, new byte[]{(byte) 255, 0, 0}); // Red
        image.setPixels(1, 0, new byte[]{0, (byte) 255, 0}); // Green
        image.setPixels(0, 1, new byte[]{0, 0, (byte) 255}); // Blue
        image.setPixels(1, 1, new byte[]{(byte) 255, (byte) 255, (byte) 255}); // White


        assertEquals((byte)255, image.getPixels(0, 0)[0]); // Red
        assertEquals((byte)255, image.getPixels(1, 0)[1]); // Green
        assertEquals((byte)255, image.getPixels(0, 1)[2]); // Blue
        assertEquals((byte)255, image.getPixels(1, 1)[0]); // white
    }

    @Test
    @DisplayName("Test PPM image loading")
    void t002() throws Exception {
        var loaded = new PPM("test.ppm");

        byte[] white = loaded.getPixels(1, 1);
        assertEquals((byte) 255, white[0]);

        byte[] blue = loaded.getPixels(0, 1);
        assertEquals((byte) 255, blue[2]);

        byte[] green = loaded.getPixels(1, 0);
        assertEquals((byte) 255, green[1]);
    }


    @Test
    @DisplayName("Test PPM image saving and loading")
    void t003 () throws Exception {
        var image = new PPM(2, 2, 24);
        image.setPixels(0, 0, new byte[]{(byte) 255, 0, 0}); // Red
        image.setPixels(1, 0, new byte[]{0, (byte) 255, 0}); // Green
        image.setPixels(0, 1, new byte[]{0, 0, (byte) 255}); // Blue
        image.setPixels(1, 1, new byte[]{(byte) 255, (byte) 255, (byte) 255}); // White
        image.save("test_copy.ppm");

        Thread.sleep(1000); // Wait for the file to be saved

        var loaded = new PPM("test_copy.ppm");

        byte[] red = loaded.getPixels(0, 0);
        assertEquals((byte) 255, red[0]);

        byte[] white = loaded.getPixels(1, 1);
        assertEquals((byte) 255, white[0]);

        byte[] blue = loaded.getPixels(0, 1);
        assertEquals((byte) 255, blue[2]);

        byte[] green = loaded.getPixels(1, 0);
        assertEquals((byte) 255, green[1]);
    }

    @Test
    @DisplayName("t004 - saving an image with 720p resolution")
    void t004() throws IOException, InterruptedException {
        var image = new PPM(1280, 720, 24);
        for(var y = 0; y < image.getHeight(); y++) {
            for(var x = 0; x < image.getWidth(); x++) {
                image.setPixels(x, y, new byte[]{(byte) 255, (byte) 255, (byte) 255}); // White
            }
        }

        image.save("test_720p.ppm");
        Thread.sleep(1000); // Wait for the file to be saved


        var loaded = new PPM("test_720p.ppm");
        assertEquals(1280, loaded.getWidth());
        assertEquals(720, loaded.getHeight());
        assertEquals(24, loaded.getBitDepth());
        var randomX = (int) (Math.random() * 1280);
        var randomY = (int) (Math.random() * 720);
        var randomPixel = loaded.getPixels(randomX, randomY);
        assertEquals((byte) 255, randomPixel[0]);
        assertEquals((byte) 255, randomPixel[1]);
        assertEquals((byte) 255, randomPixel[2]);
    }
}