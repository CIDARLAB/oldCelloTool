# CelloTool

```
cd lib/
mvn install:install-file -Dfile=eugene-2.0.1-SNAPSHOT-jar-with-dependencies.jar -DgroupId=org.cidarlab -DartifactId=eugene -Dversion=2.0.1-SNAPSHOT -Dclassifier=withDependencies -Dpackaging=jar
mvn install:install-file -Dfile=byuediftools-0.5.2.jar -DgroupId=edu.byu.ece -DartifactId=byuediftools -Dversion=0.5.2 -Dpackaging=jar
cd ../cellotool
mvn package
```
