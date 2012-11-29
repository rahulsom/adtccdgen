package testdata
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