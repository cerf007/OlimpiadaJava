<div align="center">

# OlimpiadaJava

**Cálculo de trayectos de costo mínimo entre sedes, con interfaz JavaFX**

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-21.0.6-1F6FB2?style=for-the-badge&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![JUnit](https://img.shields.io/badge/JUnit-5.12-25A162?style=for-the-badge&logo=junit5&logoColor=white)
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
- Interfaz gráfica en JavaFX.
- Pruebas unitarias con JUnit 5.

## Tecnologías

| Componente | Versión |
|------------|---------|
| Java       | 21      |
| JavaFX     | 21.0.6  |
| Maven      | 3.9+    |
| JUnit      | 5.12.1  |

## Estructura del proyecto

```text
OlimpiadasReto/
├── pom.xml
└── src/
    ├── main/java/
    │   ├── com/olimpia/
    │   │   ├── domain/        Sede, Conexion, Mapa, RutaResultado, EstadoRuta, MapaException
    │   │   ├── service/       Algoritmo de Dijkstra
    │   │   ├── persistence/   Guardado, carga, importación y exportación
    │   │   └── ui/            Controlador y vista JavaFX
    │   └── ni/edu/uam/olimpiadas/olimpiadasreto/   Aplicación base JavaFX
    └── test/java/com/olimpia/
```

| Paquete       | Responsabilidad                                              |
|---------------|--------------------------------------------------------------|
| `domain`      | Entidades del problema y reglas de validación                |
| `service`     | Lógica de cálculo de rutas                                   |
| `persistence` | Almacenamiento local del mapa                                |
| `ui`          | Interfaz de usuario                                          |

## Requisitos

- JDK 21 o superior
- Maven 3.9 o superior (o el wrapper incluido, `mvnw`)

## Ejecución

Todos los comandos se ejecutan desde la carpeta `OlimpiadasReto`.

```bash
cd OlimpiadasReto
```

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
| Sede sin identificador          | Rechazada con `IllegalArgumentException`    |

## Pruebas

El proyecto contempla 13 pruebas unitarias distribuidas en tres suites:

| Suite                 | Pruebas | Cobertura                                     |
|-----------------------|---------|-----------------------------------------------|
| `MapaTest`            | 5       | Creación de sedes y validación de conexiones  |
| `DijkstraServiceTest` | 6       | Rutas mínimas, sin ruta y casos límite        |
| `MapaRepositoryTest`  | 2       | Guardado, recarga e importación               |

## Estado del desarrollo

| Módulo                        | Estado       |
|-------------------------------|--------------|
| Dominio y validación          | Completado   |
| Servicio de Dijkstra          | Pendiente    |
| Persistencia e import/export  | Pendiente    |
| Interfaz JavaFX               | Pendiente    |

## Flujo de trabajo

Cada integrante trabaja en una rama `feature/*` y entrega mediante pull request. Antes de comenzar una tarea se sincroniza la rama con `develop`:

```bash
git checkout develop
git pull origin develop
git checkout feature/<nombre>
git merge develop
```

Tras la validación en `develop`, los cambios se integran en `main` para la entrega final.
