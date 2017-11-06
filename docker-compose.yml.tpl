version: '2'

services:
  siglaprint:
    image: docker.si.cnr.it/##{CONTAINER_ID}##
    command: java -Duser.country=IT -Duser.language=it -Dfile.encoding=UTF8 -Xmx1g -Xss512k -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8787 -Djava.security.egd=file:/dev/./urandom -jar /opt/sigla-print.war --print.server.url=http://sigla-print.test.si.cnr.it --logging.config=classpath:logback-local.xml --spring.profiles.active=common,prod
    mem_limit: 1g
    network_mode: bridge
    labels:
      SERVICE_NAME: "##{SERVICE_NAME}##"
    environment:
    - LANG=it_IT.UTF-8
    - LANGUAGE=it_IT:it
    - LC_ALL=it_IT.UTF-8
