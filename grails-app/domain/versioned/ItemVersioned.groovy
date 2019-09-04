package versioned

class ItemVersioned {

    static constraints = {
        id bindable:true
    }

    static mapping = {
        id generator: "assigned"
    }

    String id
    String description

}
