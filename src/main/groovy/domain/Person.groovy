package domain

import helpers.IdGenerator

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
      ids.get(domain) << IdGenerator.nextId
    }
    ids.get(domain)
  }

  Date dob
  Address address

  @Override
  String toString() {
    "(Person) ${firstName} ${lastName} ${gender}/${dob.format('yyyyMMdd')}"
  }

  // add some vitals
  final baseHeight = Person.random.nextGaussian() * 60 + 140
  final baseWeight = 60 + Person.random.nextGaussian() * 10
  final baseSystolic = 120 + Person.random.nextGaussian() * 10
  final baseDiastolic = 80 + Person.random.nextGaussian() * 10
  final baseHeartRate = 72 + Person.random.nextGaussian() * 20

  int getHeight(Date d) {
    baseHeight
  }
  int getWeight(Date d) {
    baseWeight
  }
  int getSystolic(Date d) {
    baseSystolic
  }
  int getDiastolic(Date d) {
    baseDiastolic
  }
  int getHeartRate (Date d) {
    baseHeartRate
  }

}
