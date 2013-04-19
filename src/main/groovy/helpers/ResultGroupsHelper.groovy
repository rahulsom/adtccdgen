package helpers

import testdata.CcdBuilder

import java.security.SecureRandom

/**
 * Created with IntelliJ IDEA.
 * User: rahulsomasunderam
 * Date: 4/18/13
 * Time: 4:14 PM
 * To change this template use File | Settings | File Templates.
 */
@Singleton
class ResultGroupsHelper extends AbstractCsvHelper {
  ResultGroupsHelper() {
    super('resultGroups.csv')
  }

  private SecureRandom r = new SecureRandom()

  void addResultGroups(CcdBuilder ccd, Date lastVisit) {
    def resultGroupsToday = r.nextGaussian() * 4
    if (resultGroupsToday > 0) {
      resultGroupsToday.times {
        int whichGroup = Math.abs(r.nextInt()) % data.size() + 1
        def thisGroup = data.find{it.id == whichGroup.toString()}.clone()
        thisGroup.date = lastVisit.format('yyyyMMdd')
        def essentialMap = thisGroup.subMap(CcdBuilder.Result.declaredFields*.name).findAll{k,v -> v}
        essentialMap.components = ResultsHelper.instance.getResults(whichGroup)
        ccd.results.add(new CcdBuilder.Result(essentialMap))
      }
    }
  }
}
