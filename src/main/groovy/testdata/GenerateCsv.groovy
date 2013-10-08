package testdata

import domain.Person
import groovy.util.logging.Log4j

import java.text.DecimalFormat

/**
 * Generates a CSV with patient data for specified number of patients
 * @author rahulsomasunderam
 */
@Log4j
class GenerateCsv {
  private static final clinicAddress = '560 S Winchester Blvd, San Jose CA 95128'
  private static patients = 1000

  public static void main(String[] args) {

    def util = new PersonFactory(maxAddresses: 1910)
    util.setCenter(clinicAddress)

    String nsid = "1005.1"
    String oid = "2.16.4.39.2.1001.78.5.1"

    def f = new File('build/pat.tsv').newPrintWriter()
    f.println (['firstname','lastName','dob','gender','street','city','state', 'zipCode','country',
        'phone','ssn','id','nsid','uid','uidtype'].collect {"\"${it}\""}.join('\t'))

    patients.times { index ->
      Person p = util.generatePerson()
      def a = p.address

      def cols = [p.firstName,p.lastName,p.dob.format('yyyyMMdd'),p.gender,a.street,a.city,a.state, a.zipCode,"USA",
          p.phone,p.ssn,'X' + new DecimalFormat('00000000').format(index + 10000000),nsid,oid,'ISO']

      f.println cols.collect {"${it}"}.join('\t')

      if (index % 10000 == 0) {
        log.debug index
      }
    }

    f.flush()
    f.close()

  }

}
