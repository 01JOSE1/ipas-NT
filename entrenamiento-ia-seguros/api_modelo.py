"""
API Flask para servir predicciones del modelo IA de análisis de riesgo.
Expone un endpoint POST que recibe datos de una póliza y devuelve nivel riesgo
"""

from flask import Flask, request, jsonify
from flask_cors import CORS
import joblib
import numpy as np
import logging

# Configuración
app = Flask(__name__)
CORS(app)

# Configurar logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Cargar modelo y encoders
try:
    modelo = joblib.load('modelo_siniestros.pkl')
    encoders = joblib.load('encoders.pkl')
    features_list = joblib.load('features.pkl')
    logger.info("✅ Modelo cargado correctamente")
except Exception as e:
    logger.error(f"❌ Error al cargar el modelo: {e}")
    modelo = None


def asignar_nivel_riesgo(probabilidad):
    """
    Asigna un nivel de riesgo basado en la probabilidad predicha
    
    Args:
        probabilidad (float): Probabilidad de siniestro (0-1)
    
    Returns:
        str: Nivel de riesgo (BAJO, MEDIO, ALTO, CRITICO)
    """
    if probabilidad < 0.25:
        return "BAJO"
    elif probabilidad < 0.50:
        return "MEDIO"
    elif probabilidad < 0.75:
        return "ALTO"
    else:
        return "CRITICO"


@app.route('/health', methods=['GET'])
def health():
    """Endpoint para verificar que la API está funcionando"""
    return jsonify({
        'status': 'ok',
        'modelo_cargado': modelo is not None
    }), 200


@app.route('/predecir-riesgo', methods=['POST'])
def predecir_riesgo():
    """
    Endpoint principal para predecir el nivel de riesgo de una póliza
    
    Espera JSON con:
    {
        "edad": int,
        "document_type": str,
        "occupation": str,
        "siniestro": str (SI/NO),
        "cliente_status": str,
        "policy_type": str,
        "premium_amount": float,
        "coverage_amount": float,
        "deductible": float,
        "policy_status": str,
        "duracion_dias": int,
        "valor_siniestro": float
    }
    
    Retorna:
    {
        "success": bool,
        "riesgo": str (BAJO/MEDIO/ALTO/CRITICO),
        "probabilidad": float,
        "mensaje": str
    }
    """
    try:
        if modelo is None:
            return jsonify({
                'success': False,
                'mensaje': 'Modelo no cargado'
            }), 500
        
        # Obtener datos del request
        datos = request.get_json()
        logger.info(f"Datos recibidos: {datos}")
        
        # Validar que tenga todos los datos necesarios
        campos_requeridos = [
            'edad', 'document_type', 'occupation', 'siniestro',
            'cliente_status', 'policy_type', 'premium_amount',
            'coverage_amount', 'deductible', 'duracion_dias'
        ]
        
        for campo in campos_requeridos:
            if campo not in datos:
                return jsonify({
                    'success': False,
                    'mensaje': f'Falta el campo requerido: {campo}'
                }), 400
        
        # Preparar features en el mismo orden que el modelo espera
        features_array = []
        
        # Edad
        features_array.append(float(datos['edad']))
        
        # Encodear variables categóricas
        try:
            # Campos categóricos y sus encoders
            categorical_fields = [
                ('document_type', 'document_type'),
                ('occupation', 'occupation'),
                ('siniestro', 'siniestro'),
                ('cliente_status', 'cliente_status'),
                ('policy_type', 'policy_type'),
            ]
            
            for field, encoder_key in categorical_fields:
                encoded = encoders[encoder_key].transform([datos[field]])[0]
                features_array.append(encoded)
            
            # premium_amount
            features_array.append(float(datos['premium_amount']))
            
            # coverage_amount
            features_array.append(float(datos['coverage_amount']))
            
            # deductible
            features_array.append(float(datos.get('deductible', 0)))
            
            # duracion_dias
            features_array.append(int(datos['duracion_dias']))
            
        except ValueError as e:
            logger.error(f"Error al encodear datos: {e}")
            return jsonify({
                'success': False,
                'mensaje': f'Error al procesar datos categóricos: {str(e)}'
            }), 400
        
        # Convertir a numpy array
        X = np.array(features_array).reshape(1, -1)
        logger.info(f"Features array: {X}")
        
        # Hacer predicción
        # Probabilidad de clase 1 (siniestro)
        probabilidad = modelo.predict_proba(X)[0][1]
        nivel_riesgo = asignar_nivel_riesgo(probabilidad)
        
        msg = f"Predicción: Riesgo={nivel_riesgo}, Prob={probabilidad:.2%}"
        logger.info(msg)
        
        return jsonify({
            'success': True,
            'riesgo': nivel_riesgo,
            'probabilidad': float(probabilidad),
            'mensaje': f'Análisis de riesgo completado: {nivel_riesgo}'
        }), 200
        
    except Exception as e:
        logger.error(f"Error en predicción: {str(e)}", exc_info=True)
        return jsonify({
            'success': False,
            'mensaje': f'Error en el servidor: {str(e)}'
        }), 500


@app.route('/info-modelo', methods=['GET'])
def info_modelo():
    """Retorna información sobre el modelo"""
    return jsonify({
        'modelo': 'Random Forest Classifier',
        'features': features_list,
        'encoders': list(encoders.keys()),
        'niveles_riesgo': ['BAJO', 'MEDIO', 'ALTO', 'CRITICO']
    }), 200


if __name__ == '__main__':
    logger.info("🚀 Iniciando API de Análisis de Riesgo...")
    logger.info("📍 API disponible en: http://localhost:5000")
    app.run(debug=True, host='0.0.0.0', port=5000)
