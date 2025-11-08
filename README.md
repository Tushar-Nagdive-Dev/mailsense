# 🧩 **PROJECT INSTRUCTION DOCUMENT — MailSense (Codename: MailAgent)**

### Version 1.0 | Prepared by: Tushar Nagdive 

---

## 🔷 1. Project Overview

### Project Name

**MailSense** — “The Inbox That Understands.”

*(Alternate brand names: MailLens, LumaMail — all refer to the same concept.)*

### One-Line Description

> MailSense is an AI-powered mail companion that reads your inbox, summarizes messages, extracts important data (OTPs, bills, links, tokens), and displays it in a simple, human-readable interface.

### Core Philosophy

Traditional email systems are built for *communication*.
MailSense is built for *comprehension*.

Where Gmail and Outlook show *everything*, MailSense shows *what matters* — distilled, structured, and ready for action.

---

## 🎯 2. Objectives

| Objective                          | Description                                                         |
| ---------------------------------- | ------------------------------------------------------------------- |
| **Simplify mail consumption**      | Convert cluttered messages into concise, actionable summaries.      |
| **Extract actionable information** | Automatically identify OTPs, bills, tokens, links, and attachments. |
| **Provide calm, modern UX**        | Deliver a macOS-inspired, distraction-free interface.               |
| **Ensure privacy-first AI**        | Process data securely, support local summarization for enterprise.  |
| **Enable exports and search**      | Users can download summaries (PDF, CSV) and find mails by meaning.  |

---

## 🧭 3. Problem Statement

* Inbox overload has become normal; people get 100+ mails daily.
* Most emails (OTP, bills, receipts, links) are *functional*, not conversational.
* Finding key info (like OTP or invoice) takes unnecessary time.
* Existing apps (Gmail, Outlook, Hey) prioritize organization, not understanding.

MailSense addresses this gap — by using **AI to interpret emails semantically**, helping users **see information, not messages**.

---

## 🌟 4. Solution Summary

**MailSense** acts as a smart “lens” for your inbox.

* Connects securely to existing mailboxes (Gmail/Outlook/IMAP)
* Fetches new mails incrementally
* Uses AI & NLP to summarize and extract essential details
* Presents results via a clean UI with filters and download options

It’s not a new email platform.
It’s a **layer of intelligence over your existing inbox**.

---

## 💡 5. Key Features

### 🧠 AI-Powered Summarization

* Extracts the “gist” of any mail in one or two lines.
* Converts formal or verbose mails into plain summaries.
* Example:
  “Your OTP for login is 492821. Valid for 10 minutes.”

### 🧩 Structured Data Extraction

* Identifies and labels:

  * OTP codes
  * Payment amounts and bill dates
  * Download links and reset URLs
  * Tokens / passwords / access keys
  * Attachments and file info

### 🗂️ Categorized Inbox

* Categories: OTPs | Bills | Subscriptions | Links | Tokens | General
* Uses AI classification and rules.
* Auto-detects common patterns and senders.

### 📤 Export Engine

* Export filtered or summarized data into:

  * PDF (Readable summary)
  * CSV (Data analysis)
  * JSON (Integration)
* Example: “Download all bills for this month.”

### 🔍 Intelligent Search

* Search by meaning:
  “Show OTPs from Amazon last week”
* Use full-text and AI semantic indexing.

### 📅 Digest Mode

* Daily AI-generated summary:

  > “You received 14 emails today: 3 bills, 4 OTPs, 2 reminders, and 5 offers.”

### 🧱 Local AI Mode (Enterprise)

* Optional offline summarization (no external API).
* Suitable for privacy-sensitive use cases.

---

## 🧰 6. Technical Stack

