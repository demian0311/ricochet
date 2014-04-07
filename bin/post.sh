#!/bin/sh

HOST='http://localhost:9000'

JSON='{"duration": 20}'

curl -s -k \
   -X POST -d "$JSON" \
   --header "content-type: application/json" \
   --verbose \
   "$HOST/metric/foo/bar" 

echo "\n\n"
