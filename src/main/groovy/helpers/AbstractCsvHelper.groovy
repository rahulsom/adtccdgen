package helpers

import au.com.bytecode.opencsv.CSVReader

/**
 * Helps with getting data out of CSV repositories
 * @author rahulsomasunderam
 */
abstract class AbstractCsvHelper {

  protected data = new ArrayList<Map<String,String>>()

  AbstractCsvHelper(resourceName) {
    def stream = this.class.classLoader.getResourceAsStream(resourceName)
    def reader = new CSVReader(stream.newReader())
    def lines = reader.readAll()
    def headers = lines.head()
    def body = lines.tail()
    data = body.collect {
      [headers, it].transpose().collectEntries {it}
    }
  }
}
