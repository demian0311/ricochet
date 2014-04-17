#!/bin/sh

HOST='http://localhost:9000/reactivegauge/foo/bar'
JSON="{\"duration\": $RANDOM}"

for iPost in {0..100}
do
   time curl $HOST
   sleep .01
done

