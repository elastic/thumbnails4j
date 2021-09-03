# thumbnails4j
Free and Open project for generating file thumbnails with the JVM

Example usage:

```java
File input = new File("/path/to/my_file.pdf");
Thumbnailer thumbnailer = new PDFThumbnailer();
Dimensions outputDimensions = new Dimensions(100, 100);
BufferedImage output = thumbnailer.getThumbnails(input, outputDimensions).get(0);
```
