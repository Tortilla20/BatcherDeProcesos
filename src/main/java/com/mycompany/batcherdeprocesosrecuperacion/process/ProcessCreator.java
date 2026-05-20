/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.batcherdeprocesosrecuperacion.process;

import com.mycompany.batcherdeprocesosrecuperacion.model.Job;
import java.io.File;

/**
 *
 * @author duroi
 */

public class ProcessCreator {

    public static Process createProcess(Job job) throws Exception {
        return createProcessWithDuration(job, job.getDurationMs());
    }

    public static Process createProcessWithDuration(Job job, long durationMs) throws Exception {
        String cp = System.getProperty("java.class.path");
        String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";

        ProcessBuilder pb = new ProcessBuilder(
                javaBin,
                "-cp",
                cp,
                "com.mycompany.batcherdeprocesosrecuperacion.worker.Worker",
                job.getId(),
                String.valueOf(durationMs)
        );
        return pb.start();
    }
}