| Layer                        | Technology                                     | Description                           |
| ---------------------------- | ---------------------------------------------- | ------------------------------------- |
| **Frontend**                 | Angular 19 + Tailwind + shadcn UI              | macOS-inspired interface              |
| **Backend**                  | Spring Boot 3.x, Java 21                       | API, business logic, AI orchestration |
| **Database**                 | PostgreSQL 15                                  | Mail metadata and extraction storage  |
| **Storage**                  | AWS S3 / MinIO                                 | Attachment & export file storage      |
| **AI Layer**                 | GPT-5 API (or OpenAI-compatible) + NLP regex   | Summarization + structured extraction |
| **Auth**                     | OAuth2 (Gmail/Outlook) + Spring Security + JWT | User and mailbox access               |
| **Message Queue (optional)** | RabbitMQ / Kafka                               | For scalable processing               |
| **Search Layer**             | Elasticsearch (phase 2)                        | For semantic and keyword search       |
| **Infrastructure**           | Docker Compose (dev) / Kubernetes (prod)       | Containerized deployment              |
| **Monitoring**               | Prometheus + Grafana                           | Health & metrics dashboard            |

---

## 🧠 7. Architecture Overview

```
 ┌───────────────────────────────────────────┐
 │               MailSense Cloud             │
 ├───────────────────────────────────────────┤
 │  1. Auth Service (OAuth2 / JWT)           │
 │  2. Mail Fetcher (IMAP / Gmail API)       │
 │  3. Summarization Engine (AI Layer)       │
 │  4. Extraction Engine (Regex + LLM)       │
 │  5. Categorization Service                │
 │  6. Storage Layer (PostgreSQL + S3)       │
 │  7. Exporter Service (PDF / CSV)          │
 │  8. REST API Gateway (Spring Boot)        │
 │  9. Webhook / Queue Worker (Kafka)        │
 ├───────────────────────────────────────────┤
 │           Angular Frontend (UI)           │
 │  - Smart Inbox (cards & filters)          │
 │  - Summary / Detail View                  │
 │  - Export Dashboard                       │
 │  - Daily Digest Page                      │
 └───────────────────────────────────────────┘
```

---

## 🔐 8. Authentication Flow

1. User signs up / logs in with Gmail or Outlook (OAuth2).
2. Access token stored securely (encrypted, read-only scope).
3. Backend periodically fetches new mails.
4. All data stored encrypted at rest.
5. JWT used for frontend API authentication.

---

## 📨 9. Email Processing Flow

1. **Fetch Mail**

   * Use Gmail API / IMAP to fetch unseen messages.
   * Extract headers, body (plain & HTML), and attachments.

2. **Normalize Content**

   * Convert MIME → plain text.
   * Strip HTML noise (using Jsoup or Apache Tika).

3. **Summarize (AI Engine)**

   * Pass normalized text to GPT model for summarization.
   * Cache summary (avoid reprocessing).

4. **Extract Key Data**

   * Regex & LLM hybrid:

     * OTP pattern detection: `\b\d{4,8}\b`
     * URLs, amounts, due dates, tokens
   * Save structured JSON in DB.

5. **Categorize**

   * Simple ML or rule-based classification.

6. **Store + Index**

   * Metadata, summary, extracted data stored in PostgreSQL.
   * Attachments → S3.

7. **Frontend Fetch**

   * API sends summarized JSON objects to Angular frontend.

---

## 🧩 10. Database Schema (Simplified)

### Table: `emails`

| Field          | Type      | Description           |
| -------------- | --------- | --------------------- |
| id             | UUID      | Primary key           |
| message_id     | VARCHAR   | RFC message ID        |
| mailbox        | VARCHAR   | Gmail/Outlook address |
| subject        | TEXT      | Email subject         |
| from_email     | VARCHAR   | Sender address        |
| received_at    | TIMESTAMP | Mail timestamp        |
| body_text      | TEXT      | Plain text            |
| body_html      | TEXT      | Raw HTML              |
| summary        | TEXT      | AI summary            |
| category       | VARCHAR   | OTP/BILL/...          |
| extracted_data | JSONB     | Extracted entities    |
| attachments    | JSONB     | Attachment metadata   |
| status         | ENUM      | RECEIVED/PROCESSED    |
| created_at     | TIMESTAMP | Insert time           |

### Table: `attachments`

| Field        | Type    | Description      |
| ------------ | ------- | ---------------- |
| id           | UUID    | Primary key      |
| email_id     | UUID    | FK to emails     |
| file_name    | VARCHAR | Attachment name  |
| mime_type    | VARCHAR | File type        |
| storage_path | VARCHAR | S3 or local path |
| size         | INT     | File size (KB)   |

