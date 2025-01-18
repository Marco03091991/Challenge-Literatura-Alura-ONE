package com.alurachallenge.literatura;

import com.alurachallenge.literatura.main.MainClass;
import com.alurachallenge.literatura.repository.AuthorsRepository;
import com.alurachallenge.literatura.repository.BooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiteraturaApplication implements CommandLineRunner {

	@Autowired
	private BooksRepository bookRepository;

	@Autowired
	private AuthorsRepository authorRepository;


	public static void main(String[] args) {
		SpringApplication.run(LiteraturaApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		MainClass mainClass = new MainClass(bookRepository, authorRepository);
		mainClass.showMenu();
	}
}
