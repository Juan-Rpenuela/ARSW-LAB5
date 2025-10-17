# ARSW-LAB5: Cliente Grueso con API REST, HTML5, JavaScript y CSS3

## Escuela Colombiana de Ingeniería  
### Arquitecturas de Software

## Descripción General

Este proyecto implementa un cliente web (“cliente grueso”) que consume un API REST de planos, usando HTML5, JavaScript (modular), CSS3 y Bootstrap. El frontend permite consultar, listar y graficar planos de un autor, integrando lógica de negocio, renderizado dinámico y consumo de datos tanto mockeados como reales.

---

## Cambios implementados respecto al enunciado (qué y dónde)

- Captura de eventos en canvas con PointerEvent y repintado inmediato:
	- `src/main/resources/static/js/app.js`
		- Funciones: `init()`, `initCanvasHandlers()`, `onPointerDown(evt)`, `drawOnCanvas(points)`.
		- Solo agrega puntos si hay un plano seleccionado/creado (`currentBlueprint`).
	- `src/main/resources/static/index.html`
		- Canvas con `touch-action:none` para permitir gestos de puntero en táctiles.

- Guardar/Actualizar (PUT) con Promesas y recálculo de puntaje:
	- Botón “Save/Update” en `index.html` (`#btn-save`).
	- `app.js`: `saveOrUpdate()` encadena `POST/PUT` → `GET /blueprints` → `map/filter/reduce` para actualizar tabla y puntaje.
	- `src/main/resources/static/js/apiclient.js`: `put(bp)` usa `$.ajax({ type:'PUT', contentType:'application/json', data: JSON.stringify(bp) })`.

- Crear nuevo blueprint (POST) y primer guardado en modo creación:
	- Botón “Create new blueprint” en `index.html` (`#btn-create`).
	- `app.js`: `createNew()` solicita nombre, limpia canvas y activa `createMode=true`; el primer `saveOrUpdate()` hace `POST`, luego `GET /blueprints` y recálculo.
	- `apiclient.js`: `post(bp)` con `$.ajax`.

- Borrado de blueprint (DELETE) con Promesas y recarga:
	- Botón “Delete” en `index.html` (`#btn-delete`).
	- `app.js`: `removeCurrent()` limpia canvas, `DELETE`, luego `GET /blueprints` y recálculo.
	- `apiclient.js`: `del(author, name)` contra `/blueprints/{author}/{name}`.

- Cálculo de puntos con operaciones funcionales (sin ciclos explícitos):
	- `app.js`: `renderTotalPoints(list)` usa `reduce`; el listado usa `map`; filtrado por autor via `filter`.

- Backend: Soporte PUT/DELETE con servicios y persistencia:
	- `src/main/java/.../controllers/BlueprintApiController.java`
		- `@PutMapping /blueprints` para actualizar.
		- `@DeleteMapping /blueprints/{author}/{bprintname}` para borrar.
	- `src/main/java/.../services/BlueprintsServices.java`
		- Métodos `updateBlueprint(...)` y `deleteBlueprint(...)`.
	- `src/main/java/.../persistence/BlueprintsPersistence.java`
		- Nuevos contratos `updateBlueprint(...)` y `deleteBlueprint(...)`.
	- `src/main/java/.../persistence/impl/InMemoryBlueprintPersistence.java`
		- Implementaciones con `replace`/`remove` del `ConcurrentHashMap` y validación de existencia.

---

## Estructura del Proyecto

- **Backend:** Spring Boot, expone el API REST de planos.
- **Frontend:**  
	- `src/main/resources/static/index.html`: Página principal.
	- `js/app.js`: Módulo principal de lógica y controlador.
	- `js/apimock.js`: Mock de datos para pruebas frontend.
	- `js/apiclient.js`: Cliente para consumir el API REST real.
	- WebJars: Bootstrap y jQuery gestionados por Maven.

---

## Detalles Técnicos de la Solución

### 1. Backend

- Se parte de un API REST Spring Boot existente.
- Se exponen/ajustan endpoints REST:
	- `GET /blueprints` (todos), `GET /blueprints/{author}`, `GET /blueprints/{author}/{bprintname}`.
	- `POST /blueprints` (crear), `PUT /blueprints` (actualizar existente), `DELETE /blueprints/{author}/{bprintname}` (borrar).
- Servicios y persistencia:
	- `BlueprintsServices.updateBlueprint(bp)` delega en `BlueprintsPersistence.updateBlueprint(bp)`; lanza `BlueprintNotFoundException` si no existe.
	- `BlueprintsServices.deleteBlueprint(author, name)` delega en `BlueprintsPersistence.deleteBlueprint(...)`.
	- `InMemoryBlueprintPersistence` usa `ConcurrentHashMap`:
		- `saveBlueprint`: `putIfAbsent` (crea si no existe; si existe lanza `BlueprintPersistenceException`).
		- `updateBlueprint`: `replace` (si no existe lanza `BlueprintNotFoundException`).
		- `deleteBlueprint`: `remove` (si no existe lanza `BlueprintNotFoundException`).

### 2. Frontend - Estructura y Componentes

#### HTML

