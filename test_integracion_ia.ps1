# Script de prueba integracion IA

$baseUrl = "http://localhost:8010"

Write-Host "====== FASE 1: LOGIN ======" -ForegroundColor Cyan

$loginBody = @{
    email = "asesor@ipas.com"
    password = "asesor123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody -TimeoutSec 10
    $token = $loginResponse.data.token
    
    Write-Host "Login exitoso!" -ForegroundColor Green
    
    $authHeaders = @{
        "Content-Type" = "application/json"
        "Authorization" = "Bearer $token"
    }
} catch {
    Write-Host "Error en login: $_" -ForegroundColor Red
    exit 1
}

Write-Host "`n====== FASE 2: CREAR CLIENTE ======" -ForegroundColor Cyan

$randomNum = Get-Random -Minimum 1000 -Maximum 9999
$clientBody = @{
    firstName = "Cliente"
    lastName = "Test$randomNum"
    email = "cliente$randomNum@example.com"
    documentNumber = "999$randomNum"
    documentType = "DNI"
    phoneNumber = "3001234567"
    address = "Calle 123"
    birthDate = "1990-05-15"
    occupation = "Ingeniero"
    siniestro = "NO"
} | ConvertTo-Json

try {
    $clientResponse = Invoke-RestMethod -Uri "$baseUrl/api/clients" -Method POST -Headers $authHeaders -Body $clientBody -TimeoutSec 10
    $clientId = $clientResponse.data.id
    
    Write-Host "Cliente creado: $clientId" -ForegroundColor Green
} catch {
    Write-Host "Error creando cliente: $_" -ForegroundColor Red
    exit 1
}

Write-Host "`n====== FASE 3: CREAR POLIZA CON IA ======" -ForegroundColor Cyan

$policyBody = @{
    clientId = $clientId
    policyType = "HOGAR"
    coverage = "Incendio y robo"
    premiumAmount = 1500.00
    coverageAmount = 50000.00
    startDate = "2025-12-01"
    endDate = "2026-12-01"
    deductible = 500.00
    beneficiaries = "Familia"
    termsConditions = "Terminos"
    valorSiniestro = 0.00
} | ConvertTo-Json

try {
    $policyResponse = Invoke-RestMethod -Uri "$baseUrl/api/policies" -Method POST -Headers $authHeaders -Body $policyBody -TimeoutSec 10
    
    if ($policyResponse.success) {
        Write-Host "Poliza creada exitosamente!" -ForegroundColor Green
        $policyData = $policyResponse.data
        
        Write-Host "ID: $($policyData.id)"
        Write-Host "Numero: $($policyData.policyNumber)"
        Write-Host "Tipo: $($policyData.policyType)"
        Write-Host "Prima: $($policyData.premiumAmount)"
        Write-Host "NIVEL DE RIESGO: $($policyData.riskLevel)" -ForegroundColor Yellow
    } else {
        Write-Host "Error: $($policyResponse.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "Error creando poliza: $_" -ForegroundColor Red
    exit 1
}

Write-Host "`nPRUEBA COMPLETADA" -ForegroundColor Green
