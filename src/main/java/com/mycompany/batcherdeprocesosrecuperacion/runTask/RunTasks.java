/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.batcherdeprocesosrecuperacion.runTask;

import com.mycompany.batcherdeprocesosrecuperacion.process.ProcessCreator;
import com.mycompany.batcherdeprocesosrecuperacion.process.ProcessCreator;
import com.mycompany.batcherdeprocesosrecuperacion.resource.ResourceManager;
import com.mycompany.batcherdeprocesosrecuperacion.resource.ResourceManager;
import com.mycompany.batcherdeprocesosrecuperacion.storage.JobStorage;
import com.mycompany.batcherdeprocesosrecuperacion.model.Job;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

/**
 *
 * @author duroi
 */
public class RunTasks {

    private ResourceManager gestorRecursos;
    private String FoR; // FCFS o RR
    private long tiempoRR; // Tiempo maximo por turno en RR en milisegundos

    public RunTasks(ResourceManager gestorRecursos, String politica, long tiempoRR) {
        this.gestorRecursos = gestorRecursos;
        this.FoR = politica;
        this.tiempoRR = tiempoRR;
    }

    // Metodo que decide que usar y lanzar jobs correspondientes
    public void launchReadyJobs(JobStorage storage) {
        if (FoR.equalsIgnoreCase("FCFS")) {
            runFCFS(storage);
        } else if (FoR.equalsIgnoreCase("RR")) {
            runRR(storage);
        }
    }

