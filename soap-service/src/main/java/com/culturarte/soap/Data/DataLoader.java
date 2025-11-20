package com.culturarte.soap.Data;

import com.culturarte.exepciones.CargaFallida;
import com.culturarte.logica.IControlador;
import com.culturarte.logica.enums.TipoEstado;
import com.culturarte.logica.enums.TipoRetorno;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.EnumSet;

@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private final IControlador controlador;

    public DataLoader(IControlador controlador) {
        logger.info("🔧 DataLoader inicializado");
        this.controlador = controlador;
    }

    @Override
    public void run(String... args) {
        logger.info("=== INICIO carga de datos de prueba ===");
        try {
            logger.info("📦 Cargando datos de prueba...");
            controlador.altaProponente(
                    "hrubino","a", "Horacio", "Rubino",
                    "horacio.rubino@guambia.com.uy", LocalDate.of(1962, 2, 25),
                    "uploads/imagenes/HR.jpeg",
                    "18 de Julio 1234",
                    "https://twitter.com/horaciorubino",
                    "Actor y conductor"
            );
            controlador.altaProponente(
                    "mbusca", "a","Martín", "Buscaglia",
                    "martin.bus@agadu.org.uy", LocalDate.of(1972, 6, 14),
                    "uploads/imagenes/MB.jpg",
                    "Colonia 4321",
                    "http://www.martinbuscaglia.com/",
                    "Músico uruguayo"
            );
            controlador.altaProponente(
                    "hectorg", "a","Héctor", "Guido",
                    "hector.gui@elgalpon.org.uy", LocalDate.of(1954, 1, 7),
                    null,
                    "Gral. Flores 5645",
                    "",
                    "Actor de teatro"
            );
            controlador.altaProponente(
                    "tabarec", "a","Tabaré", "Cardozo",
                    "tabare.car@agadu.org.uy", LocalDate.of(1971, 7, 24),
                    null,
                    "Santiago Rivas 1212",
                    "https://www.facebook.com/Tabaré-Cardozo-55179094281/?ref=br_rs",
                    "Cantante murguista"
            );
            controlador.altaProponente(
                    "cachilas", "a","Waldemar \"Cachila\"", "Silva",
                    "cachila.sil@c1080.org.uy", LocalDate.of(1947, 1, 1),
                    null,
                    "Br. Artigas 4567",
                    "https://www.facebook.com/C1080?ref=br_rs",
                    "Director comparsa"
            );
            controlador.altaProponente(
                    "juliob", "a","Julio", "Bocca",
                    "juliobocca@sodre.com.uy", LocalDate.of(1967, 3, 16),
                    null,
                    "Benito Blanco 4321",
                    "",
                    "Bailarín"
            );
            controlador.altaProponente(
                    "diegop", "a","Diego", "Parodi",
                    "diego@efectocine.com", LocalDate.of(1975, 1, 1),
                    null,
                    "Emilio Frugoni 1138 Ap. 02",
                    "http://www.efectocine.com",
                    "Cineasta"
            );
            controlador.altaProponente(
                    "kairoh", "a","Kairo", "Herrera",
                    "kairoher@pilsenrock.com.uy", LocalDate.of(1840, 4, 25),
                    null,
                    "Paraguay 1423",
                    "",
                    "Organizador eventos"
            );
            controlador.altaProponente(
                    "losBardo", "a","Los", "Bardo",
                    "losbardo@bardocientifico.com", LocalDate.of(1980, 10, 31),
                    "uploads/imagenes/LB.jpg",
                    "8 de Octubre 1429",
                    "https://bardocientifico.com/",
                    "Divulgación científica"
            );
            // Colaboradores
            controlador.altaColaborador(
                    "robinh", "a","Robin", "Henderson",
                    "robin.h@tinglesa.com.uy", LocalDate.of(1940, 8, 3),
                    null
            );
            controlador.altaColaborador(
                    "marcelot", "a","Marcelo", "Tinelli",
                    "marcelot@ideasdelsur.com.ar", LocalDate.of(1960, 4, 1),
                    null
            );
            controlador.altaColaborador(
                    "novick", "a","Edgardo", "Novick",
                    "edgardo@novick.com.uy", LocalDate.of(1952, 7, 17),
                    null
            );
            controlador.altaColaborador(
                    "sergiop", "a","Sergio", "Puglia",
                    "puglia@alpanpan.com.uy", LocalDate.of(1950, 1, 28),
                    "uploads/imagenes/SP.jpg"
            );
            controlador.altaColaborador(
                    "chino", "a","Alvaro", "Recoba",
                    "chino@trico.org.uy", LocalDate.of(1976, 3, 17),
                    null
            );
            controlador.altaColaborador(
                    "tonyp", "a","Antonio", "Pacheco",
                    "tonyp@manya.org.uy", LocalDate.of(1955, 2, 14),
                    "uploads/imagenes/AP.jpg"
            );
            controlador.altaColaborador(
                    "nicoJ", "a","Nicolás", "Jodal",
                    "jodal@artech.com.uy", LocalDate.of(1960, 8, 9),
                    "uploads/imagenes/NJ.jpg"
            );
            controlador.altaColaborador(
                    "juanP", "a","Juan", "Perez",
                    "juanp@elpueblo.com", LocalDate.of(1970, 1, 1),
                    null
            );
            controlador.altaColaborador(
                    "Mengano", "a","Mengano", "Gómez",
                    "menganog@elpueblo.com", LocalDate.of(1982, 2, 2),
                    null
            );
            controlador.altaColaborador(
                    "Perengano", "a","Perengano", "López",
                    "pere@elpueblo.com", LocalDate.of(1985, 3, 3),
                    null
            );
            controlador.altaColaborador(
                    "Tiajaci", "a","Tía", "Jacinta",
                    "jacinta@elpueblo.com", LocalDate.of(1990, 4, 4),
                    null
            );

            // HR sigue a: MB, HG, TC
            controlador.seguirUsuario("hrubino", "mbusca");
            controlador.seguirUsuario("hrubino", "hectorg");
            controlador.seguirUsuario("hrubino", "tabarec");

            // MB sigue a: HR, HG, TC

            controlador.seguirUsuario("mbusca", "hrubino");
            controlador.seguirUsuario("mbusca", "hectorg");
            controlador.seguirUsuario("mbusca", "tabarec");

            // HG sigue a: HR, TC
            controlador.seguirUsuario("hectorg", "hrubino");
            controlador.seguirUsuario("hectorg", "tabarec");

            // TC sigue a: HR, HG
            controlador.seguirUsuario("tabarec", "hrubino");
            controlador.seguirUsuario("tabarec", "hectorg");

            // CS sigue a: HR
            controlador.seguirUsuario("cachilas", "hrubino");

            // JB sigue a: HR, TC
            controlador.seguirUsuario("juliob", "hrubino");
            controlador.seguirUsuario("juliob", "tabarec");

            // DP sigue a: HR, TC
            controlador.seguirUsuario("diegop", "hrubino");
            controlador.seguirUsuario("diegop", "tabarec");

            // KH sigue a: HR
            controlador.seguirUsuario("kairoh", "hrubino");

            // LB sigue a: HR, TC
            controlador.seguirUsuario("losBardo", "hrubino");
            controlador.seguirUsuario("losBardo", "tabarec");

            // RH sigue a: HR, MB, HG
            controlador.seguirUsuario("robinh", "hrubino");
            controlador.seguirUsuario("robinh", "mbusca");
            controlador.seguirUsuario("robinh", "hectorg");

            // MT sigue a: HR, MB, HG
            controlador.seguirUsuario("marcelot", "hrubino");
            controlador.seguirUsuario("marcelot", "mbusca");
            controlador.seguirUsuario("marcelot", "hectorg");

            // EN sigue a: HR, MB, HG
            controlador.seguirUsuario("novick", "hrubino");
            controlador.seguirUsuario("novick", "mbusca");
            controlador.seguirUsuario("novick", "hectorg");

            // SP sigue a: HR, MB, HG
            controlador.seguirUsuario("sergiop", "hrubino");
            controlador.seguirUsuario("sergiop", "mbusca");
            controlador.seguirUsuario("sergiop", "hectorg");

            // AR sigue a: HR
            controlador.seguirUsuario("chino", "hrubino");

            // AP sigue a: HR
            controlador.seguirUsuario("tonyp", "hrubino");

            // NJ sigue a: HR, MB
            controlador.seguirUsuario("nicoJ", "hrubino");
            controlador.seguirUsuario("nicoJ", "mbusca");

            // JP sigue a: HR, MB, HG
            controlador.seguirUsuario("juanP", "hrubino");
            controlador.seguirUsuario("juanP", "mbusca");
            controlador.seguirUsuario("juanP", "hectorg");

            // MG sigue a: HR, MB, HG
            controlador.seguirUsuario("Mengano", "hrubino");
            controlador.seguirUsuario("Mengano", "mbusca");
            controlador.seguirUsuario("Mengano", "hectorg");

            // PL sigue a: HR, MB
            controlador.seguirUsuario("Perengano", "hrubino");
            controlador.seguirUsuario("Perengano", "mbusca");

            // TJ sigue a: HR, MB, HG
            controlador.seguirUsuario("Tiajaci", "hrubino");
            controlador.seguirUsuario("Tiajaci", "mbusca");
            controlador.seguirUsuario("Tiajaci", "hectorg");


            // Categorias
            controlador.altaCategoria("Teatro", null);
            controlador.altaCategoria("Literatura", null);
            controlador.altaCategoria("Música", null);
            controlador.altaCategoria("Cine", null);
            controlador.altaCategoria("Danza", null);
            controlador.altaCategoria("Carnaval", null);

            // Subcategorías de Teatro
            controlador.altaCategoria("Teatro Dramático", "Teatro");
            controlador.altaCategoria("Teatro Musical", "Teatro");
            controlador.altaCategoria("Comedia", "Teatro");
            controlador.altaCategoria("Stand-up", "Comedia");

            // Subcategorías de Música
            controlador.altaCategoria("Festival", "Música");
            controlador.altaCategoria("Concierto", "Música");

            // Subcategorías de Cine
            controlador.altaCategoria("Cine al Aire Libre", "Cine");
            controlador.altaCategoria("Cine a Pedal", "Cine");

            // Subcategorías de Danza
            controlador.altaCategoria("Ballet", "Danza");
            controlador.altaCategoria("Flamenco", "Danza");

            // Subcategorías de Carnaval
            controlador.altaCategoria("Murga", "Carnaval");
            controlador.altaCategoria("Humoristas", "Carnaval");
            controlador.altaCategoria("Parodistas", "Carnaval");
            controlador.altaCategoria("Lubolos", "Carnaval");
            controlador.altaCategoria("Revista", "Carnaval");

            // Propuestas
            controlador.altaPropuesta("Cine en el Botánico",
                    "El 16 de Diciembre a la hora 20 se proyectará la película \"Clever\", en el Jardín Botánico (Av. 19 de Abril 1181). Entrada libre. Organiza: Intendencia de Montevideo.",
                    "Jardín Botánico",
                    LocalDate.of(2025, 9, 16),
                    200f,
                    150000f,
                    EnumSet.of(TipoRetorno.PORCENTAJEGANANCIA),
                    null,
                    "diegop",
                    "Cine",
                    LocalDate.of(2025, 5, 15),
                    LocalTime.of(15, 30)
            );

            controlador.altaPropuesta("Religiosamente",
                    "MOMOSAPIENS presenta \"Religiosamente\", en el Teatro de Verano. El espectáculo se realiza el 7 de Octubre, hora 21.",
                    "Teatro de Verano",
                    LocalDate.of(2025, 10, 7),
                    300f,
                    300000f,
                    EnumSet.of(TipoRetorno.ENTRADAGRATIS, TipoRetorno.PORCENTAJEGANANCIA),
                    null,
                    "hrubino",
                    "Parodistas",
                    LocalDate.of(2025, 6, 18),
                    LocalTime.of(4, 28)
            );

            controlador.altaPropuesta("El Pimiento Indomable",
                    "El Pimiento Indomable, formación compuesta por Kiko Veneno y Martín Buscaglia, se presentará el 19 de Octubre en el Teatro Solís. Entradas por Tickantel.",
                    "Teatro Solís",
                    LocalDate.of(2025, 10, 19),
                    400f,
                    400000f,
                    EnumSet.of(TipoRetorno.PORCENTAJEGANANCIA),
                    "uploads/imagenes/PIM.jpg",
                    "mbusca",
                    "Concierto",
                    LocalDate.of(2025, 7, 26),
                    LocalTime.of(15, 30)
            );

            controlador.altaPropuesta("Pilsen Rock",
                    "La edición 2025 del Pilsen Rock se celebrará el 21 de Octubre en la Rural del Prado. Entradas a la venta en RedTickets.",
                    "Rural del Prado",
                    LocalDate.of(2025, 10, 21),
                    1000f,
                    900000f,
                    EnumSet.of(TipoRetorno.ENTRADAGRATIS, TipoRetorno.PORCENTAJEGANANCIA),
                    "uploads/imagenes/PIL.jpg",
                    "kairoh",
                    "Festival",
                    LocalDate.of(2025, 7, 30),
                    LocalTime.of(15, 40)
            );

            controlador.altaPropuesta("Romeo y Julieta",
                    "Romeo y Julieta de Kenneth MacMillan se presentará nuevamente el 5 de Noviembre en el Auditorio Nacional del Sodre. Entradas en Tickantel.",
                    "Auditorio Nacional del Sodre",
                    LocalDate.of(2025, 11, 5),
                    800f,
                    750000f,
                    EnumSet.of(TipoRetorno.PORCENTAJEGANANCIA),
                    null,
                    "juliob",
                    "Ballet",
                    LocalDate.of(2025, 8, 4),
                    LocalTime.of(12, 20)
            );

            controlador.altaPropuesta("Un día de Julio",
                    "La Catalina presenta el espectáculo \"Un Día de Julio\" en Landia. Función el 16 de Noviembre a las 21 hs.",
                    "Landia",
                    LocalDate.of(2025, 11, 16),
                    650f,
                    300000f,
                    EnumSet.of(TipoRetorno.ENTRADAGRATIS, TipoRetorno.PORCENTAJEGANANCIA),
                    "uploads/imagenes/UDJ.jpg",
                    "tabarec",
                    "Murga",
                    LocalDate.of(2025, 8, 6),
                    LocalTime.of(2, 0)
            );

            controlador.altaPropuesta("El Lazarillo de Tormes",
                    "Vuelve una de las producciones de El Galpón más aclamadas por crítica y público. Funciones desde el 3 de Diciembre.",
                    "Teatro el Galpón",
                    LocalDate.of(2025, 12, 3),
                    350f,
                    175000f,
                    EnumSet.of(TipoRetorno.ENTRADAGRATIS),
                    null,
                    "hectorg",
                    "Teatro Dramático",
                    LocalDate.of(2025, 8, 18),
                    LocalTime.of(2, 40)
            );

            controlador.altaPropuesta("Bardo en la FING",
                    "El 10 de Diciembre se presentará Bardo Científico en la FING, Anfiteatro del Edificio José Luis Massera. Entrada libre.",
                    "Anfiteatro Edificio José Luis Massera",
                    LocalDate.of(2025, 12, 10),
                    200f,
                    100000f,
                    EnumSet.of(TipoRetorno.ENTRADAGRATIS),
                    null,
                    "losBardo",
                    "Stand-up",
                    LocalDate.of(2025, 8, 23),
                    LocalTime.of(2, 12)
            );

            // Propuestas publicadas
            controlador.nuevoEstadoPropuesta("Cine en el Botánico", TipoEstado.PUBLICADA, LocalDate.of(2025, 6, 1), LocalTime.of(10, 15));
            controlador.nuevoEstadoPropuesta("Religiosamente", TipoEstado.PUBLICADA, LocalDate.of(2025, 7, 5), LocalTime.of(11, 10));
            controlador.nuevoEstadoPropuesta("El Pimiento Indomable", TipoEstado.PUBLICADA, LocalDate.of(2025, 7, 27), LocalTime.of(9, 45));
            controlador.nuevoEstadoPropuesta("Pilsen Rock", TipoEstado.PUBLICADA, LocalDate.of(2025, 8, 2), LocalTime.of(16, 5));
            controlador.nuevoEstadoPropuesta("Romeo y Julieta", TipoEstado.PUBLICADA, LocalDate.of(2025, 8, 9), LocalTime.of(14, 50));
            controlador.nuevoEstadoPropuesta("Un día de Julio", TipoEstado.PUBLICADA, LocalDate.of(2025, 8, 14), LocalTime.of(13, 10));
            controlador.nuevoEstadoPropuesta("El Lazarillo de Tormes", TipoEstado.PUBLICADA, LocalDate.of(2025, 8, 20), LocalTime.of(17, 20));

            controlador.altaColaboracion(50000, LocalDate.of(2025, 5, 20), LocalTime.of(14,30), TipoRetorno.PORCENTAJEGANANCIA, "Cine en el Botánico", "novick");
            controlador.altaColaboracion(50000, LocalDate.of(2025, 5, 24), LocalTime.of(17,25), TipoRetorno.PORCENTAJEGANANCIA, "Cine en el Botánico", "robinh");
            controlador.altaColaboracion(50000, LocalDate.of(2025, 5, 30), LocalTime.of(18,30), TipoRetorno.PORCENTAJEGANANCIA, "Cine en el Botánico", "nicoJ");
            controlador.altaColaboracion(200000, LocalDate.of(2025, 6, 30), LocalTime.of(14,25), TipoRetorno.PORCENTAJEGANANCIA, "Religiosamente", "marcelot");
            controlador.altaColaboracion(500, LocalDate.of(2025, 7, 1), LocalTime.of(18,05), TipoRetorno.ENTRADAGRATIS, "Religiosamente", "Tiajaci");
            controlador.altaColaboracion(600, LocalDate.of(2025, 7, 7), LocalTime.of(17,45), TipoRetorno.ENTRADAGRATIS, "Religiosamente", "Mengano");
            controlador.altaColaboracion(50000, LocalDate.of(2025, 7, 10), LocalTime.of(14,35), TipoRetorno.PORCENTAJEGANANCIA, "Religiosamente", "novick");
            controlador.altaColaboracion(50000, LocalDate.of(2025, 7, 15), LocalTime.of(9,45), TipoRetorno.PORCENTAJEGANANCIA, "Religiosamente", "sergiop");
            controlador.altaColaboracion(200000, LocalDate.of(2025, 8, 1), LocalTime.of(7,40), TipoRetorno.PORCENTAJEGANANCIA, "El Pimiento Indomable", "marcelot");
            controlador.altaColaboracion(80000, LocalDate.of(2025, 8, 3), LocalTime.of(9,25), TipoRetorno.PORCENTAJEGANANCIA, "El Pimiento Indomable", "sergiop");
            controlador.altaColaboracion(50000, LocalDate.of(2025, 8, 5), LocalTime.of(16,50), TipoRetorno.ENTRADAGRATIS, "Pilsen Rock", "chino");
            controlador.altaColaboracion(120000, LocalDate.of(2025, 8, 10), LocalTime.of(15,50), TipoRetorno.PORCENTAJEGANANCIA, "Pilsen Rock", "novick");
            controlador.altaColaboracion(120000, LocalDate.of(2025, 8, 15), LocalTime.of(19,30), TipoRetorno.ENTRADAGRATIS, "Pilsen Rock", "tonyp");
            controlador.altaColaboracion(100000, LocalDate.of(2025, 8, 13), LocalTime.of(4,58), TipoRetorno.PORCENTAJEGANANCIA, "Romeo y Julieta", "sergiop");
            controlador.altaColaboracion(200000, LocalDate.of(2025, 8, 14), LocalTime.of(11,25), TipoRetorno.PORCENTAJEGANANCIA, "Romeo y Julieta", "marcelot");
            controlador.altaColaboracion(30000, LocalDate.of(2025, 8, 15), LocalTime.of(04,48), TipoRetorno.ENTRADAGRATIS, "Un día de Julio", "tonyp");
            controlador.altaColaboracion(150000, LocalDate.of(2025, 8, 17), LocalTime.of(15,30), TipoRetorno.PORCENTAJEGANANCIA, "Un día de Julio", "marcelot");



            // Propuestas Canceladas
            controlador.nuevoEstadoPropuesta("Cine en el Botánico", TipoEstado.CANCELADA, LocalDate.of(2025, 6, 15), LocalTime.of(14, 50));

            logger.info("✅ Datos de prueba cargados exitosamente");
            System.out.println("✅ Datos de prueba cargados al iniciar la app");
        } catch (Exception e) {
            logger.error("❌ Error cargando datos de prueba: {}", e.getMessage(), e);
            System.err.println("❌ Error cargando datos de prueba: " + e.getMessage());
            e.printStackTrace();
        }
        logger.info("=== FIN carga de datos de prueba ===");
    }

}