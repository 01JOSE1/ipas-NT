package com.ipas.ipas.model.service;

import com.ipas.ipas.model.entity.Client;
import com.ipas.ipas.model.entity.Policy;
import com.ipas.ipas.view.dto.RiesgoSiniestroRequestDTO;
import com.ipas.ipas.view.dto.RiesgoSiniestroResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

/**
 * Servicio para consultar el modelo IA de análisis de riesgo de siniestros
 * Se conecta a la API Flask que expone el modelo entrenado
 */
@Service
public class IAModeloService {

    private static final Logger logger = LoggerFactory.getLogger(IAModeloService.class);

    @Value("${ia.modelo.url:http://localhost:5000}")
    private String modeloApiUrl;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Predice el nivel de riesgo para una póliza basándose en los datos del cliente y la póliza
     * 
     * @param client Cliente asociado a la póliza
     * @param policy Póliza para la que se predice el riesgo
     * @return DTO con la predicción de riesgo
     */
    public RiesgoSiniestroResponseDTO predecirRiesgo(Client client, Policy policy) {
        try {
            // Construir request con los datos necesarios
            BigDecimal premiumAmount = policy.getPremiumAmount() != null ? 
                policy.getPremiumAmount() : BigDecimal.ZERO;
            BigDecimal coverageAmount = policy.getCoverageAmount() != null ? 
                policy.getCoverageAmount() : BigDecimal.ZERO;
            BigDecimal deductible = policy.getDeductible() != null ? 
                policy.getDeductible() : BigDecimal.ZERO;
            BigDecimal valorSiniestro = policy.getValorSiniestro() != null ? 
                policy.getValorSiniestro() : BigDecimal.ZERO;
            
            // Normalizar documentType (el modelo solo conoce DNI)
            String documentType = normalizeDocumentType(
                client.getDocumentType() != null ? client.getDocumentType().name() : "DNI"
            );
            
            RiesgoSiniestroRequestDTO requestDTO = new RiesgoSiniestroRequestDTO(
                client.getEdad(),
                documentType,
                client.getOccupation() != null ? client.getOccupation() : "UNKNOWN",
                client.getSiniestro() != null ? client.getSiniestro() : "NO",
                client.getStatus() != null ? client.getStatus().name() : "ACTIVE",
                policy.getPolicyType(),
                premiumAmount,
                coverageAmount,
                deductible,
                policy.getStatus() != null ? policy.getStatus().name() : "ACTIVE",
                Integer.valueOf((int) ChronoUnit.DAYS.between(
                    policy.getStartDate(), policy.getEndDate())),
                valorSiniestro
            );

            logger.info("Enviando datos al modelo IA: " + requestDTO);

            // Configurar headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Crear request HTTP
            HttpEntity<RiesgoSiniestroRequestDTO> request = new HttpEntity<>(requestDTO, headers);

            // Llamar a la API Python
            String url = modeloApiUrl + "/predecir-riesgo";
            logger.info("Llamando a: " + url);

            RiesgoSiniestroResponseDTO response = restTemplate.postForObject(
                url,
                request,
                RiesgoSiniestroResponseDTO.class
            );

            if (response != null && response.getSuccess()) {
                logger.info("Predicción exitosa - Riesgo: " + response.getRiesgo() + 
                           ", Probabilidad: " + response.getProbabilidad());
            } else {
                logger.warn("Error en la predicción: " + 
                           (response != null ? response.getMensaje() : "Sin respuesta"));
            }

            return response;

        } catch (Exception e) {
            logger.error("Error al conectar con el modelo IA: " + e.getMessage());
            e.printStackTrace();
            
            // Retornar respuesta con error
            return new RiesgoSiniestroResponseDTO(
                false,
                "DESCONOCIDO",
                0.0,
                "Error al predecir riesgo: " + e.getMessage()
            );
        }
    }

    /**
     * Normaliza el tipo de documento a valores que el modelo IA entiende.
     * El modelo fue entrenado solo con 'DNI', por lo que se mapean otros tipos a DNI
     * 
     * @param documentType tipo de documento en formato ENUM
     * @return tipo de documento normalizado
     */
    private String normalizeDocumentType(String documentType) {
        // El modelo solo conoce DNI, mapear otros tipos a DNI
        switch (documentType) {
            case "DNI":
                return "DNI";
            case "PASSPORT":
            case "CARNET_EXTRANJERIA":
                // Mapear estos tipos a DNI porque el modelo fue entrenado solo con DNI
                logger.info("Normalizando documentType " + documentType + " a DNI para el modelo IA");
                return "DNI";
            default:
                return "DNI";
        }
    }

    /**
     * Verifica que la API del modelo esté disponible
     * @return true si la API responde correctamente
     */
    public boolean verificarConexion() {
        try {
            String url = modeloApiUrl + "/health";
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> response = restTemplate.getForObject(url, java.util.Map.class);
            return response != null && "ok".equals(response.get("status"));
        } catch (Exception e) {
            logger.warn("No se puede conectar con la API del modelo: " + e.getMessage());
            return false;
        }
    }
}
