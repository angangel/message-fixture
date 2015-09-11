# Maven setup #

Since MQ is a proprietary software, the JAR files required for message-fixture can not be re-distributed. Therefore, you will need to use the JAR files from your own installation of MQ. The following describes the steps you must perform.

  1. Upload MQ JARs
    1. You will need a local installation of MQ server (will possibly also work with the free client, but it is still untested).
    1. Note that if your MQ version differes from that below, you should use a different version in the commands. You will also have to update pom.xml found in the project root to correctly use your version.
    1. On Windows, open a Command prompt and run
```
mvn install:install-file -DgroupId=mq -DartifactId=com.ibm.mq -Dversion=6.0.2.0 -Dpackaging=jar -Dfile="%MQ_JAVA_LIB_PATH%"/com.ibm.mq.jar
mvn install:install-file -DgroupId=mq -DartifactId=com.ibm.mqjms -Dversion=6.0.2.0 -Dpackaging=jar -Dfile="%MQ_JAVA_LIB_PATH%"/com.ibm.mqjms.jar
mvn install:install-file -DgroupId=mq -DartifactId=dhbcore -Dversion=6.0.2.0 -Dpackaging=jar -Dfile="%MQ_JAVA_LIB_PATH%"/dhbcore
```
  1. You will also have to download the [PCF helper classes for MQ](http://www-1.ibm.com/support/docview.wss?rs=203&uid=swg24000668&loc=en_US&cs=utf-8&lang=en).
    1. After unzipping, run the following command
```
mvn install:install-file -DgroupId=mq -DartifactId=com.ibm.mq.pcf -Dversion=6.0.3 -Dpackaging=jar -Dfile=com.ibm.mq.pcf-6.0.3.jar
```

# Eclipse setup #
Since the WMB toolkit requires some special settings in the Eclipse files (.project and .classpath), we have included them in SVN. So, you should not run Maven to generate them.

## Todo ##
Include shell commands for Linux