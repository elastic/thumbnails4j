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

package co.elastic.thumbnails4j.xlsx;

import co.elastic.thumbnails4j.core.Dimensions;
import co.elastic.thumbnails4j.core.ThumbnailUtils;
import co.elastic.thumbnails4j.core.Thumbnailer;
import co.elastic.thumbnails4j.core.ThumbnailingException;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class XLSXThumbnailer implements Thumbnailer {

    Logger logger = LoggerFactory.getLogger(XLSXThumbnailer.class);

    @Override
    public List<BufferedImage> getThumbnails(File input, List<Dimensions> dimensions) throws ThumbnailingException {
        try {
            return getThumbnails(WorkbookFactory.create(input), dimensions);
        } catch (IOException e) {
            logger.error("Failed to parse XLSX: ", e);
            throw new ThumbnailingException(e);
        }
    }

    @Override
    public List<BufferedImage> getThumbnails(InputStream input, List<Dimensions> dimensions) throws ThumbnailingException {
        try {
            return getThumbnails(WorkbookFactory.create(input), dimensions);
        } catch (IOException e) {
            logger.error("Failed to parse XLSX: ", e);
            throw new ThumbnailingException(e);
        }
    }

    public List<BufferedImage> getThumbnails(Workbook workbook, List<Dimensions> dimensions) {
        XlsxToHtmlSerializer serializer = new XlsxToHtmlSerializer(workbook);
        byte[] htmlBytes =  serializer.getHtml();
        BufferedImage image = ThumbnailUtils.clipHtmlToImage(htmlBytes, ThumbnailUtils.getMaxInMemoryBuffer());
        List<BufferedImage> results = new ArrayList<>();
        for(Dimensions singleDimension: dimensions){
            results.add(ThumbnailUtils.scaleImage(image, singleDimension));
        }
        return results;
    }







}
