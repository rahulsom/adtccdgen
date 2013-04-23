package helpers

import testdata.CcdBuilder

import java.security.SecureRandom

/**
 * Created with IntelliJ IDEA.
 * User: rahulsomasunderam
 * Date: 4/18/13
 * Time: 4:13 PM
 * To change this template use File | Settings | File Templates.
 */
@Singleton
class ResultsHelper extends AbstractCsvHelper{
  private ResultsHelper() {
    super('results.csv')
  }

  static final r = new SecureRandom()
  def getResults(int group) {
    def retval = data.findAll {it.id.toString() == group.toString()}.collect {
      def c = it.clone()
      def low = it.rangeLow.toDouble()
      def high = it.rangeHigh.toDouble()
      c.refRange = "${low} - ${high} ${it.unit}"
      def value = (low + high) / 2 + r.nextGaussian() * (high - low) / 3
      def scale = Math.round(Math.log10(value))
      c.value = Math.round(value*Math.pow(10,3-scale))/Math.pow(10,3-scale)
      c.intCode = c.value < low ? 'L' : c.value > high ? 'H' : 'N'
      def e = c.subMap(CcdBuilder.Result.Component.declaredFields*.name).findAll{k,v -> v}
      new CcdBuilder.Result.Component(e)
    }
    retval
  }
}
