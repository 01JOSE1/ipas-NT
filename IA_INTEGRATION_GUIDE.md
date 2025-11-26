# 🚀 Integración Completa: Spring Boot + Modelo IA

## 📋 Descripción General

Este documento explica cómo está integrado el modelo IA de análisis de riesgo de siniestros con tu aplicación Spring Boot IPAS. Cada vez que se crea una póliza, automáticamente se consulta el modelo Python para asignar un nivel de riesgo.

---

## 🔄 Flujo Completo del Sistema

```
Usuario crea póliza en UI 
    ↓
JavaScript envía datos a Spring Boot (POST /api/policies)
    ↓
PolicyPresenter.handleCreatePolicy() recibe la solicitud
    ↓
IAModeloService.predecirRiesgo() llama a la API Python
    ↓
API Flask en http://localhost:5000/predecir-riesgo
    ↓
Modelo IA analiza datos y devuelve: {riesgo: "BAJO|MEDIO|ALTO|CRITICO", probabilidad: 0.XX}
    ↓
Spring Boot guarda la póliza con el risk_level automáticamente asignado
    ↓
UI muestra la póliza con el nivel de riesgo coloreado
```

---

## 🛠️ Archivos Creados/Modificados

### Frontend (JavaScript/HTML)

**`templates/policies.html`**
- Tabla de pólizas muestra columna "Nivel de Riesgo" con badges coloreados
- Detalles de póliza incluyen el nivel de riesgo
- El nivel se calcula automáticamente en el backend (no se envía desde UI)

### Backend (Java)

#### 1. **DTOs para comunicación con el modelo IA**

**`view/dto/RiesgoSiniestroRequestDTO.java`**
```java
// Datos que se envían AL modelo Python
{
    "edad": 35,
    "document_type": "DNI",
    "occupation": "Ingeniero",
    "siniestro": "SI",
    "cliente_status": "ACTIVE",
    "policy_type": "AUTOMOVIL",
    "premium_amount": 500.00,
    "coverage_amount": 50000.00,
    "deductible": 1000.00,
    "duracion_dias": 365,
    "valor_siniestro": 0.00
}
```

**`view/dto/RiesgoSiniestroResponseDTO.java`**
```java
// Respuesta que regresa DEL modelo Python
{
    "success": true,
    "riesgo": "MEDIO",           // BAJO|MEDIO|ALTO|CRITICO
    "probabilidad": 0.45,         // 0.0 a 1.0
    "mensaje": "Análisis de riesgo completado: MEDIO"
}
```

#### 2. **Servicio para consultar el modelo IA**

**`model/service/IAModeloService.java`**
```java
@Service
public class IAModeloService {
    
    public RiesgoSiniestroResponseDTO predecirRiesgo(Client client, Policy policy) {
        // Construye RiesgoSiniestroRequestDTO con datos del cliente y póliza
        // Llama a http://localhost:5000/predecir-riesgo
        // Retorna RiesgoSiniestroResponseDTO con el nivel de riesgo predicho
    }
    
    public boolean verificarConexion() {
        // Verifica que la API del modelo esté disponible
    }
}
```

#### 3. **Configuración de RestTemplate**

**`config/RestTemplateConfig.java`**
```java
@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
            .setConnectTimeout(Duration.ofSeconds(10))
            .setReadTimeout(Duration.ofSeconds(30))
            .build();
    }
}
```

#### 4. **Actualización de PolicyPresenter**

**`presenter/PolicyPresenter.java`**
```java
@Component
public class PolicyPresenter {
    
    @Autowired
    private IAModeloService iaModeloService;  // ← NUEVO
    
    public ResponseEntity<Map<String, Object>> handleCreatePolicy(...) {
        // ...
        
        // Consultar el modelo IA para predecir el nivel de riesgo
        RiesgoSiniestroResponseDTO riesgoResponse = iaModeloService.predecirRiesgo(client, policy);
        if (riesgoResponse != null && riesgoResponse.getSuccess()) {
            policy.setRiskLevel(riesgoResponse.getRiesgo());  // ← ASIGNA EL RIESGO
        } else {
            policy.setRiskLevel("DESCONOCIDO");  // Por defecto si hay error
        }
        
        Policy savedPolicy = policyService.save(policy);
        // ... retorna DTO con riskLevel
    }
}
```

