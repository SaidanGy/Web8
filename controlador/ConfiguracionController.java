package org.example.base2.controlador;

import com.mongodb.client.MongoCollection;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.base2.Bean.MongoBean;
import org.example.base2.modelo.Configuracion;
import org.example.base2.modelo.ConfiguracionRequest;
import org.example.base2.persistencia.ConfiguracionDB;
import org.example.base2.utiles.ConexionMongoDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "ConfiguracionController", value = "/configuraciones/ConfiguracionController")
public class ConfiguracionController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ConfiguracionController.class);

    @Inject
    private MongoBean mongoBean;
    private ConexionMongoDB conexion;
    private ConfiguracionDB configuracionDB;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        this.conexion = mongoBean.getConexion();
        MongoCollection<Configuracion> collection;
        try {
            // Cambio clave: apuntamos a la colección 'configuraciones_aplicacion'
            collection = (MongoCollection<Configuracion>)conexion.getPOJOCollection(
                    "configuraciones_aplicacion",
                    "configuraciones_aplicacion",
                    Configuracion.class
            );
        } catch (Exception e) {
            logger.error("No se pudo acceder a la colección configuraciones_aplicacion");
            logger.error("Error: " + e.getMessage());
            configuracionDB = null;
            return;
        }

        configuracionDB = new ConfiguracionDB(collection);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (configuracionDB == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error de conexión a la base de datos");
            return;
        }

        String accion = request.getParameter("accion");
        if (accion == null) accion = "listar";

        logger.info("Módulo Configuraciones - Acción: " + accion);

        String resultado;
        String mensaje;

        switch (accion) {
            case "agregar":
                crearConfiguracion(request, response);
                break;
            case "guardar":
                resultado = validarConfiguracion(request);
                if (resultado.isEmpty()) {
                    if (guardarConfiguracion(request)) {
                        mensaje = "Configuración guardada correctamente";
                    } else {
                        mensaje = "Error al intentar guardar la configuración";
                    }
                    listarConfiguraciones(mensaje, request, response);
                } else {
                    mostrarErrores(resultado, request, response);
                }
                break;
            case "editar":
                if (!editarConfiguracion(request, response)) {
                    mensaje = "Error al recuperar los datos de configuración";
                    listarConfiguraciones(mensaje, request, response);
                }
                break;
            case "eliminar":
                if (eliminarConfiguracion(request)) {
                    mensaje = "Configuración eliminada correctamente";
                } else {
                    mensaje = "Error al eliminar la configuración";
                }
                listarConfiguraciones(mensaje, request, response);
                break;
            default:
                listarConfiguraciones(null, request, response);
                break;
        }
    }

    private void listarConfiguraciones(String mensaje, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        List<Configuracion> configuraciones;
        try {
            configuraciones = configuracionDB.getConfiguraciones();
        } catch (Exception e) {
            logger.error("Error recuperando listado!");
            configuraciones = new ArrayList<>();
        }
        request.setAttribute("lista_configs", configuraciones);
        request.setAttribute("mensaje", mensaje);
        request.getRequestDispatcher("/configuraciones.jsp").forward(request, response);
    }

    private void crearConfiguracion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        ConfiguracionRequest configReq = new ConfiguracionRequest();
        request.setAttribute("config", configReq);
        request.setAttribute("errores", null);
        request.getRequestDispatcher("/formularioconfig.jsp").forward(request, response);
    }

    private boolean editarConfiguracion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        String codigo = request.getParameter("codigo");
        Configuracion config;
        try {
            config = configuracionDB.buscar(codigo);
            if (config == null) return false;
        } catch (Exception e) {
            return false;
        }
        request.setAttribute("config", new ConfiguracionRequest(config));
        request.setAttribute("errores", null);
        request.getRequestDispatcher("/formularioconfig.jsp").forward(request, response);
        return true;
    }

    private String validarConfiguracion(HttpServletRequest request) {
        String entorno = request.getParameter("entorno");
        String modulo = request.getParameter("modulo");
        String parametros = request.getParameter("parametros"); // Este es el valor "nadia = 5"
        String version = request.getParameter("version_config"); // Este es "1.0.0"
        return configuracionDB.validar(entorno, modulo, version, parametros);
    }

    private void mostrarErrores(String errores, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ConfiguracionRequest configReq = new ConfiguracionRequest(
                request.getParameter("codigo"),
                request.getParameter("entorno"),
                request.getParameter("modulo"),
                request.getParameter("parametros"),
                request.getParameter("version_config"),
                String.valueOf(request.getParameter("activo") != null)
        );

        request.setAttribute("config", configReq);
        request.setAttribute("errores", errores);
        request.getRequestDispatcher("/formularioconfig.jsp").forward(request, response);
    }

    private boolean guardarConfiguracion(HttpServletRequest request) {
        String codigo = request.getParameter("codigo");
        String entorno = request.getParameter("entorno");
        String modulo = request.getParameter("modulo");
        String parametros = request.getParameter("parametros");
        String version = request.getParameter("version_config");
        boolean activo = request.getParameter("activo") != null;

        if (codigo == null || "0".equals(codigo) || "null".equals(codigo)) {
            return configuracionDB.insertar(entorno, modulo, parametros, version, activo);
        } else {
            return configuracionDB.editar(codigo, entorno, modulo, parametros, version, activo);
        }
    }

    private boolean eliminarConfiguracion(HttpServletRequest request) {
        return configuracionDB.eliminar(request.getParameter("codigo"));
    }

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
}
