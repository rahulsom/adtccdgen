package testdata

/**
 * Creates a file representing a URL. This is helpful when you need to access a file
 * whose URL you know, but it is too huge and you need it every time you run.
 *
 * @author rahulsomasunderam
 */
class CachedFile {
  URL url

  String getText() {
    def fileParts = url.toString().split('/')
    def fileName = fileParts[-1]
    if (!new File(fileName).exists()) {
      new File(fileName).text = url.text
    }
    new File(fileName).text
  }
}
