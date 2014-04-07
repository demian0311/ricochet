#!/bin/sh

HOST='http://localhost:9000'

JSON="{\"duration\": $RANDOM}"

curl -s -k \
   -X POST -d "$JSON" \
   --header "content-type: application/json" \
   --verbose \
   "$HOST/gauge/foo/bar" 

echo "\n\n"
