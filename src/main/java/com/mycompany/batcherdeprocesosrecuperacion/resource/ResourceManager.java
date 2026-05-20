/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.batcherdeprocesosrecuperacion.resource;

import com.mycompany.batcherdeprocesosrecuperacion.storage.JobStorage;
import com.mycompany.batcherdeprocesosrecuperacion.model.Job;
import java.util.Iterator;

/**
 *
 * @author duroi
 */

public class ResourceManager {

    private int totalCores;
    private int availableCores;
    private int totalMemMb;
    private int availableMemMb;

    public ResourceManager(int cores, int memMb) {
        this.totalCores = cores;
        this.availableCores = cores;
        this.totalMemMb = memMb;
        this.availableMemMb = memMb;
    }
    
    public int getAvailableCores() {
        return availableCores;
    }

    public int getAvailableMemMb() {
        return availableMemMb;
    }

    public int getTotalCores() {
        return totalCores;
    }

    public int getTotalMemMb() {
        return totalMemMb;
    }

    public void setTotalCores(int totalCores) {
        this.totalCores = totalCores;
    }

    public void setAvailableCores(int availableCores) {
        this.availableCores = availableCores;
    }

    public void setTotalMemMb(int totalMemMb) {
        this.totalMemMb = totalMemMb;
    }

    public void setAvailableMemMb(int availableMemMb) {
        this.availableMemMb = availableMemMb;
    }

    // 1 - Admitir jobs desde new Ready o Waiting
    public void admitJobs(JobStorage storage) {
        Iterator<Job> iterator = storage.getNewJobs().iterator();

        while (iterator.hasNext()) {
            Job job = iterator.next();
            if (canRun(job)) {
                // 2 -  Si hay recursos poner a Ready
                reserveResources(job);
                job.setState(Job.State.READY);
                storage.getReadyJobs().add(job);
                System.out.println("Job pasa a READY: " + job.getId());
            } else {
                // 3 - Si no hay recursos ponee a Waiting
                job.setState(Job.State.WAITING);
                storage.getWaitingJobs().add(job);
                System.out.println("Job pasa a WAITING: " + job.getId());
            }
            iterator.remove();
        }
    }
    
    //4 -  Revisar si ahora los JOBS en waiting pueden pasar a ready
    public void checkWaitingJobs(JobStorage storage) {
        Iterator<Job> iterator = storage.getWaitingJobs().iterator();

        while (iterator.hasNext()) {
            Job job = iterator.next();
            if (canRun(job)) {
                reserveResources(job);
                job.setState(Job.State.READY);
                storage.getReadyJobs().add(job);
                System.out.println("WAITING -> READY: " + job.getId());
                iterator.remove();
            }
        }
    }

    // Comprueba si hay recursos suficientes
    private boolean canRun(Job job) {
        return job.getCpuCores() <= availableCores && job.getMemMb() <= availableMemMb;
    }

    // Reservar recursos cuando un job entra en READY/RUNNING
    private void reserveResources(Job job) {
        availableCores -= job.getCpuCores();
        availableMemMb -= job.getMemMb();
    }

    // Liberar recursos cuando un job termina
    public void releaseResources(Job job) {
        availableCores += job.getCpuCores();
        availableMemMb += job.getMemMb();
    }
}