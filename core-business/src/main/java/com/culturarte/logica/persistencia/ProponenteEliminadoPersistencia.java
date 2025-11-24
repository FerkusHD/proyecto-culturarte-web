package com.culturarte.logica.persistencia;

import com.culturarte.logica.datatypes.DTProponente;
import com.culturarte.logica.datatypes.DTPropuesta;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProponenteEliminadoPersistencia {

    private static final Logger logger = LoggerFactory.getLogger(ProponenteEliminadoPersistencia.class);

    private final ObjectMapper objectMapper;
    private final Path directorioBase;

    public ProponenteEliminadoPersistencia() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        String userHome = System.getProperty("user.home");
        this.directorioBase = Paths.get(userHome, ".Culturarte", "proponentes_eliminados");

        try {
            Files.createDirectories(directorioBase);
            logger.info("Directorio de persistencia inicializado: {}", directorioBase);
        } catch (IOException e) {
            logger.error("Error al crear directorio de persistencia", e);
        }
    }

    public void guardarProponenteEliminado(DTProponente proponente) throws IOException {
        if (proponente == null || proponente.getNickname() == null) {
            throw new IllegalArgumentException("El proponente y su nickname no pueden ser null");
        }

        ProponenteEliminadoData data = new ProponenteEliminadoData();
        data.setNickname(proponente.getNickname());
        data.setNombre(proponente.getNombre());
        data.setApellido(proponente.getApellido());
        data.setEmail(proponente.getEmail());
        data.setFechaNacimiento(proponente.getFechaNacimiento());
        data.setImagen(proponente.getImagen());
        data.setDireccion(proponente.getDireccion());
        data.setBiografia(proponente.getBiografia());
        data.setLinkWeb(proponente.getLinkWeb());
        data.setFechaEliminacion(LocalDate.now());
        data.setPropuestas(proponente.getPropuestas());

        String nombreArchivo = String.format("%s_%d.json",
                sanitizarNombreArchivo(proponente.getNickname()),
                System.currentTimeMillis());

        Path archivoDestino = directorioBase.resolve(nombreArchivo);

        objectMapper.writeValue(archivoDestino.toFile(), data);

        logger.info("Proponente eliminado guardado en disco: {} -> {}",
                proponente.getNickname(), archivoDestino);
    }

    public ArrayList<DTProponente> listarProponentesEliminados() {
        ArrayList<DTProponente> resultado = new ArrayList<>();

        try {
            File directorio = directorioBase.toFile();
            if (!directorio.exists() || !directorio.isDirectory()) {
                logger.warn("Directorio de proponentes eliminados no existe: {}", directorioBase);
                return resultado;
            }

            File[] archivos = directorio.listFiles((dir, name) -> name.endsWith(".json"));

            if (archivos == null || archivos.length == 0) {
                logger.info("No hay proponentes eliminados en disco");
                return resultado;
            }

            logger.info("Recuperando {} archivos de proponentes eliminados", archivos.length);

            for (File archivo : archivos) {
                try {
                    ProponenteEliminadoData data = objectMapper.readValue(archivo, ProponenteEliminadoData.class);
                    logger.info("Proponente leído: {}, propuestas: {}", data.getNickname(),
                            data.getPropuestas() != null ? data.getPropuestas().size() : 0);
                    if (data.getPropuestas() != null && !data.getPropuestas().isEmpty()) {
                        DTPropuesta primera = data.getPropuestas().get(0);
                        logger.info("Primera propuesta: titulo={}, estado={}",
                                primera.getTitulo(), primera.getEstadoActual());
                    }
                    DTProponente dtp = convertirADTProponente(data);
                    resultado.add(dtp);
                } catch (IOException e) {
                    logger.error("Error al leer archivo de proponente eliminado: {}", archivo.getName(), e);
                }
            }

            logger.info("Se recuperaron {} proponentes eliminados desde disco", resultado.size());

        } catch (Exception e) {
            logger.error("Error al listar proponentes eliminados desde disco", e);
        }

        return resultado;
    }

    public boolean eliminarRegistro(String nickname) {
        try {
            File directorio = directorioBase.toFile();
            File[] archivos = directorio.listFiles(
                    (dir, name) -> name.startsWith(sanitizarNombreArchivo(nickname) + "_") && name.endsWith(".json"));

            if (archivos != null && archivos.length > 0) {
                for (File archivo : archivos) {
                    if (archivo.delete()) {
                        logger.info("Registro de proponente eliminado borrado: {}", archivo.getName());
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error al eliminar registro de proponente: {}", nickname, e);
        }
        return false;
    }

    private DTProponente convertirADTProponente(ProponenteEliminadoData data) {
        DTProponente dtp = new DTProponente();
        dtp.setNickname(data.getNickname());
        dtp.setNombre(data.getNombre());
        dtp.setApellido(data.getApellido());
        dtp.setEmail(data.getEmail());
        dtp.setFechaNacimiento(data.getFechaNacimiento());
        dtp.setImagen(data.getImagen());
        dtp.setDireccion(data.getDireccion());
        dtp.setBiografia(data.getBiografia());
        dtp.setLinkWeb(data.getLinkWeb());
        dtp.setFechaEliminacion(data.getFechaEliminacion());
        dtp.setPropuestas(data.getPropuestas() != null ? data.getPropuestas() : new ArrayList<>());
        return dtp;
    }

    private String sanitizarNombreArchivo(String nombre) {
        return nombre.replaceAll("[^a-zA-Z0-9_-]", "_");
    }

    public static class ProponenteEliminadoData {
        private String nickname;
        private String nombre;
        private String apellido;
        private String email;
        private LocalDate fechaNacimiento;
        private String imagen;
        private String direccion;
        private String biografia;
        private String linkWeb;
        private LocalDate fechaEliminacion;
        private ArrayList<DTPropuesta> propuestas;

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getApellido() {
            return apellido;
        }

        public void setApellido(String apellido) {
            this.apellido = apellido;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public LocalDate getFechaNacimiento() {
            return fechaNacimiento;
        }

        public void setFechaNacimiento(LocalDate fechaNacimiento) {
            this.fechaNacimiento = fechaNacimiento;
        }

        public String getImagen() {
            return imagen;
        }

        public void setImagen(String imagen) {
            this.imagen = imagen;
        }

        public String getDireccion() {
            return direccion;
        }

        public void setDireccion(String direccion) {
            this.direccion = direccion;
        }

        public String getBiografia() {
            return biografia;
        }

        public void setBiografia(String biografia) {
            this.biografia = biografia;
        }

        public String getLinkWeb() {
            return linkWeb;
        }

        public void setLinkWeb(String linkWeb) {
            this.linkWeb = linkWeb;
        }

        public LocalDate getFechaEliminacion() {
            return fechaEliminacion;
        }

        public void setFechaEliminacion(LocalDate fechaEliminacion) {
            this.fechaEliminacion = fechaEliminacion;
        }

        public ArrayList<DTPropuesta> getPropuestas() {
            return propuestas;
        }

        public void setPropuestas(ArrayList<DTPropuesta> propuestas) {
            this.propuestas = propuestas;
        }
    }
}
