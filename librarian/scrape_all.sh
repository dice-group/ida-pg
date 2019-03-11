#!/bin/bash

cd "${BASH_SOURCE%/*}" || exit

for lib in $(find libs/* -type d)
do
	echo "Scraping $lib..."
	java -jar scraper/target/librarian-scraper.jar s $lib
done

echo "Done."
