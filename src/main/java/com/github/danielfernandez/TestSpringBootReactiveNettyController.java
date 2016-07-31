package com.github.danielfernandez;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class TestSpringBootReactiveNettyController {

	@RequestMapping(value = "/")
	public Mono<SomeBean> root() {
		return Mono.just(new SomeBean("Ola mundo", "Hola mundo"));
	}

}
