Hi there. Tt's my first project on Java. This is a simple network chat.
There are server and clients that connect to the server.
The entire history of notifications is stored in temp for each client and can be viewed at any time.
I used Javafx, Java.io, Java.net, Java.sql, Log4j on this project.

### Docker build:
```
docker build . -t january-chat-server
```
### Docker save built image in .tar file:
```
docker save -o target/dist/january-chat-server.tar chat-server
```
### Simple container run:
```
docker run -d --name january-chat-server january-chat-server
```
#### -add port forwarding:
```
-p 8189:8189
```
