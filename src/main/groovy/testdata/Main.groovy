package testdata

import domain.Person
import senders.AdtSender
import senders.CcdSender

import java.security.SecureRandom
import java.util.concurrent.Executors

/**
 * TODO Documentation.
 * @author rahulsomasunderam
 * @since 10/26/12 10:13 AM
 */
class Main {

  static class Facility {
    String host
    int port
    String ns
    String uid
    String nn

    String getDomain() {
      "${ns}&${uid}&ISO"
    }

    String toString() {
      "${nn}@${host}"
    }

  }

  static facilities = [
      clinic:
          new Facility(host: 'qa-dvorak', port: 8888, ns: '1003.1', uid: '2.16.4.39.2.1001.78.3.1', nn: 'c1-dvorak'),
      hospital:
          new Facility(host: 'qa-dvorak', port: 8890, ns: '1003.2', uid: '2.16.4.39.2.1001.78.3.2', nn: 'h1-dvorak'),
      lab:
          new Facility(host: 'qa-dvorak', port: 8892, ns: '1003.3', uid: '2.16.4.39.2.1001.78.3.3', nn: 'l1-dvorak'),
  ]

  private static final clinicAddress = '560 S Winchester Blvd, San Jose CA 95128'
  private static patients = 100
  private static final SecureRandom RANDOM = new SecureRandom()

  private static delayed = Executors.newFixedThreadPool(2)

  public static void main(String[] args) {

    def util = new PersonFactory(maxAddresses: patients / 100)
    util.setCenter(clinicAddress)

    patients.times { index ->
      try {
        Person p = util.generatePerson()
        new AdtSender().send(p, facilities.clinic)
        int ccdsForPatientAtClinic = RANDOM.nextDouble() * 4 - 2
        println "Sending ${ccdsForPatientAtClinic > 0? ccdsForPatientAtClinic: 0} CCDs for patient at Clinic..."
        if (ccdsForPatientAtClinic > 0) {
          ccdsForPatientAtClinic.times {
            delayed.submit {
              new CcdSender().send(p, facilities.clinic)
            }
          }
        }

        new AdtSender().send(p, facilities.hospital)
        int ccdsForPatientAtHospital = RANDOM.nextDouble() * 4 - 2
        println "Sending ${ccdsForPatientAtHospital > 0? ccdsForPatientAtHospital: 0} CCDs for patient at Hospital..."
        if (ccdsForPatientAtHospital > 0) {
          ccdsForPatientAtHospital.times {
            delayed.submit {
              new CcdSender().send(p, facilities.hospital)
            }
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

    delayed.shutdown()

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
