package testdata

import ca.uhn.hl7v2.app.Connection
import ca.uhn.hl7v2.app.ConnectionHub
import ca.uhn.hl7v2.llp.MinLowerLayerProtocol
import ca.uhn.hl7v2.model.Message
import ca.uhn.hl7v2.parser.CanonicalModelClassFactory
import ca.uhn.hl7v2.parser.GenericParser
import ca.uhn.hl7v2.parser.PipeParser
import domain.Person
import groovy.xml.XmlUtil
import helpers.ImmunizationHelper
import helpers.ResultGroupsHelper
import wslite.rest.ContentType
import wslite.rest.RESTClient

import java.security.SecureRandom

/**
 * TODO Documentation.
 * @author rahulsomasunderam
 * @since 10/26/12 10:13 AM
 */
class Main {

  private static final hostName = 'qa-dvorak'
  private static final adtPort = 8888
  private static final clinicAddress = '560 S Winchester Blvd, San Jose CA 95128'
  private static final reps = 2

  public static void main(String[] args) {
    ConnectionHub connectionHub = ConnectionHub.instance;
    Connection connection = connectionHub.attach(hostName, adtPort, new PipeParser(), MinLowerLayerProtocol);
    def i = connection.initiator
    i.setTimeoutMillis(10000)

    def util = new PersonFactory(maxAddresses: reps / 100)
    util.setCenter(clinicAddress)

    String nsid = "1003.1"
    String oid = "2.16.4.39.2.1001.78.3.1"
    String domain = "${nsid}&${oid}&ISO"

    reps.times { index ->
      try {
        Person p = util.generatePerson()

        String messageString = generateAdt(p, domain)

        println '>  ' + messageString.replaceAll('\r', '\n>  ')
        println ''

        def start = System.currentTimeMillis()
        def parser = new GenericParser(new CanonicalModelClassFactory('2.6'));
        Message adt = parser.parse(messageString);

        def resp = i.sendAndReceive(adt)

        println '<  ' + resp.encode().replaceAll('\r', '\n<  ')
        println "Time: ${System.currentTimeMillis() - start} ms"
        println '\n\n'

        def r = new SecureRandom()
        int ccdsForPatient = 3 + r.nextGaussian() * 4
        if (ccdsForPatient > 0) {
          ccdsForPatient.times {
            def id = p.getId(domain)[0]
            def ccdText = getCcd([identifier: id, universalId: oid, firstName: p.firstName, lastName: p.lastName,
                gender: p.gender, dob: p.dob.format('yyyyMMdd')], p)

            def ccdClient = new RESTClient("http://${hostName}/healthdock/c1-dvorak/")
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
      } finally {
        println "Completed (${index + 1}/${reps})"
      }
    }

    connection.close()
    connectionHub.discard(connection);

  }

  private static String generateAdt(Person p, String domain) {
    def id = p.getId(domain)[0]
    def phone = p.phone
    def address = p.address
    def addressString = "${address.street}^^${address.city}^${address.state}^${address.zipCode}"
    String messageString = """MSH|^~\\&|MSH3|MSH4|LABADT|MCM|20120109|SECURITY|ADT^A04|MSG00001|P|2.4
            |EVN|A01|198808181123
            |PID|||${id}^^^${domain}||${p.lastName}^${p.firstName}||${p.dob.format('yyyyMMdd')}|${p.gender}||2106-3|${addressString}|GL||||S||ADT_PID18^2^M10|${p.ssn}|9-87654^NC
            |NK1|1|JONES^BARBARA^K|SPO|||||20011105
            |NK1|1|JONES^MICHAEL^A|FTH""".stripMargin().replaceAll('\n', '\r')
    return messageString
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
}