#### 5. **Entidad Policy actualizada**

**`model/entity/Policy.java`**
```java
@Entity
@Table(name = "policies")
public class Policy {
    // ... otros campos ...
    
    @Column(name = "risk_level", length = 50)
    private String riskLevel;  // ← Campo nuevo
    
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
}
```

#### 6. **DTO PolicySimpleDTO actualizado**

**`view/dto/PolicySimpleDTO.java`**
```java
public class PolicySimpleDTO {
    // ... otros campos ...
    private String riskLevel;  // ← Campo nuevo
    
    // Constructor ahora incluye riskLevel
    public PolicySimpleDTO(..., String riskLevel) {
        // ...
        this.riskLevel = riskLevel;
    }
}
```

### Python (Modelo IA)

**`entrenamiento-ia-seguros/api_modelo.py`**
```python
from flask import Flask, request, jsonify
import joblib
import numpy as np

@app.route('/predecir-riesgo', methods=['POST'])
def predecir_riesgo():
    """
    Recibe datos de una póliza en JSON
    Devuelve el nivel de riesgo predicho
    """
    datos = request.get_json()
    
    # Preparar features en el orden que el modelo espera
    features_array = [...]
    X = np.array(features_array).reshape(1, -1)
    
    # Hacer predicción
    probabilidad = modelo.predict_proba(X)[0][1]
    nivel_riesgo = asignar_nivel_riesgo(probabilidad)
    
    return jsonify({
        'success': True,
        'riesgo': nivel_riesgo,
        'probabilidad': float(probabilidad),
        'mensaje': f'Análisis de riesgo completado: {nivel_riesgo}'
    })
```

### Configuración

**`application.properties`**
```properties
# IA Model Configuration
ia.modelo.url=http://localhost:5000
```

---

## 🚀 Cómo Ejecutar

### Paso 1: Entrenar el modelo (primera vez)
```bash
cd entrenamiento-ia-seguros
python entrenar_modelo.py
```
Esto genera:
- `modelo_siniestros.pkl`
- `encoders.pkl`
- `features.pkl`

### Paso 2: Ejecutar la API Flask
```bash
cd entrenamiento-ia-seguros
python api_modelo.py
```
Verás:
```
🚀 Iniciando API de Análisis de Riesgo...
📍 API disponible en: http://localhost:5000
```

### Paso 3: Ejecutar Spring Boot
```bash
cd ipas
./mvnw.cmd clean install
./mvnw.cmd spring-boot:run
```

O si ya está compilado:
```bash
java -jar target/ipas-0.0.1-SNAPSHOT.jar
```

### Paso 4: Acceder a la aplicación
```
http://localhost:8010
```

---

## 🧪 Pruebas

### Test 1: Verificar conexión con la API del modelo

En el navegador o con curl:
```bash
curl http://localhost:5000/health
```

Respuesta esperada:
```json
{
  "status": "ok",
  "modelo_cargado": true
}
```

### Test 2: Crear una póliza desde la UI

1. Accede a http://localhost:8010
2. Dirígete a "Pólizas"
3. Haz clic en "+ Nueva Póliza"
4. Llena el formulario y haz clic en "Guardar"
5. Verifica en la tabla que aparezca un "Nivel de Riesgo" coloreado

### Test 3: Revisar logs de la API Python

Deberías ver algo como:
```
INFO:__main__:Datos recibidos: {'edad': 35, 'document_type': 'DNI', ...}
INFO:__main__:Predicción: Riesgo=MEDIO, Probabilidad=45.32%
```

### Test 4: Revisar logs de Spring Boot

Deberías ver:
```
Enviando datos al modelo IA: RiesgoSiniestroRequestDTO@...
Predicción exitosa - Riesgo: MEDIO, Probabilidad: 0.4532
```

---

## 📊 Niveles de Riesgo

