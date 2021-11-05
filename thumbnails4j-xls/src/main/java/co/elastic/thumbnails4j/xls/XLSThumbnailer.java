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

/**
 * A {@link Thumbnailer} for legacy <a href="https://en.wikipedia.org/wiki/Microsoft_Excel">Microsoft Excel</a> formatted
 * documents. These documents often have a {@code .xls} extension. This {@link Thumbnailer} is not intended for documents
 * with {@code .xlsx} extensions.
 *
 * {@link XLSThumbnailer} will attempt to clip the upper-right portion of the spreadsheet, and generate an image from that
 * limited view. This prevents un-identifiable views of spreadsheets with many rows and/or columns, by avoiding attemping
 * to show every cell in the thumbnail. The size of this view is controlled by {@link ThumbnailUtils#getMaxInMemoryBuffer()}.
 */
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
