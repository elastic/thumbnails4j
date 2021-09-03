package co.elastic.thumbnails4j.xls;

import co.elastic.thumbnails4j.core.Dimensions;
import co.elastic.thumbnails4j.core.ThumbnailUtils;
import co.elastic.thumbnails4j.core.Thumbnailer;
import co.elastic.thumbnails4j.core.ThumbnailingException;
import org.apache.poi.hssf.converter.ExcelToHtmlConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XLSThumbnailer implements Thumbnailer {
    Logger logger = LoggerFactory.getLogger(XLSThumbnailer.class);

    @Override
    public List<BufferedImage> getThumbnails(File input, List<Dimensions> dimensions) throws ThumbnailingException {
        try {
            return getThumbnails(ExcelToHtmlConverter.process(input), dimensions);
        } catch (IOException|ParserConfigurationException|TransformerException e) {
            logger.error("Failed to parse XLS: ", e);
            throw new ThumbnailingException(e);
        }
    }

    @Override
    public List<BufferedImage> getThumbnails(InputStream input, List<Dimensions> dimensions) throws ThumbnailingException {
        try {
            return getThumbnails(ExcelToHtmlConverter.process(input), dimensions);
        } catch (IOException|ParserConfigurationException|TransformerException e) {
            logger.error("Failed to parse XLS: ", e);
            throw new ThumbnailingException(e);
        }
    }

    private List<BufferedImage> getThumbnails(Document xhtml_dom, List<Dimensions> dimensions) throws TransformerException {
        DOMSource domSource = new DOMSource(xhtml_dom);
        ByteArrayOutputStream html = new ByteArrayOutputStream();
        StreamResult streamResult = new StreamResult(html);
        ThumbnailUtils.getTransformerForXhtmlDOM().transform(domSource, streamResult);
        BufferedImage image = ThumbnailUtils.clipHtmlToImage(html.toByteArray(), ThumbnailUtils.getMaxInMemoryBuffer());
        List<BufferedImage> results = new ArrayList<>();
        for(Dimensions singleDimension: dimensions){
            results.add(ThumbnailUtils.scaleImage(image, singleDimension));
        }
        return results;
    }
}
