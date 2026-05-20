/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.batcherdeprocesosrecuperacion.storage;

import com.mycompany.batcherdeprocesosrecuperacion.model.Job;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author duroi
 */

public class JobStorage {

    // Cola de jobs recien cargados
    private Queue<Job> newJobs = new ConcurrentLinkedQueue<>();

    // Cola listos para ejecutar
    private Queue<Job> readyJobs = new ConcurrentLinkedQueue<>();

    // Cola esperando recursos
    private Queue<Job> waitingJobs = new ConcurrentLinkedQueue<>();

    // Jobs en ejecución
    private ConcurrentHashMap<Long, Job> runningJobs = new ConcurrentHashMap<>();

    // Jobs terminados correctamente
    private Queue<Job> doneJobs = new ConcurrentLinkedQueue<>();

    // Jobs que fallaron
    private Queue<Job> failedJobs = new ConcurrentLinkedQueue<>();

    public Queue<Job> getNewJobs() {
        return newJobs;
    }

    public void setNewJobs(Queue<Job> newJobs) {
        this.newJobs = newJobs;
    }

    public Queue<Job> getReadyJobs() {
        return readyJobs;
    }

    public void setReadyJobs(Queue<Job> readyJobs) {
        this.readyJobs = readyJobs;
    }

    public Queue<Job> getWaitingJobs() {
        return waitingJobs;
    }

    public void setWaitingJobs(Queue<Job> waitingJobs) {
        this.waitingJobs = waitingJobs;
    }

    public ConcurrentHashMap<Long, Job> getRunningJobs() {
        return runningJobs;
    }

    public void setRunningJobs(ConcurrentHashMap<Long, Job> runningJobs) {
        this.runningJobs = runningJobs;
    }

    public Queue<Job> getDoneJobs() {
        return doneJobs;
    }

    public void setDoneJobs(Queue<Job> doneJobs) {
        this.doneJobs = doneJobs;
    }

    public Queue<Job> getFailedJobs() {
        return failedJobs;
    }

    public void setFailedJobs(Queue<Job> failedJobs) {
        this.failedJobs = failedJobs;
    }
}