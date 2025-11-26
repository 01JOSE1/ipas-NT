# 🚀 GUÍA RÁPIDA DE INSTALACIÓN - IA + Spring Boot

## 📋 Lo que hemos hecho

Se ha integrado completamente el modelo IA entrenado con tu aplicación Spring Boot. Ahora cada vez que crees una póliza, automáticamente se analizará con el modelo y se asignará un nivel de riesgo (BAJO, MEDIO, ALTO, CRITICO).

---

## ⚡ Pasos para ejecutar (En orden)

### **PASO 1: Terminal 1 - Ejecutar la API Python**

```bash
# Navega a la carpeta del modelo
cd entrenamiento-ia-seguros

# Instala las dependencias (primera vez)
pip install -r requirements.txt

# Ejecuta el modelo IA
python api_modelo.py
```

**Deberías ver:**
```
🚀 Iniciando API de Análisis de Riesgo...
📍 API disponible en: http://localhost:5000
 * Running on http://0.0.0.0:5000
```

✅ **La API Python está lista cuando ves estos mensajes**

---

### **PASO 2: Terminal 2 - Ejecutar Spring Boot**

```bash
# En otra terminal/CMD
cd ipas

# Compila el proyecto (primera vez o si hay cambios)
mvnw.cmd clean install

# Ejecuta la aplicación
java -jar target/ipas-0.0.1-SNAPSHOT.jar
```

O más simple:
```bash
mvnw.cmd spring-boot:run
```

**Deberías ver:**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_|\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::       (v X.X.X.RELEASE)

...
2025-11-25 14:30:45.123  INFO ... IpasApplication : Started IpasApplication in X.XXX seconds (JVM running for X.XXX)
```

✅ **Spring Boot está listo cuando ves "Started IpasApplication"**

---

### **PASO 3: Usar la aplicación en el navegador**

```
http://localhost:8010
```

Inicia sesión con:
- **Email:** asesor@ipas.com
- **Password:** password

O tu usuario registrado

---

## 🧪 Probar la Integración

### Test 1: Crear una póliza

1. Ve a **Pólizas**
2. Haz clic en **"+ Nueva Póliza"**
3. Llena el formulario:
   - **Cliente:** Selecciona un cliente
   - **Tipo de Póliza:** Cualquiera
   - **Prima:** 500
   - **Cobertura Total:** 50000
   - **Fecha Inicio:** Hoy
   - **Fecha Vencimiento:** +365 días
   - **Descripción:** Cualquiera
4. Haz clic en **"Guardar"**

### Test 2: Verificar el resultado

En la tabla de pólizas, deberías ver:

| # | Tipo | Cliente | **Nivel de Riesgo** | Prima | Estado |
|---|------|---------|---------------------|-------|--------|
| P001 | AUTO | Juan Pérez | 🟢 **BAJO** | S/ 500 | Activa |

✅ El nivel de riesgo aparece automáticamente

---

## 🔍 Verificar que todo funciona

### Comprobar API Python

En PowerShell/CMD:
```bash
curl http://localhost:5000/health
```

Respuesta esperada:
```json
{"status":"ok","modelo_cargado":true}
```

### Ver logs de Spring Boot

Busca estos mensajes en la consola:
```
Enviando datos al modelo IA: RiesgoSiniestroRequestDTO@...
Predicción exitosa - Riesgo: MEDIO, Probabilidad: 0.45
```

### Ver logs de Python API

En la terminal de Python deberías ver:
```
INFO:__main__:Datos recibidos: {'edad': 35, 'document_type': 'DNI', ...}
INFO:__main__:Predicción: Riesgo=MEDIO, Probabilidad=45.32%
```

---

## 🎨 Colores de los Niveles de Riesgo

En la UI aparecerán con estos colores en la tabla:

```
🟢 BAJO      → Verde
🟡 MEDIO     → Naranja
🔴 ALTO      → Rojo
🔴 CRITICO   → Rojo
⚪ DESCONOCIDO → Gris (si hay error)
```

---

## ⚠️ Si algo no funciona

### Problema: "Connection refused" en Spring Boot

```
ERROR: Connection refused to host: 127.0.0.1, port: 5000
```

**Solución:**
1. ¿Está Python corriendo? → Abre Terminal 1 y ejecuta `python api_modelo.py`
2. ¿Está en el puerto 5000? → Sí, debe estar en `http://localhost:5000`
3. Firewall bloqueando → Comprueba que `localhost:5000` no está bloqueado

