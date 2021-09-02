package co.elastic.thumbnails4j.core;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface Thumbnailer {

    public List<BufferedImage> getThumbnails(File input, List<Dimensions> dimensions) throws ThumbnailingException;
    public List<BufferedImage> getThumbnails(InputStream input, List<Dimensions> dimensions) throws ThumbnailingException;
}
