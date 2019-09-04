package versioned

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class ItemNotVersionedSpec extends Specification implements DomainUnitTest<ItemNotVersioned> {

    def setup() {
    }

    def cleanup() {
    }

    void "test something"() {
        expect:"fix me"
            true == false
    }
}