    private void runFCFS(JobStorage storage) {

        // 1 - Mientras haya jobs listos para ejecutar
        while (!storage.getReadyJobs().isEmpty()) {

            //  2- mirar el primero sin sacarlo
            Job job = storage.getReadyJobs().peek(); // usar peek para ver el primer job de la cola

            // 3 - Comprobar si hay recursos suficientes para el job
            boolean hayCpu = job.getCpuCores() <= gestorRecursos.getAvailableCores();
            boolean hayRam = job.getMemMb() <= gestorRecursos.getAvailableMemMb();

            if (hayCpu && hayRam) {
                storage.getReadyJobs().poll(); //usar poll para sacar de la cola Ready

                try {
                    // 5 - Crear proceso para el job
                    Process proceso = ProcessCreator.createProcess(job);
                    long pid = proceso.pid();

                    job.setState(Job.State.RUNNING);
                    job.setStartTime(java.time.Instant.now());
                    storage.getRunningJobs().put(pid, job);
                    System.out.println("RUNNING (FCFS): " + job.getId() + " PID=" + pid);

                    // 6 - Crear hilo para leer el progreso que imprime el worker
                    new Thread(() -> {
                        try (BufferedReader lector = new BufferedReader(new InputStreamReader(proceso.getInputStream()))) {
                            String linea;
                            while ((linea = lector.readLine()) != null) {
                                String[] partes = linea.split(":");
                                if (partes.length == 2) {
                                    job.setProgress(Double.parseDouble(partes[1]));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();

                    // 7 - Crear hilo para esperar a que el proceso termine y liberar recursos
                    new Thread(() -> {
                        try {
                            int codigoSalida = proceso.waitFor();
                            job.setEndTime(java.time.Instant.now());
                            gestorRecursos.releaseResources(job);
                            gestorRecursos.checkWaitingJobs(storage); // ver si allgun job en waiting puede pasar a ready
                            storage.getRunningJobs().remove(pid);

                            if (codigoSalida == 0) {
                                job.setState(Job.State.DONE);
                                storage.getDoneJobs().add(job);
                                System.out.println("DONE: " + job.getId());
                            } else {
                                job.setState(Job.State.FAILED);
                                storage.getFailedJobs().add(job);
                                System.out.println("FAILED: " + job.getId());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();

                } catch (Exception e) {
                    e.printStackTrace();
                    // Si falla al lanzar el proceso marcar como failed
                    job.setState(Job.State.FAILED);
                    storage.getFailedJobs().add(job);
                    gestorRecursos.releaseResources(job);
                    gestorRecursos.checkWaitingJobs(storage);
                }

            } else {
                break;
            }
        }
    }

    private void runRR(JobStorage storage) {

        // Si ya hay un job corriendo return
        if (!storage.getRunningJobs().isEmpty()) {
            return;
        }

        // Si no hay jobs listos return
        if (storage.getReadyJobs().isEmpty()) {
            return;
        }

        // Coger el siguiente job de la cola
        Job job = storage.getReadyJobs().poll();
        if (job == null) {
            return;
        }

        //1 - Calcular cuanto tiempo de ejecucion
        long tiempoRestante = job.getDurationMs();
        long tiempoEsteTurno = Math.min(tiempoRestante, tiempoRR);

        try {
            // 2 - Crear proceso con la duracion del turno turno con dos hilos para leer el progreso y esperar a que termine el turno
            Process proceso = ProcessCreator.createProcessWithDuration(job, tiempoEsteTurno);
            long pid = proceso.pid();

            job.setState(Job.State.RUNNING);
            if (job.getStartTime() == null) {
                job.setStartTime(Instant.now()); // solo para la primera vez
            }
            storage.getRunningJobs().put(pid, job);
            System.out.println("RUNNING (RR): " + job.getId() + " PID=" + pid + " | restante=" + tiempoRestante + "ms | turno=" + tiempoEsteTurno + "ms");

            // 3 - Hilo para leer el progreso del worker y convertir el progreso
            new Thread(() -> {
                try (BufferedReader lector = new BufferedReader(new InputStreamReader(proceso.getInputStream()))) {
                    String linea;
                    while ((linea = lector.readLine()) != null) {
                        String[] partes = linea.split(":");
                        // Comprobar que tiene el formato que se espera
                        if (partes.length == 2) {
                            // Progreso que reporta el Worker
                            double progresoDelTurno = Double.parseDouble(partes[1]);

                            // Tiempo ya completado en turnos anteriores
                            // tiempo ya completado = duracion en MS - el tiempo restante
                            double tiempoYaCompletado = job.getDurationMs() - tiempoRestante;

                            // Progreso global = (lo ya hecho + lo que lleva de este turno) / duracion total
                            double progresoGlobal = (tiempoYaCompletado + progresoDelTurno * tiempoEsteTurno) / job.getDurationMs();

                            job.setProgress(progresoGlobal);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            // 4 - Hilo para esperar a que termine el turno
            new Thread(() -> {
                try {
                    int codigoSalida = proceso.waitFor();
                    storage.getRunningJobs().remove(pid);

                    long tiempoTrasEsteTurno = tiempoRestante - tiempoEsteTurno;

                    if (tiempoTrasEsteTurno <= 0) {
                        // El job termino
                        job.setEndTime(java.time.Instant.now());
                        gestorRecursos.releaseResources(job);
                        gestorRecursos.checkWaitingJobs(storage);
                        job.setState(Job.State.DONE);
                        job.setProgress(1.0);
                        storage.getDoneJobs().add(job);
                        System.out.println("DONE (RR): " + job.getId());

                    } else if (codigoSalida != 0) {
                        // El job fallo
                        job.setEndTime(java.time.Instant.now());
                        gestorRecursos.releaseResources(job);
                        gestorRecursos.checkWaitingJobs(storage);
                        job.setState(Job.State.FAILED);
                        storage.getFailedJobs().add(job);
                        System.out.println("FAILED (RR): " + job.getId());

                    } else {
                        // El job no termino hacer que vuelva a la cola con el tiempo restante
                        job.setDurationMs(tiempoTrasEsteTurno);
                        job.setState(Job.State.READY);
                        storage.getReadyJobs().add(job);
                        System.out.println("VOLVER COLA (RR): " + job.getId() + " | restante=" + tiempoTrasEsteTurno + "ms");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
            job.setState(Job.State.FAILED);
            storage.getFailedJobs().add(job);
            gestorRecursos.releaseResources(job);
            gestorRecursos.checkWaitingJobs(storage);
        }
    }
}