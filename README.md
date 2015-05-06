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


supported HTTP status codes
---
200 OK
The request has succeeded.

400 Bad Request
The request could not be understood by the server due to malformed syntax.

404 Not Found
The server has not found anything matching the Request-URI.

405 Method Not Allowed
The method specified in the Request-Line is not allowed for the resource identified by the Request-URI.
TODO
The response MUST include an Allow header containing a list of valid methods for the requested resource.

500 Internal Server Error
The server encountered an unexpected condition which prevented it from fulfilling the request.

[status code definitions](http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html)


## XML configuration file

settings:
listen port: the listening port
statistics port: the port that the server serves the statistics page
log, access filepath: the access log file path
log, error filepath: the error log file path
documentroot: the root directory of the server
runphp: whether to run PHP scripts (optionally with parameters)
denyaccess, ip: IP address to deny service with optional CIDR notation

example configuration file:
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<ce325server>
    <listen port="8000" />
    <statistics port="8001" />
    <log>
        <access filepath="/path/to/access.log" />
        <error filepath="/path/to/error.log" />
    </log>
    <documentroot filepath="/path/to/document-root/" />
    <runphp>yes</runphp>
    <denyaccess>
        <ip>52.8.64.0/24</ip>
        <ip>128.44.0.0/16</ip>
    </denyaccess>
</ce325server>
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
    create and run the server


## Settings.java


## Server.java
    listen for requests

    spawn a worker thread for every incoming request


## Worker.java
    parse requests

    NEXT
    serve files with correct mimetype (especially *.html/*.htm ones)
        search for a library that gives you the mimetype of a file
