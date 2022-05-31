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

package co.elastic.thumbnails4j.docx;

import co.elastic.thumbnails4j.core.Dimensions;
import co.elastic.thumbnails4j.core.ThumbnailUtils;
import co.elastic.thumbnails4j.core.Thumbnailer;
import co.elastic.thumbnails4j.core.ThumbnailingException;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Thumbnailer} for <a href="https://en.wikipedia.org/wiki/Office_Open_XML">Office Open XML Formatted</a>
 * documents. These files typically end in a {@code .docx} extension. This is not intended to support files with a
 * {@code .doc} extension.
 *
 * The Office Open XML specification allows a thumbnail image to be defined and included in the file binary. If it is
 * present, this image will be returned (with any appropriate scaling).
 *
 * Otherwise, the provided document will first be converted to XHTML before it is rendered. This means that more complex
 * formatting that cannot be expressed in raw XHTML will be lost (non-standard fonts, spacing, images, etc).
 */
public class DOCXThumbnailer implements Thumbnailer {
    Logger logger = LoggerFactory.getLogger(DOCXThumbnailer.class);

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
        List<BufferedImage> results = new ArrayList<>();
        try (XWPFDocument docx = new XWPFDocument(input)){
            InputStream imageStream = docx.getProperties().getThumbnailImage();
            if (imageStream==null) {
                byte[] htmlBytes = htmlBytesFromDocx(docx);
                for(Dimensions singleDimension: dimensions){
                    Dimensions expectedDimensions = docxPageDimensions(docx, singleDimension);
                    BufferedImage image = ThumbnailUtils.scaleHtmlToImage(htmlBytes, expectedDimensions);
                    results.add(ThumbnailUtils.scaleImage(image, singleDimension));
                }
            } else {
                BufferedImage image = ImageIO.read(imageStream);
                for(Dimensions singleDimension: dimensions) {
                    results.add(ThumbnailUtils.scaleImage(image, singleDimension));
                }

            }
        } catch (IOException e) {
            logger.error("Failed to read thumbnails from DOCX", e);
            throw new ThumbnailingException(e);
        }
        return results;
    }

    private Dimensions docxPageDimensions(XWPFDocument docx, Dimensions dimensions){
        CTPageSz pageSz = null;
        try {
            pageSz = docx.getDocument().getBody().getSectPr().getPgSz();
        } catch (NullPointerException e){
            logger.debug("No page size detected for DOCX document");
        }
        if (pageSz == null){
            return dimensions;
        } else {
            return new Dimensions(pageSz.getW().intValue(), pageSz.getH().intValue());
        }

    }

    private byte[] htmlBytesFromDocx(XWPFDocument docx) throws IOException {
        ByteArrayOutputStream htmlStream = new ByteArrayOutputStream();
        XHTMLConverter.getInstance().convert(docx, htmlStream, null);
        return htmlStream.toByteArray();
    }
}
