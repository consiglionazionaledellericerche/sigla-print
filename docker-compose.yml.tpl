siglaprint:
  image: docker.si.cnr.it/##{CONTAINER_ID}##
  command: echo siglaprint
  #command: java -Xmx512m -Xss512k -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8787 -Djava.security.egd=file:/dev/./urandom -jar /opt/sigla-print.war --print.server.url=http://sigla-print.test.si.cnr.it
  mem_limit: 1024m
  environment:
  - SERVICE_TAGS=webapp
  - SERVICE_NAME=##{SERVICE_NAME}##
