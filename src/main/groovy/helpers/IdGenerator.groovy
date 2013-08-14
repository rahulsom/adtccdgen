package helpers

import java.text.DecimalFormat

/**
 * Generates IDs from sequence
 * @author rahulsomasunderam
 */
class IdGenerator {

  static synchronized getNextId() {
    if (!new File('id.txt').exists()) {
      new File('id.txt').text = 1000000
    }
    def lastId = new File('id.txt').text.toLong()
    new File('id.txt').text = (lastId + 1).toString()
    new DecimalFormat('0000000000').format(lastId)
  }

}
