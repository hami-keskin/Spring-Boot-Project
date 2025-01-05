package com.example.ProductService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProductServiceApplicationTests {

	private final ApplicationContext applicationContext;

	// Constructor-based dependency injection
	public ProductServiceApplicationTests(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Test
	void contextLoads() {
		// Application context'in yüklendiğinden emin oluruz.
		assertThat(applicationContext).isNotNull();
	}

	@Test
	void testBeanLoad() {
		// Örneğin, ProductService bean'inin yüklendiğini kontrol edebiliriz.
		boolean isProductServiceBeanPresent = applicationContext.containsBean("productService");
		assertThat(isProductServiceBeanPresent).isTrue();
	}
}
