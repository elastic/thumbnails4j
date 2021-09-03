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

public class PPTXThumbnailer implements Thumbnailer {
    Logger logger = LoggerFactory.getLogger(PPTXThumbnailer.class);

    @Override
    public List<BufferedImage> getThumbnails(File input, List<Dimensions> dimensions) throws ThumbnailingException {
        FileInputStream fis;
        try {
            fis = new FileInputStream(input);
        } catch (FileNotFoundException e) {
            logger.error("Could not find file {}", input.getAbsolutePath());
            logger.error("With stack: ", e);
            throw new IllegalArgumentException(e);
        }
        return getThumbnails(fis, dimensions);
    }

    @Override
    public List<BufferedImage> getThumbnails(InputStream input, List<Dimensions> dimensions) throws ThumbnailingException {
        XMLSlideShow pptx;
        try {
            pptx = new XMLSlideShow(input);
        } catch (IOException e) {
            logger.error("Failed to parse PPTX from stream: ", e);
            throw new ThumbnailingException(e);
        }
        BufferedImage image = pptxToImage(pptx);
        List<BufferedImage> results = new ArrayList<>();
        for(Dimensions singleDimension: dimensions){
            results.add(ThumbnailUtils.scaleImage(image, singleDimension));
        }
        return results;
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
