# CelloTool

```
cd cellotool
mvn install

java -cp "./target/*" org.cellocad.dnacompiler.runtime.Main -verilogFile test/dnacompiler/and_gate.v \
     	 	      					    -targetDataDir test/dnacompiler \
							    -targetDataFile Eco1C1G1T0-synbiohub.UCF.json \
							    -configDir test/dnacompiler \
							    -configFile config.json \
							    -outputDir /path/to/output/dir
```

## dependencies
You must have >=readline-7.0 installed for the bundled yosys binaries to run.
