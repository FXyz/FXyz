#!/usr/bin/env bash

# Release artifacts
./gradlew :FXyz-Core:bintrayUpload -PbintrayUsername=$bintrayUsername -PbintrayApiKey=$bintrayApiKey
./gradlew :FXyz-Client:bintrayUpload -PbintrayUsername=$bintrayUsername -PbintrayApiKey=$bintrayApiKey
./gradlew :FXyz-Importers:bintrayUpload -PbintrayUsername=$bintrayUsername -PbintrayApiKey=$bintrayApiKey
