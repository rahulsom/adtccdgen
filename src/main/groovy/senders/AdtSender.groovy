package senders

import ca.uhn.hl7v2.app.Connection
import ca.uhn.hl7v2.app.ConnectionHub
import ca.uhn.hl7v2.llp.MinLowerLayerProtocol
import ca.uhn.hl7v2.model.Message
import ca.uhn.hl7v2.parser.CanonicalModelClassFactory
import ca.uhn.hl7v2.parser.GenericParser
import ca.uhn.hl7v2.parser.PipeParser
import domain.Person
import groovy.util.logging.Log4j
import helpers.IdGenerator
import testdata.Main

/**
 * Sends an ADT for given person
 * @author rahulsomasunderam
 */
@Log4j
class AdtSender {

  String getFullId(Person person, Main.Facility facility) {
    def id = person.getId(facility.domain)[0]
    "${id}^^^${facility.domain}"
  }


  def send(Person p, Main.Facility theFacility) {
    def host = theFacility.host
    def port = theFacility.port
    def id = getFullId(p, theFacility)
    ConnectionHub connectionHub = ConnectionHub.instance;
    Connection connection = connectionHub.attach(host, port, new PipeParser(), MinLowerLayerProtocol);
    def i = connection.initiator
    i.setTimeoutMillis(30000)

    //def id = 'H' + p.getId(domain).find{it}
    def address = p.address
    def addressString = "${address.street}^^${address.city}^${address.state}^${address.zipCode}"
    String messageString = """\
        |MSH|^~\\&|MSH3|MSH4|LABADT|MCM|20120109|SECURITY|ADT^A04|MSG${IdGenerator.nextId}|P|2.4
        |EVN|A01|198808181123
        |PID|||${id}||${p.lastName}^${p.firstName}||${p.dob.format('yyyyMMdd')}|${p.gender}||2106-3|${addressString}|GL||||S||ADT_PID18^2^M10|${p.ssn}|9-87654^NC""".stripMargin().replaceAll('\n', '\r')

    def parser = new GenericParser(new CanonicalModelClassFactory('2.6'));
    Message adt = parser.parse(messageString);

    Main.time("${theFacility.host}.${theFacility.nn}.adt.txt") { Main.BooleanHolder h ->
      try {
        log.debug '>  ' + messageString.replaceAll('\r', '\n>  ')
        def resp = parser.parse(i.sendAndReceive(adt).encode())
        log.debug  '<  ' + resp.encode().replaceAll('\r', '\n<  ')
        log.info "ADT - ${p} - ${resp.MSA.acknowledgmentCode.encode()}"
      } catch (Exception e) {
        log.error "ADT - ${p} - ERROR", e
        h.value = false
      }
    }

    connection.close()
    connectionHub.discard(connection);
  }
}
