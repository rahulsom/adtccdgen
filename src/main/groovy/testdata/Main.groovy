package testdata

import domain.Person
import senders.AdtSender
import senders.CcdSender

import java.security.SecureRandom

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
      try {
        Person p = util.generatePerson()
        def theFacility = facilities.clinic

        new AdtSender().send(theFacility.host, theFacility.port,p, getFullId(p, theFacility))

        def r = new SecureRandom()
        int ccdsForPatient = r.nextDouble() * 4 - 2
        println "Sending ${ccdsForPatient > 0? ccdsForPatient: 0} CCDs from patient..."
        if (ccdsForPatient > 0) {
          ccdsForPatient.times {
            new CcdSender().send(p, theFacility)
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
