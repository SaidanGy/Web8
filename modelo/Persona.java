package org.example.base2.modelo;

import java.util.Date;
import org.bson.types.ObjectId;

public class Persona {
    private ObjectId id;
    private String nombres; // <-- CAMBIO: De 'nombre' a 'nombres' (plural)
    private String apellidos;
    private Date fechaNac;
    private int experiencia;

    public Persona(){
        this.id = null;
        this.nombres = "";
        this.apellidos = "";
        this.fechaNac = null;
        this.experiencia = 0;
    }

    // Constructor actualizado
    public Persona(String nombres, String apellidos, Date fechaNac, int experiencia ){
        this.id = new ObjectId();
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fechaNac = fechaNac;
        this.experiencia = experiencia;
    }

    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    // Ahora el getter coincide exactamente con el nombre del atributo 'nombres'
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public Date getFechaNac() { return fechaNac; }
    public void setFechaNac(Date fechaNac) { this.fechaNac = fechaNac; }

    public int getExperiencia() { return experiencia; }
    public void setExperiencia(int experiencia) { this.experiencia = experiencia; }
}