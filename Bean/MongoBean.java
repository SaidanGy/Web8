package org.example.base2.Bean;
///////////     http://localhost:8080/BASE2-1.0-SNAPSHOT/
import com.mongodb.MongoException;
import org.example.base2.utiles.ConexionMongoDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@ApplicationScoped
public class MongoBean {
    private static final Logger logger = LoggerFactory.getLogger(MongoBean.class);
    private ConexionMongoDB conexion;

    public MongoBean() {
        conexion = new ConexionMongoDB();
    }

    @PostConstruct
    public void iniciar() {
        logger.info("Iniciando el pool de conexiones MongoDB");
        try {
            conexion.crearConexion();

        } catch (MongoException e) {
            logger.error("No fue posible iniciar la conexion");
            logger.error("Detalle:" + e.getMessage());
            this.conexion = null;
        }
        logger.info("Conexion iniciada");
    }

    @PreDestroy
    public void finalizar() {
        logger.info("Finalizando el pool de conexiones MongoDB");
        conexion.cerrarConexion();
    }

    public ConexionMongoDB getConexion() {
        return conexion;
    }

    public void setConexion(ConexionMongoDB conexion) {
        this.conexion = conexion;
    }
}