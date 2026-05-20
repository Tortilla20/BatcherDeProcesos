/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.batcherdeprocesosrecuperacion.loader;

import com.mycompany.batcherdeprocesosrecuperacion.model.YamlJob;
import com.mycompany.batcherdeprocesosrecuperacion.model.Job;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.mycompany.batcherdeprocesosrecuperacion.storage.JobStorage;
import java.io.File;
import java.io.IOException;
import java.time.Instant;

/**
 *
 * @author duroi
 */

public class JobLoader {

    private ObjectMapper mapper;

    public JobLoader() {
        // Configurar Jackson para YAML
        mapper = new ObjectMapper(new YAMLFactory());
        // Ignorar campos extra
        mapper.findAndRegisterModules();
    }

    // Leer todos los YAML de una carpeta
    public void loadJobs(String folderPath, JobStorage storage) {

        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Ruta no valida");
            return;
        }
        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.getName().endsWith(".yaml") || file.getName().endsWith(".yml")) {
                try {
                    // Leer YAML
                    YamlJob yamlJob = mapper.readValue(file, YamlJob.class);
                    // Convertir a Job
                    Job job = convertToJob(yamlJob);
                    // Validar
                    if (validateJob(job)) {
                        job.setState(Job.State.NEW);
                        job.setArrivalTime(Instant.now());
                        storage.getNewJobs().add(job);
                        System.out.println("Job cargado: " + job.getId());
                    } else {
                        System.out.println("Job no valido: " + file.getName());
                    }
                } catch (IOException ex) {
                    System.out.println("Error: " + file.getName());
                }
            }
        }
    }

    // Convertir YAML a Job
    private Job convertToJob(YamlJob y) {
        Job job = new Job();
        job.setId(y.id);
        job.setName(y.name);
        job.setPriority(y.priority);
        job.setCpuCores(y.resources.cpu_cores);
        job.setMemMb(parseMemory(y.resources.memory));
        job.setDurationMs(y.workload.duration_ms);
        return job;
    }

    // Pasar a mb
    private int parseMemory(String mem) {
        String[] partes = mem.split(" ");
        int value = Integer.parseInt(partes[0]);
        String unidad = partes[1].toUpperCase();
        if (unidad.equals("GB")) {
            return value * 1024;
        }
        return value;
    }

    // Validacion
    private boolean validateJob(Job job) {
        if (job.getId() == null || job.getId().isEmpty()) {
            return false;
        }
        if (job.getName() == null || job.getName().isEmpty()) {
            return false;
        }
        if (job.getPriority() < 0 || job.getPriority() > 4) {
            return false;
        }
        if (job.getCpuCores() < 1) {
            return false;
        }
        if (job.getMemMb() <= 0) {
            return false;
        }
        if (job.getDurationMs() <= 0) {
            return false;
        }
        return true;
    }
}