package versioned

class ItemNotVersioned {

    static constraints = {
        id bindable:true
    }

    static mapping = {
        version false
        id generator: "assigned"
    }

    String id
    String description

}
