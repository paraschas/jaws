Jaws -- A simple Java web server.
===

## description

uses the CSS stylesheets and the favicon from [HTML5 Boilerplate](https://html5boilerplate.com/) version 5.2.0


## specification

multi-threaded

does not parse PHP files

gets configuration from an XML file passed as an argument

display a page with statistics regarding the uptime, the number of connections, the errors occured, the mean page serve time

keeps an access log

TODO
TEST
keeps an error log

supported file types:
text files, jpg, png, tiff, bmp, avi, mpg4, mp3, ogg, pdf, ms-word, ms-excel, ms-ppt

on a directory resource request, serve index.htm or index.html if one exists;

else return an HTML page with the contents of the directory as links, including a link to the parent directory, if one exists (see document root)


## not supported

sessions state

cookies

HTTP method except for GET

authentication (optional to implement this)


## HTTP protocol

the only supported method is the GET method of HTTP version 1.0 and 1.1

TODO
no supported headers for the request for HTTP 1.0 and only the Host header for HTTP 1.1

headers send by the server: Date, Server, Last-Modified, Connection, Content-Length, Content-Type


supported HTTP status codes
---
200 OK
The request has succeeded.

TODO
400 Bad Request
The request could not be understood by the server due to malformed syntax.

404 Not Found
The server has not found anything matching the Request-URI.

405 Method Not Allowed
The method specified in the Request-Line is not allowed for the resource identified by the Request-URI.
TODO
The response MUST include an Allow header containing a list of valid methods for the requested resource.

TODO
500 Internal Server Error
The server encountered an unexpected condition which prevented it from fulfilling the request.

[status code definitions](http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html)


## XML configuration file

listen port: the listening port
statistics port: the port that the server serves the statistics page
log, access filepath: the access log file path
log, error filepath: the error log file path
documentroot: the root directory of the server
runphp: whether to run PHP scripts (not implemented, will not implement)
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

## Runner.java
    get configuration from an XML file
        parse xml file
        store the configuration data to a Settings object
        get and check the data from the Settings object
    create and start the resources server
    create and start the statistics server


## Server.java
    listen for requests of resources or the statistics page
    spawn a worker thread for every incoming request


## ResourcesWorker.java
    parse requests
    serve files, set Content-Length, Content-Type (mimetype)
    generate and serve an html page with the contents of the directory that was requested
        create links for each file in the directory


## StatisticsWorker.java
    parse requests
    generate and serve the statistics page


## Settings.java
    store the settings of the server; handle with accessors and mutators


## Statistics.java
    store and update statistics for the server
