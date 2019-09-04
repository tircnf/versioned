A grails 3.3.10 application with two domain objects.

ItemVersioned and ItemNotVersioned.

They are classes with an assigned id of type String.  
One of the classes has a version: false in the mapping Block.

Calling save on ItemNotVersioned will silently fail and not update the database if the primary key is already in the
database.

