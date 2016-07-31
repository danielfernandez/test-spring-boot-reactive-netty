# test-spring-boot-reactive-netty

This repository tests a possible issue when using Netty to serve a Spring Boot-enabled *Reactive Web* application.

This application is very simple: it was created at [http://start.spring.io](http://start.spring.io) and the following
modification was made to its `pom.xml` in order to disable the Tomcat starter and use Netty instead:

```xml
<dependency>
    <groupId>org.springframework.boot.experimental</groupId>
    <artifactId>spring-boot-starter-web-reactive</artifactId>
    <exclusions>
        <!-- Excluded because we will be using netty -->
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<dependency>
    <groupId>io.projectreactor.ipc</groupId>
    <artifactId>reactor-netty</artifactId>
</dependency>
```

The controller is very simple:
```java
@RestController
public class TestSpringBootReactiveNettyController {

	@RequestMapping(value = "/")
	public Mono<SomeBean> root() {
		return Mono.just(new SomeBean("Ola mundo", "Hola mundo"));
	}

}
```

# THE ISSUE

The issue that this tries to show is that, when accessed from a browser, many times no HTTP response is sent by netty.
This happens approximately once each two requests (one request receives no answer, the following does).

Tested browsers: Chrome on MacOS, Postman (Chrome-based) on MacOS, Firefox on MacOS.

By means of debugging it has been determined that Netty **does** receive an HTTP request, even if no response
is issued.

By means of network sniffing it has been determined that the HTTP request looks alright, and exactly the same
both when there is HTTP response and when there isn't. Example:

```
GET / HTTP/1.1
Host: localhost:8080
User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:45.0) Gecko/20100101 Firefox/45.0
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
Accept-Language: en-US,en;q=0.5
Accept-Encoding: gzip, deflate
Connection: keep-alive
```

This **cannot be replicated using `curl`, `httpie` or `wget`**. Only browsers seem to show this behaviour (or
Postman, which works on the Chrome engine).

Also, this behaviour also happens when the application is configured to return HTML (actually this issue initially
appeared at the [Thymeleaf + Spring Reactive sandbox application](https://github.com/thymeleaf/thymeleafsandbox-springreactive)).

