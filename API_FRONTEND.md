# Guía de conexión Frontend → Backend

## Datos de conexión

| Campo        | Valor                          |
|--------------|-------------------------------|
| Base URL     | `http://localhost:8081`        |
| Swagger UI   | `http://localhost:8081/swagger-ui.html` |
| API Docs     | `http://localhost:8081/v3/api-docs` |

> En producción reemplaza `localhost:8081` por la IP o dominio del servidor.

---

## CORS

El backend acepta peticiones desde cualquier origen (`*`).  
Métodos permitidos: `GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `OPTIONS`.  
Si tu frontend usa cookies o autenticación, asegúrate de enviar `credentials: 'include'` en fetch o `withCredentials: true` en axios.

---

## API 1 — Verificación de Email

Base path: `/api/email`

### Crear — verificar un email
```
POST /api/email/verify
Content-Type: application/json

{ "email": "ejemplo@gmail.com" }
```

### Leer — historial completo
```
GET /api/email
```

### Leer — buscar por ID
```
GET /api/email/{id}
```

### Actualizar — cambiar email y re-verificar
```
PUT /api/email/{id}
Content-Type: application/json

{ "email": "nuevo@gmail.com" }
```

### Eliminar (borrado lógico)
```
DELETE /api/email/{id}
```

### Restaurar
```
PATCH /api/email/{id}/restore
```

### Respuesta de ejemplo
```json
{
  "id": "69f59c2508b9af588ed2aec9",
  "email": "dayron@gmail.com",
  "valid": true,
  "status": "valid",
  "reason": "accepted_email",
  "verifiedAt": "2026-05-02T01:39:33.329",
  "deleted": false,
  "deletedAt": null
}
```

---

## API 2 — Traductor de Texto

Base path: `/api/translator`

### Idiomas disponibles (valor a enviar en `from` y `to`)

| Valor          | Idioma              |
|----------------|---------------------|
| `ESPANOL`      | Español             |
| `INGLES`       | Inglés              |
| `FRANCES`      | Francés             |
| `ALEMAN`       | Alemán              |
| `PORTUGUES`    | Portugués           |
| `ITALIANO`     | Italiano            |
| `JAPONES`      | Japonés             |
| `COREANO`      | Coreano             |
| `CHINO_SIMPLIFICADO` | Chino Simplificado |
| `RUSO`         | Ruso                |
| `ARABE`        | Árabe               |
| `AUTO`         | Detectar automáticamente |

### Crear — traducir un texto
```
POST /api/translator/translate?text=Hola mundo&from=ESPANOL&to=INGLES
```

### Leer — historial completo
```
GET /api/translator
```

### Leer — buscar por ID
```
GET /api/translator/{id}
```

### Actualizar — cambiar texto e idiomas
```
PUT /api/translator/{id}?text=Buenos dias&from=ESPANOL&to=FRANCES
```

### Eliminar (borrado lógico)
```
DELETE /api/translator/{id}
```

### Restaurar
```
PATCH /api/translator/{id}/restore
```

### Respuesta de ejemplo
```json
{
  "id": "abc123",
  "originalText": "Hola mundo",
  "translatedText": "Hello world",
  "fromLanguage": "es",
  "toLanguage": "en",
  "translatedAt": "2026-05-02T10:00:00",
  "deleted": false,
  "deletedAt": null
}
```

---

## Ejemplo de conexión con JavaScript (fetch)

```js
const BASE_URL = 'http://localhost:8081';

// Verificar email
const verificarEmail = async (email) => {
  const res = await fetch(`${BASE_URL}/api/email/verify`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email })
  });
  return res.json();
};

// Traducir texto
const traducir = async (text, from = 'AUTO', to) => {
  const params = new URLSearchParams({ text, from, to });
  const res = await fetch(`${BASE_URL}/api/translator/translate?${params}`, {
    method: 'POST'
  });
  return res.json();
};

// Historial de emails
const historialEmails = async () => {
  const res = await fetch(`${BASE_URL}/api/email`);
  return res.json();
};

// Historial de traducciones
const historialTraducciones = async () => {
  const res = await fetch(`${BASE_URL}/api/translator`);
  return res.json();
};
```

## Ejemplo con Axios

```js
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8081',
  headers: { 'Content-Type': 'application/json' }
});

// Verificar email
api.post('/api/email/verify', { email: 'test@gmail.com' });

// Traducir
api.post('/api/translator/translate', null, {
  params: { text: 'Hola', from: 'ESPANOL', to: 'INGLES' }
});

// Eliminar (borrado lógico)
api.delete(`/api/email/${id}`);

// Restaurar
api.patch(`/api/translator/${id}/restore`);
```
