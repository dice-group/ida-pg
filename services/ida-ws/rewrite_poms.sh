#!/usr/bin/env bash

# Removes librarian dependencies from pom files. Used for dependency caching.

p='s/<dependency>\s*<groupId>librarian<\/groupId>\s*<artifactId>[^<]*<\/artifactId>\s*<version>[^<]*<\/version>\s*<\/dependency>//g'

for f in librarian/modules/*/pom.xml; do
	content=$(cat $f | tr '\n' ' ')
	newContent=$(echo $content | sed $p)
	echo $newContent > $f
done
