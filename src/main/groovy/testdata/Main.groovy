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

    def util = new PersonFactory(maxAddresses: reps/100)
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
                gender: p.gender, dob: p.dob.format('yyyyMMdd')])

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

  private static String getCcd(Map data) {
    def r = new SecureRandom()
    def b = new CcdBuilder()
    b.createCcd(data) { CcdBuilder ccd ->

      int attempts = 5 + r.nextGaussian() * 4
      def c = Calendar.instance

      // add some vitals
      def baseHeight = r.nextGaussian() * 60 + 140
      def baseWeight = 60 + r.nextGaussian() * 10
      def baseSystolic = 120 + r.nextGaussian() * 10
      def baseDiastolic = 80 + r.nextGaussian() * 10

      attempts.times {
        c.add(Calendar.MINUTE, - Math.abs(r.nextGaussian() * 365 * 24 * 60 as int))
        def lastVisit = c.time

        // add an encounter
        ccd.encounters.add(new CcdBuilder.Encounter(code: 'GENRL', codeSystem: '2.16.840.1.113883.5.4',
            displayName: 'General', date: lastVisit.format('yyyyMMdd'), docFirst: 'Robert', docLast: 'Dolin'))


        // may be add some immunizations
        ImmunizationHelper.instance.addImmunizations(ccd, lastVisit)

        ccd.vitals.add(new CcdBuilder.Vital(code: '46680005', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Vital signs', date: lastVisit.format('yyyyMMdd'),
            components: [
                new CcdBuilder.Vital.Component(code: '50373000', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Body height',
                    value: baseHeight, unit: 'cm', type: 'PQ',
                ),
                new CcdBuilder.Vital.Component(code: '27113001', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Body weight',
                    value: baseWeight + r.nextGaussian() * 10, unit: 'kg', type: 'PQ',
                ),
                new CcdBuilder.Vital.Component(code: '271649006', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Systolic BP',
                    value: baseSystolic + r.nextGaussian() * 6, unit: 'mm[Hg]', type: 'PQ',
                ),
                new CcdBuilder.Vital.Component(code: '271650006', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Diastolic BP',
                    value: baseDiastolic + r.nextGaussian() * 6, unit: 'mm[Hg]', type: 'PQ',
                ),
            ]
        ))

        // order some results
        def howManyResults = r.nextGaussian() * 4
        if (howManyResults > 0)
          howManyResults.times {
            def whichResult = Math.abs(r.nextInt()) % 2
            switch(whichResult) {
              case 0:
                def hgb = 14 + r.nextGaussian() * 4
                def wbc = 7 + r.nextGaussian() * 6
                def plt = 250 + r.nextGaussian() * 200
                ccd.results.add(new CcdBuilder.Result(code: '43789009', codeSystem: '2.16.840.1.113883.6.96', displayName: 'CBC WO DIFFERENTIAL', date: lastVisit.format('yyyyMMdd'),
                    components: [
                        new CcdBuilder.Result.Component(code: '30313-1', codeSystem: '2.16.840.1.113883.6.1', displayName: 'HGB',
                            value: hgb, unit: 'g/dl', type: 'PQ', intCode: hgb < 13 ? 'L' : hgb < 17 ? 'N' : 'H', intSystem: '2.16.840.1.113883.5.83', refRange: 'M 13-18 g/dl; F 12-16 g/dl'
                        ),
                        new CcdBuilder.Result.Component(code: '33765-9', codeSystem: '2.16.840.1.113883.6.1', displayName: 'WBC',
                            value: wbc, unit: '10+3/ul', type: 'PQ', intCode: wbc < 4.3 ? 'L' : wbc < 10.8 ? 'N' : 'H', intSystem: '2.16.840.1.113883.5.83', refRange: '4.3 - 10.8 10+3/ul'
                        ),
                        new CcdBuilder.Result.Component(code: '26515-7', codeSystem: '2.16.840.1.113883.6.1', displayName: 'PLT',
                            value: plt, unit: '10+3/ul', type: 'PQ', intCode: plt < 150 ? 'L' : plt < 350 ? 'N' : 'H', intSystem: '2.16.840.1.113883.5.83', refRange: '150 - 350 10+3/ul'
                        ),
                    ]
                ))
                break
              case 1:
                def sodium = 140 + r.nextGaussian() * 10
                def potassium = 4.3 + r.nextGaussian() * 2
                def chlorine = 103 + r.nextGaussian() * 10
                def bicarb = 20 + r.nextGaussian() * 4
                ccd.results.add(new CcdBuilder.Result(code: '20109005', codeSystem: '2.16.840.1.113883.6.96', displayName: 'LYTES', date:  lastVisit.format('yyyyMMdd'),
                    components:  [
                        new CcdBuilder.Result.Component(code: '2951-2', codeSystem: '2.16.840.1.113883.6.1', displayName: 'NA',
                            value: sodium, unit: 'meq/l', type: 'PQ', intCode: sodium < 135 ? 'L' : sodium < 145 ? 'N' : 'H', intSystem: '2.16.840.1.113883.5.83', refRange: '135 - 145 meq/l'
                        ),
                        new CcdBuilder.Result.Component(code: '2823-3', codeSystem: '2.16.840.1.113883.6.1', displayName: 'K',
                            value: potassium, unit: 'meq/l', type: 'PQ', intCode: potassium < 3.5 ? 'L' : potassium < 5.0 ? 'N' : 'H', intSystem: '2.16.840.1.113883.5.83', refRange: '3.5 - 5.0 meq/l'
                        ),
                        new CcdBuilder.Result.Component(code: '2075-0', codeSystem: '2.16.840.1.113883.6.1', displayName: 'CL',
                            value: chlorine, unit: 'meq/l', type: 'PQ', intCode: chlorine < 98 ? 'L' : chlorine < 106 ? 'N' : 'H', intSystem: '2.16.840.1.113883.5.83', refRange: '98 - 106 meq/l'
                        ),
                        new CcdBuilder.Result.Component(code: '1963-8', codeSystem: '2.16.840.1.113883.6.1', displayName: 'HCO3',
                            value: bicarb, unit: 'meq/l', type: 'PQ', intCode: bicarb < 18 ? 'L' : bicarb < 23 ? 'N' : 'H', intSystem: '2.16.840.1.113883.5.83', refRange: '18 - 23 meq/l'
                        ),
                    ]
                ))
                break
              default:
                throw new Error('Bad code in results')
            }
          }

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
