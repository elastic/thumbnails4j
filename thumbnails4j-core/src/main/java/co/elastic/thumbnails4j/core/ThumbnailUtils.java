package co.elastic.thumbnails4j.core;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ThumbnailUtils {

    /**
     * This algorithm is based off: https://community.oracle.com/docs/DOC-983611
     * Fundamentally, it scales the images down progressively by halves, until
     * it reaches the desired width/height. By doing this, more of the original
     * image quality is preserved.
     *
     * @param image
     * @param dimensions
     * @return The scaled image
     */
    public static BufferedImage scaleImage(BufferedImage image, Dimensions dimensions){
        int scaledWidth = image.getWidth();
        int scaledHeight = image.getHeight();

        double widthRatio = ((double)scaledWidth)/dimensions.getWidth();
        double heightRatio = ((double) scaledHeight)/dimensions.getHeight();

        double aspectRatio = Math.max(widthRatio, heightRatio);
        int targetWidth = (int) Math.round(scaledWidth/aspectRatio);
        int targetHeight = (int) Math.round(scaledHeight/aspectRatio);

        // until we reach the desired width/height
        while (scaledWidth != targetWidth || scaledHeight != targetHeight) {
            scaledWidth = scaleDimension(scaledWidth, targetWidth);
            scaledHeight = scaleDimension(scaledHeight, targetHeight);

            // redraw the image with the new width/height for this iteration
            BufferedImage temp = new BufferedImage(scaledWidth, scaledHeight, image.getType());
            Graphics2D graphics = temp.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.drawImage(image, 0, 0, scaledWidth, scaledHeight, null);
            graphics.dispose();
            image = temp;
        }

        return image;
    }

    public static int scaleDimension(int value, int targetValue){
        if (value > targetValue) {
            return Math.max(value / 2, targetValue);
        } else if (value < targetValue) {
            return Math.min(value * 2, targetValue);
        } else {
            return value;
        }
    }
}
