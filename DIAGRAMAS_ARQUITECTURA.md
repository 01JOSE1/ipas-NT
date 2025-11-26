# 🎯 DIAGRAMA DE LA INTEGRACIÓN IA

## 1️⃣ ARQUITECTURA GENERAL

```
┌────────────────────────────────────────────────────────────┐
│                    NAVEGADOR DEL USUARIO                    │
│              http://localhost:8010                          │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  FORMULARIO: Nueva Póliza                            │   │
│  │  • Cliente: [Select]                                 │   │
│  │  • Tipo: [Select]                                    │   │
│  │  • Prima: [500.00]                                   │   │
│  │  • Cobertura: [50000.00]                             │   │
│  │  • Fecha Inicio: [2025-11-25]                        │   │
│  │  • Fecha Fin: [2026-11-25]                           │   │
│  │  [Guardar]                                           │   │
│  └──────────────────────────────────────────────────────┘   │
│                      │ (Click)                               │
│                      ▼ (AJAX/Fetch)                          │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  TABLA DE PÓLIZAS                                    │   │
│  ├───┬──────┬────────┬────────┬──────┬──────────┬───────┤   │
│  │ # │ Tipo │Cliente │Risk 🔴│Prima │ Vto.   │Acciones│  │
│  ├───┼──────┼────────┼────────┼──────┼──────────┼───────┤   │
│  │P1 │ AUTO │ Juan   │🟢 BAJO │500  │26-Nov │  ✏️ 👁️  │   │
│  │P2 │ VIDA │ María  │🟡 MEDI│1000 │25-Dic │  ✏️ 👁️  │   │
│  │P3 │HOGAR │ Pedro  │🔴 ALTO│2000 │10-Ene │  ✏️ 👁️  │   │
│  └───┴──────┴────────┴────────┴──────┴──────────┴───────┘   │
│                                                              │
└────────────────────────────────────────────────────────────┘
                           │
           HTTP POST (JSON) │ /api/policies/create
                           ▼
┌────────────────────────────────────────────────────────────┐
│           SPRING BOOT (Puerto 8010)                        │
│                                                             │
│  ┌────────────────────────────────────────────────────┐   │
│  │ PolicyController.createPolicy()                    │   │
│  │ • Valida entrada                                   │   │
│  │ • Crea objeto PolicyRequest                        │   │
│  │ • Llama a PolicyPresenter                          │   │
│  └─────────────────────┬──────────────────────────────┘   │
│                        │                                    │
│  ┌────────────────────────────────────────────────────┐   │
│  │ PolicyPresenter.handleCreatePolicy()               │   │
│  │ • Obtiene cliente de BD                            │   │
│  │ • Crea objeto Policy                               │   │
│  │ • ⭐ LLAMADA A IA ⭐                              │   │
│  │   iaModeloService.predecirRiesgo(client, policy)  │   │
│  │ • Asigna: policy.setRiskLevel(resultado)          │   │
│  │ • Guarda policy en BD                              │   │
│  │ • Retorna PolicySimpleDTO con riskLevel           │   │
│  └─────────────────────┬──────────────────────────────┘   │
│                        │                                    │
│  ┌────────────────────────────────────────────────────┐   │
│  │ IAModeloService.predecirRiesgo()                   │   │
│  │ • Construye RiesgoSiniestroRequestDTO              │   │
│  │ • Hace HTTP POST a Python API                      │   │
│  │ • Recibe RiesgoSiniestroResponseDTO                │   │
│  │ • Retorna respuesta                                │   │
│  └─────────────────────┬──────────────────────────────┘   │
│                        │                                    │
└─────────────────────────┼─────────────────────────────────┘
                          │
         HTTP POST (JSON) │ /predecir-riesgo
                          ▼
┌────────────────────────────────────────────────────────────┐
│           FLASK API PYTHON (Puerto 5000)                   │
│                                                             │
│  ┌────────────────────────────────────────────────────┐   │
│  │ @app.route('/predecir-riesgo', methods=['POST'])   │   │
│  │                                                     │   │
│  │ INPUT JSON:                                        │   │
│  │ {                                                  │   │
│  │   "edad": 35,                                      │   │
│  │   "document_type": "DNI",                          │   │
│  │   "occupation": "Ingeniero",                       │   │
│  │   "siniestro": "SI",                               │   │
│  │   "cliente_status": "ACTIVE",                      │   │
│  │   "policy_type": "AUTOMOVIL",                      │   │
│  │   "premium_amount": 500.00,                        │   │
│  │   "coverage_amount": 50000.00,                     │   │
│  │   "deductible": 1000.00,                           │   │
│  │   "duracion_dias": 365,                            │   │
│  │   "valor_siniestro": 0.00                          │   │
│  │ }                                                  │   │
│  │                                                    │   │
│  │ PROCESAMIENTO:                                     │   │
│  │ 1. Cargar encoders (LabelEncoder para cat.)        │   │
│  │ 2. Encodear variables categóricas                 │   │
│  │ 3. Preparar array de features                     │   │
│  │ 4. Invocar modelo RandomForest                    │   │
│  │    modelo.predict_proba(X)                        │   │
│  │ 5. Obtener probabilidad de siniestro              │   │
│  │ 6. Convertir probabilidad a nivel:                │   │
│  │    • P < 0.25 → "BAJO"                            │   │
│  │    • P < 0.50 → "MEDIO"                           │   │
│  │    • P < 0.75 → "ALTO"                            │   │
│  │    • P ≥ 0.75 → "CRITICO"                         │   │
│  │                                                    │   │
│  │ OUTPUT JSON:                                       │   │
│  │ {                                                  │   │
│  │   "success": true,                                 │   │
│  │   "riesgo": "MEDIO",                               │   │
│  │   "probabilidad": 0.4532,                          │   │
│  │   "mensaje": "Análisis completado: MEDIO"          │   │
│  │ }                                                  │   │
│  └────────────────────┬─────────────────────────────┘   │
│                       │                                   │
│  ┌────────────────────────────────────────────────────┐  │
│  │ modelo_siniestros.pkl                              │  │
│  │ • RandomForestClassifier entrenado                │  │
│  │ • 100 árboles, max_depth=10                       │  │
│  │ • Entrenado con datos de clientes + pólizas       │  │
│  └────────────────────────────────────────────────────┘  │
│                       │                                   │
│  ┌────────────────────────────────────────────────────┐  │
│  │ encoders.pkl                                        │  │
│  │ • LabelEncoder para cada columna categórica        │  │
│  │ • document_type, occupation, siniestro, etc.      │  │
│  └────────────────────────────────────────────────────┘  │
│                       │                                   │
│  ┌────────────────────────────────────────────────────┐  │
│  │ features.pkl                                        │  │
│  │ • Lista de features en el orden esperado          │  │
│  │ • edad, document_type_encoded, ...                │  │
│  └────────────────────────────────────────────────────┘  │
│                                                             │
└──────────────────────────┬─────────────────────────────────┘
                           │
                HTTP POST  │ (Response JSON)
                           ▼
┌────────────────────────────────────────────────────────────┐
│           SPRING BOOT (Puerto 8010)                        │
│                                                             │
│  • Recibe respuesta con riesgo = "MEDIO"                  │
│  • Asigna policy.setRiskLevel("MEDIO")                    │
│  • Guarda en BD: INSERT INTO policies (..., risk_level='MEDIO')
│  • Retorna JSON con PolicySimpleDTO                       │
│  • Incluye: { ..., "riskLevel": "MEDIO", ... }           │
│                                                             │
└────────────────────────────┬─────────────────────────────┘
                             │
                  HTTP (JSON Response)
                             ▼
┌────────────────────────────────────────────────────────────┐
│                 NAVEGADOR DEL USUARIO                      │
│                                                             │
│  • JavaScript recibe respuesta exitosa                     │
│  • Actualiza tabla automáticamente                        │
│  • Muestra póliza nueva con badge:                        │
│                                                             │
│    P99 │ AUTO │ Juan   │🟡 MEDIO│500  │26-Nov │  ✏️ 👁️   │
│                           ↑ Color naranja, nivel MEDIO
│                                                             │
│  • Usuario ve el Nivel de Riesgo inmediatamente           │
│                                                             │
└────────────────────────────────────────────────────────────┘
```

