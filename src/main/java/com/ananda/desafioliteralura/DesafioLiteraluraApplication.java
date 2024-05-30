package com.ananda.desafioliteralura;

import com.ananda.desafioliteralura.models.Idioma;
import com.ananda.desafioliteralura.repository.AutorRepository;
import com.ananda.desafioliteralura.repository.IdiomaRepository;
import com.ananda.desafioliteralura.repository.LibroRepository;
import com.ananda.desafioliteralura.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ananda.desafioliteralura.principal.Principal;


@SpringBootApplication
public class DesafioLiteraluraApplication implements CommandLineRunner {

	@Autowired
	private LibroService libroService;

	@Autowired
	private LibroRepository libroRepository;

	@Autowired
	private AutorRepository autorRepository;

	@Autowired
	private IdiomaRepository idiomaRepository;

	public static void main(String[] args) {
		SpringApplication.run(DesafioLiteraluraApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal(libroService, libroRepository, autorRepository, idiomaRepository);
		principal.ejecutaElPrograma();
	}

}

