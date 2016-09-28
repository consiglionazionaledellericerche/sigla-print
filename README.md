Sigla print [![Build Status](http://build.si.cnr.it/job/dev-sigla-print-master/badge/icon)](http://build.si.cnr.it/job/dev-sigla-print-master/)
===

# Usage

    curl -s -u foo:bar  http://localhost:8080/metrics | jq ''

    curl -s -u foo:bar -X POST http://localhost:8080/api/v1/print -H'Content-type: application/json' --data '{"name":"xyz","path":"reports/logs/batchlog.jrxml"}'

    curl -s -u foo:bar -X POST http://localhost:8080/api/v1/hook -H'Content-type: application/json' --data @src/test/resources/lint.json
