package com.ananda.desafioliteralura.principal;

import com.ananda.desafioliteralura.models.*;
import com.ananda.desafioliteralura.service.LibroService;
import com.ananda.desafioliteralura.repository.LibroRepository;
import com.ananda.desafioliteralura.repository.AutorRepository;
import com.ananda.desafioliteralura.repository.IdiomaRepository;
import com.ananda.desafioliteralura.service.ConsumoAPI;
import com.ananda.desafioliteralura.service.Conversor;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {
    private Scanner teclado = new Scanner(System.in);

    private LibroService libroService;
    @Autowired
    private LibroRepository libroRepository;
    @Autowired
    private AutorRepository autorRepository;
    @Autowired
    private IdiomaRepository idiomaRepository;

    private DatosDeListaDeLibros datosDeListaDeLibros;
    private DatosDeLibro datosDeLibro;
    private List<DatosDeLibro> datosDeLibros = new ArrayList<>();
    private Integer numeroDeLibros;

    private Libro libro = new Libro();
    private List<Libro> libros = new ArrayList<>();
    private List<Autor> autores = new ArrayList<>();
    private List<Idioma> idiomas = new ArrayList<>();

    private String json;

    @Autowired
    public Principal(LibroService libroService, LibroRepository libroRepository, AutorRepository autorRepository, IdiomaRepository idiomaRepository) {
        this.libroService = libroService;
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
        this.idiomaRepository = idiomaRepository;
    }

    public void ejecutaElPrograma() throws JsonProcessingException {
        String menu = """
                ---------------
                Elije la opción a través de un número:
                1 - buscar libro por título
                2 - listar libros registrados
                3 - listar autores registrados
                4 - listar autores vivos en un determinado año
                5 - listar libros por idioma                
                0 - Salir
                """;

        Integer opcion = -1;

        while (opcion != 0) {
            System.out.println(menu);
            if (teclado.hasNextInt()) {
                opcion = teclado.nextInt();
                teclado.nextLine();
            } else {
                opcion = -1;
                teclado.nextLine();
            }

            switch (opcion) {
                case 1:
                    registraLibros();
                    break;
                case 2:
                    muestraLibrosRegistrados();
                    break;
                case 3:
                    muestraAutoresRegistrados();
                    break;
                case 4:
                    muestraAutoresDeUnAnio();
                    break;
                case 5:
                    muestraLibrosPorIdioma();
                    break;
                case 0:
                    System.out.println("Terminando la aplicación...");
                    break;
                default:
                    System.out.println("!Opción inválida!");
                    break;
            }
        }
    }

    //-------------------------------------
    // Registrar libros en la base de datos
    private void registraLibros() {

        System.out.println("Escribe el título del libro que te interesa");
        String tituloBuscado = teclado.nextLine().replace(" ", "+");
        json = ConsumoAPI.obtenDatos("https://gutendex.com/books/?search=" + tituloBuscado);

        // Analizar la respuesta JSON
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode rootNode = objectMapper.readTree(json);
            // Verificar si la respuesta contiene resultados
            if (!(rootNode.has("results") && rootNode.get("results").size() > 0)) {
                System.out.println("El título buscado no existe.");
            } else {
                System.out.println(json);

                // Convierte el JSON a una lista de DatosDeLibro
                numeroDeLibros = rootNode.get("results").size();
                datosDeListaDeLibros = new Conversor().convierte(json, DatosDeListaDeLibros.class);
                datosDeLibros = datosDeListaDeLibros.datosDeLibros();

                // Escoger el libre que se guardará en la base de datos
                for (int i = 0; i < numeroDeLibros; i++) {
                    System.out.println("----------");
                    System.out.println(i+1);
                    System.out.println("Título: " + datosDeLibros.get(i).titulo());
                    System.out.println("Autores: " + datosDeLibros.get(i).autores());
                    System.out.println("Idiomas: " + datosDeLibros.get(i).idiomas());
                    System.out.println("Número de descargas: " + datosDeLibros.get(i).descargas());
                }
                System.out.println();
                System.out.println("Escribe el número del libro que quieres guardar");
                System.out.println("(si escribes 0, no se guardará ninguno)");

                // Leer la respuesta del usuario y guardar/o no guardar los datos
                if (teclado.hasNextInt()) {
                    Integer opcionLibro = teclado.nextInt();
                    teclado.nextLine();
                    if ((opcionLibro > 0) && (opcionLibro <= numeroDeLibros)) {
                        DatosDeLibro datosDeLibro = datosDeLibros.get(opcionLibro-1);
                        try {
                            // Intentar guardar el libro
                            libroService.saveLibro(datosDeLibro);
                            // Mostrar mensaje de confirmación
                            System.out.println("Se guardó el libro número " + opcionLibro);
                        } catch (DataIntegrityViolationException e) {
                            // Manejar la excepción cuando el libro ya existe en la DB
                            System.out.println("El libro ya existe en la base de datos.");
                        }
                    } else {
                        System.out.println("No se guardará ningún libro.");
                    }
                } else {
                    teclado.nextLine();
                    System.out.println("No se guardará ningún libro.");
                }
            }
        } catch (JsonProcessingException e) {
            // Manejar la excepción cuando Json no da resultados
            throw new RuntimeException(e);
        }
    } // Termina registraLibros

    //-------------------------------------
    // Muestra libros registrados en la base de datos
    private void muestraLibrosRegistrados(){

        libros = libroRepository.findAll();
        imprimeLibros(libros);

    } // Termina muestraLibrosRegistrados

    //-------------------------------------
    // Muestra autores registrados en la base de datos con sus libros
    private void muestraAutoresRegistrados(){

        autores = autorRepository.findAll();
        imprimeAutores(autores);

    } // Termina muestraAutoresRegistrados

    //-------------------------------------
    // Muestra autores que vivieron cierto año y sus libros
    private void muestraAutoresDeUnAnio(){

        System.out.println("Escribe el año en cual vivieron los autores que te interesan");
        Integer opcion;
        if (teclado.hasNextInt()) {
            opcion = teclado.nextInt();
            teclado.nextLine();
            autores = autorRepository.encuentraAutoresPorAnio(opcion);
            imprimeAutores(autores);
        } else {
            System.out.println("Año invalido");;
            teclado.nextLine();
        }
    } // Termina muestraAutoresDeUnAnio

    //-------------------------------------
    // Muestra libros de un idioma escogido
    private void muestraLibrosPorIdioma(){

        // Mostrar los idiomas existentes y recibir el idioma del usuario
        idiomas = idiomaRepository.findAll();
        List<String> listaIdiomas = new ArrayList<>();
        System.out.println("Tenemos libros de siguientes idiomas:");
        idiomas.forEach(i -> System.out.print(i.getNombre() + "  "));
        System.out.println();
        System.out.println("Escribe el idioma que te interesa");
        String idiomaDeseado = teclado.nextLine();

        // Checar si este idioma existe y mostrar los libros de este idioma
        Optional<Idioma> existingIdioma = idiomaRepository.findByNombre(idiomaDeseado);
        if (existingIdioma.isPresent()) {
            libros = libroRepository.encuentaLibrosPorIdioma(existingIdioma.get().getId());
            imprimeLibros(libros);
        } else {
            System.out.println("Este idioma no existe");
        }
    } // Termina muestraLibrosPorIdioma

    //-------------------------------------
    // Imprime lista de libros
    private void imprimeLibros(List<Libro> libros){

        libros.forEach(libro -> {
            System.out.println("-----------------");
            System.out.println("Título: " + libro.getTitulo());

            autores = new ArrayList<>();
            autores = libroRepository.encuentraAutoresDeLibro(libro.getId());
            System.out.println("Autor(es): " + autores.get(0).getNombre());
            for (int i = 1; i < autores.size(); i++) {
                System.out.println("           " + autores.get(i).getNombre());
            }

            idiomas = new ArrayList<>();
            idiomas = libroRepository.encuentraIdiomasDeLibro(libro.getId());
            System.out.println("Idioma(s): " + idiomas.get(0).getNombre());
            for (int i = 1; i < idiomas.size(); i++) {
                System.out.println("           " + idiomas.get(i).getNombre());
            }

            System.out.println("Número de descargas: " + libro.getDescargas());
            System.out.println();
        });
    } // Termina imprimeLibros

    //-------------------------------------
    // Imprime lista de autores con sus libros
    private void imprimeAutores(List<Autor> autores){

        if (!autores.isEmpty()) {
            autores.forEach(autor -> {
                System.out.println("-----------------");
                System.out.println("Autor: " + autor.getNombre());
                System.out.println("Año de nacimiento: " + autor.getNacimiento());
                System.out.println("Año de fallecimiento: " + autor.getFallecimiento());

                libros = libroRepository.encuentraLibrosDeAutor(autor.getId());
                System.out.print("Libro(s): ");
                System.out.println(libros.get(0).getTitulo());
                for (int i = 1; i < libros.size(); i++) {
                    System.out.println("          " + libros.get(i).getTitulo());
                }
                System.out.println();
            });
        } else {
            System.out.println("No hay ningún autor para mostrar");
        }
    } // Termina imprimeAutores
}
