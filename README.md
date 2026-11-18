# SIGECO - Sistema de Gestion de Contactos

Aplicacion Android en **Kotlin** con **Jetpack Compose**, **Material 3** y **Room** (SQLite) para la gestion de contactos personales.

---

## Laboratorio 10.1 - Semana 10

Curso: Desarrollo de Sistemas Moviles / UNMSM

### Conceptos implementados

1. **Jetpack Compose**: UI declarativa (reemplaza layouts XML)
2. **Material 3 (Material You)**: tema con colores dinamicos en Android 12+
3. **Room ORM**: persistencia en SQLite con anotaciones `@Entity`, `@Dao`, `@Database`
4. **ViewModel + StateFlow**: estado reactivo que sobrevive rotaciones de pantalla
5. **Navigation Compose**: navegacion entre pantallas con back stack
6. **Coroutines**: operaciones de BD en hilo secundario con `suspend` y `Flow`
7. **Validaciones**: campos obligatorios, formato email, telefono unico de 9 digitos

---

## Estructura del proyecto

```
sigeco/
├── app/src/main/java/com/sigeco/contactos/
│   ├── MainActivity.kt                    # Punto de entrada
│   ├── data/
│   │   ├── Contacto.kt                   # @Entity - tabla SQLite
│   │   ├── ContactoDao.kt                # @Dao - operaciones SQL
│   │   └── AppDatabase.kt                # @Database - config Room
│   ├── ui/
│   │   ├── viewmodel/
│   │   │   └── ContactoViewModel.kt      # Logica + validaciones + estado
│   │   ├── screens/
│   │   │   ├── ListaContactosScreen.kt   # Pantalla principal (grilla)
│   │   │   └── FormularioContactoScreen.kt # Crear/editar contacto
│   │   └── theme/
│   │       ├── Color.kt                  # Paleta de colores
│   │       ├── Theme.kt                  # Tema Material 3
│   │       └── Type.kt                   # Tipografia
│   └── navigation/
│       └── NavGraph.kt                   # Rutas entre pantallas
├── build.gradle.kts                       # Config Gradle (proyecto)
├── app/build.gradle.kts                   # Config Gradle (app + dependencias)
└── gradle/libs.versions.toml             # Catalogo de versiones
```

---

## Dependencias principales

| Libreria | Proposito |
|----------|-----------|
| Jetpack Compose (BOM 2024.11) | UI declarativa |
| Material 3 | Componentes y tema visual |
| Room 2.6.1 | ORM para SQLite |
| Navigation Compose 2.8.4 | Navegacion entre pantallas |
| Lifecycle ViewModel 2.8.7 | Estado que sobrevive rotaciones |
| KSP 2.0.21 | Procesador de anotaciones (Room) |

---

## Requisitos

- **Android Studio** 2024.x o superior
- **JDK 11** o superior
- **Android SDK** API 35 (compileSdk)
- Dispositivo/emulador con **Android 8.0+** (minSdk 26)

---

## Instrucciones de ejecucion

1. Abrir la carpeta `sigeco/` en Android Studio
2. Esperar a que Gradle sincronice las dependencias
3. Conectar un dispositivo o iniciar un emulador
4. Ejecutar la app con el boton Run

> **Nota SSL (Windows)**: `gradle.properties` ya incluye la configuracion para usar el trust store de Windows.

---

## Funcionalidades (CRUD)

| Operacion | Descripcion |
|-----------|-------------|
| **Crear** | Formulario con validacion de campos y telefono unico |
| **Consultar** | Busqueda aproximada por nombre, apellido, telefono o email |
| **Actualizar** | Seleccionar contacto de la grilla para editar |
| **Eliminar** | Seleccionar uno o mas contactos con checkbox + confirmacion |

### Validaciones implementadas

- Todos los campos son obligatorios
- Telefono: exactamente 9 digitos, unico en la BD
- Email: formato valido (xxx@yyyy.uuuu)
- Nombre y Apellido: maximo 200 caracteres
- Confirmacion antes de eliminar

---

## Autor

- **Usuario:** poma2020diego
- **Curso:** Desarrollo de Sistemas Moviles / UNMSM
