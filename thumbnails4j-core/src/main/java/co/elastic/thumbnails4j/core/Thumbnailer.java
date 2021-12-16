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

package co.elastic.thumbnails4j.core;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * A simple interface that any thumbnail image generating engine should be able to implement. Given either a {@link File}
 * or an {@link InputStream} and an ordered {@link List} of {@link Dimensions}, we expect to be able to generate
 * thumbnail images from those binaries of the provided dimensions.
 */
public interface Thumbnailer {

    /**
     * Given a {@link File}, generates a {@link List} of thumbnail images of the provided {@link Dimensions}
     * @param input the File pointer for which to generate thumbnails.
     * @param dimensions the ordered list of dimensions the resulting thumbnails should be.
     * @return the output list of thumbnails. The order of this list matches the order of the {@code dimensions} parameter.
     * @throws ThumbnailingException if there was any error related to generating a thumbnail for this {@link File}
     */
    public List<BufferedImage> getThumbnails(File input, List<Dimensions> dimensions) throws ThumbnailingException;

    /**
     * Given an {@link InputStream}, generates a {@link List} of thumbnail images of the provided {@link Dimensions}
     * @param input the binary input stream containing the contents of a document for which to generate thumbnails.
     * @param dimensions the ordered list of dimensions the resulting thumbnails should be.
     * @return the output list of thumbnails. The order of this list matches the order of the {@code dimensions} parameter.
     * @throws ThumbnailingException if there was any error related to generating a thumbnail for this {@link InputStream}
     */
    public List<BufferedImage> getThumbnails(InputStream input, List<Dimensions> dimensions) throws ThumbnailingException;
}
