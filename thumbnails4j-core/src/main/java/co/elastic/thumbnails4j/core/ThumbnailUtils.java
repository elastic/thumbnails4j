package co.elastic.thumbnails4j.core;

import javax.swing.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class ThumbnailUtils {

    private static Dimensions MAX_IN_MEMORY_BUFFER = new Dimensions(310, 430);

    public static Dimensions getMaxInMemoryBuffer() {
        return MAX_IN_MEMORY_BUFFER;
    }

    public static void setMaxInMemoryBuffer(Dimensions maxInMemoryBuffer) {
        MAX_IN_MEMORY_BUFFER = maxInMemoryBuffer;
    }

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
        return scaleImage(image, dimensions, image.getType());
    }

    public static BufferedImage scaleImage(BufferedImage image, Dimensions dimensions, int imageType){
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
            BufferedImage temp = new BufferedImage(scaledWidth, scaledHeight, imageType);
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

    public static Dimensions memoryOptimiseDimension(Dimensions dimensions){
        if(dimensions.does_fit_inside(MAX_IN_MEMORY_BUFFER)){
            return dimensions;
        } else {
            double givenXYRatio = ((double) dimensions.getWidth()) / (dimensions.getHeight());
            Dimensions result = new Dimensions(dimensions.getWidth(), dimensions.getHeight());
            double destXYRatio = ((double) MAX_IN_MEMORY_BUFFER.getWidth()) / (MAX_IN_MEMORY_BUFFER.getHeight());
            if (givenXYRatio > destXYRatio) {
                // X is the limiting factor
                result.setWidth(MAX_IN_MEMORY_BUFFER.getWidth());
                result.setHeight((int)(((double) MAX_IN_MEMORY_BUFFER.getWidth()) / givenXYRatio));
            } else {
                // Y is the limiting factor
                result.setHeight(MAX_IN_MEMORY_BUFFER.getHeight());
                result.setWidth((int)(MAX_IN_MEMORY_BUFFER.getHeight() * givenXYRatio));
            }
            return result;
        }
    }

    public static Transformer getTransformerForXhtmlDOM() throws TransformerConfigurationException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml"); // see https://stackoverflow.com/a/5126973/2479282. JPaneEditor uses an xml parser for html
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        return transformer;
    }

    public static BufferedImage htmlToImage(JEditorPane htmlComponent, Dimensions dimensions){
        Dimensions optimizedDimensions = memoryOptimiseDimension(dimensions);
        double scale_factor = ((double) optimizedDimensions.getWidth()) / dimensions.getWidth();
        BufferedImage image = new BufferedImage(optimizedDimensions.getWidth(), optimizedDimensions.getHeight(), BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = image.createGraphics();
        graphics.setPaint(Color.white);
        graphics.fill(new Rectangle2D.Float(0, 0, optimizedDimensions.getWidth(), optimizedDimensions.getHeight()));
        graphics.scale(scale_factor, scale_factor);
        htmlComponent.print(graphics);
        graphics.dispose();
        return image;
    }

    public static BufferedImage scaleHtmlToImage(byte[] htmlBytes, Dimensions dimensions) throws UnsupportedEncodingException {
        JEditorPane htmlComponent = new JEditorPane("text/html", new String(htmlBytes, StandardCharsets.UTF_8));
        Dimension preferredSize = htmlComponent.getPreferredSize();
        int width = preferredSize.width;
        double ratio = ((double) width) / dimensions.getWidth(); // determine the ratio between the actual width and the expected width
        int height = (int) (ratio * dimensions.getHeight()); // use that ratio to set the height (ignoring actual height)
        htmlComponent.setSize(width, height); // this html may be much "longer" proportionally, so we "fit" it to the expected page or thumbnail size

        return htmlToImage(htmlComponent, new Dimensions(width, height));
    }

    public static BufferedImage clipHtmlToImage(byte[] htmlBytes, Dimensions dimensions){
        JEditorPane htmlComponent = new JEditorPane("text/html", new String(htmlBytes, StandardCharsets.UTF_8));
        Dimension preferredSize = htmlComponent.getPreferredSize();
        htmlComponent.setSize(preferredSize.width, preferredSize.height);
        return htmlToImage(htmlComponent, dimensions);
    }
}
