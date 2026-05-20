<h1 align="center">Batcher de Procesos</h1>
<p align="center">Iván Duro Fernández</p>

## Índice
- [Breve Explicación](#breve-explicación)
- [Requisitos del Proyecto](#requisitos-del-proyecto)
- [Estructura del proyecto](#Estructura-del-proyecto)
- [Explicación de funcionalidades](#explicación-de-funcionalidades)
- [Ejecución](#ejecución)

- - -

## Breve Explicación

<p align="center">Simulador de un batcher de procesos en Java, que permite cargar jobs desde YAML, planificarlos y ejecutarlos simulando el uso de recursos del sistema.</p>

- - -

## Requisitos del Proyecto

1. **Java 25** - El proyecto se realizó utilizando Java 25, por lo que para su ejecución se recomienda ejecutarlo en esa versión o en versiones futuras superiores a ella
2. **Maven** - También cabe destacar que se completo en Maven
3. **Netbeans** - Por útlimo, se utilizó el IDE Netbeans en el que se escribió el código y se añadiron las interfaces gráficas, por lo que se recomienda, aunque no es necesario, utilizar este IDE para su ejecución

- - -

## Estructura del proyecto

La estructura del proyecto tiene los siguiente paquetes y clases relizados en un modelo MVC - Modelo, Vista y Controlador:

- 📦 **com.mycompany.batcherdeprocesosrecuperacion**
   - 📄 BatcherDeProcesosRecuperacion
- 📦 **loader**
   - 📄 JobLoader.java
- 📦 **model**
   - 📄 Job.java
   - 📄 YamlJob.java
- 📦 **monitor**
   - 📄 Monitor.java
- 📦 **process**
   - 📄 ProcessCreator.java
- 📦 **resource**
   - 📄 ResourceManager.java
- 📦 **scheduler**
   - 📄 Scheduler.java
- 📦 **storage**
   - 📄 JobStorage.java
- 📦 **worker**
   - 📄 WorkerMain.java
   
- - -

## Explicación de funcionalidades

1. **Carga de jobs**  
   - Lee archivos YAML desde la carpeta `jobs`.  
   - Cada job define: `id`, `name`, `priority`, recursos (`cpu_cores`, `memory`) y duración (`duration_ms`).  
   - Los jobs se almacenan en colas según su estado: `NEW`, `READY`, `WAITING`.

2. **Gestión de recursos**  
   - Controla cores de CPU y memoria disponible.  
   - Los jobs solo pasan a `READY` si hay recursos suficientes; si no, van a `WAITING`.

3. **Planificación de jobs**  
   - **FCFS (First Come, First Served):** ejecuta los jobs por orden de llegada a `READY`.  
   - **Round Robin (RR):** reparte la CPU por turnos según un quantum configurable.  

4. **Ejecución de procesos**  
   - Cada job `RUNNING` se lanza como un proceso hijo (`WorkerMain`).  
   - Los workers simulan la ejecución con `Thread.sleep()` y envían progresos periódicos por stdout.  
   - Al finalizar, los jobs pasan a `DONE` o `FAILED`, liberando recursos para nuevos jobs.

5. **Monitor en tiempo real**  
   - Imprime el estado de los recursos y colas: `READY`, `WAITING`, `RUNNING`, `DONE`, `FAILED`.  
   - Muestra detalles de los jobs en ejecución: PID, prioridad, cores, memoria y progreso.
  
- - -

## Ejecución

1. Coloca los YAML de tus jobs en la carpeta `jobs`.  
2. Ejecuta el proyecto desde NetBeans o Maven.  
3. Observar en consola cómo los jobs pasan por los estados y cómo evoluciona su progreso en tiempo real.
