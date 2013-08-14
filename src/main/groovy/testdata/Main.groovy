package testdata

import domain.Person
import groovy.xml.XmlUtil
import helpers.ImmunizationHelper
import helpers.ResultGroupsHelper
import senders.AdtSender
import wslite.rest.ContentType
import wslite.rest.RESTClient

import java.security.SecureRandom
import java.text.DecimalFormat

/**
 * TODO Documentation.
 * @author rahulsomasunderam
 * @since 10/26/12 10:13 AM
 */
class Main {

  static facilities = [
      clinic:
          [host: 'qa-dvorak', port: 8888, ns: '1003.1', uid: '2.16.4.39.2.1001.78.3.1', nn: 'c1-dvorak'],
      hospital:
          [host: 'qa-dvorak', port: 8890, ns: '1003.2', uid: '2.16.4.39.2.1001.78.3.2', nn: 'h1-dvorak'],
      lab:
          [host: 'qa-dvorak', port: 8892, ns: '1003.3', uid: '2.16.4.39.2.1001.78.3.3', nn: 'l1-dvorak'],
  ]

  private static final clinicAddress = '560 S Winchester Blvd, San Jose CA 95128'
  private static patients = 100

  private static getFullId(Person person, Map facility) {
    def domain = "${facility.ns}&${facility.uid}&ISO"
    def id = person.getId(domain)[0]
    "${id}^^^${domain}"
  }

  public static void main(String[] args) {

    def util = new PersonFactory(maxAddresses: patients / 100)
    util.setCenter(clinicAddress)

    patients.times { index ->
      def start = System.currentTimeMillis()
      try {
        Person p = util.generatePerson()
        new AdtSender().send(facilities.clinic.host, facilities.clinic.port,p, getFullId(p, facilities.clinic))

        def r = new SecureRandom()
        int ccdsForPatient = 0//3 + r.nextDouble() * 4
        println "Sending ${ccdsForPatient} CCDs from patient..."
        if (ccdsForPatient > 0) {
          ccdsForPatient.times {
            def id = 'A' + new DecimalFormat('00000000').format(index + 10000000)
            def ccdText = getCcd([identifier: id, universalId: oid, firstName: p.firstName, lastName: p.lastName,
                gender: p.gender, dob: p.dob.format('yyyyMMdd')], p)

            def ccdClient = new RESTClient("http://${hostName}/hl/pamf-mtv/")
            def ccdResp = ccdClient.post(path: 'ccd.xml') {
              type ContentType.XML
              text ccdText
            }
            println XmlUtil.serialize(ccdResp.xml)
          }
        }

      } catch (Exception e) {
        println "Missed chance"
        e.printStackTrace()
        sleep 100000
      } finally {
        println "Completed (${index + 1}/${patients})"
      }
    }

  }

  private static String getCcd(Map data, Person person) {
    def r = new SecureRandom()
    def b = new CcdBuilder()
    b.createCcd(data) { CcdBuilder ccd ->

      int visits = 5 + r.nextGaussian() * 4
      def c = Calendar.instance

      visits.times {
        c.add(Calendar.MINUTE, -Math.abs(r.nextGaussian() * 365 * 24 * 60 as int))
        def lastVisit = c.time

        // add an encounter
        ccd.encounters.add(new CcdBuilder.Encounter(code: 'GENRL', codeSystem: '2.16.840.1.113883.5.4',
            displayName: 'General', date: lastVisit.format('yyyyMMdd'), docFirst: 'Robert', docLast: 'Dolin'))

        // may be add some immunizations
        ImmunizationHelper.instance.addImmunizations(ccd, lastVisit)

        ccd.vitals.add(new CcdBuilder.Vital(code: '46680005', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Vital signs', date: lastVisit.format('yyyyMMdd'),
            components: [
                new CcdBuilder.Vital.Component(code: '50373000', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Body height',
                    value: person.getHeight(lastVisit), unit: 'cm', type: 'PQ',
                ),
                new CcdBuilder.Vital.Component(code: '27113001', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Body weight',
                    value: person.getWeight(lastVisit), unit: 'kg', type: 'PQ',
                ),
                new CcdBuilder.Vital.Component(code: '271649006', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Systolic BP',
                    value: person.getSystolic(lastVisit), unit: 'mm[Hg]', type: 'PQ',
                ),
                new CcdBuilder.Vital.Component(code: '271650006', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Diastolic BP',
                    value: person.getDiastolic(lastVisit), unit: 'mm[Hg]', type: 'PQ',
                ),
                new CcdBuilder.Vital.Component(code: '364075005', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Heart rate',
                    value: person.getHeartRate(lastVisit), unit: 'mm[Hg]', type: 'PQ',
                ),
            ]
        ))

        ResultGroupsHelper.instance.addResultGroups(ccd, lastVisit)
        // order some results

        def proceduresToday = 3 * r.nextGaussian() - 2
        if (proceduresToday > 0) {
          def whichResult = Math.abs(r.nextInt()) % 1
          switch (whichResult) {
            case 0:
              ccd.procedures.add(new CcdBuilder.Procedure(code: '52734007', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Total hip replacement', date: lastVisit.format('yyyyMMdd'),
                  qualifiers: [
                      new CcdBuilder.Procedure.Qualifier(
                          name: new CcdBuilder.Procedure.Qualifier.CodedValue(code: '272741003', displayName: 'Laterality'),
                          value: new CcdBuilder.Procedure.Qualifier.CodedValue(code: '7771000', displayName: 'Left')
                      )
                  ]
              ))

              break
            default:
              throw new Error('Bad code in procedures')
          }

        }

        c = Calendar.instance
        int plans = 2 + r.nextGaussian() * 3
        if (plans > 0) {
          plans.times {
            c.add(Calendar.MINUTE, Math.abs(r.nextGaussian() * 365 * 24 * 60 as int))
            def latestDate = c.time
            ccd.plans.add(new CcdBuilder.Plan(code: '23426006', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Pulmonary function test', date: latestDate.format('yyyyMMdd')))
          }
        }

      }

    }
  }

  public static time(String filename, Closure closure) {
    def start = System.nanoTime()
    def retval = closure.call()
    def stop = System.nanoTime()
    long timing = stop - start
    synchronized (this) {
      File file = new File(filename)
      boolean append = true
      FileWriter fileWriter = new FileWriter(file, append)
      BufferedWriter buffWriter = new BufferedWriter(fileWriter)

      println "Time: ${timing/1000000} ms"
      buffWriter.write("${timing/1000000}\n")
      buffWriter.flush()
      buffWriter.close()
    }

    retval
  }
}
