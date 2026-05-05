package org.example.base2.modelo;

import org.bson.types.ObjectId;
import java.util.Map;
import java.util.HashMap;

public class Configuracion {
    private ObjectId id;
    private String entorno;         // dev, test, prod
    private String modulo;          // nombre del módulo
    private Map<String, Object> parametros; // Objeto clave-valor
    private String version_config;  // versión de la configuración
    private boolean activo;         // estado booleano

    public Configuracion(){
        this.id = null;
        this.entorno = "";
        this.modulo = "";
        this.parametros = new HashMap<>();
        this.version_config = "1.0.0";
        this.activo = true;
    }

    public Configuracion(String entorno, String modulo, Map<String, Object> parametros, String version_config, boolean activo ){
        this.id = new ObjectId();
        this.entorno = entorno;
        this.modulo = modulo;
        this.parametros = parametros;
        this.version_config = version_config;
        this.activo = activo;
    }

    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public String getEntorno() { return entorno; }
    public void setEntorno(String entorno) { this.entorno = entorno; }

    public String getModulo() { return modulo; }
    public void setModulo(String modulo) { this.modulo = modulo; }

    public Map<String, Object> getParametros() { return parametros; }
    public void setParametros(Map<String, Object> parametros) { this.parametros = parametros; }

    public String getVersion_config() { return version_config; }
    public void setVersion_config(String version_config) { this.version_config = version_config; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}