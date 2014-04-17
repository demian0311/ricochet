#!/bin/sh

HOST='http://localhost:9000/reactivegauge/foo/bar'
JSON="{\"duration\": $RANDOM}"

for iPost in {0..100}
do
   time curl -s -k \
      -X POST -d "$JSON" \
      --header "content-type: application/json" \
      "$HOST" &
   sleep .25
done

time curl $HOST
