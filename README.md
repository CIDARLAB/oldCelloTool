# CelloTool

```
cd lib/
mvn install:install-file -Dfile=eugene-2.0.1-SNAPSHOT-jar-with-dependencies.jar -DgroupId=org.cidarlab -DartifactId=eugene -Dversion=2.0.1-SNAPSHOT -Dclassifier=withDependencies -Dpackaging=jar
mvn install:install-file -Dfile=byuediftools-0.5.2.jar -DgroupId=edu.byu.ece -DartifactId=byuediftools -Dversion=0.5.2 -Dpackaging=jar
cd ../cellotool
mvn package

java -cp "./target/*" org.cellocad.dnacompiler.runtime.Main -verilogFile test/dnacompiler/and_gate.v \
     	 	      					    -targetDataDir test/dnacompiler \
							    -targetDataFile Eco1C1G1T0-synbiohub.UCF.json \
							    -configDir test/dnacompiler \
							    -configFile config.json \
							    -outputDir /path/to/output/dir
```

