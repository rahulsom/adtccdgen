package helpers

import testdata.CcdBuilder

import java.security.SecureRandom

/**
 * TODO Documentation.
 * @author rahulsomasunderam
 * @since 10/26/12 10:11 AM
 */
@Singleton
class ImmunizationHelper extends AbstractCsvHelper {

  private ImmunizationHelper() {
    super('immunizations.csv')
  }

  private SecureRandom r = new SecureRandom()

  void addImmunizations(CcdBuilder ccd, Date lastVisit) {
    def immunizationsToday = r.nextGaussian() * 6 - 3
    if (immunizationsToday > 0) {
      immunizationsToday.times {
        int whichImm = Math.abs(r.nextInt()) % data.size()
        def thisIm = data[whichImm].clone()
        thisIm.date = lastVisit.format('yyyyMMdd')
        ccd.immunizations.add(new CcdBuilder.Immunization(thisIm))
      }
    }
  }
}
