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

/**
 * A generic {@link Exception} used for wrapping various exceptions that may occur while generating thumbnails.
 */
public class ThumbnailingException extends Exception {

    /**
     * Create a {@link ThumbnailingException} that wraps another {@link Exception}
     * @param e the other exception to wrap
     */
    public ThumbnailingException(Exception e) {
        super(e);
    }

    /**
     * Create a {@link ThumbnailingException} that has a specific exception message
     * @param msg the message to be contained by this exception
     */
    public ThumbnailingException(String msg) {
        super(msg);
    }
}
