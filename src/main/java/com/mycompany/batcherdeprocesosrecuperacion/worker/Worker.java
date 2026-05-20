/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.batcherdeprocesosrecuperacion.worker;

/**
 *
 * @author duroi
 */

public class Worker {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.exit(1);
        }
        
        String jobId = args[0];
        long duration = Long.parseLong(args[1]);
        long sleep = 0;
        
        while (sleep < duration) {
            try {
                Thread.sleep(500); // cada medio segundos
            } catch (InterruptedException e) {
                System.exit(0);
            }
            sleep += 500;

            double progress = (double) sleep / duration;

            System.out.println(jobId + ":" + progress);
        }
        System.exit(0);
    }
}