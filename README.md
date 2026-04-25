# AS241S5_AEJ_01-be

API RESTful reactiva construida con Spring WebFlux y MongoDB Atlas que consume 2 APIs de inteligencia artificial.

## APIs IA integradas

### 1. Shazam API Free (RapidAPI - diyorbekkanal)
- Reconocimiento de canciones a partir de un clip de audio
- Endpoint: `POST /api/shazam/recognize` — sube un archivo de audio y devuelve título, artista y portada
- Endpoint: `GET /api/shazam/history` — historial de canciones reconocidas
- Endpoint: `GET /api/shazam/history/artist/{artist}` — filtrar por artista
- Los resultados se almacenan en la colección `song_recognitions` de MongoDB Atlas

### 2. Validect - Email Verification (RapidAPI - pmtech)
- Verificación de emails en tiempo real (válido, inválido, desechable)
- Endpoint: `GET /api/email/verify?email=` — verifica si un email es real o falso
- Endpoint: `GET /api/email/history` — historial completo de verificaciones
- Endpoint: `GET /api/email/history/{email}` — historial por correo
- Endpoint: `GET /api/email/history/valid` — solo correos válidos
- Endpoint: `GET /api/email/history/invalid` — solo correos inválidos/falsos
- Los resultados se almacenan en la colección `email_verifications` de MongoDB Atlas

## Herramientas y versiones

| Herramienta | Versión |
|---|---|
| Java | 17 |
| Spring Boot | 3.2.3 |
| Spring WebFlux | 6.1.4 |
| Spring Data MongoDB Reactive | 4.2.3 |
| MongoDB Atlas | Cloud |
| Lombok | 1.18.30 |
| SpringDoc OpenAPI (Swagger) | 2.3.0 |
| Maven | 3.x |

## Base de datos

MongoDB Atlas — base de datos cloud `dayroncasas`

Colecciones:
- `song_recognitions` — resultados de reconocimiento de audio con Shazam
- `email_verifications` — resultados de verificación de emails con Validect

## Credenciales

Todas las credenciales están en `src/main/resources/application.yml`:
- URI de conexión a MongoDB Atlas
- API Key de RapidAPI
- Hosts y URLs base de cada API

## Documentación

Swagger UI disponible en: `http://localhost:8081/swagger-ui.html`

## Kubernetes — Despliegue y Validación

### Despliegue inicial

```bash
kubectl apply -f manifest-dayron-casas/dayron-casas-06-namespace.yml
kubectl apply -f manifest-dayron-casas/dayron-casas-06-secret.yml
kubectl apply -f manifest-dayron-casas/dayron-casas-06-service.yml
kubectl apply -f manifest-dayron-casas/dayron-casas-06-deployment.yml
```

### Comprobar que todo está corriendo

```bash
kubectl get all -n dayron-casas
```

### Demostración de dependencia del Secret

**1. Eliminar el secret y el deployment:**
```bash
kubectl delete -f manifest-dayron-casas/dayron-casas-06-secret.yml
kubectl delete -f manifest-dayron-casas/dayron-casas-06-deployment.yml
```

**2. Volver a crear solo el deployment (sin el secret):**
```bash
kubectl apply -f manifest-dayron-casas/dayron-casas-06-deployment.yml
```

**3. Comprobar que el pod falla por falta del secret:**
```bash
kubectl get pods -n dayron-casas
```
El pod quedará en estado `CreateContainerConfigError` porque depende del secret para obtener las credenciales.

### Restaurar todo al estado funcional

```bash
kubectl apply -f manifest-dayron-casas/dayron-casas-06-secret.yml
kubectl rollout restart deployment/dayron-casas-deployment -n dayron-casas
kubectl get pods -n dayron-casas
```

El pod volverá a estado `1/1 Running`.
