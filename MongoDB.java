package org.example.base1;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;
import org.example.base1.entidades.Materia;
import org.example.base1.entidades.Persona;
import org.example.base1.repositorio.ConexionMongoDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;

import java.util.Arrays;
import java.util.List;

public class MongoDB {
    private static final Logger logger = LoggerFactory.getLogger(MongoDB.class);
    public static void main(String[] args) {
     ConexionMongoDB mongoDB = new ConexionMongoDB();

        try {
            mongoDB.crearConexion();
        } catch (MongoException e) {
            logger.error("Error: " + e.getMessage());
            return;
        }
        mongoDB.mostrarInformacionCluster();
        mongoDB.mostrarBasesDeDatos();

        // ------------------------------------------------------------------
        MongoCollection<Persona> personas = null;
        try {
            personas = (MongoCollection<Persona>) mongoDB.getPOJOCollection("Escuela", "personas", Persona.class);
            List<Persona> listaPersonas = Arrays.asList(
                    new Persona(null, "Nadia", "Trinidad", 23),
                    new Persona(null, "Roberto", "Cervantes", 22),
                    new Persona(null, "Isaac", "Pulido", 13)
            );

            for (Persona p : listaPersonas) {
                personas.replaceOne(
                        Filters.and(
                                Filters.eq("nombre", p.getNombre()),
                                Filters.eq("apellidos", p.getApellidos())
                        ),
                        p,
                        new ReplaceOptions().upsert(true)
                );
            }
            logger.info("Sincronización de personas completada con éxito.");
        } catch (Exception e) {
            logger.error("Error en la colección personas: {}", e.getMessage());
        }
        // ------------------------------------------------------------------
        MongoCollection<Materia> materias;
        try {
            materias = (MongoCollection<Materia>) mongoDB.getPOJOCollection("Escuela", "materias", Materia.class);
            Materia nuevaMateria = new Materia(null, "Redes", "CS", 5);

            materias.replaceOne(
                    Filters.eq("clave", nuevaMateria.getClave()),
                    nuevaMateria,
                    new ReplaceOptions().upsert(true)
            );
            logger.info("Materia '{}' sincronizada correctamente.", nuevaMateria.getNombre());
        } catch (Exception e) {
            logger.error("Error crítico en materias: {}", e.getMessage());
        }
        //-------------------------------------------------------------
        mongoDB.crearColeccion("Escuela", "Prueba");
        mongoDB.mostrarColecciones("Escuela");
        
        //personas.drop(); //elimina una entidad de la base de datos
        try {
            MongoDatabase database = mongoDB.getDatabase("Escuela");
            database.drop();
        } catch (Exception e) {
            logger.error("Error eliminando base de datos: " + e.getMessage());
        }
        mongoDB.cerrarConexion();


    }
}
