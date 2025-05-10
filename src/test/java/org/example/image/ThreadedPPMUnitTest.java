package org.example.image;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
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

        var loadedSync = new ThreadedPPM("thread_ppm.ppm", 4, false);

        var red = loadedSync.getPixels(0, 0);
        assertEquals((byte)255, red[0]);

        var blue = loadedSync.getPixels(0, 1);
        assertEquals((byte) 255, blue[2]);

        var green = loadedSync.getPixels(1, 0);
        assertEquals((byte)255, green[1]);
    }

    @Test
    @DisplayName("t003- Async vs Sync - Async should be faster")
    void t003() throws Exception {
        var image = new ThreadedPPM(2, 2, 24);
        image.setPixels(0, 0, new byte[]{(byte) 255, 0, 0}); // Red
        image.setPixels(1, 0, new byte[]{0, (byte) 255, 0}); // Green
        image.setPixels(0, 1, new byte[]{0, 0, (byte) 255}); // Blue
        image.setPixels(1, 1, new byte[]{(byte) 255, (byte) 255, (byte) 255}); // White
        image.save("thread_ppm.ppm");

        sleep(1000);

        long syncStartTime = System.nanoTime();
        var loadedSync = new ThreadedPPM("thread_ppm.ppm", 4, true);
        long syncEndTime = System.nanoTime() - syncStartTime;

        var red = loadedSync.getPixels(0, 0);
        assertEquals((byte)255, red[0]);

        var blue = loadedSync.getPixels(0, 1);
        assertEquals((byte) 255, blue[2]);

        var green = loadedSync.getPixels(1, 0);
        assertEquals((byte)255, green[1]);

        long aSyncStartTime = System.nanoTime();
        var loadedAsync = new ThreadedPPM("thread_ppm.ppm", 4, false);
        long aSyncEndTime = System.nanoTime() - aSyncStartTime;

        var ared = loadedAsync.getPixels(0, 0);
        assertEquals((byte)255, ared[0]);

        var ablue = loadedAsync.getPixels(0, 1);
        assertEquals((byte) 255, ablue[2]);

        var agreen = loadedAsync.getPixels(1, 0);
        assertEquals((byte)255, agreen[1]);

        assertThat(syncEndTime).isGreaterThan(aSyncEndTime);
    }

}