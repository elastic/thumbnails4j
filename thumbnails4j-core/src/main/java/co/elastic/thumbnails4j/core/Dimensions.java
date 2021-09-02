package co.elastic.thumbnails4j.core;

public class Dimensions {
    public int x;
    public int y;

    public Dimensions(int width, int height){
        this.x = width;
        this.y = height;
    }

    public int getX() {
        return x;
    }

    public int getWidth() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setWidth(int width) {
        this.x = width;
    }

    public int getY() {
        return y;
    }

    public int getHeight() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setHeight(int height) {
        this.y = height;
    }

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
