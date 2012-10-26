import wslite.rest.RESTClient

import java.security.SecureRandom
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

class NGUtil {
  int maxAddresses = 200
  def lastNamesSource = new CachedFile(url: 'http://www.census.gov/genealogy/www/data/1990surnames/dist.all.last'.toURL()).text.replaceAll('\r','\n').replaceAll('\n+', '\n')
  def lastNames = lastNamesSource.split('\n').collect { it.split(' ')[0] }

  def femaleSource = new CachedFile(url: 'http://www.census.gov/genealogy/www/data/1990surnames/dist.female.first'.toURL()).text.replaceAll('\r','\n').replaceAll('\n+', '\n')
  def females = femaleSource.split('\n').collect { it.split(' ')[0] }

  def maleSource = new CachedFile(url: 'http://www.census.gov/genealogy/www/data/1990surnames/dist.male.first'.toURL()).text.replaceAll('\r','\n').replaceAll('\n+', '\n')
  def males = maleSource.split('\n').collect { it.split(' ')[0] }

  final r = new SecureRandom()

  def dateCenter = new Date(70, 01, 01)
  def cityCenter = [37.346961,-121.882668]

  def generateAddress() {
    try {
      println "Generating address"
      def latlng = [ cityCenter[0] + r.nextGaussian()*0.2,cityCenter[1] + r.nextGaussian()*0.2 ]

      def geocoder = new RESTClient('http://maps.googleapis.com/maps/api/geocode')
      def resp = geocoder.get(path: "json?latlng=${latlng[0]},${latlng[1]}&sensor=true")

      if (resp.statusCode == 200) {
        def firstShot = resp.json.results[0].address_components
        def retVal = [
            streetNumber: firstShot.find{it.types.contains('street_number')}.short_name,
            streetName:firstShot.find{it.types.contains('route')}.short_name,
            city:firstShot.find{it.types.contains('locality')}.short_name,
            state:firstShot.find{it.types.contains('administrative_area_level_1')}.short_name,
            zipCode:firstShot.find{it.types.contains('postal_code')}.short_name,
        ]
        retVal.street = "${retVal.streetNumber} ${retVal.streetName}"
        return retVal
      } else {
        return null
      }
    } catch (Exception ignore) {
      null
    }
  }

  def addresses = []

  def getCachedAddress() {
    if (!addresses && new File(addressFileName).exists()) {
      addresses = new JsonSlurper().parseText(new File(addressFileName).text)
    }
    while(addresses.size() < 2) {
      addNewAddress()
    }
    if (addresses.size() < maxAddresses) {
      addNewAddress()
    }
    addresses[r.nextInt(addresses.size())]
  }

  private void addNewAddress() {
    def address = generateAddress()
    if (address) {
      addresses << address
    }
    def json = new JsonBuilder(addresses).toPrettyString()
    new File(addressFileName).text = json
  }

  String addressFileName
  void setCenter(address) {
    addressFileName = address.replaceAll(' ', '_') + '.json'
    address = address.replaceAll(' ', '+')
    def geocoder = new RESTClient('http://maps.googleapis.com/maps/api/geocode')
    def resp = geocoder.get(path: "json?address=${address}&sensor=true")

    if (resp.statusCode == 200) {
      def location = resp.json.results[0].geometry.location
      def retVal = [location.lat, location.lng]
      if (retVal && retVal[0] && retVal[1]) {
        cityCenter = retVal
      }
      println "Center Set"
    } else {
      println resp.contentAsString
    }

  }

  def getLastName() {
    lastNames[r.nextInt(lastNames.size())]
  }

  def getMale() {
    males[r.nextInt(males.size())]
  }

  def getFemale() {
    females[r.nextInt(females.size())]
  }

  def getDob() {
    dateCenter + (int)(r.nextGaussian() * 365 * 40)
  }
}