# Sign Document — Backend

Spring Boot REST API for uploading PDFs and embedding digital signatures at a user-specified position.

**Live demo:** https://sign-document-frontend.onrender.com
**Frontend repo:** https://github.com/charlesprabha/sign-document-frontend

## Tech Stack

- Java 17
- Spring Boot 4.1.1
- Apache PDFBox 3.0.6 (PDF manipulation)
- Maven
- Docker (for deployment)

## Features

- Upload a PDF document
- Accept a signature image (drawn or uploaded) and position coordinates
- Embed the signature onto the specified page at the specified location
- Return the signed PDF for download
- Strips PDF encryption dictionaries automatically before saving, where present

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/documents/upload` | Upload a PDF, returns a `documentId` |
| POST | `/api/documents/{documentId}/sign` | Embed a signature image at given `page`, `x`, `y`, `width`, `height` |
| GET | `/api/documents/{documentId}` | Retrieve the original uploaded PDF |

## Running Locally

```bash
./mvnw spring-boot:run
```
Server starts on `http://localhost:8080`.

## Deployment

Deployed on [Render](https://render.com) as a Dockerized web service. See `Dockerfile` for the build configuration.

## Known Limitations (POC scope)

- Uses local filesystem storage (`uploads/` folder) — files do not persist across redeploys on Render's free tier
- No authentication/authorization
- DOCX upload not yet supported (PDF only)
