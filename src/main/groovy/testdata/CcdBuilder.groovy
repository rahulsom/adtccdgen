package testdata

import groovy.transform.ToString
import groovy.util.logging.Log4j
import groovy.xml.MarkupBuilder
import helpers.IdGenerator

import java.security.MessageDigest

/**
 * TODO Documentation.
 * @author rahulsomasunderam
 * @since 10/26/12 10:01 AM
 */
@ToString
@Log4j
class CcdBuilder {

  def createCcd(Map data, Closure closure = null) {

    plans.clear()
    closure?.call(this)

    def sw = new StringWriter()
    def x = new MarkupBuilder(sw)

    assert data.identifier
    assert data.universalId
    assert data.firstName
    assert data.lastName
    assert data.gender
    assert data.dob

    x.ClinicalDocument('xmlns:xsi': 'http://www.w3.org/2001/XMLSchema-instance',
        'xsi:schemaLocation': 'urn:hl7-org:v3CDA.xsd', xmlns: 'urn:hl7-org:v3') {
      typeId(root: '2.16.840.1.113883.1.3', extension: 'POCD_HD000040')
      templateId(root: '2.16.840.1.113883.10.20.1')
      id(root: data.universalId, extension: IdGenerator.nextId)
      code(code: '34133-9', codeSystem: '2.16.840.1.113883.6.1', displayName: 'Summarization of episode note')
      title('Good Health Clinic Continuity of Care Document')
      effectiveTime(value: '20000407130000+0500')
      confidentialityCode(code: 'N', codeSystem: '2.16.840.1.113883.5.25')
      languageCode(code: 'en-US')
      recordTarget {
        patientRole {
          id(extension: data.identifier, root: data.universalId)
          patient {
            name {
              given(data.firstName)
              family(data.lastName)
            }
            administrativeGenderCode(code: data.gender, codeSystem: '2.16.840.1.113883.5.1')
            birthTime(value: data.dob)
          }
          providerOrganization {
            id(root: '2.16.840.1.113883.19.5')
            name('Good Health Clinic')
          }
        }
      }
      author {
        time(value: '20000407130000+0500')
        assignedAuthor {
          id(root: '20cf14fb-b65c-4c8c-a54d-b0cca834c18c')
          assignedPerson {
            name {
              prefix('Dr.')
              given('Robert')
              family('Dolin')
            }
          }
          representedOrganization {
            id(root: '2.16.840.1.113883.19.5')
            name('Good Health Clinic')
          }
        }
      }
      informant {
        assignedEntity {
          id(nullFlavor: 'NI')
          representedOrganization {
            id(root: '2.16.840.1.113883.19.5')
            name('Good Health Clinic')
          }
        }
      }
      custodian {
        assignedCustodian {
          representedCustodianOrganization {
            id(root: '2.16.840.1.113883.19.5')
            name('Good Health Clinic')
          }
        }
      }
      legalAuthenticator {
        time(value: '20000407130000+0500')
        signatureCode(code: 'S')
        assignedEntity {
          id(nullFlavor: 'NI')
          representedOrganization {
            id(root: '2.16.840.1.113883.19.5')
            name('Good Health Clinic')
          }
        }
      }
      participant(typeCode: 'IND') {
        associatedEntity(classCode: 'GUAR') {
          id(root: '4ff51570-83a9-47b7-91f2-93ba30373141')
          addr {
            streetAddressLine('17 Daws Rd.')
            city('Blue Bell')
            state('MA')
            postalCode('02368')
          }
          telecom(value: 'tel:(888)555-1212')
          associatedPerson {
            name {
              given('Kenneth')
              family('Ross')
            }
          }
        }
      }
      participant(typeCode: 'IND') {
        associatedEntity(classCode: 'NOK') {
          id(root: '4ac71514-6a10-4164-9715-f8d96af48e6d')
          code(code: '65656005', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Biiological mother')
          telecom(value: 'tel:(999)555-1212')
          associatedPerson {
            name {
              given('Henrietta')
              family('Levin')
            }
          }
        }
      }
      documentationOf {
        serviceEvent(classCode: 'PCPR') {
          effectiveTime {
            low(value: '19320924')
            high(value: '20000407')
          }
          performer(typeCode: 'PRF') {
            functionCode(code: 'PP', codeSystem: '2.16.840.1.113883.5.88')
            time {
              low(value: '1990')
              high(value: '20000407')
            }
            assignedEntity {
              id(root: '2.16.840.1.113883.19.5', extension: '20cf14fb-b65c-4c8c-a54d-b0cca834c18c')
              assignedPerson {
                name {
                  prefix('Dr.')
                  given('Robert')
                  family('Dolin')
                }
              }
              representedOrganization {
                id(root: '2.16.840.1.113883.19.5')
                name('Good Health Clinic')
              }
            }
          }
        }
      }
      component {
        structuredBody {
          component {
            section {
              templateId(root: '2.16.840.1.113883.10.20.1.13')
              code(code: '48764-5', codeSystem: '2.16.840.1.113883.6.1')
              title('Summary Purpose')
              entry(typeCode: 'DRIV') {
                act(classCode: 'ACT', moodCode: 'EVN') {
                  templateId(root: '2.16.840.1.113883.10.20.1.30')
                  code(code: '23745001', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Documentation procedure')
                  statusCode(code: 'completed')
                  entryRelationship(typeCode: 'RSON') {
                    act(classCode: 'ACT', moodCode: 'EVN') {
                      code(code: '308292007', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Transfer of care')
                      statusCode(code: 'completed')
                    }
                  }
                }
              }
            }
          }
          component {
            section {
              templateId(root: '2.16.840.1.113883.10.20.1.9')
              code(code: '48768-6', codeSystem: '2.16.840.1.113883.6.1')
              title('Payers')
              entry(typeCode: 'DRIV') {
                act(classCode: 'ACT', moodCode: 'DEF') {
                  templateId(root: '2.16.840.1.113883.10.20.1.20')
                  id(root: '1fe2cdd0-7aad-11db-9fe1-0800200c9a66')
                  code(code: '48768-6', codeSystem: '2.16.840.1.113883.6.1', displayName: 'Payment sources')
                  statusCode(code: 'completed')
                  entryRelationship(typeCode: 'COMP') {
                    act(classCode: 'ACT', moodCode: 'EVN') {
                      templateId(root: '2.16.840.1.113883.10.20.1.26')
                      id(root: '3e676a50-7aac-11db-9fe1-0800200c9a66')
                      code(code: 'EHCPOL', codeSystem: '2.16.840.1.113883.5.4', displayName: 'Extended healthcare')
                      statusCode(code: 'completed')
                      performer(typeCode: 'PRF') {
                        assignedEntity {
                          id(root: '329fcdf0-7ab3-11db-9fe1-0800200c9a66')
                          representedOrganization {
                            name('Good Health Insurance')
                          }
                        }
                      }
                      participant(typeCode: 'COV') {
                        participantRole {
                          id(root: '14d4a520-7aae-11db-9fe1-0800200c9a66')
                          code(code: 'SELF', codeSystem: '2.16.840.1.113883.5.111', displayName: 'Self')
                        }
                      }
                      entryRelationship(typeCode: 'REFR') {
                        act(classCode: 'ACT', moodCode: 'EVN') {
                          templateId(root: '2.16.840.1.113883.10.20.1.19')
                          id(root: 'f4dce790-8328-11db-9fe1-0800200c9a66')
                          code(nullFlavor: 'NA')
                          entryRelationship(typeCode: 'SUBJ') {
                            procedure(classCode: 'PROC', moodCode: 'PRMS') {
                              code(code: '73761001', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Colonoscopy')
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
          component {
            section {
              templateId(root: '2.16.840.1.113883.10.20.1.1')
              code(code: '42348-3', codeSystem: '2.16.840.1.113883.6.1')
              title('Advance Directives')
              entry(typeCode: 'DRIV') {
                observation(classCode: 'OBS', moodCode: 'EVN') {
                  templateId(root: '2.16.840.1.113883.10.20.1.17')
                  id(root: '9b54c3c9-1673-49c7-aef9-b037ed72ed27')
                  code(code: '304251008', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Resuscitation')
                  statusCode(code: 'completed')
                  value(code: '304253006', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Do not resuscitate',
                      'xsi:type': 'CD') {
                    originalText('Do not resuscitate')
                  }
                  participant(typeCode: 'VRF') {
                    templateId(root: '2.16.840.1.113883.10.20.1.58')
                    time(value: '19991107')
                    participantRole {
                      id(root: '20cf14fb-b65c-4c8c-a54d-b0cca834c18c')
                    }
                  }
                  entryRelationship(typeCode: 'REFR') {
                    observation(classCode: 'OBS', moodCode: 'EVN') {
                      templateId(root: '2.16.840.1.113883.10.20.1.37')
                      code(code: '33999-4', codeSystem: '2.16.840.1.113883.6.1', displayName: 'Status')
                      statusCode(code: 'completed')
                      value(code: '15240007', codeSystem: '2.16.840.1.113883.6.96',
                          displayName: 'Current and verified', 'xsi:type': 'CE')
                    }
                  }
                  reference(typeCode: 'REFR') {
                    externalDocument {
                      templateId(root: '2.16.840.1.113883.10.20.1.36')
                      id(root: 'b50b7910-7ffb-4f4c-bbe4-177ed68cbbf3')
                      code(code: '371538006', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Advance directive')
                      text(mediaType: 'application/pdf') {
                        reference(value: 'AdvanceDirective.b50b7910-7ffb-4f4c-bbe4-177ed68cbbf3.pdf')
                      }
                    }
                  }
                }
              }
            }
          }
          component {
            section {
              templateId(root: '2.16.840.1.113883.10.20.1.5')
              code(code: '47420-5', codeSystem: '2.16.840.1.113883.6.1')
              title('Functional Status')
              entry(typeCode: 'DRIV') {
                act(classCode: 'ACT', moodCode: 'EVN') {
                  templateId(root: '2.16.840.1.113883.10.20.1.27')
                  id(root: '6z2fa88d-4174-4909-aece-db44b60a3abb')
                  code(nullFlavor: 'NA')
                  entryRelationship(typeCode: 'SUBJ') {
                    observation(classCode: 'OBS', moodCode: 'EVN') {
                      templateId(root: '2.16.840.1.113883.10.20.1.28')
                      id(root: 'fd07111a-b15b-4dce-8518-1274d07f142a')
                      code(code: 'ASSERTION', codeSystem: '2.16.840.1.113883.5.4')
                      statusCode(code: 'completed')
                      effectiveTime {
                        low(value: '1998')
                      }
                      value(code: '105504002', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Dependence on cane',
                          'xsi:type': 'CD')
                      entryRelationship(typeCode: 'REFR') {
                        observation(classCode: 'OBS', moodCode: 'EVN') {
                          templateId(root: '2.16.840.1.113883.10.20.1.44')
                          code(code: '33999-4', codeSystem: '2.16.840.1.113883.6.1', displayName: 'Status')
                          statusCode(code: 'completed')
                          value(code: '55561003', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Active',
                              'xsi:type': 'CE')
                        }
                      }
                    }
                  }
                }
              }
              entry(typeCode: 'DRIV') {
                act(classCode: 'ACT', moodCode: 'EVN') {
                  templateId(root: '2.16.840.1.113883.10.20.1.27')
                  id(root: '64606e86-c080-11db-8314-0800200c9a66')
                  code(nullFlavor: 'NA')
                  entryRelationship(typeCode: 'SUBJ') {
                    observation(classCode: 'OBS', moodCode: 'EVN') {
                      templateId(root: '2.16.840.1.113883.10.20.1.28')
                      id(root: '6be2930a-c080-11db-8314-0800200c9a66')
                      code(code: 'ASSERTION', codeSystem: '2.16.840.1.113883.5.4')
                      statusCode(code: 'completed')
                      effectiveTime {
                        low(value: '1999')
                      }
                      value(code: '386807006', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Memory impairment',
                          'xsi:type': 'CD')
                      entryRelationship(typeCode: 'REFR') {
                        observation(classCode: 'OBS', moodCode: 'EVN') {
                          templateId(root: '2.16.840.1.113883.10.20.1.44')
                          code(code: '33999-4', codeSystem: '2.16.840.1.113883.6.1', displayName: 'Status')
                          statusCode(code: 'completed')
                          value(code: '55561003', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Active',
                              'xsi:type': 'CE')
                        }
                      }
                    }
                  }
                }
              }
            }
          }
          component {
            section {
              templateId(root: '2.16.840.1.113883.10.20.1.11')
              code(code: '11450-4', codeSystem: '2.16.840.1.113883.6.1')
              title('Problems')
              entry(typeCode: 'DRIV') {
                act(classCode: 'ACT', moodCode: 'EVN') {
                  templateId(root: '2.16.840.1.113883.10.20.1.27')
                  id(root: '6a2fa88d-4174-4909-aece-db44b60a3abb')
                  code(nullFlavor: 'NA')
                  entryRelationship(typeCode: 'SUBJ') {
                    observation(classCode: 'OBS', moodCode: 'EVN') {
                      templateId(root: '2.16.840.1.113883.10.20.1.28')
                      id(root: 'd11275e7-67ae-11db-bd13-0800200c9a66')
                      code(code: 'ASSERTION', codeSystem: '2.16.840.1.113883.5.4')
                      statusCode(code: 'completed')
                      effectiveTime {
                        low(value: '1950')
                      }
                      value(code: '195967001', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Asthma',
                          'xsi:type': 'CD')
                      entryRelationship(typeCode: 'REFR') {
                        observation(classCode: 'OBS', moodCode: 'EVN') {
                          templateId(root: '2.16.840.1.113883.10.20.1.50')
                          code(code: '33999-4', codeSystem: '2.16.840.1.113883.6.1', displayName: 'Status')
                          statusCode(code: 'completed')
                          value(code: '55561003', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Active',
                              'xsi:type': 'CE')
                        }
                      }
                    }
                  }
                }
              }
              entry(typeCode: 'DRIV') {
                act(classCode: 'ACT', moodCode: 'EVN') {
                  templateId(root: '2.16.840.1.113883.10.20.1.27')
                  id(root: 'ec8a6ff8-ed4b-4f7e-82c3-e98e58b45de7')
                  code(nullFlavor: 'NA')
                  entryRelationship(typeCode: 'SUBJ') {
                    observation(classCode: 'OBS', moodCode: 'EVN') {
                      templateId(root: '2.16.840.1.113883.10.20.1.28')
                      id(root: 'ab1791b0-5c71-11db-b0de-0800200c9a66')
                      code(code: 'ASSERTION', codeSystem: '2.16.840.1.113883.5.4')
                      statusCode(code: 'completed')
                      effectiveTime {
                        low(value: '199701')
                      }
                      value(code: '233604007', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Pneumonia',
                          'xsi:type': 'CD')
                      entryRelationship(typeCode: 'REFR') {
                        observation(classCode: 'OBS', moodCode: 'EVN') {
                          templateId(root: '2.16.840.1.113883.10.20.1.50')
                          code(code: '33999-4', codeSystem: '2.16.840.1.113883.6.1', displayName: 'Status')
                          statusCode(code: 'completed')
                          value(code: '413322009', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Resolved',
                              'xsi:type': 'CE')
                        }
                      }
                    }
                  }
                }
              }
              entry(typeCode: 'DRIV') {
                act(classCode: 'ACT', moodCode: 'EVN') {
                  templateId(root: '2.16.840.1.113883.10.20.1.27')
                  id(root: 'd11275e9-67ae-11db-bd13-0800200c9a66')
                  code(nullFlavor: 'NA')
                  entryRelationship(typeCode: 'SUBJ') {
                    observation(classCode: 'OBS', moodCode: 'EVN') {
                      templateId(root: '2.16.840.1.113883.10.20.1.28')
                      id(root: '9d3d416d-45ab-4da1-912f-4583e0632000')
                      code(code: 'ASSERTION', codeSystem: '2.16.840.1.113883.5.4')
                      statusCode(code: 'completed')
                      effectiveTime {
                        low(value: '199903')
                      }
                      value(code: '233604007', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Pneumonia',
                          'xsi:type': 'CD')
                      entryRelationship(typeCode: 'REFR') {
                        observation(classCode: 'OBS', moodCode: 'EVN') {
                          templateId(root: '2.16.840.1.113883.10.20.1.50')
                          code(code: '33999-4', codeSystem: '2.16.840.1.113883.6.1', displayName: 'Status')
                          statusCode(code: 'completed')
                          value(code: '413322009', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Resolved',
                              'xsi:type': 'CE')
                        }
                      }
                    }
                  }
                  entryRelationship(typeCode: 'SUBJ', inversionInd: 'true') {
                    observation(classCode: 'OBS', moodCode: 'EVN') {
                      templateId(root: '2.16.840.1.113883.10.20.1.41')
                      code(code: 'ASSERTION', codeSystem: '2.16.840.1.113883.5.4')
                      statusCode(code: 'completed')
                      value(code: '404684003', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Clinical finding',
                          'xsi:type': 'CD') {
                        qualifier {
                          name(code: '246456000', displayName: 'Episodicity')
                          value(code: '288527008', displayName: 'New episode')
                        }
                      }
                      entryRelationship(typeCode: 'SAS') {
                        act(classCode: 'ACT', moodCode: 'EVN') {
                          id(root: 'ec8a6ff8-ed4b-4f7e-82c3-e98e58b45de7')
                          code(nullFlavor: 'NA')
                        }
                      }
                    }
                  }
                }
              }
              entry(typeCode: 'DRIV') {
                act(classCode: 'ACT', moodCode: 'EVN') {
                  templateId(root: '2.16.840.1.113883.10.20.1.27')
                  id(root: '5a2c903c-bd77-4bd1-ad9d-452383fbfefa')
                  code(nullFlavor: 'NA')
                  entryRelationship(typeCode: 'SUBJ') {
                    observation(classCode: 'OBS', moodCode: 'EVN') {
                      templateId(root: '2.16.840.1.113883.10.20.1.28')
                      code(code: 'ASSERTION', codeSystem: '2.16.840.1.113883.5.4')
                      statusCode(code: 'completed')
                      effectiveTime {
                        low(value: '199701')
                      }
                      value(code: '22298006', codeSystem: '2.16.840.1.113883.6.96',
                          displayName: 'Myocardial infarction', 'xsi:type': 'CD')
                      entryRelationship(typeCode: 'REFR') {
                        observation(classCode: 'OBS', moodCode: 'EVN') {
                          templateId(root: '2.16.840.1.113883.10.20.1.50')
                          code(code: '33999-4', codeSystem: '2.16.840.1.113883.6.1', displayName: 'Status')
                          statusCode(code: 'completed')
                          value(code: '413322009', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Resolved',
                              'xsi:type': 'CE')
                        }
                      }
                    }
                  }
                }
              }
            }
          }
          component {
            section {
              templateId(root: '2.16.840.1.113883.10.20.1.4')
              code(code: '10157-6', codeSystem: '2.16.840.1.113883.6.1')
              title('Family history')

              entry(typeCode: 'DRIV') {
                organizer(moodCode: 'EVN', classCode: 'CLUSTER') {
                  templateId(root: '2.16.840.1.113883.10.20.1.23')
                  statusCode(code: 'completed')
                  subject {
                    relatedSubject(classCode: 'PRS') {
                      code(code: '9947008', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Biological father')
                      subject {
                        administrativeGenderCode(code: 'M', codeSystem: '2.16.840.1.113883.5.1', displayName: 'Male')
                        birthTime(value: '1912')
                      }
                    }
                  }
                  component {
                    observation(classCode: 'OBS', moodCode: 'EVN') {
                      templateId(root: '2.16.840.1.113883.10.20.1.42')
                      id(root: 'd42ebf70-5c89-11db-b0de-0800200c9a66')
                      code(code: 'ASSERTION', codeSystem: '2.16.840.1.113883.5.4')
                      statusCode(code: 'completed')
                      value(code: '22298006', codeSystem: '2.16.840.1.113883.6.96', displayName: 'MI', 'xsi:type': 'CD')
                      entryRelationship(typeCode: 'CAUS') {
                        observation(classCode: 'OBS', moodCode: 'EVN') {
                          id(root: '6898fae0-5c8a-11db-b0de-0800200c9a66')
                          code(code: 'ASSERTION', codeSystem: '2.16.840.1.113883.5.4')
                          statusCode(code: 'completed')
                          value(code: '419099009', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Dead',
                              'xsi:type': 'CD')
                        }
                      }
                      entryRelationship(typeCode: 'SUBJ', inversionInd: 'true') {
                        observation(classCode: 'OBS', moodCode: 'EVN') {
                          templateId(root: '2.16.840.1.113883.10.20.1.38')
                          code(code: '397659008', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Age')
                          statusCode(code: 'completed')
                          value(value: '57', 'xsi:type': 'INT')
                        }
                      }
                    }
                  }
                  component {
                    observation(classCode: 'OBS', moodCode: 'EVN') {
                      templateId(root: '2.16.840.1.113883.10.20.1.22')
                      id(root: '5bfe3ec0-5c8b-11db-b0de-0800200c9a66')
                      code(code: 'ASSERTION', codeSystem: '2.16.840.1.113883.5.4')
                      statusCode(code: 'completed')
                      value(code: '59621000', codeSystem: '2.16.840.1.113883.6.96', displayName: 'HTN',
                          'xsi:type': 'CD')
                      entryRelationship(typeCode: 'SUBJ', inversionInd: 'true') {
                        observation(classCode: 'OBS', moodCode: 'EVN') {
                          templateId(root: '2.16.840.1.113883.10.20.1.38')
                          code(code: '397659008', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Age')
                          statusCode(code: 'completed')
                          value(value: '40', 'xsi:type': 'INT')
                        }
                      }
                    }
                  }
                }
              }
              entry(typeCode: 'DRIV') {
                organizer(moodCode: 'EVN', classCode: 'CLUSTER') {
                  templateId(root: '2.16.840.1.113883.10.20.1.23')
                  statusCode(code: 'completed')
                  subject {
                    relatedSubject(classCode: 'PRS') {
                      code(code: '65656005', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Biological mother')
                      subject {
                        administrativeGenderCode(code: 'F', codeSystem: '2.16.840.1.113883.5.1', displayName: 'Female')
                        birthTime(value: '1912')
                      }
                    }
                  }
                  component {
                    observation(classCode: 'OBS', moodCode: 'EVN') {
                      templateId(root: '2.16.840.1.113883.10.20.1.22')
                      id(root: 'a13c6160-5c8b-11db-b0de-0800200c9a66')
                      code(code: 'ASSERTION', codeSystem: '2.16.840.1.113883.5.4')
                      statusCode(code: 'completed')
                      effectiveTime {
                        low(value: '1942')
                      }
                      value(code: '195967001', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Asthma',
                          'xsi:type': 'CD')
                    }
                  }
                }
              }
            }
          }
          component {
            section {
              templateId(root: '2.16.840.1.113883.10.20.1.15')
              code(code: '29762-2', codeSystem: '2.16.840.1.113883.6.1')
              title('Social History')
              entry(typeCode: 'DRIV') {
                observation(classCode: 'OBS', moodCode: 'EVN') {
                  templateId(root: '2.16.840.1.113883.10.20.1.33')
                  id(root: '9b56c25d-9104-45ee-9fa4-e0f3afaa01c1')
                  code(code: '230056004', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Cigarette smoking')
                  statusCode(code: 'completed')
                  effectiveTime {
                    low(value: '1947')
                    high(value: '1972')
                  }
                  value('1 pack per day', 'xsi:type': 'ST')
                }
              }
              entry(typeCode: 'DRIV') {
                observation(classCode: 'OBS', moodCode: 'EVN') {
                  templateId(root: '2.16.840.1.113883.10.20.1.33')
                  id(root: '45efb604-7049-4a2e-ad33-d38556c9636c')
                  code(code: '230056004', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Cigarette smoking')
                  statusCode(code: 'completed')
                  effectiveTime {
                    low(value: '1973')
                  }
                  value('None', 'xsi:type': 'ST')
                  entryRelationship(typeCode: 'SUBJ', inversionInd: 'true') {
                    observation(classCode: 'OBS', moodCode: 'EVN') {
                      templateId(root: '2.16.840.1.113883.10.20.1.41')
                      code(code: 'ASSERTION', codeSystem: '2.16.840.1.113883.5.4')
                      statusCode(code: 'completed')
                      value(code: '404684003', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Clinical finding',
                          'xsi:type': 'CD') {
                        qualifier {
                          name(code: '246456000', displayName: 'Episodicity')
                          value(code: '288527008', displayName: 'New episode')
                        }
                      }
                      entryRelationship(typeCode: 'SAS') {
                        observation(classCode: 'OBS', moodCode: 'EVN') {
                          id(root: '9b56c25d-9104-45ee-9fa4-e0f3afaa01c1')
                          code(code: '230056004', codeSystem: '2.16.840.1.113883.6.96',
                              displayName: 'Cigarette smoking')
                        }
                      }
                    }
                  }
                }
              }
              entry(typeCode: 'DRIV') {
                observation(classCode: 'OBS', moodCode: 'EVN') {
                  templateId(root: '2.16.840.1.113883.10.20.1.33')
                  id(root: '37f76c51-6411-4e1d-8a37-957fd49d2cef')
                  code(code: '160573003', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Alcohol consumption')
                  statusCode(code: 'completed')
                  effectiveTime {
                    low(value: '1973')
                  }
                  value('None', 'xsi:type': 'ST')
                }
              }
            }
          }
          component {
            section {
              templateId(root: '2.16.840.1.113883.10.20.1.2')
              code(code: '48765-2', codeSystem: '2.16.840.1.113883.6.1')
              title('Allergies, Adverse Reactions, Alerts')

              entry(typeCode: 'DRIV') {
                act(classCode: 'ACT', moodCode: 'EVN') {
                  templateId(root: '2.16.840.1.113883.10.20.1.27')
                  id(root: '36e3e930-7b14-11db-9fe1-0800200c9a66')
                  code(nullFlavor: 'NA')
                  entryRelationship(typeCode: 'SUBJ') {
                    observation(classCode: 'OBS', moodCode: 'EVN') {
                      templateId(root: '2.16.840.1.113883.10.20.1.18')
                      id(root: '4adc1020-7b14-11db-9fe1-0800200c9a66')
                      code(code: 'ASSERTION', codeSystem: '2.16.840.1.113883.5.4')
                      statusCode(code: 'completed')
                      value(code: '282100009', codeSystem: '2.16.840.1.113883.6.96',
                          displayName: 'Adverse reaction to substance', 'xsi:type': 'CD')
                      participant(typeCode: 'CSM') {
                        participantRole(classCode: 'MANU') {
                          playingEntity(classCode: 'MMAT') {
                            code(code: '70618', codeSystem: '2.16.840.1.113883.6.88', displayName: 'Penicillin')
                          }
                        }
                      }
                      entryRelationship(typeCode: 'MFST', inversionInd: 'true') {
                        observation(classCode: 'OBS', moodCode: 'EVN') {
                          templateId(root: '2.16.840.1.113883.10.20.1.54')
                          code(code: 'ASSERTION', codeSystem: '2.16.840.1.113883.5.4')
                          statusCode(code: 'completed')
                          value(code: '247472004', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Hives',
                              'xsi:type': 'CD')
                        }
                      }
                      entryRelationship(typeCode: 'REFR') {
                        observation(classCode: 'OBS', moodCode: 'EVN') {
                          templateId(root: '2.16.840.1.113883.10.20.1.39')
                          code(code: '33999-4', codeSystem: '2.16.840.1.113883.6.1', displayName: 'Status')
                          statusCode(code: 'completed')
                          value(code: '55561003', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Active',
                              'xsi:type': 'CE')
                        }
                      }
                    }
                  }
                }
              }
              entry(typeCode: 'DRIV') {
                act(classCode: 'ACT', moodCode: 'EVN') {
                  templateId(root: '2.16.840.1.113883.10.20.1.27')
                  id(root: 'eb936010-7b17-11db-9fe1-0800200c9a66')
                  code(nullFlavor: 'NA')
                  entryRelationship(typeCode: 'SUBJ') {
                    observation(classCode: 'OBS', moodCode: 'EVN') {
                      templateId(root: '2.16.840.1.113883.10.20.1.18')
                      id(root: 'eb936011-7b17-11db-9fe1-0800200c9a66')
                      code(code: 'ASSERTION', codeSystem: '2.16.840.1.113883.5.4')
                      statusCode(code: 'completed')
                      value(code: '282100009', codeSystem: '2.16.840.1.113883.6.96',
                          displayName: 'Adverse reaction to substance', 'xsi:type': 'CD')
                      participant(typeCode: 'CSM') {
                        participantRole(classCode: 'MANU') {
                          playingEntity(classCode: 'MMAT') {
                            code(code: '1191', codeSystem: '2.16.840.1.113883.6.88', displayName: 'Aspirin')
                          }
                        }
                      }
                      entryRelationship(typeCode: 'MFST', inversionInd: 'true') {
                        observation(classCode: 'OBS', moodCode: 'EVN') {
                          templateId(root: '2.16.840.1.113883.10.20.1.54')
                          code(code: 'ASSERTION', codeSystem: '2.16.840.1.113883.5.4')
                          statusCode(code: 'completed')
                          value(code: '56018004', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Wheezing',
                              'xsi:type': 'CD')
                        }
                      }
                      entryRelationship(typeCode: 'REFR') {
                        observation(classCode: 'OBS', moodCode: 'EVN') {
                          templateId(root: '2.16.840.1.113883.10.20.1.39')
                          code(code: '33999-4', codeSystem: '2.16.840.1.113883.6.1', displayName: 'Status')
                          statusCode(code: 'completed')
                          value(code: '55561003', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Active',
                              'xsi:type': 'CE')
                        }
                      }
                    }
                  }
                }
              }
              entry(typeCode: 'DRIV') {
                act(classCode: 'ACT', moodCode: 'EVN') {
                  templateId(root: '2.16.840.1.113883.10.20.1.27')
                  id(root: 'c3df3b61-7b18-11db-9fe1-0800200c9a66')
                  code(nullFlavor: 'NA')
                  entryRelationship(typeCode: 'SUBJ') {
                    observation(classCode: 'OBS', moodCode: 'EVN') {
                      templateId(root: '2.16.840.1.113883.10.20.1.18')
                      id(root: 'c3df3b60-7b18-11db-9fe1-0800200c9a66')
                      code(code: 'ASSERTION', codeSystem: '2.16.840.1.113883.5.4')
                      statusCode(code: 'completed')
                      value(code: '282100009', codeSystem: '2.16.840.1.113883.6.96',
                          displayName: 'Adverse reaction to substance', 'xsi:type': 'CD')
                      participant(typeCode: 'CSM') {
                        participantRole(classCode: 'MANU') {
                          playingEntity(classCode: 'MMAT') {
                            code(code: '2670', codeSystem: '2.16.840.1.113883.6.88', displayName: 'Codeine')
                          }
                        }
                      }
                      entryRelationship(typeCode: 'MFST', inversionInd: 'true') {
                        observation(classCode: 'OBS', moodCode: 'EVN') {
                          templateId(root: '2.16.840.1.113883.10.20.1.54')
                          code(code: 'ASSERTION', codeSystem: '2.16.840.1.113883.5.4')
                          statusCode(code: 'completed')
                          value(code: '73879007', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Nausea',
                              'xsi:type': 'CD')
                        }
                      }
                      entryRelationship(typeCode: 'REFR') {
                        observation(classCode: 'OBS', moodCode: 'EVN') {
                          templateId(root: '2.16.840.1.113883.10.20.1.39')
                          code(code: '33999-4', codeSystem: '2.16.840.1.113883.6.1', displayName: 'Status')
                          statusCode(code: 'completed')
                          value(code: '55561003', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Active',
                              'xsi:type': 'CE')
                        }
                      }
                    }
                  }
                }
              }
            }
          }
          component {
            section {
              templateId(root: '2.16.840.1.113883.10.20.1.8')
              code(code: '10160-0', codeSystem: '2.16.840.1.113883.6.1')
              title('Medications')
              informant {
                assignedEntity {
                  id(extension: '996-756-495', root: '2.16.840.1.113883.19.5')
                  representedOrganization {
                    id(root: '2.16.840.1.113883.19.5')
                    name('Good Health Clinic')
                  }
                }
              }
              entry(typeCode: 'DRIV') {
                substanceAdministration(classCode: 'SBADM', moodCode: 'EVN') {
                  templateId(root: '2.16.840.1.113883.10.20.1.24')
                  id(root: 'cdbd33f0-6cde-11db-9fe1-0800200c9a66')
                  statusCode(code: 'active')
                  effectiveTime('xsi:type': 'PIVL_TS') {
                    period(value: '6', unit: 'h')
                  }
                  routeCode(code: 'IPINHL', codeSystem: '2.16.840.1.113883.5.112',
                      codeSystemName: 'RouteOfAdministration', displayName: 'Inhalation, oral')
                  doseQuantity(value: '2')
                  administrationUnitCode(code: '415215001', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Puff')
                  consumable {
                    manufacturedProduct {
                      templateId(root: '2.16.840.1.113883.10.20.1.53')
                      manufacturedMaterial {
                        code(code: '307782', codeSystem: '2.16.840.1.113883.6.88',
                            displayName: 'Albuterol 0.09 MG/ACTUAT inhalant solution') {
                          originalText('Albuterol inhalant')
                        }
                      }
                    }
                  }
                  precondition(typeCode: 'PRCN') {
                    criterion {
                      code(code: 'ASSERTION', codeSystem: '2.16.840.1.113883.5.4')
                      value(code: '56018004', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Wheezing',
                          'xsi:type': 'CE')
                    }
                  }
                }
              }
              entry(typeCode: 'DRIV') {
                substanceAdministration(classCode: 'SBADM', moodCode: 'EVN') {
                  templateId(root: '2.16.840.1.113883.10.20.1.24')
                  id(root: 'cdbd5b05-6cde-11db-9fe1-0800200c9a66')
                  statusCode(code: 'active')
                  effectiveTime('xsi:type': 'PIVL_TS') {
                    period(value: '24', unit: 'h')
                  }
                  routeCode(code: 'PO', codeSystem: '2.16.840.1.113883.5.112', codeSystemName: 'RouteOfAdministration')
                  doseQuantity(value: '1')
                  consumable {
                    manufacturedProduct {
                      templateId(root: '2.16.840.1.113883.10.20.1.53')
                      manufacturedMaterial {
                        code(code: '309362', codeSystem: '2.16.840.1.113883.6.88',
                            displayName: 'Clopidogrel 75 MG oral tablet') {
                          originalText('Clopidogrel')
                        }
                        name('Plavix')
                      }
                    }
                  }
                }
              }
              entry(typeCode: 'DRIV') {
                substanceAdministration(classCode: 'SBADM', moodCode: 'EVN') {
                  templateId(root: '2.16.840.1.113883.10.20.1.24')
                  id(root: 'cdbd5b01-6cde-11db-9fe1-0800200c9a66')
                  statusCode(code: 'active')
                  effectiveTime('xsi:type': 'PIVL_TS') {
                    period(value: '12', unit: 'h')
                  }
                  routeCode(code: 'PO', codeSystem: '2.16.840.1.113883.5.112', codeSystemName: 'RouteOfAdministration')
                  doseQuantity(value: '1')
                  consumable {
                    manufacturedProduct {
                      templateId(root: '2.16.840.1.113883.10.20.1.53')
                      manufacturedMaterial {
                        code(code: '430618', codeSystem: '2.16.840.1.113883.6.88',
                            displayName: 'Metoprolol 25 MG oral tablet') {
                          originalText('Metoprolol')
                        }
                      }
                    }
                  }
                }
              }
              entry(typeCode: 'DRIV') {
                substanceAdministration(classCode: 'SBADM', moodCode: 'EVN') {
                  templateId(root: '2.16.840.1.113883.10.20.1.24')
                  id(root: 'cdbd5b03-6cde-11db-9fe1-0800200c9a66')
                  statusCode(code: 'active')
                  effectiveTime('xsi:type': 'IVL_TS') {
                    low(value: '20000328')
                  }
                  effectiveTime(operator: 'A', 'xsi:type': 'PIVL_TS') {
                    period(value: '24', unit: 'h')
                  }
                  routeCode(code: 'PO', codeSystem: '2.16.840.1.113883.5.112', codeSystemName: 'RouteOfAdministration')
                  doseQuantity(value: '1')
                  consumable {
                    manufacturedProduct {
                      templateId(root: '2.16.840.1.113883.10.20.1.53')
                      manufacturedMaterial {
                        code(code: '312615', codeSystem: '2.16.840.1.113883.6.88',
                            displayName: 'Prednisone 20 MG oral tablet') {
                          originalText('Prednisone')
                        }
                      }
                    }
                  }
                }
              }
              entry(typeCode: 'DRIV') {
                substanceAdministration(classCode: 'SBADM', moodCode: 'EVN') {
                  templateId(root: '2.16.840.1.113883.10.20.1.24')
                  id(root: 'cdbd5b07-6cde-11db-9fe1-0800200c9a66')
                  statusCode(code: 'completed')
                  effectiveTime('xsi:type': 'IVL_TS') {
                    low(value: '20000328')
                    high(value: '20000404')
                  }
                  effectiveTime(operator: 'A', 'xsi:type': 'PIVL_TS') {
                    period(value: '6', unit: 'h')
                  }
                  routeCode(code: 'PO', codeSystem: '2.16.840.1.113883.5.112', codeSystemName: 'RouteOfAdministration')
                  doseQuantity(value: '1')
                  consumable {
                    manufacturedProduct {
                      templateId(root: '2.16.840.1.113883.10.20.1.53')
                      manufacturedMaterial {
                        code(code: '197454', codeSystem: '2.16.840.1.113883.6.88',
                            displayName: 'Cephalexin 500 MG oral tablet') {
                          originalText('Cephalexin')
                        }
                        name('Keflex')
                      }
                    }
                  }
                  entryRelationship(typeCode: 'RSON') {
                    observation(classCode: 'COND', moodCode: 'EVN') {
                      templateId(root: '2.16.840.1.113883.10.20.1.28')
                      id(root: 'cdbd5b08-6cde-11db-9fe1-0800200c9a66')
                      code(code: 'ASSERTION', codeSystem: '2.16.840.1.113883.5.4')
                      statusCode(code: 'completed')
                      effectiveTime('xsi:type': 'IVL_TS') {
                        low(value: '20000328')
                      }
                      value(code: '32398004', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Bronchitis',
                          'xsi:type': 'CE')
                    }
                  }
                }
              }
            }
          }
          component {
            section {
              templateId(root: '2.16.840.1.113883.10.20.1.7')
              code(code: '46264-8', codeSystem: '2.16.840.1.113883.6.1')
              title('Medical Equipment')

              entry(typeCode: 'DRIV') {
                supply(classCode: 'SPLY', moodCode: 'EVN') {
                  templateId(root: '2.16.840.1.113883.10.20.1.34')
                  id(root: '2413773c-2372-4299-bbe6-5b0f60664446')
                  statusCode(code: 'completed')
                  effectiveTime('xsi:type': 'IVL_TS') {
                    center(value: '199911')
                  }
                  participant(typeCode: 'DEV') {
                    participantRole(classCode: 'MANU') {
                      templateId(root: '2.16.840.1.113883.10.20.1.52')
                      playingDevice {
                        code(code: '72506001', codeSystem: '2.16.840.1.113883.6.96',
                            displayName: 'Automatic implantable cardioverter/defibrillator')
                      }
                    }
                  }
                }
              }
              entry(typeCode: 'DRIV') {
                supply(classCode: 'SPLY', moodCode: 'EVN') {
                  templateId(root: '2.16.840.1.113883.10.20.1.34')
                  id(root: '230b0ab7-206d-42d8-a947-ab4f63aad795')
                  statusCode(code: 'completed')
                  effectiveTime('xsi:type': 'IVL_TS') {
                    center(value: '1998')
                  }
                  participant(typeCode: 'DEV') {
                    participantRole(classCode: 'MANU') {
                      templateId(root: '2.16.840.1.113883.10.20.1.52')
                      id(root: '03ca01b0-7be1-11db-9fe1-0800200c9a66')
                      playingDevice {
                        code(code: '304120007', codeSystem: '2.16.840.1.113883.6.96',
                            displayName: 'Total hip replacement prosthesis')
                      }
                      scopingEntity {
                        id(root: '0abea950-5b40-4b7e-b8d9-2a5ea3ac5500')
                        desc('Good Health Prostheses Company')
                      }
                    }
                  }
                }
              }
              entry(typeCode: 'DRIV') {
                supply(classCode: 'SPLY', moodCode: 'EVN') {
                  templateId(root: '2.16.840.1.113883.10.20.1.34')
                  id(root: 'c4ffe98e-3cd3-4c54-b5bd-08ecb80379e0')
                  statusCode(code: 'completed')
                  effectiveTime('xsi:type': 'IVL_TS') {
                    center(value: '1999')
                  }
                  participant(typeCode: 'DEV') {
                    participantRole(classCode: 'MANU') {
                      templateId(root: '2.16.840.1.113883.10.20.1.52')
                      playingDevice {
                        code(code: '58938008', codeSystem: '2.16.840.1.113883.6.96', displayName: 'Wheelchair')
                      }
                    }
                  }
                }
              }
            }
          }
          if (immunizations) {
            mkp.comment "\nImmunizations\n"
            component {
              section {
                templateId(root: '2.16.840.1.113883.10.20.1.6')
                code(code: '11369-6', codeSystem: '2.16.840.1.113883.6.1')
                title('Immunizations')

                immunizations.each { enc ->
                  entry(typeCode: 'DRIV') {
                    substanceAdministration(classCode: 'SBADM', moodCode: 'EVN') {
                      templateId(root: '2.16.840.1.113883.10.20.1.24')
                      id(root: UUID.randomUUID().toString())
                      statusCode(code: 'completed')
                      effectiveTime('xsi:type': 'IVL_TS') {
                        center(value: enc.date)
                      }
                      routeCode(code: enc.routeCode, codeSystem: enc.routeCodeSystem,
                          codeSystemName: enc.routeCodeSystemName,
                          displayName: enc.routeCodeDisplay)
                      consumable {
                        manufacturedProduct {
                          templateId(root: '2.16.840.1.113883.10.20.1.53')
                          manufacturedMaterial {
                            code(code: enc.code, codeSystem: enc.codeSystem, displayName: enc.displayName) {
                              originalText(enc.displayName)
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
          if (vitals) {
            mkp.comment "\nVitals\n"
            component {
              section {
                templateId(root: '2.16.840.1.113883.10.20.1.16')
                code(code: '8716-3', codeSystem: '2.16.840.1.113883.6.1')
                title('Vital Signs')

                vitals.each { enc ->
                  entry(typeCode: 'DRIV') {
                    organizer(classCode: 'CLUSTER', moodCode: 'EVN') {
                      templateId(root: '2.16.840.1.113883.10.20.1.35')
                      id(root: 'c6f88320-67ad-11db-bd13-0800200c9a66')
                      code(code: enc.code, codeSystem: enc.codeSystem, displayName: enc.displayName)
                      statusCode(code: 'completed')
                      effectiveTime(value: enc.date)
                      enc.components.each { c ->
                        component {
                          observation(classCode: 'OBS', moodCode: 'EVN') {
                            templateId(root: '2.16.840.1.113883.10.20.1.31')
                            id(root: 'c6f88321-67ad-11db-bd13-0800200c9a66')
                            code(code: c.code, codeSystem: c.codeSystem, displayName: c.displayName)
                            statusCode(code: 'completed')
                            effectiveTime(value: enc.date)
                            value(value: c.value, unit: c.unit, 'xsi:type': c.type)
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
          if (results) {
            mkp.comment "\nResults\n"

            component {
              section {
                templateId(root: '2.16.840.1.113883.10.20.1.14')
                code(code: '30954-2', codeSystem: '2.16.840.1.113883.6.1')
                title('Results')

                results.each { enc ->

                  entry(typeCode: 'DRIV') {
                    organizer(classCode: 'BATTERY', moodCode: 'EVN') {
                      templateId(root: '2.16.840.1.113883.10.20.1.32')
                      id(root: UUID.randomUUID().toString())
                      code(code: enc.code, codeSystem: enc.codeSystem, displayName: enc.displayName)
                      statusCode(code: 'completed')
                      effectiveTime(value: enc.date)
                      enc.components.each { c ->
                        component {
                          observation(classCode: 'OBS', moodCode: 'EVN') {
                            templateId(root: '2.16.840.1.113883.10.20.1.31')
                            id(root: UUID.randomUUID().toString())
                            code(code: c.code, codeSystem: c.codeSystem, displayName: c.displayName)
                            statusCode(code: 'completed')
                            effectiveTime(value: enc.date)
                            value(value: c.value, unit: c.unit, 'xsi:type': c.type)
                            interpretationCode(code: c.intCode, codeSystem: c.intSystem)
                            referenceRange {
                              observationRange {
                                text(c.refRange)
                              }
                            }
                          }
                        }
                      }

                    }
                  }
                }

              }
            }
          }
          if (procedures) {
            mkp.comment "\nProcedures\n"
            component {
              section {
                templateId(root: '2.16.840.1.113883.10.20.1.12')
                code(code: '47519-4', codeSystem: '2.16.840.1.113883.6.1')
                title('Procedures')

                procedures.each { enc ->

                  entry(typeCode: 'DRIV') {
                    procedure(classCode: 'PROC', moodCode: 'EVN') {
                      templateId(root: '2.16.840.1.113883.10.20.1.29')
                      id(root: UUID.randomUUID().toString())
                      code(code: enc.code, codeSystem: enc.codeSystem, displayName: enc.displayName) {
                        enc.qualifiers.each { q ->
                          qualifier {
                            name(code: q.name.code, displayName: q.name.displayName)
                            value(code: q.value.code, displayName: q.value.displayName)
                          }
                        }
                      }
                      statusCode(code: 'completed')
                      effectiveTime(value: enc.date)
                      participant(typeCode: 'DEV') {
                        participantRole(classCode: 'MANU') {
                          templateId(root: '2.16.840.1.113883.10.20.1.52')
                          id(root: '03ca01b0-7be1-11db-9fe1-0800200c9a66')
                        }
                      }
                    }
                  }
                }
              }
            }
          }
          printEncounters(x)
          printPlans(x)
        }
      }
    }
    def retval = sw.toString()
    log.debug "CCD size: ${retval.length()}, hash: ${generateMD5 (retval)}"
    return retval
  }

  def generateMD5(String s) {
    MessageDigest digest = MessageDigest.getInstance("MD5")
    digest.update(s.bytes);
    new BigInteger(1, digest.digest()).toString(16).padLeft(32, '0')
  }

  void printEncounters(def builder) {
    if (!encounters)
      return
    builder.component {
      section {
        templateId(root: '2.16.840.1.113883.10.20.1.3')
        code(code: '46240-8', codeSystem: '2.16.840.1.113883.6.1')
        title('Encounters')

        encounters.each { enc ->

          entry(typeCode: 'DRIV') {
            encounter(classCode: 'ENC', moodCode: 'EVN') {
              templateId(root: '2.16.840.1.113883.10.20.1.21')
              id(root: UUID.randomUUID().toString())
              code(code: enc.code, codeSystem: enc.codeSystem, displayName: enc.displayName)
              effectiveTime(value: enc.date)
              participant(typeCode: 'LOC') {
                templateId(root: '2.16.840.1.113883.10.20.1.45')
                participantRole(classCode: 'SDLOC') {
                  id(root: '2.16.840.1.113883.19.5')
                  playingEntity(classCode: 'PLC') {
                    name('Good Health Clinic')
                  }
                }
              }
              performer {
                assignedEntity {
                  assignedPerson {
                    name {
                      given(enc.docFirst)
                      family(enc.docLast)
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  void printPlans(def builder) {
    if (!plans)
      return
    builder.component {
      section {
        templateId(root: '2.16.840.1.113883.10.20.1.10')
        code(code: '18776-5', codeSystem: '2.16.840.1.113883.6.1')
        title('Plan')

        plans.each { enc ->
          entry(typeCode: 'DRIV') {
            observation(classCode: 'OBS', moodCode: 'RQO') {
              templateId(root: '2.16.840.1.113883.10.20.1.25')
              id(root: UUID.randomUUID().toString())
              code(code: enc.code, codeSystem: enc.codeSystem, displayName: enc.displayName)
              statusCode(code: 'new')
              effectiveTime {
                center(value: enc.date)
              }
            }
          }
        }
      }
    }
  }

  @ToString
  static class Immunization {
    String routeCode
    String routeCodeSystem
    String routeCodeSystemName
    String routeCodeDisplay

    String code
    String codeSystem
    String displayName
    String date
  }

  @ToString
  static class Vital {
    String code
    String codeSystem
    String displayName
    String date

    @ToString
    static class Component {
      String code, codeSystem, displayName
      String value, unit, type
    }

    List<Component> components

  }
  @ToString
  static class Result {
    String code
    String codeSystem
    String displayName
    String date

    @ToString
    static class Component {
      String code, codeSystem, displayName
      String value, unit, type
      String intCode, intSystem, refRange
    }

    List<Component> components
  }
  @ToString
  static class Procedure {
    String code
    String codeSystem
    String displayName
    String date
    @ToString
    static class Qualifier {
      @ToString
      static class CodedValue {
        String code
        String displayName
      }
      CodedValue name
      CodedValue value
    }
    List<Qualifier> qualifiers
  }
  @ToString
  static class Encounter {
    String code
    String codeSystem
    String displayName
    String date
    String docFirst
    String docLast
  }
  @ToString
  static class Plan {
    String code
    String codeSystem
    String displayName
    String date
  }

  List<Immunization> immunizations = []
  List<Vital> vitals = []
  List<Result> results = []
  List<Procedure> procedures = []
  List<Encounter> encounters = []
  List<Plan> plans = []
}
