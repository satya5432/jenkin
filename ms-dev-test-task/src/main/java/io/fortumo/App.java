package io.fortumo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * This is a server app!
 *
 */
//@ComponentScan
//@SpringBootApplication
public class App {
	public static void main(String[] args) {
		//SpringApplication.run(App.class, args);
		int[] i = {1};
		App a = new App();
		a.in(i);
		System.out.println(i[0]);
	}

	private void in(int[] i) {
		i[0]++;
		
	}
}
