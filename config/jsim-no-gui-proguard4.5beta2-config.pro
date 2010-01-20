-injars ../jar/simulator.jar
# -injars ../lib(**/*.jar;)

# library jars:
# here put only the required jars!
-injars ../lib/log4j/log4j-1.2.15.jar
-injars ../lib/commons-cli/commons-cli-1.1.jar
-injars ../lib/commons-logging/commons-logging-1.1.1.jar

-outjars ../jar/jsim-no-gui.jar

-libraryjars <java.home>/../Classes/classes.jar

-verbose

# do not obfuscate:
-dontobfuscate
# do not shrink:
-dontshrink
# do not optimize:
-dontoptimize
# do not preverify:
-dontpreverify

# options we do not use now:
# -optimizationpasses 5
# -ignorewarnings
# -printconfiguration ../jar/jsim-all-proguard4.5beta2-config.txt
# -printseeds ../jar/jsim-all-seeds.txt

# do not write dead code:
# -printusage ../jar/jsim-dead-code.txt

