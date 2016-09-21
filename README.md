# test-spring-boot-reactive-netty

This repository tests an appearing issue when using Netty to serve a Spring Boot-enabled *Reactive Web* application.

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

# How to replicate

Requirements: Java 8, Maven 3, git client, web browser (Chrome or Firefox). Tested on several Operating Systems:
Mac OS 10.11 El Capitan, Windows 10, Ubuntu 16.

  * Clone this repository
  * `cd` to the project's folder
  * Start the app with `mvn -U clean compile spring-boot:run`
  * Open browser and go to `http://localhost:8080`
  * If a small piece of JSON loads OK the first time, hit refresh several times until some of those *refreshes* stop responding at all


# Issue description

Environments in which this has been replicated:

   * Chrome 52, Postman (Chrome-based), Firefox 49 and Safari 9.1.3 on **Mac OS X 10.11 El Capitan**.
   * Chrome 52 and Firefox 49 on **Windows 10**.
   * Chromium 52 and Firefox 48 on **Ubuntu 16**.

Symptoms: only alternate requests have response: 1. Response, 2. No response, 3. Response, 4. No response…

By means of debugging it has been determined that Netty **does** receive an HTTP request in every case. But no HTTP
response is sent half of the times.

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



