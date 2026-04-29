# CyberMath: Protocol Zero 🛡️🔢

> *Un sistema cibernético educativo donde las matemáticas son tu única arma contra el colapso digital*

**CyberMath** es un videojuego de escritorio desarrollado en Java 17 con JavaFX, diseñado para fortalecer el pensamiento lógico-matemático y las competencias en ciberseguridad mediante la gamificación. El proyecto sumerge al usuario en una narrativa de "hacker renegado" donde la resolución de retos matemáticos es la única herramienta para interceptar malware y restaurar nodos corruptos en un entorno de red simulado.

Este software fue desarrollado como parte del **Proyecto Integrador I** del programa de Ingeniería de Sistemas en la **Universidad de Santander (UDES)**.

---

## ✨ Características Principales

### 🖥️ Estética Terminal Hacker
Interfaz inmersiva diseñada con JavaFX y CSS personalizado que replica la apariencia de una consola de comandos real con efectos de texto neón y animaciones cinemáticas.

### 🎲 Generación Procedimental de Retos
El motor de juego genera problemas matemáticos de forma aleatoria, asegurando variedad y aumentando la dificultad progresivamente a través de tres dominios: aritmética, álgebra y cálculo.

### 🗺️ Mapa de Nodos No Lineal
Progresión a través de un árbol de misiones inspirado en ramas de ciberseguridad reales:
- **Phishing** - Detección de patrones
- **Firewall** - Lógica booleana
- **Criptografía** - Secuencias numéricas
- **DDoS** - Optimización bajo presión

### ⚠️ Mecánica de Integridad y Permadeath
- Sistema de **HP (Integridad del Sistema)** que se reduce al cometer errores o agotar el tiempo
- **Permadeath real**: Si la integridad llega a 0%, el perfil del operador es purgado permanentemente del sistema
- Decisiones con consecuencias permanentes que elevan la tensión narrativa

### 💰 Mercado Negro (Tienda)
Sistema económico interno basado en **BTC (Bitcoin)** obtenido al completar misiones:
- **Parches de Kernel**: Restauran integridad del sistema
- **Backups de Emergencia**: Puntos de recuperación ante fallos críticos
- **Upgrades de Protocolo**: Mejoras para operaciones futuras

### 💾 Persistencia Local
Gestión de múltiples **ranuras de memoria (slots)** mediante serialización de objetos Java, permitiendo:
- Guardar hasta 3 perfiles de operador simultáneos
- Almacenamiento seguro del progreso en disco
- Recuperación de sesiones anteriores

---

## 🛠️ Stack Tecnológico

| Componente | Tecnología |
|------------|------------|
| **Lenguaje** | Java 17 |
| **Interfaz Gráfica** | JavaFX (Scene Graph, CSS, Animaciones) |
| **Multimedia** | JavaFX Media (efectos de sonido interactivos) |
| **Gestión de Dependencias** | Apache Maven |
| **Arquitectura** | POO + Patrón MVC + Diagramación UML |

---

## 📂 Estructura del Proyecto

El código aplica el patrón **Modelo-Vista-Controlador (MVC)** para separar responsabilidades:

```
cybermath/
├── src/main/java/
│   ├── CyberMathApp.java          # Controlador central del flujo de escenas
│   ├── LogicaJuego.java           # Motor backend de retos y validación
│   ├── Usuario.java               # Modelo de datos del perfil del operador
│   ├── GestorArchivos.java        # Utilidad de I/O y persistencia binaria
│   └── [otros módulos]
├── src/main/resources/
│   ├── css/                       # Hojas de estilo personalizadas
│   ├── sounds/                    # Efectos de audio
│   └── fxml/                      # Vistas JavaFX
├── pom.xml                        # Configuración Maven
└── README.md
```

### Componentes Clave

- **`CyberMathApp`**: Orquesta el ciclo de vida de la aplicación y gestiona transiciones entre escenas
- **`LogicaJuego`**: Implementa los algoritmos de generación de retos matemáticos y sistema de puntuación
- **`Usuario`**: Clase serializable que encapsula el estado del jugador (integridad, BTC, progreso)
- **`GestorArchivos`**: Maneja operaciones de lectura/escritura para persistencia de datos

---

## 🚀 Instalación y Ejecución

### Requisitos Previos
- **JDK 17** o superior
- **Apache Maven** 3.6+

### Pasos de Instalación

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/tu-usuario/cybermath.git
   cd cybermath
   ```

2. **Compilar el proyecto**
   ```bash
   mvn clean install
   ```

3. **Ejecutar la aplicación**
   ```bash
   mvn javafx:run
   ```

### Ejecución Alternativa (JAR)
```bash
mvn package
java -jar target/cybermath-1.0.jar
```

---

## 🎮 Cómo Jugar

1. **Iniciar Sesión**: Crea un nuevo perfil de operador o carga una ranura existente
2. **Seleccionar Misión**: Navega por el mapa de nodos y elige tu rama de ataque
3. **Resolver Retos**: Completa problemas matemáticos antes de que expire el tiempo
4. **Gestionar Recursos**: Usa tus BTC sabiamente en el Mercado Negro
5. **Sobrevivir**: Mantén tu integridad por encima del 0% para evitar el borrado permanente

---

## 🧪 Propósito Educativo

Este proyecto integra conceptos de:
- **Matemáticas Aplicadas**: Desde aritmética básica hasta cálculo diferencial
- **Ciberseguridad**: Terminología y conceptos reales de la industria
- **Ingeniería de Software**: Patrones de diseño, arquitectura limpia y persistencia de datos
- **Gamificación**: Mecánicas de recompensa, progresión y narrativa inmersiva

---

## 👥 Autores

**Desarrolladores**  
- Brandon Sayid Capacho Leal  
- Daniel Alejandro Perlaza Peñalver  

**Director Académico**  
- Mg. Miguel Fabián Robles Angarita  

**Institución**  
Universidad de Santander (UDES) - Ingeniería de Sistemas

---

## 📄 Licencia

Este proyecto fue desarrollado con fines académicos como parte del Proyecto Integrador I. 

---

## 🔮 Trabajo Futuro

- [ ] Implementación de modo multijugador competitivo
- [ ] Integración de leaderboards globales
- [ ] Expansión del árbol de misiones con nuevas ramas temáticas
- [ ] Sistema de logros y badges coleccionables
- [ ] Soporte para más dominios matemáticos (teoría de números, geometría)

---

<div align="center">

**[Iniciar Protocolo]** | **[Documentación]** | **[Reportar Bug]**

*"En el reino digital, las matemáticas son el único firewall confiable"*

</div>
