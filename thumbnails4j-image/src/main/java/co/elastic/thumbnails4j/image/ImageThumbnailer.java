package co.elastic.thumbnails4j.image;

import co.elastic.thumbnails4j.core.Dimensions;
import co.elastic.thumbnails4j.core.ThumbnailUtils;
import co.elastic.thumbnails4j.core.Thumbnailer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageThumbnailer implements Thumbnailer {

    private static Logger logger = LoggerFactory.getLogger(ImageThumbnailer.class);

    @Override
    public List<BufferedImage> getThumbnails(File input, List<Dimensions> dimensions) {
        return getThumbnailsHelper(input, dimensions);
    }

    @Override
    public List<BufferedImage> getThumbnails(InputStream input, List<Dimensions> dimensions) {
        return getThumbnailsHelper(input, dimensions);
    }

    private List<BufferedImage> getThumbnailsHelper(Object input, List<Dimensions> dimensions) {
        BufferedImage image;
        try {
            if (input instanceof File) {
                image = ImageIO.read((File) input);
            } else if (input instanceof InputStream) {
                image = ImageIO.read((InputStream) input);
            } else {
                throw new IllegalArgumentException("Unexpected input class: " + input.getClass().getSimpleName());
            }
        } catch (IOException e) {
            logger.error("Failed to read image from file {}", input);
            logger.error("With stacktrace: ", e);
            throw new RuntimeException(e);
        }
        List<BufferedImage> output = new ArrayList<>();
        for (Dimensions singleDimension: dimensions) {
            output.add(ThumbnailUtils.scaleImage(image, singleDimension));
        }
        return output;
    }
}
