<div align="center">

# OlimpiadaJava

**Cálculo de trayectos de costo mínimo entre sedes, con interfaz JavaFX**

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-17.0.10-1F6FB2?style=for-the-badge&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![JUnit](https://img.shields.io/badge/JUnit-5.10-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![Estado](https://img.shields.io/badge/estado-en%20desarrollo-F2B01E?style=for-the-badge)

</div>

---

## Descripción

OlimpiadaJava modela una institución con varias sedes conectadas entre sí. Cada conexión es dirigida y tiene un costo entero no negativo. A partir de ese mapa, la aplicación calcula la ruta de menor costo entre dos sedes usando el algoritmo de Dijkstra, y permite guardar y recargar el mapa desde disco.

## Características

- Modelo de dominio con validaciones estrictas (sedes, conexiones y mapa).
- Cálculo de ruta mínima con el algoritmo de Dijkstra.
- Manejo controlado de errores: pesos negativos, sedes inexistentes y sedes aisladas.
- Persistencia local del mapa en archivos `.properties`, con importación y exportación.
- Interfaz JavaFX con campos validados, listas desplegables de sedes, botones que se habilitan solo con datos válidos y tablas de sedes y conexiones.
- Pruebas unitarias con JUnit 5.

## Tecnologías

| Componente | Versión |
|------------|---------|
| Java       | 17      |
| JavaFX     | 17.0.10 |
| Maven      | 3.9+    |
| JUnit      | 5.10.2  |

## Estructura del proyecto

```text
OlimpiadaJava/
├── pom.xml
├── datos/ejemplo.properties
└── src/
    ├── main/java/com/olimpia/
    │   ├── App.java            Punto de entrada JavaFX
    │   ├── model/              Sede, Conexion, Mapa, RutaResultado, EstadoRuta, MapaException
    │   ├── service/            DijkstraService
    │   ├── persistence/        FileMapaRepository, MapaImporterExporter
    │   ├── controller/         MapaController
    │   └── ui/                 MapaView
    ├── main/resources/styles/  app.css
    └── test/java/com/olimpia/  model, service, persistence
```

| Paquete       | Responsabilidad                                              |
|---------------|--------------------------------------------------------------|
| `model`       | Entidades del problema y reglas de validación                |
| `service`     | Dijkstra con `PriorityQueue` y distancias `long`             |
| `persistence` | Almacenamiento local, importación y exportación              |
| `controller`  | Coordina modelo, servicio y persistencia para la vista       |
| `ui`          | Interfaz JavaFX                                              |

## Requisitos

- JDK 17 o superior
- Maven 3.9 o superior (o el wrapper incluido, `mvnw`)

## Ejecución

Todos los comandos se ejecutan desde la raíz del repositorio.

Ejecutar las pruebas:

```bash
mvn clean test
```

Iniciar la aplicación:

```bash
mvn clean javafx:run
```

## Ejemplo

Dado el siguiente mapa:

| Origen | Destino | Costo |
|--------|---------|-------|
| A      | B       | 4     |
| A      | C       | 1     |
| C      | B       | 2     |

La ruta óptima de `A` a `B` es `A -> C -> B`, con un costo total de **3**.

## Reglas y manejo de errores

| Situación                       | Comportamiento                              |
|---------------------------------|---------------------------------------------|
| Costo negativo                  | Rechazado con `MapaException`               |
| Sede inexistente                | Error controlado con `MapaException`        |
| Sede sin camino al destino      | Resultado `SIN_RUTA`                        |
| Origen igual al destino         | Ruta de una sola sede con costo 0           |
| Sede con id repetido            | Rechazada con `MapaException`               |
| Sede sin identificador          | Rechazada con `IllegalArgumentException`    |

## Pruebas

El proyecto incluye 15 pruebas unitarias distribuidas en tres suites:

| Suite                 | Pruebas | Cobertura                                     |
|-----------------------|---------|-----------------------------------------------|
| `MapaTest`            | 7       | Creación de sedes y validación de conexiones  |
| `DijkstraServiceTest` | 6       | Rutas mínimas, sin ruta y casos límite        |
| `MapaRepositoryTest`  | 2       | Guardado, recarga e importación               |

## Complejidad

Sea V el número de sedes y E el número de conexiones.

| Operación                         | Tiempo               | Espacio |
|-----------------------------------|----------------------|---------|
| Dijkstra con `PriorityQueue`      | O((V + E) log V)     | O(V + E) |
| Reconstrucción de la ruta         | O(V)                 | O(V)    |
| Alta de sede / conexión           | O(1) amortizado      | O(1)    |
| Guardado y carga del mapa         | O(V + E)             | O(V + E) |

La cola puede contener entradas obsoletas; se descartan al extraerlas comparando con la distancia vigente, por lo que no se necesita decrease-key.

## Persistencia, importación y exportación

El estado se guarda en `data/mapa.properties` (formato `.properties`, sin dependencias externas). La aplicación recupera ese archivo al iniciar y lo guarda al cerrar; también hay botones para guardar y cargar manualmente. La interfaz permite importar y exportar (`MapaImporterExporter`) el mapa desde cualquier ruta.

Datos de ejemplo ficticios: [`datos/ejemplo.properties`](datos/ejemplo.properties) (incluye la sede aislada `E`, útil para probar `SIN_RUTA`). Para usarlo, cópialo a `data/mapa.properties` o impórtalo con `MapaImporterExporter.importar`.

## Decisiones de diseño

- Las conexiones son dirigidas y se guardan en listas de adyacencia dentro de `Mapa`.
- La validación ocurre en dos niveles: `Conexion` y `Sede` validan sus propios datos, y `Mapa` valida la consistencia (sedes existentes, costo no negativo) antes de insertar, de modo que una conexión rechazada nunca se añade.
- Los costos son `long` y el servicio no valida desbordamiento por sumas extremas.
- `RutaResultado` es inmutable y expresa la ausencia de ruta con `EstadoRuta.SIN_RUTA` en lugar de excepciones.
- Arquitectura en capas simple (model, service, persistence, controller, ui) bajo un único paquete raíz `com.olimpia`.

## Limitaciones conocidas

- No hay pruebas concurrentes ni sincronización: `Mapa` no es seguro entre hilos (el enunciado no exige concurrencia).
- Las pruebas de la interfaz JavaFX no están automatizadas.
- Sin validación de desbordamiento de `long` en la suma de costos.

## Flujo de trabajo

Cada integrante trabaja en una rama `feature/*` y entrega mediante pull request. Antes de comenzar una tarea se sincroniza la rama con `develop`:

```bash
git checkout develop
git pull origin develop
git checkout feature/<nombre>
git merge develop
```

Tras la validación en `develop`, los cambios se integran en `main` para la entrega final.
