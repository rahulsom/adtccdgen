package testdata

import domain.Person
import groovy.util.logging.Log4j
import senders.AdtSender
import senders.CcdSender

import java.security.SecureRandom
import java.util.concurrent.Executors

import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.log4j.PatternLayout
import org.apache.log4j.RollingFileAppender

/**
 * TODO Documentation.
 * @author rahulsomasunderam
 * @since 10/26/12 10:13 AM
 */
@Log4j
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
  private static patients = 100000
  private static final SecureRandom RANDOM = new SecureRandom()



  public static void main(String[] args) {

    Logger rootLogger = Logger.getRootLogger()
    rootLogger.level = Level.DEBUG
    rootLogger.removeAllAppenders()

    def layout = '%d{ISO8601} [%15.15t] %-5p %30.30c{2} - %m%n'
    def appender = new RollingFileAppender(name: 'file', file: 'testdata.log', maxBackupIndex: 10,
        layout: new PatternLayout(layout), threshold: Level.DEBUG, maxFileSize: 100*1000*1000)
    appender.activateOptions()
    rootLogger.addAppender(appender)

    def console = new AnsiConsoleAppender(name: 'stdout', layout: new PatternLayout(layout),
        threshold: Level.INFO, writer: new PrintWriter(new OutputStreamWriter(System.err)))
    rootLogger.addAppender(console)
    console.activateOptions()

    new Main().run()

  }

  void run() {

    def delayed = Executors.newFixedThreadPool(1)

    def util = new PersonFactory(maxAddresses: patients / 100)
    util.setCenter(clinicAddress)

    patients.times { index ->
      try {
        Person p = util.generatePerson()
        new AdtSender().send(p, facilities.clinic)
        int ccdsForPatientAtClinic = RANDOM.nextDouble() * 6 - 2
        log.info "(${index + 1}/${patients})Sending ${ccdsForPatientAtClinic > 0 ? ccdsForPatientAtClinic : 0} CCDs for ${p} at Clinic..."
        if (ccdsForPatientAtClinic > 0) {
          ccdsForPatientAtClinic.times {
            delayed.submit {
              new CcdSender().send(p, facilities.clinic)
            }
          }
        }

        if (RANDOM.nextGaussian() < 1) {
          new AdtSender().send(p, facilities.hospital)
          int ccdsForPatientAtHospital = RANDOM.nextDouble() * 4 - 2
          log.info "Sending ${ccdsForPatientAtHospital > 0 ? ccdsForPatientAtHospital : 0} CCDs for ${p} at Hospital..."
          if (ccdsForPatientAtHospital > 0) {
            ccdsForPatientAtHospital.times {
              delayed.submit {
                // new CcdSender().send(p, facilities.hospital)
              }
            }
          }
        }

        if (RANDOM.nextGaussian() < 1) {
          new AdtSender().send(p, facilities.lab)
          /*
          int ccdsForPatientAtHospital = RANDOM.nextDouble() * 4 - 2
          log.info "Sending ${ccdsForPatientAtHospital > 0? ccdsForPatientAtHospital: 0} CCDs for ${p} at Lab..."
          if (ccdsForPatientAtHospital > 0) {
              ccdsForPatientAtHospital.times {
                  delayed.submit {
                      new CcdSender().send(p, facilities.lab)
                  }
              }
          }*/
        }

      } catch (Exception e) {
        log.error "Missed chance", e
      } finally {
        log.debug "Completed (${index + 1}/${patients})"
      }
    }

    delayed.shutdown()
  }

  public static time(String filename, Closure closure) {
    def BooleanHolder h = new BooleanHolder()
    h.value = true
    def f = new File("build/timings")
    if (!f.exists()) {
      f.mkdirs()
    }
    def start = System.nanoTime()
    closure.call(h)
    def stop = System.nanoTime()
    long timing = (stop - start) * (h.value ? 1 : -1)
    synchronized (this) {
      File file = new File("build/timings/${filename}")
      boolean append = true
      FileWriter fileWriter = new FileWriter(file, append)
      BufferedWriter buffWriter = new BufferedWriter(fileWriter)

      log.info "${filename} Time: ${timing / 1000000} ms"
      buffWriter.write("${timing / 1000000}\n")
      buffWriter.flush()
      buffWriter.close()
    }

    timing
  }

  static class BooleanHolder  {
    boolean value
  }
}
