#!/usr/bin/env bash

# Just a small script to manually test whether clustering works. Faster than typing everything out in the web frontend.

curl 'http://localhost:8080/ida-ws//message/sendmessage?msg=I%20would%20like%20to%20run%20the%20k_means%20algorithm%20on%20the%20current%20table&actvScrId=1&actvTbl=movehubcostofliving.csv&actvDs=city' -c /tmp/ida-jar
echo ""

curl 'http://localhost:8080/ida-ws//message/sendmessage?msg=Optional%20parameters%20should%20be%20n_clusters&actvScrId=1&actvTbl=movehubcostofliving.csv&actvDs=city' -b /tmp/ida-jar
echo ""

curl 'http://localhost:8080/ida-ws//message/sendmessage?msg=Set%20n_clusters%20as%205&actvScrId=1&actvTbl=movehubcostofliving.csv&actvDs=city' -b /tmp/ida-jar
echo ""

curl 'http://localhost:8080/ida-ws//message/sendmessage?msg=Set%20precompute_distances%20as%20auto&actvScrId=1&actvTbl=movehubcostofliving.csv&actvDs=city' -b /tmp/ida-jar
echo ""

curl 'http://localhost:8080/ida-ws//message/sendmessage?msg=Clustering%20features%20are%20wine,%20cinema%20and%20gasoline&actvScrId=1&actvTbl=movehubcostofliving.csv&actvDs=city' -b /tmp/ida-jar
echo ""

echo "Run clustering..."

curl 'http://localhost:8080/ida-ws//message/sendmessage?msg=Label%20feature%20should%20be%20city&actvScrId=1&actvTbl=movehubcostofliving.csv&actvDs=city' -b /tmp/ida-jar
