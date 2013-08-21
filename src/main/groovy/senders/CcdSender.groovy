package senders

import domain.Person
import groovy.xml.XmlUtil
import helpers.ImmunizationHelper
import helpers.ResultGroupsHelper
import testdata.CcdBuilder
import testdata.Main
import wslite.rest.ContentType
import wslite.rest.RESTClient

import java.security.SecureRandom

/**
 * Sends CCD to specified facility.
 * @author rahulsomasunderam
 */
class CcdSender {
  def send(Person p, Main.Facility theFacility) {
    println "Sending CCD to ${theFacility}\n\n"
    def ccdText = getCcd([identifier: p.getId("${theFacility.ns}&${theFacility.uid}&ISO")[0],
                             universalId: theFacility.uid,
                             firstName: p.firstName, lastName: p.lastName, gender: p.gender,
                             dob: p.dob.format('yyyyMMdd')], p)

    def ccdClient = new RESTClient("http://${theFacility.host}/hl/${theFacility.nn}/")

    try {
      def ccdResp = Main.time('ccd.txt') {
        ccdClient.post(path: 'ccd.xml') {
          type ContentType.XML
          text ccdText
        }
      }
      println "CCD Response: ${XmlUtil.serialize(ccdResp.xml)}"
    } catch (Exception e) {
      e.printStackTrace()
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
        ccd.encounters.add(new CcdBuilder.Encounter(
            code: 'GENRL', codeSystem: '2.16.840.1.113883.5.4',
            displayName: 'General', date: lastVisit.format('yyyyMMdd'), docFirst: 'Robert', docLast: 'Dolin'))

        // may be add some immunizations
        ImmunizationHelper.instance.addImmunizations(ccd, lastVisit)

        ccd.vitals.add(new CcdBuilder.Vital(
            code: '46680005', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Vital signs',
            date: lastVisit.format('yyyyMMdd'),
            components: [
                new CcdBuilder.Vital.Component(
                    code: '50373000', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Body height',
                    value: person.getHeight(lastVisit), unit: 'cm', type: 'PQ',),
                new CcdBuilder.Vital.Component(
                    code: '27113001', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Body weight',
                    value: person.getWeight(lastVisit), unit: 'kg', type: 'PQ',),
                new CcdBuilder.Vital.Component(
                    code: '271649006', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Systolic BP',
                    value: person.getSystolic(lastVisit), unit: 'mm[Hg]', type: 'PQ',),
                new CcdBuilder.Vital.Component(
                    code: '271650006', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Diastolic BP',
                    value: person.getDiastolic(lastVisit), unit: 'mm[Hg]', type: 'PQ',),
                new CcdBuilder.Vital.Component(
                    code: '364075005', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Heart rate',
                    value: person.getHeartRate(lastVisit), unit: 'mm[Hg]', type: 'PQ',),]))

        ResultGroupsHelper.instance.addResultGroups(ccd, lastVisit)
        // order some results

        def proceduresToday = 3 * r.nextGaussian() - 2
        if (proceduresToday > 0) {
          def whichResult = Math.abs(r.nextInt()) % 1
          switch (whichResult) {
            case 0:
              ccd.procedures.add(new CcdBuilder.Procedure(
                  code: '52734007', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Total hip replacement',
                  date: lastVisit.format('yyyyMMdd'),
                  qualifiers: [
                      new CcdBuilder.Procedure.Qualifier(
                          name: new CcdBuilder.Procedure.Qualifier.CodedValue(
                              code: '272741003', displayName: 'Laterality'),
                          value: new CcdBuilder.Procedure.Qualifier.CodedValue(code: '7771000', displayName: 'Left'))]))

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
            ccd.plans.add(new CcdBuilder.Plan(
                code: '23426006', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Pulmonary function test',
                date: latestDate.format('yyyyMMdd')))
          }
        }

      }

    }
  }
}
