# thumbnails4j
Open source project for generating file thumbnails with the JVM

### Example usage

```java
import co.elastic.thumbnails4j.core.Dimensions;
import co.elastic.thumbnails4j.pdf.PDFThumbnailer;

File input = new File("/path/to/my_file.pdf");
Thumbnailer thumbnailer = new PDFThumbnailer();
Dimensions outputDimensions = new Dimensions(100, 100);
BufferedImage output = thumbnailer.getThumbnails(input, outputDimensions).get(0);
```

### Building

To build locally, run:
```bash
mvn clean install
```
