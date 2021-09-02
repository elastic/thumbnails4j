package co.elastic.thumbnails4j.pdf;

import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdmodel.DefaultResourceCache;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDAbstractPattern;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;

import java.io.IOException;

/**
 * Trades off speed of parsing for memory performance
 */
public class NullCache extends DefaultResourceCache {

    @Override
    public void put(COSObject indirect, PDFont font) throws IOException {
        //no op
    }

    @Override
    public void put(COSObject indirect, PDColorSpace colorSpace) throws IOException {
        //no op
    }

    @Override
    public void put(COSObject indirect, PDExtendedGraphicsState extGState) {
        //no op
    }

    @Override
    public void put(COSObject indirect, PDShading shading) throws IOException {
        //no op
    }

    @Override
    public void put(COSObject indirect, PDAbstractPattern pattern) throws IOException {
        //no op
    }

    @Override
    public void put(COSObject indirect, PDPropertyList propertyList) {
        //no op
    }

    @Override
    public void put(COSObject indirect, PDXObject xobject) throws IOException {
        //no op
    }
}
