package senders

import ca.uhn.hl7v2.app.Connection
import ca.uhn.hl7v2.app.ConnectionHub
import ca.uhn.hl7v2.llp.MinLowerLayerProtocol
import ca.uhn.hl7v2.model.Message
import ca.uhn.hl7v2.parser.CanonicalModelClassFactory
import ca.uhn.hl7v2.parser.GenericParser
import ca.uhn.hl7v2.parser.PipeParser
import domain.Person
import helpers.IdGenerator
import testdata.Main

/**
 * Sends an ADT for given person
 * @author rahulsomasunderam
 */
class AdtSender {
  def send(String host, int port, Person p, String id) {
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

    Main.time('adt.txt') {
      println '>  ' + messageString.replaceAll('\r', '\n>  ')
      println ''
      def resp = i.sendAndReceive(adt)
      println '<  ' + resp.encode().replaceAll('\r', '\n<  ')
    }
    println '\n\n'

    connection.close()
    connectionHub.discard(connection);
  }
}