---

## 📡 11. REST API Endpoints

### Authentication

| Method | Endpoint              | Description           |
| ------ | --------------------- | --------------------- |
| POST   | `/auth/login`         | JWT login             |
| GET    | `/auth/oauth2/google` | Gmail OAuth2 login    |
| GET    | `/auth/logout`        | Logout & revoke token |

### Mail Management

| Method | Endpoint                 | Description                          |
| ------ | ------------------------ | ------------------------------------ |
| GET    | `/mails`                 | Get list of mails                    |
| GET    | `/mails/{id}`            | Get full details                     |
| POST   | `/mails/fetch`           | Trigger mailbox fetch                |
| GET    | `/mails/category/{type}` | Filter by category (OTP, BILL, etc.) |
| GET    | `/mails/search?q=`       | Semantic search                      |

### Export

| Method | Endpoint                  | Description    |
| ------ | ------------------------- | -------------- |
| GET    | `/export/pdf/{category}`  | Export as PDF  |
| GET    | `/export/csv/{category}`  | Export as CSV  |
| GET    | `/export/json/{category}` | Export as JSON |

---

## 💄 12. Frontend (Angular) Components

| Component                 | Description                                      |
| ------------------------- | ------------------------------------------------ |
| **SmartInboxComponent**   | Categorized view of summarized mails             |
| **MailSummaryCard**       | Compact display: sender, summary, extracted info |
| **MailDetailView**        | Full summary, attachments, export options        |
| **FilterSidebar**         | Filter by category/date/sender                   |
| **SearchBarComponent**    | Full-text + semantic search                      |
| **ExportDialogComponent** | PDF/CSV export actions                           |
| **DigestViewComponent**   | Daily/weekly mail digest summary                 |
| **SettingsComponent**     | Auth, privacy, theme settings                    |

### UI Theme:

* macOS-inspired
* Light and dark mode
* Rounded corners, soft shadows
* Consistent padding and typography
* Accent colors: **#007AFF (blue)** + **#F5F5F7 (neutral)**

---

## 🧩 13. AI Layer (Functional Details)

### Summarization Prompt Template

```
You are an AI assistant summarizing an email for a human.
Summarize clearly and concisely (under 2 lines).
Extract key points like amount, sender, purpose, and actions if applicable.
Return result as JSON:
{
  "summary": "...",
  "intent": "...",
  "key_data": {"otp": "...", "amount": "...", "link": "..."}
}
```

### Extraction Pipeline

1. **Regex Pass** — detects OTP, URLs, dates, tokens.
2. **AI Validation Pass** — confirm context (e.g., OTP validity).
3. **Entity Mapping** — assign labels to extracted entities.
4. **Storage** — save in `emails.extracted_data`.

---

## ⚙️ 14. Implementation Phases

### Phase 1 — Foundation (2 weeks)

* Setup project structure (Spring Boot + Angular + PostgreSQL)
* OAuth2 Gmail integration
* Basic mail fetching & parsing
* Store metadata and text

### Phase 2 — AI Layer (2–3 weeks)

* Integrate GPT summarization API
* Implement extraction rules
* Save summaries and key data in DB

### Phase 3 — Frontend Integration (2 weeks)

* Build Angular Smart Inbox
* Display summaries & extracted data
* Add filters and category views

### Phase 4 — Export & Digest (1 week)

* Implement PDF/CSV export
* Add daily digest view

### Phase 5 — Optimization & Privacy (2 weeks)

* Local AI mode (optional)
* Performance tuning
* Logging, monitoring, testing

---

## 🧱 15. Development Environment

* **Java 21**
* **Node.js 20+**
* **Angular CLI 19**
* **PostgreSQL 15**
* **Docker / Docker Compose**
* **Maven / npm**
* **Spring Boot DevTools**
* **Mailtrap or Gmail Sandbox for testing**

---

## 🧪 16. Testing Strategy