---

## 2️⃣ FLUJO SIMPLIFICADO (Paso a Paso)

```
1. Usuario llena formulario de póliza
   └─ Cliente: Juan, Prima: 500, Tipo: AUTO

2. Click en "Guardar"
   └─ JavaScript hace POST a /api/policies

3. Spring Boot recibe la solicitud
   └─ PolicyController.createPolicy()

4. PolicyPresenter procesa la creación
   └─ Obtiene cliente del BD
   └─ Crea objeto Policy (sin riskLevel aún)

5. ⭐ CONSULTA AL MODELO ⭐
   └─ IAModeloService.predecirRiesgo(client, policy)
   └─ Construye datos: {edad: 35, occupation: "...", ...}
   └─ HTTP POST a http://localhost:5000/predecir-riesgo

6. Python API recibe la solicitud
   └─ Carga el modelo entrenado
   └─ Codifica los datos categóricos
   └─ Predicción: RandomForest.predict_proba()
   └─ Resultado: 45% de probabilidad de siniestro
   └─ Conversión: 45% → "MEDIO"
   └─ HTTP Response: {"success": true, "riesgo": "MEDIO", "probabilidad": 0.45}

7. Spring Boot recibe la respuesta
   └─ policy.setRiskLevel("MEDIO")
   └─ PolicyService.save(policy)
   └─ Guarda en BD con risk_level = 'MEDIO'

8. PolicyPresenter retorna DTO
   └─ { id: 99, policyNumber: "P99", ..., riskLevel: "MEDIO" }

9. JavaScript recibe la respuesta JSON
   └─ addRow a la tabla de pólizas
   └─ Muestra badge coloreado: 🟡 MEDIO

10. Usuario ve la póliza con su Nivel de Riesgo
```