- **Formulario de autor:** Input para capturar el nombre del autor y botón “Get blueprints”.
- **Tabla de planos:** Muestra nombre, número de puntos y botón “Ver plano” por fila.
- **Total de puntos:** Campo que suma los puntos de todos los planos listados.
- **Canvas:** Elemento `<canvas>` para graficar el plano seleccionado; atributo `style="touch-action:none"` para habilitar `PointerEvent` en pantallas táctiles.
- **Botones:** “Create new blueprint”, “Save/Update” y “Delete”.
- **Campos dinámicos:** Para mostrar el autor y el nombre del plano seleccionado.

#### JavaScript (app.js)

- **Patrón Módulo:**  
	- Se implementa un módulo IIFE que encapsula el estado y expone una API pública.
	- Variables privadas: autor seleccionado, lista de planos resumidos, `currentBlueprint {author,name,points[]}`, y flag `createMode`.
	- Funciones públicas:
		- `setAuthor(autor)`, `updateBlueprintsByAuthor(autor)`, `drawBlueprint(autor,nombre)`.
		- `saveOrUpdate()`: POST (si `createMode`) o PUT; encadena GET y refresca UI (tabla + puntaje).
		- `createNew()`: solicita nombre, limpia canvas y activa `createMode` para que el primer guardado sea POST.
		- `removeCurrent()`: DELETE del plano actual y refresco con GET.
	- Renderizado:
		- Tabla dinámica con botón “Ver plano”.
		- Total de puntos con `reduce`; transformación a resumen con `map`; filtrado por autor con `filter`.
		- Canvas: limpia y dibuja polilínea con `moveTo/lineTo` recorriendo `points`.
	- Integración con el DOM:
		- Se usan selectores jQuery para actualizar campos y construir la tabla.
		- Botones “Get blueprints”, “Create new blueprint”, “Save/Update” y “Delete” están cableados a sus flujos.

- **Manejadores de Canvas (PointerEvent):**
  - `initCanvasHandlers()` registra `pointerdown` (o `click/touchstart` como fallback).
  - `onPointerDown(evt)`: traduce coordenadas del puntero al sistema del canvas (usando `getBoundingClientRect()`), agrega `{x,y}` a `currentBlueprint.points` y llama `drawOnCanvas` para repintar.
  - No hace nada si `currentBlueprint` es `null` (no hay canvas seleccionado/creado).

- **apimock.js:**  
	- Mock local que simula el API REST, permitiendo desarrollo y pruebas sin backend real.
	- Métodos: `getBlueprintsByAuthor`, `getBlueprintsByNameAndAuthor`.

- **apiclient.js:**  
	- Cliente REST con Promesas usando `$.ajax` para `GET/POST/PUT/DELETE`.
	- Ejemplo técnico de PUT requerido en el enunciado:
	  - `$.ajax({ url:'/blueprints', type:'PUT', data: JSON.stringify(bp), contentType:'application/json' })`.
	- Flujo con Promesas: `post/put/del(...).then(() => getAll()).then(refrescarUI)`.

#### CSS y Bootstrap

- Bootstrap se usa para la estructura visual y estilos responsivos.
- La tabla, botones y formularios usan clases Bootstrap para una apariencia profesional y consistente.

---

## Flujo de la Aplicación

1. El usuario ingresa un autor y presiona “Get blueprints”.
2. Se consulta el mock/API y se listan los planos en la tabla, mostrando nombre, número de puntos y un botón para graficar.
3. Al hacer clic en “Ver plano”, se consulta el plano y se dibuja en el canvas, mostrando su nombre.
4. El usuario puede hacer click/tap en el canvas para agregar puntos al final de la polilínea (en memoria) y se repinta inmediatamente.
5. “Save/Update”: si el plano es nuevo (modo creación) hace POST; si existe, hace PUT. Tras confirmar, hace GET de todos los planos y actualiza tabla y puntaje del autor seleccionado.
6. “Create new blueprint”: solicita nombre y prepara un plano vacío para el autor. El primer “Save/Update” hará POST.
7. “Delete”: borra el plano actual en backend (DELETE), limpia canvas y refresca tabla y puntaje con GET.
8. El total de puntos se calcula siempre con `reduce` sobre el listado visible.

---

## Consideraciones Técnicas

- **Encapsulamiento:** El uso del patrón módulo garantiza que el estado y las funciones auxiliares no contaminen el espacio global.
- **Renderizado reactivo:** Cada acción del usuario actualiza el DOM de forma eficiente usando jQuery.
- **Extensibilidad:** El diseño permite agregar nuevas vistas, validaciones o integraciones (por ejemplo, autenticación) fácilmente.
- **Pruebas y desarrollo:** El mock permite desarrollar el frontend sin depender del backend.
- **Consumo de API REST:** El cliente real usa AJAX y callbacks para mantener la interfaz reactiva y desacoplada.

---

## Ejecución

1. Instalar dependencias y compilar:
	 ```sh
	 mvn clean install
	 ```
2. Ejecutar el backend:
	 ```sh
	 mvn spring-boot:run
	 ```
3. Acceder a la aplicación:
	 ```
	 http://localhost:8080/index.html
	 ```
4. Usar la consola del navegador para depuración y pruebas de los módulos.


## Autores

- Juan-Rpenuela

---