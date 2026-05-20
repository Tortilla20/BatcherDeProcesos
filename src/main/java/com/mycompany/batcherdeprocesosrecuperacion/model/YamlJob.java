/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.batcherdeprocesosrecuperacion.model;

/**
 *
 * @author duroi
 */

// Clase que representa el YAML
public class YamlJob {

    public String id;
    public String name;
    public int priority;

    public Resources resources;
    public Workload workload;

    // Metodos que se llaman a la clase JobLoader
    public static class Resources {
        public int cpu_cores;
        public String memory;
    }

    public static class Workload {
        public long duration_ms;
    }
}