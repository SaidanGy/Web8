package org.example.base2.persistencia;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.types.ObjectId;
import org.example.base2.modelo.Configuracion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfiguracionDB {
    private MongoCollection<Configuracion> collection;
    private static final Logger logger = LoggerFactory.getLogger(ConfiguracionDB.class);

    public ConfiguracionDB(MongoCollection<Configuracion> collection) {
        this.collection = collection;
    }

    public List<Configuracion> getConfiguraciones() {
        List<Configuracion> lista = new ArrayList<>();
        try {
            collection.find().into(lista);
        } catch (Exception e) {
            logger.error("Error al obtener configuraciones: " + e.getMessage());
        }
        return lista;
    }

    public Configuracion buscar(String codigo) {
        try {
            ObjectId id = new ObjectId(codigo);
            return collection.find(Filters.eq("_id", id)).first();
        } catch (Exception e) {
            logger.error("Error al buscar: " + e.getMessage());
            return null;
        }
    }

    public boolean insertar(String entorno, String modulo, String parametrosStr, String version, boolean activo) {
        try {
            Map<String, Object> parametrosMap = procesarParametros(parametrosStr);
            Configuracion config = new Configuracion(entorno, modulo, parametrosMap, version, activo);
            collection.insertOne(config);
            return true;
        } catch (Exception e) {
            logger.error("ERROR AL INSERTAR: " + e.getMessage());
            return false;
        }
    }

    public boolean editar(String codigo, String entorno, String modulo, String parametrosStr, String version, boolean activo) {
        try {
            ObjectId id = new ObjectId(codigo);
            Map<String, Object> parametrosMap = procesarParametros(parametrosStr);

            Configuracion nuevaConfig = new Configuracion(entorno, modulo, parametrosMap, version, activo);
            nuevaConfig.setId(id);

            collection.replaceOne(Filters.eq("_id", id), nuevaConfig);
            return true;
        } catch (Exception e) {
            logger.error("ERROR AL EDITAR: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(String codigo) {
        try {
            ObjectId id = new ObjectId(codigo);
            collection.deleteOne(Filters.eq("_id", id));
            return true;
        } catch (Exception e) {
            logger.error("Error al eliminar: " + e.getMessage());
            return false;
        }
    }

    private Map<String, Object> procesarParametros(String texto) {
        Map<String, Object> mapa = new HashMap<>();
        if (texto == null || texto.trim().isEmpty() || texto.equals("{}")) return mapa;

        // Limpieza de caracteres de formato
        texto = texto.replace("{", "").replace("}", "").replace("[", "").replace("]", "");

        String[] pares = texto.split(",");
        for (String par : pares) {
            if (par.contains("=")) {
                String[] claveValor = par.split("=");
                if (claveValor.length == 2) {
                    mapa.put(claveValor[0].trim(), claveValor[1].trim());
                }
            }
        }
        return mapa;
    }

    public String validar(String entorno, String modulo, String version, String parametros) {
        StringBuilder resultado = new StringBuilder("");

        if (entorno == null || entorno.isEmpty()) {
            resultado.append("<p>El entorno es obligatorio.</p>");
        } else if (!entorno.matches("^(dev|test|prod)$")) {
            resultado.append("<p>El entorno debe ser: dev, test o prod.</p>");
        }

        if (modulo == null || modulo.trim().isEmpty()) {
            resultado.append("<p>El módulo no puede estar vacío.</p>");
        }

        if (version == null || version.trim().isEmpty()) {
            resultado.append("<p>La versión es obligatoria.</p>");
        }

        if (parametros == null || (!parametros.contains("=") && !parametros.equals("{}"))) {
            resultado.append("<p>Formato de parámetros inválido (debe ser clave=valor).</p>");
        }

        return resultado.toString();
    }
}