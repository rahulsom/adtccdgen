package helpers

import au.com.bytecode.opencsv.CSVReader
import groovy.util.logging.Log4j

/**
 * Created with IntelliJ IDEA.
 * User: rahulsomasunderam
 * Date: 4/18/13
 * Time: 3:22 PM
 * To change this template use File | Settings | File Templates.
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
