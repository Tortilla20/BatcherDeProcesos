/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.batcherdeprocesosrecuperacion;

import com.mycompany.batcherdeprocesosrecuperacion.resource.ResourceManager;
import com.mycompany.batcherdeprocesosrecuperacion.runTask.RunTasks;
import com.mycompany.batcherdeprocesosrecuperacion.storage.JobStorage;
import com.mycompany.batcherdeprocesosrecuperacion.loader.JobLoader;

/**
 *
 * @author duroi
 */
public class BatcherDeProcesosRecuperacion {
    
    public static void main(String[] args) throws InterruptedException {

        // Crear recursos
        JobStorage storage = new JobStorage();
        // 4 cores, 2048 mb ram
        ResourceManager resourceManager = new ResourceManager(4, 2048);

        // Cargar jobs desde yaml
        JobLoader loader = new JobLoader();
        loader.loadJobs("jobs", storage);

        // Admintir los jobs
        resourceManager.admitJobs(storage);

        // Indicar FCFS o RR
        RunTasks scheduler = new RunTasks(resourceManager, "FCFS", 2000);

        // Bucle while mientras haya jobs pendientes lanzar mas
        while (!storage.getNewJobs().isEmpty() || !storage.getReadyJobs().isEmpty() || !storage.getWaitingJobs().isEmpty() || !storage.getRunningJobs().isEmpty()) {
            scheduler.launchReadyJobs(storage);
            resourceManager.checkWaitingJobs(storage);
            printState(storage, resourceManager);
            Thread.sleep(500);
        }
        
        System.out.println("Todos los jobs han terminado");
    }

    private static void printState(JobStorage storage, ResourceManager resourceManager) {
        System.out.println("\n===== BATCHER MONITOR =====");
        int usedCores = resourceManager.getTotalCores() - resourceManager.getAvailableCores();
        int usedMem = resourceManager.getTotalMemMb() - resourceManager.getAvailableMemMb();
        System.out.println("CPU: " + usedCores + "/" + resourceManager.getTotalCores());
        System.out.println("RAM: " + usedMem + "/" + resourceManager.getTotalMemMb() + " MB");
        System.out.println("READY: " + storage.getReadyJobs().size());
        System.out.println("WAITING: " + storage.getWaitingJobs().size());
        System.out.println("RUNNING: " + storage.getRunningJobs().size());
        System.out.println("DONE: " + storage.getDoneJobs().size());
        System.out.println("FAILED: " + storage.getFailedJobs().size());
        System.out.println("---- RUNNING JOBS ----");
        for (Long pid : storage.getRunningJobs().keySet()) {
            com.mycompany.batcherdeprocesosrecuperacion.model.Job job = storage.getRunningJobs().get(pid);
            System.out.println(job.getId() + " | PID = " + pid + " | CPU = " + job.getCpuCores() + " | MEM = " + job.getMemMb() + "MB | PROG = " + (int) (job.getProgress() * 100) + " %");
        }
        System.out.println("===========================\n");
    }
}
