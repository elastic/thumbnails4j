/*
 *
 *  * Licensed to Elasticsearch B.V. under one or more contributor
 *  * license agreements. See the NOTICE file distributed with
 *  * this work for additional information regarding copyright
 *  * ownership. Elasticsearch B.V. licenses this file to you under
 *  * the Apache License, Version 2.0 (the "License"); you may
 *  * not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *	http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

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

/**
 * A utility class containing common methods used for generating thumbnails, regardless of the source document type.
 */
public class ThumbnailUtils {

    private static Dimensions MAX_IN_MEMORY_BUFFER = new Dimensions(310, 430);

    /**
     * @return The largest allowed {@link Dimensions} to attempt to write an image into. By default, 310 pixels by 430 pixels
     */
    public static Dimensions getMaxInMemoryBuffer() {
        return MAX_IN_MEMORY_BUFFER;
    }

    /**
     * @param maxInMemoryBuffer the new maximum {@link Dimensions} to allow an image to be written into.
     */
    public static void setMaxInMemoryBuffer(Dimensions maxInMemoryBuffer) {
        MAX_IN_MEMORY_BUFFER = maxInMemoryBuffer;
    }

    /**
     * Scale the provided image to the provided dimensions.
     *
     * This algorithm is based off: https://community.oracle.com/docs/DOC-983611
     * Fundamentally, it scales the images down progressively by halves, until
     * it reaches the desired width/height. By doing this, more of the original
     * image quality is preserved.
     *
     * @param image the image to scale
     * @param dimensions the dimensions to scale the image to
     * @return The scaled image. Note the scaled image will be of the same image type as the provided image.
     */
    public static BufferedImage scaleImage(BufferedImage image, Dimensions dimensions){
        return scaleImage(image, dimensions, image.getType());
    }

    /**
     * Scale the provided image to the provided dimensions.
     *
     * This algorithm is based off: https://community.oracle.com/docs/DOC-983611
     * Fundamentally, it scales the images down progressively by halves, until
     * it reaches the desired width/height. By doing this, more of the original
     * image quality is preserved.
     *
     * @param image the image to scale
     * @param dimensions the dimensions to scale the image to
     * @param imageType the {@link BufferedImage} type (for example, {@link BufferedImage#TYPE_INT_RGB} the resulting
     *                  image should be written as.
     * @return The scaled image of the specified type.
     */
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

    /**
     * Given {@link ThumbnailUtils#getMaxInMemoryBuffer()}, resize the provided dimensions, while maintaining aspect
     * ratio, to ensure memory requirements are not exceeded.
     * @param dimensions the initial dimensions
     * @return the input dimensions if they would fit it the max-memory buffer. Otherwise, a scaled-down version of the
     * input dimensions that will fit in the max-memory buffer.
     */
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

    /**
     * @return a transformer capable of reading an XHTML DOM
     * @throws TransformerConfigurationException
     */
    public static Transformer getTransformerForXhtmlDOM() throws TransformerConfigurationException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml"); // see https://stackoverflow.com/a/5126973/2479282. JPaneEditor uses an xml parser for html
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        return transformer;
    }

    /**
     * Renter an image from an HTML UI component
     * @param htmlComponent a {@link JEditorPane} that contains HTML. See also {@link ThumbnailUtils#scaleHtmlToImage(byte[], co.elastic.thumbnails4j.core.Dimensions)}
     * @param dimensions the desired dimensions of the output image
     * @return the rendered image
     */
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

    /**
     * Render an image from raw HTML
     * @param htmlBytes the raw html
     * @param dimensions the desired dimensions of the output image
     * @return the rendered image
     * @throws UnsupportedEncodingException if the raw HTML can not be read as a UTF-8 {@link String}
     */
    public static BufferedImage scaleHtmlToImage(byte[] htmlBytes, Dimensions dimensions) throws UnsupportedEncodingException {
        JEditorPane htmlComponent = new JEditorPane("text/html", new String(htmlBytes, StandardCharsets.UTF_8));
        Dimension preferredSize = htmlComponent.getPreferredSize();
        int width = preferredSize.width;
        double ratio = ((double) width) / dimensions.getWidth(); // determine the ratio between the actual width and the expected width
        int height = (int) (ratio * dimensions.getHeight()); // use that ratio to set the height (ignoring actual height)
        htmlComponent.setSize(width, height); // this html may be much "longer" proportionally, so we "fit" it to the expected page or thumbnail size

        return htmlToImage(htmlComponent, new Dimensions(width, height));
    }

    /**
     * Crop the upper-left {@code dimensions}-worth of an image that would be rendered from raw html.
     * This is particularly useful when your HTML describes a very large display, and a zoomed-out view of such a
     * display would be meaningless. Sometimes, its better to just show a smaller, zoomed-in, sample of the document.
     * @param htmlBytes the raw HTML
     * @param dimensions the desired dimensions to keep in the crop/clip.
     * @return the clipped image resulting from rendering just a portion of the raw HTML.
     */
    public static BufferedImage clipHtmlToImage(byte[] htmlBytes, Dimensions dimensions){
        JEditorPane htmlComponent = new JEditorPane("text/html", new String(htmlBytes, StandardCharsets.UTF_8));
        Dimension preferredSize = htmlComponent.getPreferredSize();
        htmlComponent.setSize(preferredSize.width, preferredSize.height);
        return htmlToImage(htmlComponent, dimensions);
    }

    private static int scaleDimension(int value, int targetValue){
        if (value > targetValue) {
            return Math.max(value / 2, targetValue);
        } else if (value < targetValue) {
            return Math.min(value * 2, targetValue);
        } else {
            return value;
        }
    }
}
