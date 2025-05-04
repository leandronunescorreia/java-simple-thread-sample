package org.example.image;


import lombok.Data;

@Data
public class Simple {
    protected int width;
    protected int height;
    protected int bitDepth;
    protected byte[] pixels;

    public Simple(int width, int height, int bitDepth) {
        this.width = width;
        this.height = height;
        this.bitDepth = bitDepth;
        this.pixels = new byte[width * height * (bitDepth / 8)];
    }

    public Simple(int width, int height, int bitDepth, byte[] pixels) {
        this.width = width;
        this.height = height;
        this.bitDepth = bitDepth;
        this.pixels = new byte[width * height * (bitDepth / 8)];
        System.arraycopy(pixels, 0, this.pixels, 0, pixels.length);
    }

    public byte[] getPixels(int x, int y) {
        int index = (y * width + x) * (bitDepth / 8);
        var r = pixels[index];
        var g = pixels[index + 1];
        var b = pixels[index + 2];

        return new byte[]{r, g, b};
    }

    public void setPixels(int x, int y, byte[] color) {
        int index = (y * width + x) * (bitDepth / 8);
        pixels[index] = color[0];
        pixels[index + 1] = color[1];
        pixels[index + 2] = color[2];
    }
}
