**Library for working with databases in asynchronous mode**

Provides easy interaction with the database asynchronously.
After creating the object Dbtools with the help Builder,
we can give commands to read or write data,
they will be executed in asynchronous mode (use Task poll),
the database opens and closes automatically (no active tasks),
the callbacks are separated for data and errors for the possibility
of using lambda expressions Java 8.
You can also simple add a ready database from assets or the file system.
This library is under development and does not have full functionality