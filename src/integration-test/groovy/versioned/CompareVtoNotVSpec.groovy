package versioned

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import org.springframework.dao.DuplicateKeyException
import spock.lang.Specification
import spock.lang.Unroll

@Integration
@Rollback
class CompareVtoNotVSpec extends Specification {

    def sessionFactory

    def setup() {
        def logger = sessionFactory.currentSession.jdbcCoordinator.statementPreparer.jdbcService.sqlStatementLogger
        logger.logToStdout = true
    }

    def cleanup() {
    }

    void "Can Create"() {
        expect: "Can create with databinding"
        def i1 = new ItemVersioned(id: "a001", description: "test").save(flush: true, failOnError: true)
        def i2 = new ItemNotVersioned(id: "a001", description: "test").save(flush: true, failOnError: true)

        i1.id == "a001"
        i1.description == "test"

        i2.id == "a001"
        i2.description == "test"
    }

    @Unroll
    void "Detects already existing item #classType"() {
        given: "An already existing item"
        def item = classType.newInstance()
        item.properties = props
        item.save(flush: true, failOnError: true)
        sessionFactory.currentSession.clear()

        when: "I Create a 2nd item"
        def item2 = classType.newInstance()
        item2.properties = props
        def res=item2.save(flush: true, failOnError: true)  // can be fixed with insert:true

        then:
        println "result from save = $res"
        def e=thrown(DuplicateKeyException)

        where:
        classType        | props
        ItemVersioned    | [id: "a001", description: "description"]
        ItemNotVersioned | [id: "a001", description: "description"]

    }

    void "nonVersioned save returns true, but doesn't save the data"() {
        given: "An existing item"
        def item=new ItemNotVersioned(id: "a001", description: "first")
        item.save(flush:true, failOnError:true)
        sessionFactory.currentSession.clear()

        when: "I save a new item with the same id (which should fail)"
        def item2=new ItemNotVersioned(id: "a001", description: "second")
        def res=item2.save(flush:true, failOnError:true)

        then: "save with flush returned true, so it should have gone to the database"
        res
        item2.description == "second"    // looks good so far

        println "Getting from session"
        ItemNotVersioned.get("a001").description == "second" // still looks good, but it was pulled from the session.
        println "Calling list, but it still pulls from session"
        ItemNotVersioned.list()[0].description == "second"   // still looks good, but it was pulled from the session.

        when: "the session is cleared"
        sessionFactory.currentSession.clear()

        then:
        item2.description == "second"    // looks good so far
        println "Actually queries the database"
        ItemNotVersioned.get("a001").description == "second" // the item never got flushed.

        println "Doesn't get ehre, because previous line failed"
        ItemNotVersioned.list()[0].description == "second"   // the item still wasn't flushed.
    }

}
