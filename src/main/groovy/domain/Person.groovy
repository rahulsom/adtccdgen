package domain

import java.security.SecureRandom

/**
 * TODO Documentation.
 * @author rahulsomasunderam
 * @since 11/28/12 2:18 PM
 */
class Person {
  private static final SecureRandom random = new SecureRandom()

  String firstName
  String lastName
  String gender
  String phone
  String ssn

  static class Address {
    String streetNumber
    String streetName

    String getStreet() {
      "${streetNumber} ${streetName}"
    }

    String city
    String state
    String zipCode
  }

  Map<String, List<String>> ids = [:]

  def getId(String domain, boolean create = false) {
    if (!ids.get(domain)) {
      ids.put(domain, [])
    }
    if (ids.get(domain).isEmpty() || create) {
      ids.get(domain) << Person.random.nextInt(9999999)
    }
    ids.get(domain)
  }

  Date dob
  Address address

  @Override
  String toString() {
    "(Person) ${firstName} ${lastName} ${gender}/${dob.format('yyyyMMdd')}"
  }
}
