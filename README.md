Jaws -- A simple Java web server.
===

## description


## specification

multi-threaded

parses PHP files if enabled in the configuration

gets configuration from an XML file passed as an argument

display a page with statistics regarding the uptime, the number of connections, the errors occured, the mean page serve time

keeps a log of the connections and the errors

supported file types:
text files, jpg, png, tiff, bmp, avi, mpg4, mp3, ogg, pdf, ms-word, ms-excel, ms-ppt

on a directory resource request, serve index.htm or index.html if one exists; else parse index.php and return the result if it exists; else return an HTML page with the contents of the directory as links, including a link to the parent directory, if one exists (see document root)


## not supported

sessions state

cookies

HTTP method except for GET

authentication (optional to implement this)


## HTTP protocol

the only supported method is the GET method of HTTP version 1.0 and 1.1

no supported headers for the request for HTTP 1.0 and only the Host header for HTTP 1.1

headers send by the server: Date, Server, Last-Modified, Connection, Content-Length, Content-Type

supported HTTP status codes:
200 OK: succesful reply
400 Bad Request: the server can't understand the request
404 Not Found: the client requests a non existing resource
405 Method Not Allowed: the request method isn't GET
500 Internal Server Error: an exception in the server


## XML configuration file

settings:
listen port: the listening port
statistics port: the port that the server serves the statistics page
log, access filepath: the access log file path
log, error filepath: the error log file path
document-root: the root directory of the server
run-php: whether to run PHP scripts (optionally with parameters)
deny-access, ip: IP address to deny service with optional CIDR notation

example configuration file:
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<ce325-server>
    <listen port="8000" />
    <statistics port="8001" />
    <log>
        <access filepath="/path/to/access.log" />
        <error filepath="/path/to/error.log" />
    </log>
    <document-root filepath="/path/to/document-root/" />
    <run-php>yes</run-php>
    <deny-access>
        <ip>52.8.64.0/24</ip>
        <ip>128.44.0.0/16</ip>
    </deny-access>
</ce325-server>
```


## server logs

access log entry format:

<IP address> - <connection datetime> - <request URL> - <response code> - <user-agent HTTP request header>

the error log entry format could be:

<IP address> - <request datetime> - <HTTP request header> - <exception stack trace>


architecture
---

## Run.java
    get configuration from an XML file
        parse xml file
        store the configuration data to a Settings object
        get and check the data from the Settings object
    NEXT
    check the server
    start the server


## Settings.java


## Server.java

spawn worker threads

listen for requests

place them in a job queue


notes
---
search for a library that gives you the mimetype of a file
