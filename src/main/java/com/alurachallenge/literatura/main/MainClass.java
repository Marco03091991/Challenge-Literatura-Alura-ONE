package com.alurachallenge.literatura.main;

import com.alurachallenge.literatura.model.Author;
import com.alurachallenge.literatura.model.Book;
import com.alurachallenge.literatura.model.DataAuthor;
import com.alurachallenge.literatura.model.DataBook;
import com.alurachallenge.literatura.repository.AuthorsRepository;
import com.alurachallenge.literatura.repository.BooksRepository;
import com.alurachallenge.literatura.service.ConsumoApi;
import com.alurachallenge.literatura.service.ConvierteDatos;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageRequest;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MainClass {
    private final String BASE_URL = "https://gutendex.com/books/";
    private BooksRepository booksRepository;
    private AuthorsRepository authorsRepository;
    private Scanner keyboard = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    ConvierteDatos convierteDatos = new ConvierteDatos();
    private List<Book> books;
    private List<Author> authors;

    public MainClass(BooksRepository booksRepository, AuthorsRepository authorsRepository) {
        this.booksRepository = booksRepository;
        this.authorsRepository = authorsRepository;
    }

    public void showMenu() {
        var optionSelected = -1;
        while (optionSelected != 0) {
            var menu = """
                    ********BIENVENID@ A LA APP LITERATURA*********
                    
                    *****ELIGE UNA OPCION******
                    1 - Buscar Libro por Titulo
                    2 - Mostrar Libros ya Registrados
                    3 - Mostrar Autores ya Registrados
                    4 - Mostrar Autores vivos (por año)
                    5 - Mostrar Libros Por idiomas
                    
                    0 - Salir
                    """;
            System.out.println(menu);
            optionSelected = keyboard.nextInt();
            keyboard.nextLine();

            switch (optionSelected) {
                case 1:
                    searchBookInWeb();
                    break;
                case 2:
                    listBooksRegistered();
                    break;
                case 3:
                    listAuthorsRegistered();
                    break;
                case 4:
                    listAuthorsAlives();
                    break;
                case 5:
                    listBooksLanguage();
                    break;
                default:
                    System.out.println("ingresa una opcion valida");
            }


        }

    }

    //////////////////////////////menú 1 ////////////////////////////////////////
    private void searchBookInWeb() {
        try {
            System.out.println("Ingrese el nombre del libro");
            var titleBook = keyboard.nextLine();
            var jsonRequest = consumoApi.obtenerDatos(BASE_URL + "?search=" + titleBook.replace(" ", "+"));

            // Convertir la respuesta JSON a un Map
            var originalData = convierteDatos.obtenerDatos(jsonRequest, Map.class);
            var results = (List<Map<String, Object>>) originalData.get("results");

            if (results == null || results.isEmpty()) {
                System.out.println("No se ha encontrado el libro solicitado.");
                return; // Salir temprano si no hay resultados
            }

            // Procesar solo el primer libro encontrado
            var firstResult = results.get(0);
            processBook(firstResult);

        } catch (Exception e) {
            System.out.println("Error al procesar los datos: " + e.getMessage());
        }
    }

    // Función auxiliar para procesar un libro y sus autores
    private void processBook(Map<String, Object> result) {
        try {
            var dataBookJson = convierteDatos.obtenerDatos(new ObjectMapper().writeValueAsString(result), DataBook.class);
            Book book = new Book(dataBookJson);

            // Verificar si el libro ya existe en la base de datos antes de insertarlo
            if (booksRepository.existsByTitle(book.getTitle())) {
                System.out.println("El libro ya está registrado: " + book.getTitle());
            } else {
                // Guardar el libro en la base de datos
                saveBook(book);
            }

            // Procesar los autores de este libro
            processAuthors(dataBookJson.authors());

        } catch (Exception e) {
            System.out.println("Error al procesar el libro: " + e.getMessage());
        }
    }

    // Función auxiliar para guardar un libro
    private void saveBook(Book book) {
        try {
            booksRepository.save(book);
            System.out.println("----Libro----");
            System.out.println("Titulo: " + book.getTitle());
            System.out.println("Autor: " + book.getAuthor());
            System.out.println("Idioma:  " + book.getLanguage());
            System.out.println("Numero de descargas: " + book.getDownload_count());
            System.out.println("-------------");
            System.out.println(" ");

        } catch (Exception e) {
            System.out.println("********");
            System.out.println("Ha ocurrido un error durante el registro del libro: " + e.getMessage());
            System.out.println("********");
        }
    }

    // Función auxiliar para procesar los autores
    private void processAuthors(List<DataAuthor> authors) {
        for (DataAuthor dataAuthor : authors) {
            try {
                Author author = new Author(dataAuthor);
                if (!authorsRepository.existsByName(author.getName())) {
                    authorsRepository.save(author);
                    System.out.println("Autor guardado en la Base de datos: " + author.getName());
                } else {
                    System.out.println("El Autor se encuentra ya registrado en base de datos: " + author.getName());
                }
            } catch (Exception e) {
                System.out.println("Ha ocurrido un error durante el registro del autor: " + e.getMessage());
            }
        }
    }
    ////////////////////////////////fin menu  1////////////////////////////////

    /////////////////////////menú 2 ////////////////////////////////
    private void listBooksRegistered() {
        try {
            books = booksRepository.findAll();
            if (books.isEmpty()) {
                System.out.println("********");
                System.out.println("No hay registros disponibles");
                System.out.println("********");
                System.out.println("Presione Enter para continuar");
                keyboard.nextLine();
            } else {
                books.forEach(l -> {
                    System.out.println("------Libro-----");
                    System.out.println("Libro: " + l.getTitle());
                    System.out.println("Autor: " + l.getAuthor());
                    System.out.println("Idioma:" + l.getLanguage());
                    System.out.println("Descargas: " + l.getDownload_count());
                    System.out.println(" ");
                });
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /////////////////////////fin menú 2 ////////////////////////////////

    ///////////////////////////////menú 3///////////////////////////////
    private void listAuthorsRegistered() {
        try {
            authors = authorsRepository.findAll();
            if (authors.isEmpty()) {
                System.out.println("No hay autores registrados");
                System.out.println("Presione Enter para continuar");
                keyboard.nextLine();
            } else {
                authors.forEach(a -> {
                    System.out.println("------------------");
                    System.out.println("Autor: " + a.getName());
                    if (a.getBirthDay() != null) {
                        System.out.println("Nacimiento: " + a.getBirthDay());
                    } else {
                        System.out.println("Informacion de nacimiento no disponible");
                    }
                    if (a.getDayDeath() != null) {
                        System.out.println("Fecha de defuncion: " + a.getDayDeath());
                    } else {
                        System.out.println("Vivo en la actualidad");
                    }
                });
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    ////////////////////////fin menu 3///////////////////////

    //////////////////////////menú 4///////////////////////////////
    private void listAuthorsAlives() {
        System.out.println("Ingrese el año de interes");
        int anho = keyboard.nextInt();
        keyboard.nextLine();

        try {
            List<Author> authorsAlive = authorsRepository.searchAuthorsAliveInYear(anho);

            if (authorsAlive.isEmpty()) {
                System.out.println("No se encuentra data disponible para:  " + anho);
            } else {

                System.out.println("Autores vivos en " + anho + " : ");
                authorsAlive.forEach(a -> {
                    System.out.println("---------------");
                    System.out.println("Autor: " + a.getName());
                    if (a.getBirthDay() != null) {
                        System.out.println("nacimiento: " + a.getBirthDay());
                    } else {
                        System.out.println("data de nacimiento no disponible");
                    }

                    if (a.getDayDeath() != null) {
                        System.out.println("fecha de defuncion: " + a.getDayDeath());
                    } else {
                        System.out.println("actualmente vivo");
                    }
                });
            }
        } catch (Exception e) {
            System.out.println("error en la consulta de datos: " + e.getMessage());
        }
    }
    ////////////////////////// fin menú 4///////////////////////////////

    ///////////////////////////////menú 5//////////////////////
    private void listBooksLanguage() {
        System.out.println("""
            Seleccione un idioma (dijita elnúmero):
            1 - Inglés (en)
            2 - Español (es)
            """);
        System.out.print("> ");
        String language = null;
        try {
            int optionSelected = Integer.parseInt(keyboard.nextLine().trim()); // Leer y validar entrada
            switch (optionSelected) {
                case 1 -> language = "en";
                case 2 -> language = "es";
                default -> {
                    System.out.println("Opción no válida. Por favor, seleccione 1 o 2.");
                    return; // Salir del método si la opción no es válida
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada no válida. Debe ingresar un número.");
            return; // Salir del método si la entrada no es válida
        }

        try {
            List<Book> booksLanguage = booksRepository.searchByLanguage(language);
            if (booksLanguage.isEmpty()) {
                System.out.println("No se encontraron libros registrados en el idioma seleccionado: " + language);
            } else {
                System.out.println("Libros disponibles en el idioma seleccionado:");
                for (Book libro : booksLanguage) {
                    System.out.println("------------------------------");
                    System.out.println("Título: " + libro.getTitle());

                    // Imprimir el autor directamente si es un String
                    if (libro.getAuthor() != null && !libro.getAuthor().isEmpty()) {
                        System.out.println("Autor: " + libro.getAuthor());
                    } else {
                        System.out.println("Autor: Información no disponible");
                    }

                    System.out.println("Idioma: " + libro.getLanguage());
                    System.out.println("Número de descargas: " + libro.getDownload_count());
                }
            }
        } catch (Exception e) {
            System.out.println("Ha ocurrido un error en la consulta: " + e.getMessage());
        }
    }
    ///////////////////////////fin menú 5//////////////////////
}