---

## 3️⃣ ESTRUCTURA DE CARPETAS

```
ipas/
├── entrenamiento-ia-seguros/     ← MODELO IA (Python)
│   ├── api_modelo.py             ✨ API FLASK
│   ├── entrenar_modelo.py
│   ├── requirements.txt
│   ├── modelo_siniestros.pkl     (Generado por entrenar_modelo.py)
│   ├── encoders.pkl              (Generado por entrenar_modelo.py)
│   └── features.pkl              (Generado por entrenar_modelo.py)
│
├── src/
│   └── main/
│       ├── java/com/ipas/ipas/
│       │   ├── model/
│       │   │   ├── entity/
│       │   │   │   ├── Policy.java         ✏️ Agregado: riskLevel
│       │   │   │   └── Client.java
│       │   │   └── service/
│       │   │       ├── IAModeloService.java ✨ NUEVO - Servicio IA
│       │   │       ├── PolicyService.java
│       │   │       └── ClientService.java
│       │   ├── view/
│       │   │   └── dto/
│       │   │       ├── RiesgoSiniestroRequestDTO.java  ✨ NUEVO
│       │   │       ├── RiesgoSiniestroResponseDTO.java ✨ NUEVO
│       │   │       ├── PolicySimpleDTO.java            ✏️ Agregado: riskLevel
│       │   │       └── PolicyRequest.java
│       │   ├── presenter/
│       │   │   ├── PolicyPresenter.java    ✏️ Llamada a IA
│       │   │   └── ClientPresenter.java
│       │   ├── config/
│       │   │   ├── RestTemplateConfig.java ✨ NUEVO - Config HTTP
│       │   │   └── DataInitializer.java
│       │   └── security/
│       │
│       └── resources/
│           ├── templates/
│           │   ├── policies.html       ✏️ Tabla con "Nivel de Riesgo"
│           │   ├── clients.html
│           │   └── ...
│           ├── static/js/
│           │   ├── policies.js
│           │   ├── clients.js
│           │   └── api.js
│           └── application.properties  ✏️ Agregado: ia.modelo.url
│
├── pom.xml
├── QUICK_START.md                    ✨ GUÍA DE INICIO
├── IA_INTEGRATION_GUIDE.md           ✨ DOCUMENTACIÓN COMPLETA
├── CAMBIOS_REALIZADOS.txt            ✨ ESTE ARCHIVO
└── run_ia_api.bat                    ✨ Script de inicio
```

