package co.elastic.thumbnails4j.pdf;

import co.elastic.thumbnails4j.core.Dimensions;
import co.elastic.thumbnails4j.core.ThumbnailUtils;
import co.elastic.thumbnails4j.core.Thumbnailer;
import co.elastic.thumbnails4j.core.ThumbnailingException;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PDFThumbnailer implements Thumbnailer {
    private static Logger logger = LoggerFactory.getLogger(PDFThumbnailer.class);

    @Override
    public List<BufferedImage> getThumbnails(File input, List<Dimensions> dimensions) throws ThumbnailingException {
        PDDocument document;
        try {
            document = PDDocument.load(input);
            return getThumbnails(document, dimensions);
        } catch (IOException e) {
            logger.error("Could not load input as PDF: ", e);
            throw new ThumbnailingException(e);
        }
    }

    @Override
    public List<BufferedImage> getThumbnails(InputStream input, List<Dimensions> dimensions) throws ThumbnailingException {

        PDDocument document;
        try {
            document = PDDocument.load(input);
            return getThumbnails(document, dimensions);
        } catch (IOException e) {
            logger.error("Could not load input as PDF: ", e);
            throw new ThumbnailingException(e);
        }
    }

    private List<BufferedImage> getThumbnails(PDDocument document, List<Dimensions> dimensions) throws IOException {
        List<BufferedImage> results = new ArrayList<>();
        document.setResourceCache(new NullCache());

        Integer thumbnailPageIndex = thumbnailPageIndex(document);
        if (thumbnailPageIndex != null){
            BufferedImage image = new PDFRenderer(document).renderImage(thumbnailPageIndex, 0.5f);
            for(Dimensions singleDimension: dimensions) {
                results.add(ThumbnailUtils.scaleImage(image, singleDimension));
            }
        }
        return results;
    }

    private Integer thumbnailPageIndex(PDDocument document){
        for(int i = 0; i < document.getNumberOfPages(); i++){
            PDPage page = document.getPage(i);
            COSDictionary dictionary = (COSDictionary) unwrapPDFObject(page.getCOSObject().getItem(COSName.RESOURCES));
            List<COSDictionary> images = collectImages(dictionary);
            if (imagesFitPage(images, page)){
                return i;
            }
        }
        return null;
    }

    private boolean imagesFitPage(List<COSDictionary> images, PDPage page){
        PDRectangle pageDimensions = page.getBBox();
        boolean allMatch = true;
        for(COSDictionary image: images){
            allMatch = allMatch && (
                    image.getInt(COSName.WIDTH, 0) <= pageDimensions.getWidth() &&
                    image.getInt(COSName.HEIGHT, 0) <= pageDimensions.getHeight()
            );
        }
        return allMatch;
    }

    private List<COSDictionary> collectImages(COSDictionary dictionary){
        List<COSDictionary> values = new ArrayList<>();
        for(Map.Entry<COSName, COSBase> entry : dictionary.entrySet()){
            COSBase value = unwrapPDFObject(entry.getValue());
            if (value instanceof COSDictionary) {
                if (isImage((COSDictionary) value)){
                    values.add((COSDictionary) value);
                } else {
                    values.addAll(collectImages((COSDictionary) value));
                }
            }
        }
        return values;
    }

    private COSBase unwrapPDFObject(COSBase object){
        return object instanceof COSObject ? ((COSObject)object).getObject() : object;
    }

    private boolean isImage(COSDictionary dictionary){
        return dictionary.getCOSName(COSName.TYPE) == COSName.XOBJECT &&
                dictionary.getCOSName(COSName.SUBTYPE) == COSName.IMAGE &&
                dictionary.containsKey(COSName.WIDTH) &&
                dictionary.containsKey(COSName.HEIGHT);
    }

}
