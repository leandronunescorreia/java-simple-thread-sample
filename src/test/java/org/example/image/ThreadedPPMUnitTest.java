package org.example.image;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

class ThreadedPPMUnitTest {


    @Test
    @DisplayName("t001-should work properly using inherited methods")
    void t001() throws Exception {
        var image = new ThreadedPPM(2, 2, 24);
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
    @DisplayName("t002-should load an image using four threads")
    void t002() throws Exception {
        var image = new ThreadedPPM(2, 2, 24);
        image.setPixels(0, 0, new byte[]{(byte) 255, 0, 0}); // Red
        image.setPixels(1, 0, new byte[]{0, (byte) 255, 0}); // Green
        image.setPixels(0, 1, new byte[]{0, 0, (byte) 255}); // Blue
        image.setPixels(1, 1, new byte[]{(byte) 255, (byte) 255, (byte) 255}); // White
        image.save("thread_ppm.ppm");

        sleep(1000);

        var loaded = new ThreadedPPM("thread_ppm.ppm", 4);

        byte[] red = loaded.getPixels(0, 0);
        assertEquals((byte) 255, red[0]);

        byte[] blue = loaded.getPixels(0, 1);
        assertEquals((byte) 255, blue[2]);

        byte[] green = loaded.getPixels(1, 0);
        assertEquals((byte) 255, green[1]);
    }

}