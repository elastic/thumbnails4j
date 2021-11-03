# thumbnails4j
Free and Open project for generating file thumbnails with the JVM

### Example usage

```java
import co.elastic.thumbnails4j.core.Dimensions;
import co.elastic.thumbnails4j.pdf.PDFThumbnailer;

File input = new File("/path/to/my_file.pdf");
Thumbnailer thumbnailer = new PDFThumbnailer();
Dimensions outputDimensions = new Dimensions(100, 100);
BufferedImage output = thumbnailer.getThumbnails(input, Collections.singletonList(outputDimensions)).get(0);
```

### Building
To build locally, run:
```bash
./mvnw clean install
```

### Where do I report issues with thumbnails4j?
If something is not working as expected, please open an [issue](https://github.com/elastic/thumbnails4j/issues/new).

### Where can I go to get help?
The Workplace Search team at Elastic maintains this library and are happy to help. Try posting your question to the 
[Elastic Workplace Search discuss forums](https://discuss.elastic.co/c/workplace-search). Be sure to mention that you're
using thumbnails4j and also let us know what file type you're trying to thumbnail, and any errors/issues you are 
encountering. You can also find us in the `#enterprise-workplace-search` channel of the 
[Elastic Community Slack](elasticstack.slack.com).

### Contribute 🚀
We welcome contributors to the project. Before you begin, a couple notes...
* Read the [thumbnails4j Contributor's Guide](https://github.com/elastic/thumbnails4j/blob/main/CONTRIBUTING.md).
* Prior to opening a pull request, please:
  * [Create an issue](https://github.com/elastic/thumbnails4j/issues) to discuss the scope of your proposal.
  * Sign the [Contributor License Agreement](https://www.elastic.co/contributor-agreement/). We are not asking you to
    assign copyright to us, but to give us the right to distribute your code without restriction. We ask this of all
    contributors in order to assure our users of the origin and continuing existence of the code. You only need to sign 
    the CLA once.
* Please write simple code and concise documentation, when appropriate.