| Nivel | Probabilidad | Color | Descripción |
|-------|-------------|-------|-------------|
| BAJO | < 25% | Verde (success) | Muy bajo riesgo de siniestro |
| MEDIO | 25% - 50% | Naranja (warning) | Riesgo moderado |
| ALTO | 50% - 75% | Rojo (error) | Riesgo elevado |
| CRITICO | > 75% | Rojo (error) | Riesgo muy elevado |
| DESCONOCIDO | Error | Gris (secondary) | Error en la predicción |

---

## 🔧 Configuración Avanzada

### Cambiar puerto de la API Python
En `entrenamiento-ia-seguros/api_modelo.py`, línea final:
```python
app.run(debug=True, host='0.0.0.0', port=5001)  # Cambiar 5001 a otro puerto
```

Luego actualizar en `application.properties`:
```properties
ia.modelo.url=http://localhost:5001
```

### Habilitar/Deshabilitar la IA en tiempo de ejecución
En `IAModeloService.java`, el método `predecirRiesgo()` retorna un error graceful si la API no está disponible, asignando `"DESCONOCIDO"` como riesgo.

### Logs detallados
En Python, los logs ya están configurados (INFO level).

En Java, agregar a `application.properties`:
```properties
logging.level.com.ipas.ipas.model.service.IAModeloService=DEBUG
```

---

## 🐛 Troubleshooting

### La póliza se guarda pero riskLevel es NULL

**Causa:** La API Python no está corriendo.
**Solución:** 
1. Inicia Python: `python api_modelo.py`
2. Verifica: `curl http://localhost:5000/health`
3. Revisa los logs de Spring Boot para el error exacto

### Error: "Connection refused" en Spring Boot

**Causa:** Python API no está disponible.
**Solución:** 
1. Verifica que Python está corriendo en el puerto correcto
2. Verifica firewall/antivirus no bloquea `localhost:5000`
3. Comprueba que los modelos están cargados en `entrenamiento-ia-seguros/`

### El modelo predice "DESCONOCIDO" siempre

**Causa:** Error en la predicción del modelo.
**Solución:**
1. Revisa los logs de Python para el error exacto
2. Verifica que los datos enviados tienen el formato correcto
3. Confirma que los encoders están correctamente cargados

### Timeout en la llamada al modelo

**Causa:** El modelo está procesando demasiado tiempo.
**Solución:**
1. Aumentar timeout en `RestTemplateConfig.java`:
   ```java
   .setReadTimeout(Duration.ofSeconds(60))  // Aumentar de 30
   ```
2. Verificar carga del servidor Python
3. Reducir complejidad del modelo si es posible

---

## 📝 Base de Datos

La tabla `policies` ahora incluye la columna `risk_level`:

```sql
ALTER TABLE policies ADD COLUMN risk_level VARCHAR(50);
```

O si está usando `spring.jpa.hibernate.ddl-auto=update`, Hibernate lo agrega automáticamente.

---

## 🎯 Resumen de Cambios

✅ **Archivos creados:**
- `entrenamiento-ia-seguros/api_modelo.py`
- `src/main/java/com/ipas/ipas/view/dto/RiesgoSiniestroRequestDTO.java`
- `src/main/java/com/ipas/ipas/view/dto/RiesgoSiniestroResponseDTO.java`
- `src/main/java/com/ipas/ipas/model/service/IAModeloService.java`
- `src/main/java/com/ipas/ipas/config/RestTemplateConfig.java`

✅ **Archivos modificados:**
- `src/main/java/com/ipas/ipas/model/entity/Policy.java` → Agregado `riskLevel`
- `src/main/java/com/ipas/ipas/view/dto/PolicySimpleDTO.java` → Agregado `riskLevel`
- `src/main/java/com/ipas/ipas/presenter/PolicyPresenter.java` → Llamada a IA antes de guardar
- `src/main/resources/templates/policies.html` → Mostrar riskLevel en tabla
- `src/main/resources/application.properties` → URL de la API

---

## 📞 Soporte

Para problemas o preguntas:
1. Revisa los logs de Python y Spring Boot
2. Verifica que ambas aplicaciones están corriendo
3. Asegúrate que los puertos son correctos (5000 para Python, 8010 para Spring)
