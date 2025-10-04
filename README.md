# ARSW-LAB5: Cliente Grueso con API REST, HTML5, JavaScript y CSS3

## Escuela Colombiana de Ingeniería  
### Arquitecturas de Software

## Descripción General

Este proyecto implementa un cliente web (“cliente grueso”) que consume un API REST de planos, usando HTML5, JavaScript (modular), CSS3 y Bootstrap. El frontend permite consultar, listar y graficar planos de un autor, integrando lógica de negocio, renderizado dinámico y consumo de datos tanto mockeados como reales.

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
- Se agregan dependencias WebJars en el `pom.xml` para Bootstrap y jQuery, permitiendo servir los recursos estáticos desde el backend.
- El API expone endpoints para consultar planos por autor y por nombre.

### 2. Frontend - Estructura y Componentes

#### HTML

- **Formulario de autor:** Input para capturar el nombre del autor y botón “Get blueprints”.
- **Tabla de planos:** Muestra nombre y número de puntos de cada plano, y un botón para graficar cada uno.
- **Total de puntos:** Campo que suma los puntos de todos los planos listados.
- **Canvas:** Elemento `<canvas>` para graficar el plano seleccionado.
- **Campos dinámicos:** Para mostrar el autor y el nombre del plano seleccionado.

#### JavaScript (app.js)

- **Patrón Módulo:**  
	- Se implementa un módulo IIFE que encapsula el estado y expone una API pública.
	- Variables privadas: autor seleccionado, lista de planos resumidos.
	- Funciones públicas:
		- `setAuthor(autor)`: Cambia y muestra el autor seleccionado.
		- `updateBlueprintsByAuthor(autor)`: Consulta los planos del autor, actualiza la tabla y el total.
		- `drawBlueprint(autor, nombre)`: Consulta y grafica el plano en el canvas, muestra el nombre.
	- Renderizado:
		- La tabla se genera dinámicamente con jQuery, agregando un botón “Ver plano” por fila.
		- El total de puntos se calcula con `reduce`.
		- El canvas se limpia y se dibujan los segmentos de recta según los puntos del plano.
	- Integración con el DOM:
		- Se usan selectores jQuery para actualizar campos y construir la tabla.
		- El botón “Get blueprints” está cableado para disparar la consulta y renderizado.

- **apimock.js:**  
	- Mock local que simula el API REST, permitiendo desarrollo y pruebas sin backend real.
	- Métodos: `getBlueprintsByAuthor`, `getBlueprintsByNameAndAuthor`.

- **apiclient.js:**  
	- Implementa la misma interfaz que apimock, pero usando AJAX (`$.get`) para consumir el API REST real.
	- Permite alternar entre mock y API real cambiando una sola línea en app.js.

#### CSS y Bootstrap

- Bootstrap se usa para la estructura visual y estilos responsivos.
- La tabla, botones y formularios usan clases Bootstrap para una apariencia profesional y consistente.

---

## Flujo de la Aplicación

1. El usuario ingresa un autor y presiona “Get blueprints”.
2. Se consulta el mock/API y se listan los planos en la tabla, mostrando nombre, número de puntos y un botón para graficar.
3. Al hacer clic en “Ver plano”, se consulta el plano y se dibuja en el canvas, mostrando su nombre.
4. El total de puntos de todos los planos se actualiza dinámicamente.
5. El usuario puede alternar entre datos mock y datos reales cambiando la referencia al proveedor de datos en app.js.

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