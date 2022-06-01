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

package co.elastic.thumbnails4j.pptx;

import co.elastic.thumbnails4j.core.Dimensions;
import co.elastic.thumbnails4j.core.ThumbnailUtils;
import co.elastic.thumbnails4j.core.Thumbnailer;
import co.elastic.thumbnails4j.core.ThumbnailingException;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Thumbnailer} for generating thumbnails from
 * <a href="https://en.wikipedia.org/wiki/Office_Open_XML">Open Offic XML Presentation</a> formatted documents. These
 * documents commonly end in a {@code .pptx} extension. This is not intended to generate thumbnails for documents with
 * a {@code .ppt} extension.
 *
 * {@link PPTXThumbnailer} generates a rendering of the first slide of the presentation, scaled as specified.
 */
public class PPTXThumbnailer implements Thumbnailer {
    Logger logger = LoggerFactory.getLogger(PPTXThumbnailer.class);

    @Override
    public List<BufferedImage> getThumbnails(File input, List<Dimensions> dimensions) throws ThumbnailingException {
        try (FileInputStream fis = new FileInputStream(input)) {
            return getThumbnails(fis, dimensions);
        } catch (FileNotFoundException e) {
            logger.error("Could not find file {}", input.getAbsolutePath());
            logger.error("With stack: ", e);
            throw new IllegalArgumentException(e);
        } catch (IOException e) {
            throw new ThumbnailingException(e);
        }
    }

    @Override
    public List<BufferedImage> getThumbnails(InputStream input, List<Dimensions> dimensions) throws ThumbnailingException {
        try (XMLSlideShow pptx = new XMLSlideShow(input)) {
            BufferedImage image = pptxToImage(pptx);
            List<BufferedImage> results = new ArrayList<>();
            for(Dimensions singleDimension: dimensions){
                results.add(ThumbnailUtils.scaleImage(image, singleDimension));
            }
            return results;
        } catch (IOException e) {
            logger.error("Failed to parse PPTX from stream: ", e);
            throw new ThumbnailingException(e);
        }
    }

    private BufferedImage pptxToImage(XMLSlideShow pptx){
        Dimension pageSize = pptx.getPageSize();
        Dimensions optimizedDimensions = ThumbnailUtils.memoryOptimiseDimension(new Dimensions(pageSize.width, pageSize.height));
        double scaleFactor = ((double) optimizedDimensions.getWidth()) / pageSize.width;
        BufferedImage image = new BufferedImage(optimizedDimensions.getWidth(), optimizedDimensions.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        graphics.setPaint(Color.white);
        graphics.fill(new Rectangle2D.Float(0, 0, optimizedDimensions.getWidth(), optimizedDimensions.getHeight()));
        graphics.scale(scaleFactor, scaleFactor);
        pptx.getSlides().get(0).draw(graphics);
        graphics.dispose();
        return image;
    }
}
