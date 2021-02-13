This folder should be configured as a local Maven repository containing the DRAC JAR file with the appropriate groupId and artifactId so that the main project's `pom.xml` file can find it.

To accomplish this, run something like the following from the root directory of the project:

```
mvn deploy:deploy-file -DgroupId=com.avocent.idrac -DartifactId=kvm -Dversion=0.0.1 -Durl=file:./lib/ -DrepositoryId=drac-jar-repo -DupdateReleaseInfo=true -Dfile=<PATH_TO_avctKVM.jar>
```

