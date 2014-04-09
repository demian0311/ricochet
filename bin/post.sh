#!/bin/sh

HOST='http://localhost:9000'

JSON="{\"duration\": $RANDOM}"

time curl -s -k \
   -X POST -d "$JSON" \
   --header "content-type: application/json" \
   "$HOST/gauge/foo/bar" 

echo "\n\n"


time curl -s -k \
   -X POST -d "$JSON" \
   --header "content-type: application/json" \
   "$HOST/reactivegauge/foo/bar" 
