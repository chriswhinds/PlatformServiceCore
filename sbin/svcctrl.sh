# ------------------------------------------------------------------------------------
# svcrun.sh - Start/Stop Script for the platform Service Adapter Container
#
# Environment Variable Prequisites
#
#   SERVICE_HOME (Optional) May point at your Servers installation directory.
#                 If not present, the current working directory is assumed.
#
#   JAVA_HOME     Optional, point to a differnet version of java if not the system default for the operating systems installation.
#
#Created: svcctrl.sh,v 1.0 2016/06/27 20:01:21 chinds
# -----------------------------------------------------------------------------


# UnComment this to set tha java home from the ENV VAR JAVA_HOME or from some explicit location Setup
#if [ "x$JAVA_HOME" = "x" ]; then
#	JAVA_HOME=/home/chinds/jdk1.6.0_21
#fi


#SET UP SERVICE HOME
SERVICE_HOME=`cd ..;pwd`

echo "Service Running from "$SERVICE_HOME

if [ "x$JAVA_HOME" -ne "x" ]; then
	echo "Service Using JDK from "$JAVA_HOME
fi




#SET runtime vars
SERVICE_NAME="$1"
INSTANCE_ID="$2"
SERVICE_LIB=${SERVICE_HOME}/lib
CONFIG_LOC=${SERVICE_HOME}/conf
LOG_LOC=${SERVICE_HOME}/logs

#DEBUG OPTIONS
JAVA_DEBUG_OPTIONS="-Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=62100,server=y,suspend=n"

#SET THE CLASSPATH
CLASSPATH=".:${SERVICE_HOME}/bin"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/activation.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/aspectj-1.6.9.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/aspectjrt.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/aspectjtools.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/aspectjweaver.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/axis.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/commons-configuration-1.6.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/commons-discovery-0.2.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/commons-lang-2.5.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/commons-logging-1.1.1.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/commons-dbcp-1.4.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/commons-pool-1.5.4.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/commons-collections-3.2.1.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/dsn.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/imap.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/jaxrpc.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/jbossall-client.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/jboss-common.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/jbossesb-rosetta.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/jbossesb-spring.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/jboss-jmx.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/jbossjta-integration.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/jbossjta.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/jboss-management.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/jboss-messaging.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/jboss-monitoring.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/jboss-remoting-int.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/jboss-remoting.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/jboss-saaj.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/jboss-serialization.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/jboss-srp.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/jbosssx.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/jboss-system.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/jboss-transaction.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/jboss-xml-binding.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/jboss-xml-binding.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/jboss-aop-jdk50.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/jms.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/javassist.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/log4j-1.2.16.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/mailapi.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/mail.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/trove.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/org.aspectj.matcher.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/org.springframework.beans-3.0.3.RELEASE.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/org.springframework.context.support-3.0.3.RELEASE.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/org.springframework.context-3.0.3.RELEASE.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/org.springframework.core-3.0.3.RELEASE.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/org.springframework.jdbc-3.0.3.RELEASE.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/org.springframework.aop-3.0.3.RELEASE.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/org.springframework.asm-3.0.3.RELEASE.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/org.springframework.spring-library-3.0.3.RELEASE.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/org.springframework.transaction-3.0.3.RELEASE.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/org.springframework.expression-3.0.3.RELEASE.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/pop3.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/saaj.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/smtp.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/wsdl4j-1.5.1.jar"
CLASSPATH="${CLASSPATH}:${SERVICE_LIB}/xstream-1.3.1.jar"

echo "=====Service Class Path BEGIN ================"
echo "${CLASSPATH}"
echo "=====Service Class Path END ================"
LOGGING_CONFIG="-Dlog.config.loc=${SERVICE_HOME}/config/logconfig.xml"
SERVICE_BOOTSTRAP_CLASS="com.bowne.service.services.ServiceBootstrap"
ARGS=""
STDOUT_LOG="${SERVICE_HOME}/logs/errors.log"
STDERR_LOG="${SERVICE_HOME}/logs/service.log"

#SET THE JAVA CMD
JAVA_CMD="${JAVA_HOME}/bin/java"

#RUN the SERVICE
#START THE RUN
# ----- Execute The Requested Command -----------------------------------------
echo "====Starting Service Using..."
echo "JDK CMD : $JAVA_CMD"
echo "JDK DEBUG OPTIONS : $JAVA_DEBUG_OPTIONS"
echo "SERVICE SPRING CONFIG LOC: $SPRING_SERVICE_CONFIG"
echo "SERVICE PROPERTIES LOC: $SERVICE_PROPERTIES_CONFIG"
echo "LOGGING CONFIG LOC: $LOGGING_CONFIG"
echo "SERVICE BOOTSTRAP CLASS NAME: $SERVICE_BOOTSTRAP_CLASS"
echo "CLASS ARGS: $ARGS"
echo "STDOUT LOG: $STDOUT_LOG"
echo "STDERR LOG: $STDERR_LOG"
echo "====Starting Service Using..."

exec $JAVA_CMD $JAVA_DEBUG_OPTIONS -cp $CLASSPATH $SPRING_SERVICE_CONFIG $LOGGING_CONFIG $SERVICE_PROPERTIES_CONFIG $SERVICE_BOOTSTRAP_CLASS $ARGS 1>$STDOUT_LOG 2>$STDERR_LOG &
# === Dinamically generate the shutdown script
echo "kill -9 "$! >shutdown.sh
chmod 777 shutdown.sh
