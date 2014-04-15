#!/bin/sh

HOST='http://localhost:9000'

JSON="{\"duration\": $RANDOM}"

time curl $HOST/gauge/foo/bar

time curl $HOST/reactivegauge/foo/bar