---

## 4️⃣ BASES DE DATOS

### Tabla: policies (MySQL)

```sql
DESCRIBE policies;

Field                Type              Null  Key  Default
─────────────────────────────────────────────────────────
id                   BIGINT            NO    PRI  auto_increment
policy_number        VARCHAR(255)      NO    UNI  -
policy_type          VARCHAR(255)      NO    -    -
coverage             LONGTEXT          NO    -    -
premium_amount       DECIMAL(12,2)     NO    -    -
coverage_amount      DECIMAL(15,2)     NO    -    -
start_date           DATE              NO    -    -
end_date             DATE              NO    -    -
status               VARCHAR(255)      NO    -    ACTIVE
deductible           DECIMAL(12,2)     YES   -    NULL
valor_siniestro      DECIMAL(15,2)     YES   -    NULL
risk_level           VARCHAR(50)       YES   -    NULL        ← NUEVO
beneficiaries        LONGTEXT          YES   -    NULL
terms_conditions     LONGTEXT          YES   -    NULL
created_at           DATETIME          YES   -    CURRENT_TIMESTAMP
updated_at           DATETIME          YES   -    CURRENT_TIMESTAMP
client_id            BIGINT            NO    FK   -
```

---

## 5️⃣ FLUJO DE DATOS JSON

### Request → Python API

```json
POST http://localhost:5000/predecir-riesgo
Content-Type: application/json

{
  "edad": 35,
  "documentType": "DNI",
  "occupation": "Ingeniero de Sistemas",
  "siniestro": "SI",
  "clienteStatus": "ACTIVE",
  "policyType": "AUTOMOVIL",
  "premiumAmount": 500.00,
  "coverageAmount": 50000.00,
  "deductible": 1000.00,
  "policyStatus": "ACTIVE",
  "duracionDias": 365,
  "valorSiniestro": 0.00
}
```

### Response ← Python API

```json
HTTP 200 OK
Content-Type: application/json

{
  "success": true,
  "riesgo": "MEDIO",
  "probabilidad": 0.4532,
  "mensaje": "Análisis de riesgo completado: MEDIO"
}
```

---

## 6️⃣ TABLA CON COLORES

| Campo | Tipo | Descripción | UI Color |
|-------|------|-------------|----------|
| BAJO | 0-25% | Muy bajo riesgo | 🟢 Verde (#28a745) |
| MEDIO | 25-50% | Riesgo moderado | 🟡 Naranja (#ffc107) |
| ALTO | 50-75% | Riesgo elevado | 🔴 Rojo (#dc3545) |
| CRITICO | 75-100% | Riesgo muy alto | 🔴 Rojo (#dc3545) |
| DESCONOCIDO | Error | No se pudo predecir | ⚪ Gris (#6c757d) |

---

## 7️⃣ ENDPOINTS HTTP

| Método | URL | Descripción | RequestBody | ResponseBody |
|--------|-----|-------------|-------------|--------------|
| POST | /predecir-riesgo | Predice riesgo | RiesgoSiniestroRequestDTO | RiesgoSiniestroResponseDTO |
| GET | /health | Verifica API | - | {status: "ok", modelo_cargado: true} |
| GET | /info-modelo | Info del modelo | - | {modelo, features, encoders, niveles_riesgo} |

---

## 8️⃣ VARIABLES DE ENTORNO (Opcional)

```bash
# Para cambiar la URL de la API IA:
set ia.modelo.url=http://mi-servidor-ia:5000

# En Linux/Mac:
export ia.modelo.url=http://mi-servidor-ia:5000
```

---

## ✅ CHECKLIST DE INTEGRACIÓN

- [x] API Python creada (api_modelo.py)
- [x] DTOs Java creados (Request/Response)
- [x] Servicio IA creado (IAModeloService)
- [x] Config REST creada (RestTemplateConfig)
- [x] Entity Policy actualizado (riskLevel)
- [x] PolicySimpleDTO actualizado (riskLevel)
- [x] PolicyPresenter actualizado (llamada a IA)
- [x] Template policies.html actualizado (mostrar riesgo)
- [x] application.properties actualizado (URL IA)
- [x] Documentación completa
- [x] Guía de inicio rápido
