package helpers

import testdata.CcdBuilder

import java.security.SecureRandom

/**
 * TODO Documentation.
 * @author rahulsomasunderam
 * @since 10/26/12 10:11 AM
 */
class ImmunizationHelper {
  SecureRandom r
  CcdBuilder ccd

  void addImmunizations(Date lastVisit) {
    def immunizationsToday = r.nextGaussian() * 6 - 3
    if (immunizationsToday > 0) {
      immunizationsToday.times {
        int whichImm = Math.abs(r.nextInt()) % 3
        switch (whichImm) {
          case 0:
            ccd.immunizations.add(new CcdBuilder.Immunization(
                code: '88', codeSystem: '2.16.840.1.113883.6.59', displayName: 'Influenza virus vaccine', date: lastVisit.format('yyyyMMdd'),
                routeCode:  'IM', routeCodeSystem: '2.16.840.1.113883.5.112', routeCodeSystemName: 'RouteOfAdministration',
                routeCodeDisplay: 'Intramuscular injection',
            ))
            break
          case 1:
            ccd.immunizations.add(new CcdBuilder.Immunization(
                code: '33', codeSystem: '2.16.840.1.113883.6.59', displayName: 'Pneumococcal polysaccharide vaccine', date: lastVisit.format('yyyyMMdd'),
                routeCode:  'IM', routeCodeSystem: '2.16.840.1.113883.5.112', routeCodeSystemName: 'RouteOfAdministration',
                routeCodeDisplay: 'Intramuscular injection',
            ))
            break
          case 2:
            ccd.immunizations.add(new CcdBuilder.Immunization(
                code: '09', codeSystem: '2.16.840.1.113883.6.59', displayName: 'Tetanus and diphtheria toxoids', date: lastVisit.format('yyyyMMdd'),
                routeCode:  'IM', routeCodeSystem: '2.16.840.1.113883.5.112', routeCodeSystemName: 'RouteOfAdministration',
                routeCodeDisplay: 'Intramuscular injection',
            ))
            break
          default:
            throw new Error('Bad code in immunizations')
        }
      }
    }
  }
}
