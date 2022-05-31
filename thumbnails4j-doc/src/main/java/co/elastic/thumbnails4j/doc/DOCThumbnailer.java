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

package co.elastic.thumbnails4j.doc;

import co.elastic.thumbnails4j.core.Dimensions;
import co.elastic.thumbnails4j.core.ThumbnailUtils;
import co.elastic.thumbnails4j.core.Thumbnailer;
import co.elastic.thumbnails4j.core.ThumbnailingException;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.model.SEPX;
import org.apache.poi.hwpf.model.SectionTable;
import org.apache.poi.hwpf.usermodel.SectionProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Thumbnailer} for <a href="https://en.wikipedia.org/wiki/Doc_(computing)">Microsoft Word Binary File Formatted</a>
 * documents. These files typically end in a {@code .doc} extension. This is not intended to support files with a
 * {@code .docx} extension.
 *
 * The provided document will first be converted to HTML before it is rendered. This means that more complex formatting
 * that cannot be expressed in raw HTML will be lost (non-standard fonts, spacing, images, etc).
 */
public class DOCThumbnailer implements Thumbnailer {

    private static Logger logger = LoggerFactory.getLogger(DOCThumbnailer.class);

    @Override
    public List<BufferedImage> getThumbnails(File input, List<Dimensions> dimensions) throws ThumbnailingException {
        try(FileInputStream fis = new FileInputStream(input)) {
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
        try (HWPFDocument document = new HWPFDocument(input)){
            byte[] htmlBytes = htmlBytesFromDoc(document);
            BufferedImage image = ThumbnailUtils.scaleHtmlToImage(htmlBytes, docPageDimensions(document));
            List<BufferedImage> results = new ArrayList<>();
            for (Dimensions singleDimension: dimensions) {
                results.add(ThumbnailUtils.scaleImage(image, singleDimension));
            }
            return results;
        } catch (IOException|ParserConfigurationException|TransformerException e) {
            logger.error("Could not parse MS Word Document from input stream");
            logger.error("With stack: ", e);
            throw new ThumbnailingException(e);
        }
    }

    private byte[] htmlBytesFromDoc(HWPFDocument document) throws ParserConfigurationException, TransformerException {
        WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
        wordToHtmlConverter.processDocument(document);
        Document html_document = wordToHtmlConverter.getDocument();
        add_margins_to_html(html_document);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            DOMSource domSource = new DOMSource(html_document);
            StreamResult streamResult = new StreamResult(out);
            ThumbnailUtils.getTransformerForXhtmlDOM().transform(domSource, streamResult);
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                logger.error("Possible resource leak - a problem occurred while closing the stream", e);
            }
        }
        return out.toByteArray();
    }

    private void add_margins_to_html(Document html_document){
        // This function is a hack to add "margins" and wrap lines for arbitrary html
        Node body = html_document.getElementsByTagName("body").item(0);
        Element page_div = html_document.createElement("div");
        page_div.setAttribute("style", "width:595.4pt;margin-bottom:36.0pt;margin-top:36.0pt;margin-left:36.0pt;margin-right:36.0pt;");
        while (body.getFirstChild() != null) {
            page_div.appendChild(body.removeChild(body.getFirstChild()));
        }
        body.appendChild(page_div);
    }

    private Dimensions docPageDimensions(HWPFDocument document){
        SectionTable st = document.getSectionTable();
        List<SEPX> sections = st.getSections();
        SectionProperties sectionProperties = sections.get(0).getSectionProperties();
        return new Dimensions(sectionProperties.getXaPage(), sectionProperties.getYaPage());
    }
}
