## Synopsis
Command-line app that allows a user to download partial chunks of a binary file
Once the chunks are downloaded, the app will reassemble the file and clean up the chunks
The app is configurable by changing the default application.properties or by issuing the 
setup command (more details below)

Make sure to change the save directory and output file to point to a valid location on the file system.

## Dependencies

This app is built using Gradle 4.2
This app requires Java 8+ to run

## Building and Running

> navigate to the project root

> gradle build

> java -jar build/libs/file-downloader-0.0.1-SNAPSHOT.jar

Spring Shell will boot up and provide a shell prompt:
shell:>

## Command Reference

Spring Shell provides syntax highlighting, help and auto-complete

Command will highlight when valid

general help
>shell:>help

command help
>shell:>help setup

print current configuration
>shell:>env

modify all configuration parameters
>shell:>setup --url <url> --save-directory <directory> --output-file <path-to-file> --chunk-size <size in bytes> --chunks <number of chunks to download

modify single configuration parameter example
>shell:>setup --chunk-size <size in bytes>

download file using current configuration
>shell:>download

download file using ad-hoc url
>shell:>download http://s3.amazon.com/somefile

## Tests

Unit Tests are automatically run as part of the gradle build process
Skip tests: 
>gradle build -x test