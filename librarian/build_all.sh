#!/bin/bash

cd "${BASH_SOURCE%/*}" || exit

echo "Compiling and installing all modules..."
lein modules do clean, install

echo "Building scraper executable..."
cd scraper
lein build-scraper

echo "Done."
