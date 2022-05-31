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

/**
 * A {@link Thumbnailer} for <a href="https://en.wikipedia.org/wiki/PDF">Portable Document Formatted (PDF)</a> documents.
 * These documents will often have a {@code .pdf} file extension.
 *
 * {@link PDFThumbnailer} attempts to find the first image in the format that actually fits on a page, and returns that
 * scaled image. This can be confusing if you attempt to thumbnail a poorly built PDF whose first page has a background
 * image that is larger than the size of the PDF's first page. However, this approach avoids Memory issues that can occur
 * when attempting to read undisplayably-large images.
 */
public class PDFThumbnailer implements Thumbnailer {
    private static Logger logger = LoggerFactory.getLogger(PDFThumbnailer.class);

    @Override
    public List<BufferedImage> getThumbnails(File input, List<Dimensions> dimensions) throws ThumbnailingException {
        try (PDDocument document = PDDocument.load(input)) {
            return getThumbnails(document, dimensions);
        } catch (IOException e) {
            logger.error("Could not load input as PDF: ", e);
            throw new ThumbnailingException(e);
        }
    }

    @Override
    public List<BufferedImage> getThumbnails(InputStream input, List<Dimensions> dimensions) throws ThumbnailingException {
        try (PDDocument document = PDDocument.load(input)) {
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
