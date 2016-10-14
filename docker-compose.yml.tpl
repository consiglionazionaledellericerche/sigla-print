siglaprint:
  image: docker.si.cnr.it/##{CONTAINER_ID}##
  command: java -Duser.country=IT -Duser.language=it -Dfile.encoding=UTF8 -Xmx2g -Xss512k -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8787 -Djava.security.egd=file:/dev/./urandom -jar /opt/sigla-print.war --print.server.url=http://sigla-print.test.si.cnr.it --logging.config=classpath:logback-local.xml --spring.profiles.active=common,prod
  mem_limit: 2g
  environment:
  - SERVICE_TAGS=webapp
  - SERVICE_NAME=##{SERVICE_NAME}##
  - LANG=it_IT.UTF-8
  - LANGUAGE=it_IT:it
  - LC_ALL=it_IT.UTF-8