| Type          | Tools           | Scope                   |
| ------------- | --------------- | ----------------------- |
| Unit Testing  | JUnit, Mockito  | Services, parsers       |
| Integration   | Testcontainers  | DB + Mail APIs          |
| E2E           | Cypress         | Angular UI              |
| Load Testing  | JMeter          | Mail ingestion pipeline |
| Security      | OWASP Zap       | OAuth2 & token handling |
| AI Validation | Manual & script | Summary accuracy        |

---

## 📈 17. KPIs (Post-launch Metrics)

| Metric                 | Target        |
| ---------------------- | ------------- |
| Summary accuracy       | ≥ 90%         |
| Extraction precision   | ≥ 95%         |
| Avg processing latency | < 3s per mail |
| DAU retention          | ≥ 60%         |
| Time saved/user        | ≥ 70%         |
| Crash-free rate        | 99.9%         |

---

## 🔐 18. Privacy & Compliance

* GDPR-compliant storage and deletion.
* Optional local-only AI model (enterprise mode).
* AES-256 encryption for stored data.
* Strict OAuth2 scopes (`readonly`).
* Users can delete cached data anytime.

---

## 🌈 19. UX Philosophy

* **Less clutter, more clarity**
  “You shouldn’t have to read — just understand.”
* **Action-first design**
  “What can the user do right now?”
* **Consistency & Calmness**
  Neutral colors, fluid transitions, readable fonts.
* **Accessibility**
  Contrast-friendly, keyboard navigation, responsive layout.

---

## 🧭 20. Long-Term Evolution

1. **Semantic Query Layer:**
   “Show my Amazon bills for October.”
2. **Personal Finance Summary:**
   Aggregate all bill payments and expenses.
3. **Offline Desktop Client:**
   Electron + local LLM integration.
4. **B2B API Offering:**
   `/api/v1/extractMailData` → third-party apps.
5. **Contextual AI Chat:**
   “Summarize this week’s invoices and OTPs.”

---

## 🪙 21. Monetization Plan

| Plan       | Features                         | Price      |
| ---------- | -------------------------------- | ---------- |
| Free       | 1 mailbox, 100 mails/month       | ₹0         |
| Pro        | Unlimited mails, Export, Digest  | ₹299/month |
| Team       | Shared mail intelligence         | ₹799/month |
| Enterprise | On-premise AI + local processing | Custom     |

---

## 🔧 22. Team Roles (Initial Build)

| Role                      | Responsibility                            |
| ------------------------- | ----------------------------------------- |
| **Backend Lead (Tushar)** | Spring Boot microservices, AI integration |
| **Frontend Developer**    | Angular UI implementation                 |
| **DevOps Engineer**       | Docker, deployment, monitoring            |
| **QA Engineer**           | Test automation, coverage                 |
| **UX Designer**           | UI flow, component design                 |
| **AI Engineer**           | Prompt tuning, extraction models          |

---

## 🚀 23. Deliverables Summary

| Milestone | Deliverable                  |
| --------- | ---------------------------- |
| M1        | Project setup + mail fetcher |
| M2        | AI summarizer + extractor    |
| M3        | Angular Smart Inbox UI       |
| M4        | Export + Digest modules      |
| M5        | Testing + Deployment         |
| M6        | Launch (Beta version)        |

---

## 🧭 24. Success Definition

MailSense is successful when:

* Users understand mails faster than they can read them.
* The inbox becomes a source of insight, not anxiety.
* Privacy and clarity coexist harmoniously.
* People describe it as:

  > “Finally, an inbox that thinks for me.”

---

### 🌐 Suggested Domain Ideas

* `mailsense.app`
* `mailsense.ai`
* `maillens.io`
* `lumamail.com`
* `maia.ai`

---

## ✅ 25. Summary

| Category            | Status                             |
| ------------------- | ---------------------------------- |
| **Concept**         | Refined and validated              |
| **Feasibility**     | High (LLM + Spring Boot + Angular) |
| **Market Fit**      | Strong (AI productivity tools)     |
| **Differentiation** | “Semantic Lens for Email”          |
| **Next Step**       | Build MVP (MailSense v0.1)         |

---

### ✨ Final Tagline Ideas

> “See what matters in your inbox.”
> “The inbox that understands you.”
> “From clutter to clarity — instantly.”
> “MailSense — read less, understand more.”
