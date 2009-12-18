-injars ../jar/simulator.jar
-injars ../lib(**/*.jar;)

-outjars ../jar/jsim-all.jar(!META-INF/MANIFEST.MF)

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

