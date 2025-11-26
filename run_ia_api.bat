@echo off
REM Script para ejecutar la integracion completa IA + Spring Boot

echo.
echo ========================================
echo  IPAS - Integracion IA de Riesgo
echo ========================================
echo.

REM Verificar si Python esta instalado
python --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Python no esta instalado o no esta en el PATH
    echo Descarga Python desde: https://www.python.org/downloads/
    pause
    exit /b 1
)

REM Verificar si Maven/Java esta instalado
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java no esta instalado o no esta en el PATH
    pause
    exit /b 1
)

echo ============================================
echo Paso 1: Entrando a carpeta de entrenamiento
echo ============================================
cd entrenamiento-ia-seguros

echo.
echo ============================================
echo Paso 2: Verificando dependencias Python
echo ============================================
pip install -r requirements.txt

echo.
echo ============================================
echo Paso 3: Entrenando el modelo
echo ============================================
echo Se ejecutara entrenar_modelo.py...
echo.
python entrenar_modelo.py

if errorlevel 1 (
    echo ERROR en entrenamiento del modelo
    pause
    exit /b 1
)

echo.
echo ============================================
echo Paso 4: Iniciando API Flask
echo ============================================
echo.
echo La API estara disponible en: http://localhost:5000
echo.
echo Presiona CTRL+C para detener la API
echo.
python api_modelo.py

pause
