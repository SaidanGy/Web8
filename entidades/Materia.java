package org.example.base1.entidades;

import org.bson.types.ObjectId;

public class Materia {
    private ObjectId id; // Cambiado de int a ObjectId
    private String nombre;
    private String clave;
    private int creditos;

    public Materia() {}

    public Materia(ObjectId id, String nombre, String clave, int creditos) {
        this.id = id;
        this.nombre = nombre;
        this.clave = clave;
        this.creditos = creditos;
    }

    // Getters y Setters
    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public int getCreditos() { return creditos; }
    public void setCreditos(int creditos) { this.creditos = creditos; }

    @Override
    public String toString() {
        return "Materia{" + "id=" + id + ", nombre='" + nombre + "', clave='" + clave + "', creditos=" + creditos + '}';
    }
}