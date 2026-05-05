package org.example.base2.contrador;

import com.mongodb.client.MongoCollection;

import jakarta.inject.Inject;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import org.example.base2.Bean.MongoBean;
import org.example.base2.modelo.Persona;
import org.example.base2.modelo.PersonaRequest;
import org.example.base2.persistencia.PersonaDB;
import org.example.base2.utiles.ConexionMongoDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@WebServlet(name = "personasController", value = "/personas/PersonasController")
public class PersonasController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(PersonasController.class);

    @Inject
    private MongoBean mongoBean;
    private ConexionMongoDB conexion;
    private PersonaDB personaDB;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.conexion = mongoBean.getConexion();

        MongoCollection<Persona> collection;
        try {
            collection = (MongoCollection<Persona>)conexion.getPOJOCollection(
                    "crudmongo","personas",Persona.class);
        } catch (Exception e) {
            logger.error("No se pudo acceder a la coleccion de personas");
            logger.error("Error: " + e.getMessage());
            personaDB=null;
            return;
        }

//        collection.insertMany(Arrays.asList(
//                new Persona("Nombre1","Apellido1",new Date(),1),
//                new Persona("Nombre2","Apellido2",new Date(),2),
//                new Persona("Nombre3","Apellido3",new Date(),3),
//                new Persona("Nombre4","Apellido4",new Date(),4),
//                new Persona("Nombre5","Apellido5",new Date(),5)
//
//        ));

        this.personaDB = new PersonaDB(collection);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "N/A";
        }

        logger.info("Se esta ejecutando el servlet. Acción: " + accion);

        String resultado;
        String mensaje;

        switch (accion) {
            case "agregar":
                crearPersona(request, response);
                break;
            case "guardar":
                resultado = validarPersona(request);
                if (resultado.isEmpty()) {
                    logger.info("Se procede a crear la persona!");
                    if (guardarPersona(request)) {
                        mensaje = "Se han guardado los datos de la persona correctamente";
                    } else {
                        mensaje = "Se ha presentado un error al guardar los datos de la persona";
                    }
                    listarPersonas(mensaje, request, response);
                } else {
                    mostrarErrores(resultado, request, response);
                }
                break;
            case "editar":
                if (!editarPersona(request, response)) {
                    mensaje = "Se ha presentado en error al recuperar los datos de la persona";
                    listarPersonas(mensaje, request, response);
                }
                break;
            case "eliminar":
                if (eliminarPersona(request)) {
                    mensaje = "Se han eliminado los datos de la persona correctamente";
                } else {
                    mensaje = "Se ha presentado un error al eliminar los datos de la persona";
                }
                listarPersonas(mensaje, request, response);
                break;
            case "listar":
            default:
                listarPersonas(null, request, response);
                break;
        }
    }

    private void listarPersonas(String mensaje, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        List<Persona> personas;

        try {
            personas = personaDB.getPersonas();
        } catch (Exception e) {
            logger.error("Error recuperando la lista de personas!");
            personas = new ArrayList<>();
        }

        request.setAttribute("lista_personas", personas);
        request.setAttribute("mensaje", mensaje);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/personas.jsp");
        dispatcher.forward(request, response);
    }

    private void crearPersona(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{

        PersonaRequest persona = new PersonaRequest();

        request.setAttribute("persona", persona);
        request.setAttribute("errores", null);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/formulariopersona.jsp");
        dispatcher.forward(request, response);
    }

    private boolean editarPersona(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{

        String codigo = request.getParameter("codigo");
        logger.info( "Codigo: {0}", codigo);

        Persona persona;

        try {
            persona = personaDB.buscar(codigo);
            if (persona == null) {
                return false;
            }
        } catch (Exception e) {
            logger.error  ( "Error recuperando los datos de la persona!");
            return false;
        }

        PersonaRequest personaRequest = new PersonaRequest(persona);

        request.setAttribute("persona", personaRequest);
        request.setAttribute("errores", null);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/formulariopersona.jsp");
        dispatcher.forward(request, response);

        return true;
    }

    private String validarPersona(HttpServletRequest request) {
        String nombres = request.getParameter("nombres");
        String apellidos = request.getParameter("apellidos");
        String fechaNac = request.getParameter("fechaNac");
        logger.info( "Fecha: {0}", fechaNac);
        String experiencia = request.getParameter("experiencia");

        return personaDB.validar(nombres, apellidos, fechaNac, experiencia);
    }

    private void mostrarErrores(String errores, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{

        String codigo = request.getParameter("codigo");
        String nombres = request.getParameter("nombres");
        String apellidos = request.getParameter("apellidos");
        String fechaNac = request.getParameter("fechaNac");
        logger.info( "Fecha: {0}", fechaNac);
        String experiencia = request.getParameter("experiencia");

        PersonaRequest persona = new PersonaRequest(codigo, nombres, apellidos, fechaNac, experiencia);

        request.setAttribute("persona", persona);
        request.setAttribute("errores", errores);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/formulariopersona.jsp");
        dispatcher.forward(request, response);
    }

    private boolean guardarPersona(HttpServletRequest request) {
        String codigo = request.getParameter("codigo"); // Asegúrate que en el JSP el name sea "codigo"
        String nombres = request.getParameter("nombres");
        String apellidos = request.getParameter("apellidos");
        String fechaNac = request.getParameter("fechaNac");
        String experiencia = request.getParameter("experiencia");

        // Log para depurar qué está llegando realmente
        logger.info("Intentando guardar. Código recibido: [{}]", codigo);

        // Si el código es "0", es nulo, o es la palabra "null", INSERTAMOS
        if (codigo == null || codigo.trim().isEmpty() || "0".equals(codigo) || "null".equals(codigo)) {
            try {
                logger.info("Insertando nueva persona...");
                return personaDB.insertar(nombres, apellidos, fechaNac, experiencia);
            } catch (Exception ex) {
                logger.error("Fallo al insertar: {}", ex.getMessage());
                return false;
            }
        } else {
            try {
                logger.info("Editando persona con ID: {}", codigo);
                return personaDB.editar(codigo, nombres, apellidos, fechaNac, experiencia);
            } catch (Exception e) {
                logger.error("Fallo al editar: {}", e.getMessage());
                return false;
            }
        }
    }

    private boolean eliminarPersona(HttpServletRequest request) {
        String codigo = request.getParameter("codigo");
        try {
            return personaDB.eliminar(codigo);
        } catch (Exception e) {
            logger.error( "Error eliminado los datos de la persona!");
            return false;
        }

    }

//        try (PrintWriter out = response.getWriter()) {
//            /* TODO output your page here. You may use following sample code. */
//            out.println("<!DOCTYPE html>");
//            out.println("<html>");
//            out.println("<head>");
//            out.println("<title>Servlet PersonasController</title>");
//            out.println("</head>");
//            out.println("<body>");
//            if (this.conexion!=null){
//
//                out.println("<h1>Servlet PersonasController: Se ha establecido la conexion con MongoDB </h1>");
//
//            }else{
//
//                out.println("<h1>Servlet PersonasController: No se ha podido establecer la conexion con MongoDB </h1>");
//
//            }
//            out.println("</body>");
//            out.println("</html>");
//        }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}