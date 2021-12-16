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
 * A simple Java Bean to represent two dimensions. X and Y, or, Width and Height.
 */
public class Dimensions {
    public int x;
    public int y;

    /**
     * Create a new instance of {@link Dimensions} with the specified width and height
     * @param width the width, or x, for these dimensions
     * @param height the height, or y, for these dimensions
     */
    public Dimensions(int width, int height){
        this.x = width;
        this.y = height;
    }

    /**
     * @return the x, or width, of these dimensions. Identical to {@link Dimensions#getWidth()}
     */
    public int getX() {
        return x;
    }

    /**
     * @return the width, or x, of these dimensions. Identical to {@link Dimensions#getX()}
     */
    public int getWidth() {
        return x;
    }

    /**
     * Change the x, or width, of these dimensions. Identical to {@link Dimensions#setWidth(int)}
     * @param x the new x, or width, to be set.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Change the width, or x, of these dimensions. Identical to {@link Dimensions#setX(int)}
     * @param width the new width, or x, to be set.
     */
    public void setWidth(int width) {
        this.x = width;
    }

    /**
     * @return the y, or height, of these dimensions. Identical to {@link Dimensions#getHeight()}
     */
    public int getY() {
        return y;
    }

    /**
     * @return the height, or y, of these dimensions. Identical to {@link Dimensions#getY()}
     */
    public int getHeight() {
        return y;
    }

    /**
     * Change the y, or height, of these dimensions. Identical to {@link Dimensions#setHeight(int)}
     * @param y the new y, or height, to be set.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Change the height, or y, of these dimensions. Identical to {@link Dimensions#setY(int)}
     * @param height the new height, or y, to be set.
     */
    public void setHeight(int height) {
        this.y = height;
    }

    /**
     * Whether this {@link Dimensions}, represented as a rectangle with corresponding width and height, could fit inside
     * the rectangle corresponding to {@code other} without any of the lines/sides intersecting.
     * @param other the other {@link Dimensions} to compare against.
     * @return true if this object is smaller-than-or-equal in both width and height to {@code other}
     */
    public boolean does_fit_inside(Dimensions other){
        return this.x <= other.getX() && this.y <= other.getY();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dimensions)) return false;

        Dimensions that = (Dimensions) o;

        if (getX() != that.getX()) return false;
        return getY() == that.getY();
    }

    @Override
    public int hashCode() {
        int result = getX();
        result = 31 * result + getY();
        return result;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }
}
