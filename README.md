# 🛍️ Aurum Verus

**Aurum Verus** es una aplicación móvil de gestión de ventas de productos de regalo, desarrollada como parte de un Trabajo de Fin de Grado en Desarrollo de Aplicaciones Multiplataforma. Esta app está diseñada para ofrecer una experiencia intuitiva tanto para vendedores como para clientes, permitiendo una gestión completa de productos, pedidos y usuarios.

---

## 📱 Descripción

La aplicación **Aurum Verus** permite a los vendedores gestionar su catálogo de productos y a los clientes explorar y comprar artículos de forma cómoda. Se ha desarrollado con el objetivo de cubrir el ciclo completo de ventas, desde la publicación de productos hasta el historial de compras, integrando funcionalidades modernas y un diseño adaptado a móviles Android.

---

## ✨ Características

- **Plataforma:** Android
- **Lenguaje y herramientas:** Kotlin, Android Studio, Firebase
- **Funcionalidades principales:**
  - Gestión de productos (confirmación, modificaciones, creación)
  - Roles de usuario (cliente y vendedor)
  - Sistema de autenticación
  - Base de datos en la nube
  - Interfaz moderna 

---

## 🗂️ Estructura del Proyecto

```
Cliente/
Archivos relevantes con las funcionalidades del cliente como Inicio/Registro, compra de productos, etc

Vendedor/
Archivos relevantes con las funcionalidades del vendedor como Inicio/Registro, gestión de productos, etc

Adaptadores/
Archivos para adaptar listas a una vista de elementos repetidos

ImagenSeleccionada/
Clase usada para manejar y transportar información sobre imágenes seleccionadas o asociadas a productos

Modelos/
Modelos de datos para representar entidades clave del sistema y facilitar la interacción con Firebase y la interfaz de usuario

Constantes.kt
Almacena constantes usadas a lo largo de todo el proyecto, en este caso un transformador de tiempo

SeleccionUsuarioActivity.kt
Elección del tipo de usuario

SplashScreenActivity.kt
Animación de inicio de la app

AndroidManifest.xml
Archivo que configura cómo se comporta tu app a nivel del sistema operativo, declarando componentes, permisos, punto de entrada, etc

/drawable
Almacenamiento de las imagenes e iconos usados en la app

/layout
Carpeta con los apartados visuales de la app

/menu
Carpeta separada de layout donde se encuentran unas vistas personalizadas para el botón desplegable

/values
Almacenamiento de "constantes" de texto, color y demás que se usan en diferentes partes del programa, pudiendo cambiarlo desde aqui en vez de archivo a archivo

/Gradle Scripts
Almacena los archivos en los que se declaran las dependencias, las librerias y otros archivos



## 🖥️ Requisitos del Sistema

Para compilar y ejecutar el proyecto, necesitas tener instalado:

- Android Studio 
- Kotlin (configurado por defecto en el proyecto)
- Firebase (configurado vía `google-services.json`)
- JDK 17

---

## 🚀 Cómo Ejecutar la App

1. Clona el repositorio:
   ```bash
   git clone https://github.com/usuario/repositorio.git
   ```
  En este caso: https://github.com/Zarckver7/AplicacionMovil_TFG.git
2. Abre el proyecto con **Android Studio**.
3. Espera a que finalice la sincronización de Gradle.
4. Conecta un dispositivo Android o utiliza un emulador.
5. Ejecuta la aplicación presionando el botón **"Run"**.

---

## 🧰 Recursos Utilizados

Esta aplicación hace uso de bibliotecas y herramientas como:

- Firebase Auth, Realtime Database y Firebase Storage
- Glide (para carga de imágenes)
- Material Components
- Otros recursos gratuitos disponibles en línea
Enlaces:
  - https://github.com/sharukhrs/ImagePicker
  - https://github.com/bumptech/glide

---

## 📜 Licencia

Este proyecto se distribuye bajo la licencia MIT. Consulta el archivo `LICENSE` para más información.

---

## 👨‍💻 Contacto

Para consultas, sugerencias o contribuciones, puedes contactar al autor:

**Nombre:** Alejandro Flores Ruiz
**Correo:** ruizfloresale48@gmail.com
**GitHub:** Zarckver7
