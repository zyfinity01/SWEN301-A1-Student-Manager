## <u>Is the design prone to memory leaks?</u>

The StudentManager class uses static HashMaps to cache student and degree data. This enhances performance but might cause memory leaks. The more unique students and degrees added, the bigger the HashMaps get, using more system memory.
Right now, the system keeps the cached data in sync with the database when students are added, changed, or removed. But there's no way to clear out old or unused data, which might interfere with garbage collection. A potential fix is to add rules to remove older entries, a method known as cache eviction, or use a special tool to manage this. This could help avoid memory problems.


## <u>How to setup and use the application:</u>
Firstly install the studentdb library to your local maven repository. This can be done by running the following command in the root directory of the project:
``` 
mvn install:install-file -Dfile=lib/studentdb-1.3.1.jar -DgroupId=nz.ac.wgtn.swen301 -DartifactId=studentdb -Dversion=1.3.1 -Dpackaging=jar
```
Then run the following command to build the project:
```
mvn package
```
This will create a jar file in the target directory called studentfinder.jar. This can be run using the following command:
```
java -cp target/studentfinder.jar nz.ac.wgtn.swen301.assignment1.cli.StudentManagerUI -fetchone 45 
```