### Problema: "riskLevel es NULL" en la póliza

**Solución:**
1. Verifica los logs de Python API
2. Ejecuta curl test: `curl http://localhost:5000/health`
3. Reinicia ambas aplicaciones

### Problema: Java/Maven no encontrado

```
'mvnw.cmd' is not recognized
```

**Solución:**
1. Asegúrate estar en la carpeta `ipas`
2. Ejecuta: `dir mvnw.cmd` para verificar que existe
3. Si no existe, usa: `mvn spring-boot:run` (si Maven está instalado)

---

## 📊 Arquitectura de la Integración

```
┌─────────────────────────────────────────────────┐
│          NAVEGADOR (http://localhost:8010)      │
│  • Formulario para crear póliza                  │
│  • Tabla con pólizas y Nivel de Riesgo          │
└──────────────────────┬──────────────────────────┘
                       │ HTTP POST
                       ▼
    ┌──────────────────────────────────┐
    │   SPRING BOOT (Puerto 8010)      │
    │                                  │
    │  PolicyPresenter.handleCreate()  │
    │    └─ IAModeloService.predict()  │
    │         └─ HTTP POST ──────┐     │
    └──────────────────────────────────┘
                                 │
                                 ▼
            ┌────────────────────────────────┐
            │  FLASK API (Puerto 5000)       │
            │  /predecir-riesgo              │
            │                                │
            │  • Carga modelo (joblib)       │
            │  • Codifica features           │
            │  • Predice probabilidad        │
            │  • Asigna nivel de riesgo      │
            │    └─ Retorna JSON ────┐       │
            └────────────────────────────────┘
                                 │
                      ┌──────────┘
                      │
                      ▼
    ┌──────────────────────────────────┐
    │   Spring Boot guarda en BD       │
    │   • Policy.riskLevel = "MEDIO"   │
    └──────────────────────────────────┘
                      │
                      ▼
    ┌──────────────────────────────────┐
    │   UI muestra póliza con riesgo   │
    │   Badge: 🟡 MEDIO                │
    └──────────────────────────────────┘
```

---

## 📁 Archivos Nuevos/Modificados

### ✨ Archivos Nuevos Creados:
- `entrenamiento-ia-seguros/api_modelo.py` → API Flask del modelo
- `src/main/java/.../RiesgoSiniestroRequestDTO.java` → DTO request
- `src/main/java/.../RiesgoSiniestroResponseDTO.java` → DTO response
- `src/main/java/.../IAModeloService.java` → Servicio para IA
- `src/main/java/.../RestTemplateConfig.java` → Config HTTP

### 📝 Archivos Modificados:
- `src/main/java/.../Policy.java` → Agregado `riskLevel`
- `src/main/java/.../PolicySimpleDTO.java` → Agregado `riskLevel`
- `src/main/java/.../PolicyPresenter.java` → Llamada a IA
- `src/main/resources/templates/policies.html` → Mostrar riesgo
- `src/main/resources/application.properties` → URL API

---

## 🚀 Próximos Pasos (Opcional)

1. **Entrenar el modelo con más datos:** Ejecuta `python entrenar_modelo.py` nuevamente
2. **Ajustar umbrales de riesgo:** En `api_modelo.py`, función `asignar_nivel_riesgo()`
3. **Agregar métricas:** Usa `/info-modelo` endpoint para ver estado
4. **Desplegar en producción:** Configura variables de entorno para URLs

---

## ✅ Checklist Final

- [ ] Terminal 1: Python API corriendo en `http://localhost:5000`
- [ ] Terminal 2: Spring Boot corriendo en `http://localhost:8010`
- [ ] Puedo acceder a `http://localhost:8010` en el navegador
- [ ] Puedo crear una póliza
- [ ] La póliza aparece con un Nivel de Riesgo (BAJO, MEDIO, ALTO, etc)
- [ ] Los logs muestran "Predicción exitosa"

---

## 📞 Documentación Completa

Para detalles técnicos, ver: **`IA_INTEGRATION_GUIDE.md`**

Este archivo contiene:
- Arquitectura detallada
- Especificación de DTOs
- Configuración avanzada
- Troubleshooting completo
- SQL schema
