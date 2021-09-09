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

package co.elastic.thumbnails4j.image;

import co.elastic.thumbnails4j.core.Dimensions;
import co.elastic.thumbnails4j.core.ThumbnailUtils;
import co.elastic.thumbnails4j.core.Thumbnailer;
import co.elastic.thumbnails4j.core.ThumbnailingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ImageThumbnailer implements Thumbnailer {
    public static int MAX_READ_MULTIPLIER = 4;
    private static final Logger logger = LoggerFactory.getLogger(ImageThumbnailer.class);


    private final int imageType;
    public ImageThumbnailer(String thumbnailType){
        if (thumbnailType.equalsIgnoreCase("png")){
            this.imageType = BufferedImage.TYPE_INT_ARGB;
        } else {
            this.imageType = BufferedImage.TYPE_INT_RGB;
        }
    }

    @Override
    public List<BufferedImage> getThumbnails(File input, List<Dimensions> dimensions) throws ThumbnailingException {
        return getThumbnailsHelper(input, dimensions);
    }

    @Override
    public List<BufferedImage> getThumbnails(InputStream input, List<Dimensions> dimensions) throws ThumbnailingException {
        return getThumbnailsHelper(input, dimensions);
    }

    private List<BufferedImage> getThumbnailsHelper(Object input, List<Dimensions> dimensions) throws ThumbnailingException {
        BufferedImage image;
        try {
            image = imageAtMost(input, maxImageReadSize());
        } catch (IOException e) {
            logger.error("Failed to read image from file {}", input);
            logger.error("With stacktrace: ", e);
            throw new ThumbnailingException(e);
        }
        List<BufferedImage> output = new ArrayList<>();
        for (Dimensions singleDimension: dimensions) {
            output.add(ThumbnailUtils.scaleImage(image, singleDimension, imageType));
        }
        return output;
    }

    private BufferedImage imageAtMost(Object input, Dimensions dimensions) throws IOException, ThumbnailingException {
        try (ImageInputStream stream = ImageIO.createImageInputStream(input)) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
            if (!readers.hasNext()) {
                throw new ThumbnailingException("Image stream contained no images");
            }
            ImageReader reader = readers.next();
            reader.setInput(stream);
            ImageReadParam param = reader.getDefaultReadParam();
            param.setSourceRegion(new Rectangle(0, 0, dimensions.getWidth(), dimensions.getHeight()));

            BufferedImage image = reader.read(0, param);
            reader.dispose();
            return image;
        }
    }

    private static Dimensions maxImageReadSize(){
        return new Dimensions(
                ThumbnailUtils.getMaxInMemoryBuffer().getWidth() * MAX_READ_MULTIPLIER,
                ThumbnailUtils.getMaxInMemoryBuffer().getHeight() * MAX_READ_MULTIPLIER
        );
    }
}
