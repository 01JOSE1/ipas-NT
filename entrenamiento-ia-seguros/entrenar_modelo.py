try:
    import pandas as pd
    import mysql.connector
    from sklearn.model_selection import train_test_split
    from sklearn.ensemble import RandomForestClassifier
    from sklearn.preprocessing import LabelEncoder
    import joblib
except ModuleNotFoundError as e:
    print(
        f"❌ Falta dependencia: {getattr(e, 'name', str(e))}. "
        "Instala dependencias con:"
    )
    print("    python -m pip install -r requirements.txt")
    import sys
    sys.exit(1)

# ============================================
# 1. CONECTAR A LA BASE DE DATOS
# ============================================


def conectar_bd():
    return mysql.connector.connect(
        host="localhost",
        user="root",
        password="123root",
        database="ipas_entrenamiento"
    )

# ============================================
# 2. EXTRAER DATOS
# ============================================


def extraer_datos():
    conn = conectar_bd()

    # Query para obtener datos de clientes y pólizas
    query = """
    SELECT
        c.edad,
        c.document_type,
        c.occupation,
        c.siniestro,
        c.status as cliente_status,
        p.policy_type,
        p.premium_amount,
        p.coverage_amount,
        p.deductible,
        p.status as policy_status,
        DATEDIFF(p.end_date, p.start_date) as duracion_dias,
        p.valor_siniestro
    FROM clients c
    INNER JOIN policies p ON c.id = p.client_id
    WHERE c.edad IS NOT NULL
    """
    
    df = pd.read_sql(query, conn)
    conn.close()

    print(f"✅ Datos extraídos: {len(df)} registros")
    return df


# ============================================
# 3. PREPARAR DATOS
# ============================================


def preparar_datos(df):
    # Encodear variables categóricas
    le_dict = {}

    columnas_categoricas = [
        'document_type', 'occupation', 'siniestro',
        'cliente_status', 'policy_type', 'policy_status'
    ]
    
    for col in columnas_categoricas:
        le = LabelEncoder()
        df[col + '_encoded'] = le.fit_transform(df[col].astype(str))
        le_dict[col] = le
    
    # Guardar encoders para usar después
    joblib.dump(le_dict, 'encoders.pkl')

    # Seleccionar features
    features = [
        'edad', 'document_type_encoded', 'occupation_encoded',
        'siniestro_encoded', 'cliente_status_encoded',
        'policy_type_encoded', 'premium_amount', 'coverage_amount',
        'deductible', 'duracion_dias'
    ]

    X = df[features].fillna(0)

    # Target: Predecir si habrá siniestro
    y_siniestro = (df['valor_siniestro'] > 0).astype(int)

    print(f"✅ Datos preparados: {X.shape}")
    return X, y_siniestro, features


# ============================================
# 4. ENTRENAR MODELO
# ============================================


def entrenar_modelo(X, y, features):
    # Dividir datos
    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.2, random_state=42
    )
    
    print("🎓 Entrenando modelo...")
    
    # Entrenar Random Forest
    modelo = RandomForestClassifier(
        n_estimators=100,
        max_depth=10,
        random_state=42,
        n_jobs=-1
    )
    
    modelo.fit(X_train, y_train)
    
    # Evaluar
    score_train = modelo.score(X_train, y_train)
    score_test = modelo.score(X_test, y_test)
    
    print(f"✅ Precisión en entrenamiento: {score_train:.2%}")
    print(f"✅ Precisión en prueba: {score_test:.2%}")
    
    # Guardar modelo
    joblib.dump(modelo, 'modelo_siniestros.pkl')
    joblib.dump(features, 'features.pkl')
    
    # Importancia de características
    importancias = pd.DataFrame({
        'feature': features,
        'importancia': modelo.feature_importances_
    }).sort_values('importancia', ascending=False)
    
    print("\n📊 Importancia de características:")
    print(importancias)

    return modelo


# ============================================
# 5. EJECUTAR ENTRENAMIENTO
# ============================================

if __name__ == "__main__":
    print("🚀 Iniciando entrenamiento del modelo...")
    print("=" * 50)
    
    # 1. Extraer datos
    df = extraer_datos()
    
    # 2. Preparar datos
    X, y, features = preparar_datos(df)
    
    # 3. Entrenar modelo
    modelo = entrenar_modelo(X, y, features)
    
    print("\n" + "=" * 50)
    print("✅ ¡ENTRENAMIENTO COMPLETADO!")
    print("Archivos generados:")
    print("  - modelo_siniestros.pkl")
    print("  - encoders.pkl")
    print("  - features.pkl")
