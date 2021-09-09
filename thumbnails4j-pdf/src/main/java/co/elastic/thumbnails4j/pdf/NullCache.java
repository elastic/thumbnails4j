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
