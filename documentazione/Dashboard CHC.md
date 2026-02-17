# Knowledge Transfer - Dashboard CHC
## Riepilogo Tecnico

**Data KT:** 13 Novembre 2025  
**Durata:** ~2 ore  
**Argomento:** Architettura Dashboard e Comunicazione BE-FE

---

## 1. SCOPO E FUNZIONALITÀ DELLA DASHBOARD

### 1.1 Definizione
La Dashboard CHC è una console web fornita a banche, GPA e utenti Nexi/CBI per il **monitoraggio del transito dei flussi** all'interno del CHC (Clearing House Centrale).

### 1.2 Macro Sezioni
La Dashboard è suddivisa in due aree principali:

#### **A. Monitoraggio Applicativo**
- Strettamente legato al transito e tracking dei flussi
- Focalizzato su **singoli flussi specifici**
- Utilizzato principalmente da **banche e GPA**
- Permette di verificare se un determinato flusso è transitato, in che termini, tempi e con quali attributi
- Limitazione: va mirato sul singolo flusso (approccio puntuale)
- Derivato da applicativi pre-esistenti in Nexi, poi adattato per CBI

#### **B. Monitoraggio Sistemico**
- Offre una **visione ad ampio raggio** di tutto ciò che transita sul CHC
- Comprende:
  - Viste aggregate per flussi e supporti logici
  - Aggregazioni per servizio e tipologia di servizio
  - Sezione dedicata a **monitoraggio e reportistica**
- Utilizzato principalmente da:
  - Operation Nexi
  - CBI
  - Team di supporto
- Fornisce lo **stato di salute del CHC**: flussi in transito, allarmi in corso, volumi
- Utilizzato per check list mattutine e controlli operativi

---

## 2. ARCHITETTURA E COMPONENTI

### 2.1 Core della Dashboard: Gli Eventi

**Gli eventi sono il cuore della Dashboard**. Ogni componente del CHC alimenta la Dashboard lasciando una "scia" di eventi quando gestisce i flussi.

#### Flusso degli Eventi:
1. Un flusso entra nel sistema (da GPA a GPA, o verso/da PA)
2. Ogni componente che lo gestisce **genera eventi**
3. Gli eventi vengono inviati su **code RabbitMQ**
4. Il **Loader** (microservizio batch) li preleva e li carica sul **DB Oracle**
5. La Dashboard utilizza questi eventi per tutte le visualizzazioni

#### Caratteristiche del Loader:
- Microservizio che processa gli eventi
- Lavora il meno possibile sui dati (preferenza per dati già pronti all'uso)
- Obiettivo: massima performance per evitare accodamenti sulle code RabbitMQ
- In alcuni casi effettua elaborazioni necessarie quando i componenti non possono fornire dati completi

### 2.2 Tracciato Eventi

- **Tracciato concordato** in fase di analisi
- Condiviso con tutti i gruppi di lavoro CHC:
  - Corebatch/CCR
  - Client
  - C-Bill
  - IQC (interfacciamento PA)
- Ogni componente ha una **suite di eventi** da produrre per ogni operazione (invio, ricezione, elaborazione)
- Include anche eventi dall'**Orchestrator**

### 2.3 Base Dati
- **Oracle Database** per la memorizzazione degli eventi
- Alimentata da tutti i componenti del CHC
- Base per monitoraggio applicativo, sistemico, reportistica e alert

---

## 3. ARCHITETTURA CHC (Contesto)

### 3.1 Architettura Generale
- CHC = **architettura a centro stella**
- Transita traffico proveniente da:
  - Diversi canali
  - Diversi input
  - Diversi GPA

### 3.2 Flusso Tipico
1. **Frontiera**: Client (per batch e online) o nuovi servizi REST API
2. **CCR** (Client Component Router)
3. **Orchestrator** (elemento centrale)
4. A seconda del workflow:
   - Torna al Corebatch → ritorna al mittente
   - Va "verso destra" → Pubblica Amministrazione, servizi centralizzati, C-Bill

### 3.3 Cambio Architetturale CB → CHC
- Passaggio da architettura **punto-punto** a **architettura centralizzata**
- Impatto su scelte architetturali e tecniche
- Riduzione impatto alla periferia (es. diagnostica spostata all'interno del CHC)
- Ottimizzazioni per alleggerire i GPA

---

## 4. MONITORAGGIO APPLICATIVO - FUNZIONALITÀ

### 4.1 Ricerca Flussi
- Ricerca per range temporali, GPA, ABI, codice servizio
- Dettaglio completo del ciclo di vita del flusso
- Possibilità di vedere eventi specifici del flusso
- Navigazione drill-down per analisi dettagliate

### 4.2 Profilazione Utenti
- Filtri disponibili in base al profilo utente
- Utenti banca: vedono solo i propri dati (GPA/ABI preimpostati)
- Utenti alto livello (Nexi/Support): accesso a tutti i GPA/ABI

### 4.3 Funzione Rispedizione Flussi
- **Funzione dispositiva** riservata al Support e utenti autorizzati
- NON disponibile a CB, banche, GPA
- Utilizzo: quando un GPA ricevente non ha ricevuto flussi
- Meccanismo: invio comando/evento al CCR per rispedire il flusso
- Processo: richiesta → validazione → abilitazione → esecuzione

---

## 5. MONITORAGGIO SISTEMICO - VISTE

### 5.1 Tipologie di Viste

#### **Viste di Monitoraggio**
Forniscono dettaglio real-time/near-real-time di ciò che transita sul CHC:

- **Workflow non chiusi (GPA-GPA)**: flussi in lavorazione da tempo anomalo
- **Flussi in ritardo**
- **Volumi per servizio**
- **Diagnostica flussi**

#### **Viste Statistiche**
Forniscono aggregazioni e trend:

- Range temporali: giornaliero, settimanale, mensile, annuale
- Aggregazioni per: GPA, ABI, service name, tipologia servizio
- Output: tabellare e grafico
- Esempio: supporti logici SEPA, messaggi logici OK/KO

### 5.2 Caratteristiche delle Viste
- Pagina di selezione con filtri (GPA, ABI, range temporale, tipo servizio)
- Descrizione funzionale disponibile (icona "i")
- Esportazione dati
- Grafici (dove previsto) - usati principalmente da CBI per reportistica business

### 5.3 Utilizzo Operativo
- Check list mattutine (Operation Nexi/CBI)
- Monitoraggio volumi e anomalie
- Apertura ticket in caso di scostamenti rispetto al giorno precedente
- Verifica stato di salute CHC

---

## 6. ALERT E ALLARMI

### 6.1 Architettura Alert
Sistema articolato su **3 componenti**:

1. **Dashboard DB**: query sugli eventi, tabella alert
2. **Sixtysense**: elabora e inietta query su Splunk
3. **Splunk**: piattaforma di alerting centrale CHC

### 6.2 Funzionamento
- Alert generati in base a condizioni sul DB eventi
- Aggiornamento sia su Splunk che su tabella Dashboard
- Alert visualizzabili nella pagina dedicata della Dashboard
- **Nota**: Dashboard mostra solo alert della Dashboard, non quelli dei singoli componenti (es. CCR)

### 6.3 Pagina Alert
Visualizza:
- Ora inizio alert
- Descrizione
- GPA impattato
- Stato alert (con icona triangolo per alert non chiudibili)
- Email di notifica (apertura e chiusura)

### 6.4 Tipologie Alert
- Alert che si **aprono e chiudono** automaticamente (es. raggiungibilità componenti)
- Alert che **non si chiudono** automaticamente:
  - Flussi in ritardo (ritardo già avvenuto)
  - Scarti oltre soglia
  - Errori diagnostica Corebatch

### 6.5 Gestione e Destinatari
- Email inviate a Support e CBI
- Alcuni report soggetti a **SLA** (penali per ritardi)
- Mail di alert per report:
  - Report generati da Dashboard → C Lake
  - Report ricevuti via Movie → owner del componente (C-Bill, Corebatch, IQC)

### 6.6 Problematica "Foto dell'Alert"
- Alert fa una "foto" dello stato quando si attiva
- Se War Room viene attivata di notte, controllando le viste dopo potrebbero essere già risolti
- Non è chiaro se era falso allarme o problema risolto
- Possibile sviluppo futuro per migliorare questa gestione

---

## 7. REPORTISTICA

### 7.1 Centralizzazione
La Dashboard è il **centro di tutta la reportistica CHC** (salvo accordi particolari).

### 7.2 Modalità di Generazione Report

#### **A. Report Generati dalla Dashboard**
- Processi **JASPER**
- Query fornite/gestite dal team Dashboard
- Generazione interna

#### **B. Report Ricevuti via Movie**
- Processo in ascolto su coda RabbitMQ
- Ricezione da altri componenti: CB, Corebatch, IQC, C-Bill
- Evento + file report
- Caricamento automatico

### 7.3 Distribuzione Report

Tre modalità:

1. **Download da Dashboard**: utente scarica manualmente
2. **Invio SFTP a CBI**: cartella condivisa per alcuni report
3. **Alert SLA**: mail automatica se report non generato/inviato in tempo

### 7.4 Pagina Reportistica
- Lista di tutti i report generati/ricevuti
- Filtri per data, tipo report
- Funzione download
- Stato invio (se previsto SFTP)

---

## 8. ARCHITETTURA TECNICA E COMUNICAZIONE BE-FE

### 8.1 Architettura a Strati

La Dashboard CHC adotta un'**architettura a microservizi** con separazione netta tra Frontend e Backend.

```
┌─────────────────────────────────────────────────┐
│         FRONTEND (Angular)                      │
│  - Interfaccia utente                           │
│  - Visualizzazioni tabellari e grafiche         │
│  - Navigazione drill-down                       │
└─────────────────┬───────────────────────────────┘
                  │ HTTP/REST
┌─────────────────▼───────────────────────────────┐
│   BACKEND FOR FRONTEND (BFF)                    │
│  - Strato di disaccoppiamento                   │
│  - Orchestrazione chiamate                      │
│  - Aggregazione dati                            │
└─────────┬───────────────┬───────────────────────┘
          │               │
          │ (90%)         │ (10%)
          ▼               ▼
┌─────────────────┐  ┌──────────────────┐
│   CORE API      │  │  EASY LAKE       │
│   (CoreAPI)     │  │  Services        │
│                 │  │                  │
│ - Servizi core  │  │ - Reportistica   │
│ - Business      │  │ - Alert          │
│   logic         │  │ - Funzioni       │
│ - Accesso DB    │  │   specializzate  │
└────────┬────────┘  └──────────────────┘
         │
         ▼
┌──────────────────────────────────────┐
│    DATABASE ORACLE (DB Eventi)       │
└──────────────────────────────────────┘
```

**Fonte**: Trascrizione KT Dashboard, timestamp 00:19:37-00:19:54, 00:33:36-00:34:05

### 8.2 Frontend

**Tecnologia**: **Angular**

> *"Front end tendenzialmente angular"* (trascrizione KT 00:19:35)

**Caratteristiche**:
- Console web moderna
- Interfaccia utente responsive
- Visualizzazioni:
  - Tabellari (griglie dati)
  - Grafiche (chart per statistiche)
- Navigazione drill-down (da lista generale → dettaglio flusso → eventi specifici)
- Gestione stato applicativo lato client

**Fonte**: Trascrizione KT Dashboard, timestamp 00:19:32-00:19:35

### 8.3 Backend For Frontend (BFF)

**Definizione**: 
> *"Il front end angular che poi ha un back end for front end, cioè uno strato che disaccoppia il front end puro da quello che è la logica"* (trascrizione KT 00:19:41-00:19:46)

**Ruolo del BFF**:
- **Disaccoppiamento**: separa il frontend dalla logica di business
- **Orchestrazione**: coordina le chiamate ai servizi backend
- **Aggregazione**: combina dati da più sorgenti (Core API + Easy Lake)
- **Trasformazione**: adatta i dati del backend al formato richiesto dal frontend
- **Sicurezza**: gestisce autenticazione e autorizzazione (integrazione con IAM)

**Pattern architetturale**: Backend For Frontend è un pattern consolidato che permette di avere un backend dedicato e ottimizzato per le esigenze specifiche del frontend web.

**Fonte**: Trascrizione KT Dashboard, timestamp 00:19:37-00:19:54

### 8.4 Core API (CoreAPI)

**Definizione**:
> *"Il Corato è proprio un servizio no? Che dove bene o male risiede la maggior parte [...] risiedono nel corapi"* (trascrizione KT 00:18:43-00:19:06)

**Responsabilità**:
- **Servizi core della Dashboard**: 90% delle chiamate dal BFF
- **Business logic principale**: elaborazione dati, query complesse
- **Accesso diretto al DB Eventi**: interrogazione tabelle Oracle
- **Funzionalità complesse**: aggregazioni, calcoli, filtraggio avanzato

**Composizione**:
> *"La dashboard per l'80-90% si basa sui servizi del corapi, quindi servizi interni"* (trascrizione KT 00:33:45-00:33:53)

**Esempi di servizi**:
- Servizi di ricerca flussi
- Servizi di generazione viste
- Servizi di interrogazione eventi
- Servizi di gestione profili utente

**Fonte**: Trascrizione KT Dashboard, timestamp 00:18:35-00:19:06, 00:33:45-00:33:58

### 8.5 Easy Lake

**Definizione**:
> *"Easy Lake è un'applicazione che io conosco solo come un box [...] un'applicazione di proprietà di Nexi"* (trascrizione KT 00:32:38-00:32:48)

**Ruolo**:
- **Applicazione esterna** alla Dashboard core
- Fornisce **funzionalità specializzate**: ~10% delle chiamate dal BFF
- Sviluppata e gestita da team Nexi dedicato

**Servizi forniti**:
- **Reportistica avanzata**: generazione report complessi
- **Alert system**: gestione alert e integrazione Splunk
- Altre funzionalità specifiche (da approfondire con team Easy Lake)

**Integrazione con Dashboard**:
> *"In qualche caso si va a richiamare dei servizi di Easy Lake e poi vedete ha anche un accesso sul database degli eventi"* (trascrizione KT 00:33:58-00:34:01)

**Nota**: Easy Lake è un box black per il team Dashboard, gestito autonomamente da altro team Nexi.

**Fonte**: Trascrizione KT Dashboard, timestamp 00:32:34-00:34:05

### 8.6 Altri Componenti Backend

#### Loader (Microservizio)
- Preleva eventi da **RabbitMQ**
- Carica eventi su **DB Oracle**
- Elaborazione minimale per performance

#### JASPER (Reportistica)
- Generazione report PDF/Excel
- Esecuzione query SQL su DB Eventi
- Schedulazione report periodici

#### Movie (File Transfer)
- Ricezione report da componenti esterni (Corebatch, IQC, C-Bill)
- Trasferimento via code RabbitMQ
- Caricamento su Dashboard

**Fonte**: Sezioni precedenti del documento, timestamp 00:06:28-00:07:24

### 8.7 Stack Tecnologico

**Backend**:
- **Spring Boot**: framework principale per microservizi
- **Java**: linguaggio di sviluppo
- **JASPER Reports**: generazione report PDF/Excel
- **Oracle JDBC**: connettività database

**Frontend**:
- **Angular**: framework JavaScript per SPA (Single Page Application)

**Infrastruttura**:
- **RabbitMQ**: messaging asincrono (AMQP)
- **Oracle Database**: persistenza dati
- **Splunk**: monitoring e alerting

> *"La tecnologia utilizzata è quella che si basa su springboot e angular sostanzialmente"* (trascrizione KT 00:31:28-00:31:34)

**Fonte**: Trascrizione KT Dashboard, timestamp 00:31:28-00:31:38

### 8.8 Integrazioni Esterne

| Sistema | Protocollo | Scopo |
|---------|-----------|-------|
| **RabbitMQ** | AMQP | Ricezione eventi asincroni da componenti CHC |
| **Splunk** | API REST | Invio alert via Sixtysense |
| **IAM** | API REST | Autenticazione e autorizzazione utenti |
| **SFTP** | SFTP | Invio report a CBI |
| **DB Oracle** | JDBC | Persistenza eventi e dati Dashboard |

**Fonte**: Trascrizione KT Dashboard, sezioni 00:06:28-00:07:24, 00:20:29-00:20:39

### 8.9 Sicurezza e Profilazione
- **Autenticazione**: integrazione con **IAM** (Identity Access Management)
- **Autorizzazione**: profilazione utenti con visibilità limitata ai propri dati
- **Controllo accessi**: filtri automatici su GPA/ABI in base al profilo
- **Funzioni dispositive**: riservate a profili autorizzati (Support, Admin)

---

## 9. REPOSITORY CENTRALE E GESTIONE FLUSSI

### 9.1 Definizione Repository Centrale

> *"Repository centrale che di fatto non è altro che un object storage"* (trascrizione KT 00:47:52-00:47:57)

**Cos'è**:
- **Object Storage** centralizzato per CHC
- Storicizza **tutto il payload** dei flussi che transitano su CHC
- Separato dal DB Eventi della Dashboard

**Differenza con DB Eventi Dashboard**:
> *"La dashboard ha un set dei dati scarno fondamentalmente, cioè non ha il payload di ciò che è transitato effettivamente su CHC"* (trascrizione KT 00:47:22-00:47:47)

| Aspetto | DB Eventi Dashboard | Repository Centrale |
|---------|-------------------|-------------------|
| **Contenuto** | Metadati, eventi, tracking | Payload completo flussi (file XML, flat, ecc.) |
| **Dimensione dati** | Leggera (solo informazioni eventi) | Pesante (contenuto effettivo flussi) |
| **Scopo** | Monitoraggio, statistiche, alert | Storicizzazione, audit, recupero flussi |
| **Tecnologia** | Oracle Database | Object Storage |
| **Accesso** | Dashboard, CoreAPI, Easy Lake | Dashboard (download flussi), componenti CHC |

**Fonte**: Trascrizione KT Dashboard, timestamp 00:47:06-00:48:03

### 9.2 Funzionalità Dashboard con Repository Centrale

#### Download Flussi
La Dashboard permette di **scaricare i flussi completi** dal Repository Centrale:
- Recupero payload flusso originale
- Visualizzazione contenuto (se non cifrato)
- Export per analisi esterna

#### Decifratura e Verifica Firme Digitali

> *"Deve essere fatta una chiamata a PK box [...] decripta il flusso lo mostra in chiaro e così come mostra quali sono i firmatari che hanno firmato digitalmente il flusso"* (trascrizione KT 00:50:44-00:50:56)

**Processo**:
1. Utente richiede visualizzazione flusso dalla Dashboard
2. Dashboard recupera flusso dal Repository Centrale
3. Se flusso è **cifrato** → chiamata a **PKBox** per decifratura
4. Se flusso è **firmato digitalmente** → chiamata a **PKBox** per verifica firme
5. Dashboard mostra:
   - Contenuto flusso in chiaro
   - Lista dei firmatari (certificati)
   - Validità firme digitali

**PKBox**: Sistema di gestione certificati e crittografia CHC

**Casi d'uso**:
- Flussi cifrati (protezione dati sensibili)
- Flussi con firma elettronica (da banche verso PA)
- Fatture elettroniche firmate digitalmente

> *"Ci potrebbe essere anche un flusso che parte dal BK con la firma elettronica che è firmato digitalmente, per esempio che transita su CHC ed è ovviamente necessario per vederlo in chiaro che la Dashboard faccia una chiamata a PK box"* (trascrizione KT 00:51:15-00:51:30)

**Fonte**: Trascrizione KT Dashboard, timestamp 00:50:34-00:51:30

### 9.3 Integrazione Architetturale

```
┌─────────────────────────────────────────┐
│         DASHBOARD UI                    │
└──────────────┬──────────────────────────┘
               │
               ▼
┌──────────────────────────────────────────┐
│       Backend For Frontend (BFF)         │
└───────┬──────────────┬───────────────────┘
        │              │
        │              ▼
        │      ┌──────────────────┐
        │      │  REPOSITORY      │
        │      │  CENTRALE        │
        │      │  (Object Storage)│
        │      └──────────────────┘
        │              │
        │              │ (se cifrato/firmato)
        │              ▼
        │      ┌──────────────────┐
        │      │     PKBox        │
        │      │  (Certificati &  │
        │      │   Crittografia)  │
        │      └──────────────────┘
        ▼
┌──────────────────┐
│   DB Eventi      │
│  (Dashboard)     │
└──────────────────┘
```

**Fonte**: Analisi architetturale trascrizione KT Dashboard, timestamp 00:47:00-00:52:00

---

## 10. AMBIENTI E PROCESSO DI SVILUPPO

### 10.1 Architettura Ambienti

La Dashboard CHC utilizza una **pipeline di sviluppo multi-ambiente**:

```
[SVILUPPO LOCALE]
       ↓
[TAS - Test/Sviluppo]
       ↓ (dopo unit test)
[INT - Integrazione]
       ↓ (dopo test integrazione)
[PRODUZIONE]
```

**Fonte**: Trascrizione KT Dashboard, timestamp 00:57:27-00:58:31

### 10.2 Ambiente TAS (Test/Sviluppo)

**Definizione**:
> *"La configurazione di sviluppo noi abbiamo usato un'architettura di sviluppo che è propria dell'ambiente TAS"* (trascrizione KT 00:57:27-00:57:36)

**Caratteristiche**:
- **Ambiente interno Nexi** per sviluppo e test
- Utilizzato per:
  - Sviluppo nuove funzionalità
  - Unit test
  - Debug e troubleshooting
- Configurazione specifica per l'architettura interna
- Non direttamente replicabile in locale (richiede infrastruttura Nexi)

**Processo**:
> *"Noi c'eravamo accordati per sviluppare in ambiente TAS. E dall'ambiente TAS, ovviamente, una volta completato e fatto le unit test rilasciamo un ambiente di INT"* (trascrizione KT 00:58:17-00:58:27)

**Fonte**: Trascrizione KT Dashboard, timestamp 00:57:27-00:58:19

### 10.3 Ambiente INT (Integrazione)

**Definizione**:
- Ambiente di **integrazione** per test end-to-end
- Test con componenti reali CHC (CCR, Orchestrator, Corebatch, ecc.)
- Validazione flussi completi

**Utilizzo**:
> *"Quando passiamo in ambiente INT entriamo nei processi"* (trascrizione KT 00:57:48-00:57:52)

**Scopo**:
- Test di integrazione tra Dashboard e componenti CHC
- Verifica eventi ricevuti dai componenti
- Test su dati realistici (non produzione)
- Validazione prima del rilascio in produzione

**Fonte**: Trascrizione KT Dashboard, timestamp 00:57:48-00:58:31

### 10.4 Ambiente di Produzione

**Caratteristiche**:
- Ambiente **live** utilizzato da utenti finali (banche, GPA, CBI, Nexi)
- Dati reali di flussi CBI/SEPA/PA
- Monitorato 24/7
- Rilasci controllati e schedulati

**Processo di rilascio**:
1. Sviluppo e unit test in **TAS**
2. Test di integrazione in **INT**
3. Validazione e approvazione
4. **Rilascio in produzione** (deployment)

**Fonte**: Trascrizione KT Dashboard, timestamp 00:57:27-00:58:31, riferimenti impliciti

### 10.5 Sviluppo Locale

**Limitazioni**:
> *"Dobbiamo aiutarci a replicarlo anche in casa nostra, ma quella ha tutta una serie di elementi, sono tipici dell'architettura interna TAS"* (trascrizione KT 00:57:41-00:57:48)

**Configurazione**:
- Possibile **setup locale limitato** per sviluppo
- Dipendenze da infrastruttura Nexi (RabbitMQ, Oracle, ecc.)
- Configurazione specifica per ambiente locale
- Non include tutti i componenti CHC reali

**Nota**: Per sviluppo completo è necessario accesso a **ambiente TAS** interno Nexi.

**Fonte**: Trascrizione KT Dashboard, timestamp 00:57:27-00:57:48

### 10.6 Pipeline CI/CD (Implicita)

Anche se non esplicitamente menzionato in dettaglio, dalla trascrizione emerge un **processo strutturato**:

1. **Sviluppo**: codice su TAS
2. **Test unitari**: su TAS
3. **Build**: generazione artifact
4. **Rilascio INT**: deployment automatico/manuale
5. **Test integrazione**: su INT
6. **Rilascio produzione**: dopo validazione

**Strumenti menzionati**:
- Repository centrale (probabilmente Git/Bitbucket)
- Sistema di build (Maven/Gradle - tipico per Spring Boot)
- Deployment su ambienti Nexi

**Fonte**: Inferenza da trascrizione KT Dashboard, timestamp 00:57:00-00:59:00

---

## 11. NOTE E CRITICITÀ

### 11.1 Limitazioni Conosciute
- Monitoraggio applicativo limitato a singoli flussi (approccio puntuale)
- Grafici delle viste statistiche non sempre allineati alla tabella (grafici custom)
- Alert non chiudibili automaticamente per alcuni scenari
- Problema "foto alert" per War Room notturne

### 11.2 Diatriba con Splunk
- Discussione aperta sulla gestione apertura/chiusura alert
- Non tutti gli alert possono chiudersi automaticamente

### 11.3 Evoluzione Architetturale
- Derivazione da applicativi Nexi pre-esistenti
- Adattamento e ampliamento per esigenze CBI/CHC
- Possibili sviluppi futuri per migliorare gestione alert

---

## 12. GLOSSARIO TECNICO

### 12.1 Flussi

**Definizione**: Un flusso è un'unità di scambio dati tra due attori del sistema CHC.

**Caratteristiche**:
- **Contenitore di alto livello** che transita attraverso il CHC
- Può andare da GPA a GPA, oppure da GPA verso/da PA (Pubblica Amministrazione)
- Identificato da un **ID di tratta** univoco nel sistema

**Attributi principali**:
- Mittente (GPA/Banca/PA)
- Destinatario (GPA/Banca/PA)  
- Direzione (inbound/outbound)
- Tipo di servizio (es. SEPA, bonifici, incassi, fatture elettroniche)
- Timestamp (ricezione, carico, scarico, spedizione, ricevuta)

**Fonte**: Trascrizione KT Dashboard, timestamp 01:33:24-01:33:38; DDL tabelle IQCI01_TB_FLUSSO_IDX, IQCD01_TB_FLUSSO_IDX

### 12.2 Eventi

**Definizione**: Gli eventi sono il **core della Dashboard**.

> *"Core della dashboard sono gli eventi, ovvero la base dati della dashboard"* (trascrizione KT 00:04:09)

**Cosa sono**:
- **Messaggi informativi** generati dai componenti CHC durante l'elaborazione dei flussi
- Ogni componente (CCR, Orchestrator, Corebatch, IQC, C-Bill, Client) ha una **suite di eventi** da produrre
- Rappresentano la **"scia"** lasciata dai componenti durante il transito dei flussi

> *"Ognuno ha un set, una suite di eventi che deve produrre ogni volta che fa qualcosa"* (trascrizione KT 00:05:54)

> *"Comunque ognuno compreso l'orchestrator comunque manda e lascia una scia come se fosse una lumacona gigante [...] e quindi alimenta la dashboard"* (trascrizione KT 00:06:10)

**Caratteristiche tecniche**:
- **Tracciato concordato**: ogni evento ha un tracciato standard definito in fase di analisi
- **Trasporto**: inviati su **code RabbitMQ**
- **Caricamento**: prelevati dal microservizio **Loader** e inseriti nel **DB Oracle degli eventi**
- **Utilizzo**: base informativa per tutte le funzionalità Dashboard (monitoraggio, viste, alert, report)

**Tipologie di eventi** (esempi):
- Eventi di ricezione flusso
- Eventi di invio flusso
- Eventi di elaborazione
- **Diagnosis Enfanted**: evento cardine del Corebatch che contiene tutti i dati del supporto logico
- Eventi di errore/scarto
- Eventi di cambio stato

**Fonte**: Trascrizione KT Dashboard, timestamp 00:04:09-00:06:25, 00:05:54-00:06:10

### 12.3 Supporti Logici

**Definizione**: *"Una sotto-entità dell'intera richiesta di servizio"* (cit. trascrizione KT 01:31:34)

**Caratteristiche**:
- **Un flusso CONTIENE uno o più supporti logici**
- Ogni supporto logico rappresenta un **singolo elemento/transazione** all'interno del flusso
- È il livello di dettaglio con cui vengono gestite le singole operazioni business
- È un'**entità logica/business** (dati della transazione)

**Composizione**:
> *"All'interno del flusso ci sono una serie di supporti logici"* (trascrizione KT 01:33:38)

**Relazione con gli Eventi**:
Gli eventi **descrivono** cosa succede ai supporti logici durante il loro ciclo di vita:

> *"L'evento cardine che mostra tutti i dati presenti all'interno del supporto logico è quello che si chiama Diagnosis Enfanted"* (trascrizione KT 01:34:04-01:34:13)

**In sintesi**:
- **Supporto Logico** = DATO (la transazione bancaria vera e propria)
- **Evento** = INFORMAZIONE su cosa sta succedendo a quel dato (ricevuto, elaborato, inviato, errore, ecc.)

**Attributi chiave**:
- Nome supporto (NOME_SUPPORTO nelle tabelle DB)
- Mittente
- Ricevente
- Data creazione (DTCREAZIONE)
- Tipo flusso
- Importo

**Chiave CBI**: La chiave che identifica univocamente un supporto logico è composta da:
- Mittente + Ricevente + Nome Supporto + Data Creazione + Tipo Flusso

**Formato tecnico**:
> *"All'interno di un flusso XML c'è un tag che incapsula i supporti logici flat"* (trascrizione KT 01:46:44)

I supporti logici in formato flat (legacy) vengono incapsulati in tracciati XML moderni.

**Fonte**: Trascrizione KT Dashboard, timestamp 01:31:23-01:31:37, 01:33:24-01:33:38, 01:34:04-01:34:13, 01:46:38-01:46:49, 01:48:33; DDL colonna NOME_SUPPORTO in tabelle IQCI01, IQCD01, IQCI09

### 12.4 Messaggi Logici

**Definizione**: Le singole disposizioni/transazioni effettive contenute all'interno dei supporti logici.

**Classificazione**:
- **Messaggi logici OK**: transazioni elaborate con successo
- **Messaggi logici KO**: transazioni con errore/scarto

**Esempio dalla Dashboard**:
> *"ha inviato questo numero 1 di supporto SEPA OK con dentro un messaggio logico i messaggi logici OK sono uno e i messaggi logici KO sono zero"* (trascrizione KT 01:56:43-01:56:52)

**Evento cardine**: L'evento principale che mostra tutti i dati del supporto logico è chiamato **"Diagnosis Enfanted"**, generato dal Corebatch dopo l'elaborazione.

**Fonte**: Trascrizione KT Dashboard, timestamp 01:34:04-01:34:16, 01:56:43-01:56:52

### 12.5 Differenza Chiave: Supporto Logico vs Evento

| Aspetto | Supporto Logico | Evento |
|---------|----------------|--------|
| **Natura** | Entità business (dato) | Informazione tecnica (metadato) |
| **Cosa rappresenta** | La transazione/operazione bancaria | Lo stato/azione su quella transazione |
| **Esempio concreto** | Bonifico SEPA di 100€ da Banca A a Banca B | "Bonifico ricevuto", "Bonifico elaborato", "Bonifico inviato" |
| **Persistenza** | Memorizzato in tabelle flussi (IQCI01, IQCD01) | Memorizzato in DB Eventi (Dashboard) |
| **Quantità** | 1 supporto logico = 1 transazione | 1 supporto logico → N eventi (ricevuto, in carico, elaborato, diagnosticato, inviato, consegnato) |
| **Chi lo crea** | Sistema mittente (banca, PA) | Componenti CHC durante elaborazione |
| **Visibilità** | Utenti business (banche, operation) | Sistema di monitoraggio (Dashboard, Support) |

**Metafora semplificata**:
- **Supporto Logico** = Un pacco spedito (il contenuto fisico)
- **Evento** = Il tracking del pacco ("In transito", "Al centro smistamento", "In consegna", "Consegnato")

**Fonte**: Analisi comparativa trascrizione KT Dashboard, sezioni 00:04:00-00:07:00 (eventi) e 01:31:00-01:35:00 (supporti logici)

### 12.6 Gerarchia Completa: Flusso → Supporto Logico → Messaggio Logico + Eventi

```
FLUSSO (es. "Flusso SEPA F4" - identificato da ID tratta)
  │
  ├── Supporto Logico 1 (nome: "SL001", data: "20260116")
  │    │
  │    ├── Messaggio Logico 1 [OK] - Bonifico 100€
  │    ├── Messaggio Logico 2 [OK] - Bonifico 250€
  │    └── Messaggio Logico 3 [KO] - Bonifico 50€ (errore)
  │    │
  │    └── EVENTI generati per SL001:
  │         ├── Evento "Flusso Ricevuto" (timestamp T1, mittente: CCR)
  │         ├── Evento "Preso in Carico" (timestamp T2, mittente: Orchestrator)
  │         ├── Evento "In Elaborazione" (timestamp T3, mittente: Corebatch)
  │         ├── Evento "Diagnosis Enfanted" (timestamp T4, mittente: Corebatch) ← CARDINE
  │         ├── Evento "Elaborato OK" (timestamp T5, mittente: Corebatch)
  │         └── Evento "Inviato" (timestamp T6, mittente: CCR)
  │
  ├── Supporto Logico 2 (nome: "SL002", data: "20260116")
  │    │
  │    ├── Messaggio Logico 4 [OK] - Bonifico 300€
  │    └── Messaggio Logico 5 [OK] - Bonifico 150€
  │    │
  │    └── EVENTI generati per SL002:
  │         ├── Evento "Flusso Ricevuto" (timestamp T1)
  │         ├── ... (altri eventi simili a SL001)
  │         └── Evento "Consegnato" (timestamp T7)
  │
  └── Supporto Logico 3 (nome: "SL003", data: "20260116")
       └── Messaggio Logico 6 [OK] - Bonifico 75€
       └── EVENTI generati per SL003: ...
```

**Come vengono usati dalla Dashboard**:

1. **DB Eventi** memorizza tutti gli eventi generati dai componenti CHC
2. **Monitoraggio Applicativo** ricostruisce il ciclo di vita del singolo flusso/supporto logico dagli eventi
3. **Viste Sistemiche** aggregano eventi per creare statistiche (volumi, tempi medi, KO, ecc.)
4. **Alert** interrogano il DB eventi per rilevare anomalie (es. supporti logici senza evento "Diagnosis Enfanted" dopo X minuti)
5. **Report** estraggono dati dagli eventi per generare statistiche e rendicontazioni

**Visualizzazione nella Dashboard**:
- **Monitoraggio Applicativo - Ricerca Flussi**: mostra il singolo flusso con tutti i suoi eventi
- **Viste Sistemiche - Supporti Logici**: elenca i supporti logici aggregati per criteri
- **Dettaglio Supporto Logico**: mostra gli attributi del supporto (mittente, ricevente, importo, messaggi OK/KO)

**Fonte**: Trascrizione KT Dashboard, analisi complessiva sezioni 01:30:00-01:35:00 e 01:46:00-01:48:00

---

## 13. COMPONENTI CHC MENZIONATI

- **Client**: frontiera batch e online
- **CCR** (Client Component Router): routing e smistamento
- **Orchestrator**: elemento centrale di coordinamento
- **Corebatch**: elaborazioni batch, genera eventi "Diagnosis Enfanted" per i supporti logici
- **C-Bill**: gestione servizi bollettini
- **IQC**: interfacciamento con Pubblica Amministrazione
- **DAM**: gestione informazioni (servizi centralizzati)
- **Dashboard**: monitoraggio e reportistica (oggetto di questo KT)

---

## 13. RIFERIMENTI

### 13.1 Relatori e Team
- **Relatori KT**: Paolo (aspetti funzionali), Stefano (architettura e aspetti tecnici)
- **Team coinvolto**: Stefania e altri colleghi menzionati
- **Contesto**: Handover da team sviluppo a team Capgemini

### 13.2 Fonti Documentali
- **Trascrizione KT**: CHC Dashboard - Archittettura Dashboard e comunicazione BE-FE (13/11/2025)
  - File: `CHC Dashboard - Archittettura Dashboard e comunicazione BE-FE-20251113_145141-Meeting Recording-it-IT.vtt`
- **Manuale utente**: CHC - Manuale Dashboard V3.0.pdf (disponibile per download dalla Dashboard)
- **DDL Database**: Tabelle Oracle IQC
  - `IQCI01_TB_FLUSSO_IDX` - Flussi in ingresso
  - `IQCD01_TB_FLUSSO_IDX` - Flussi in uscita  
  - `IQCI09_TS_FLUSSO_IDX` - Flussi con timestamp
  - Colonne chiave: `NOME_SUPPORTO`, `DIR_FLUSSO`, `FLUSSO_ORIG`, `FLUSSO_SPED`

### 13.3 Riferimenti Temporali nelle Fonti
Le informazioni sui concetti chiave sono estratte dai seguenti timestamp della trascrizione:

**Eventi**:
- **00:04:09-00:04:15**: Definizione "Core della dashboard sono gli eventi"
- **00:05:54-00:06:10**: Suite di eventi, metafora della "scia"
- **00:06:04-00:06:25**: Ogni componente alimenta la Dashboard con eventi

**Architettura BE-FE**:
- **00:18:35-00:19:06**: CoreAPI come servizio principale con business logic
- **00:19:32-00:19:35**: Frontend Angular
- **00:19:37-00:19:54**: Backend For Frontend (BFF) come strato di disaccoppiamento
- **00:20:29-00:20:39**: Integrazione con IAM per autenticazione
- **00:31:28-00:31:34**: Stack tecnologico Spring Boot e Angular
- **00:32:34-00:32:48**: Easy Lake come applicazione esterna
- **00:33:36-00:34:05**: Distribuzione chiamate (90% CoreAPI, 10% Easy Lake)

**Repository Centrale e PKBox**:
- **01:09:41-01:09:56**: Repository Centrale definito come object storage
- **01:10:30-01:10:44**: Archiviazione firmati su Repository Centrale
- **01:11:04-01:11:19**: PKBox per cifratura e certificati
- **01:11:22-01:11:35**: Decifratura contenuti file cifrati tramite PKBox

**Ambienti e Sviluppo**:
- **00:57:27-00:57:36**: Ambiente TAS per sviluppo e configurazione
- **00:57:41-00:57:48**: Limitazioni setup locale, necessità infrastruttura TAS
- **00:57:48-00:57:52**: Passaggio in ambiente INT per processi integrazione
- **00:58:17-00:58:27**: Pipeline: sviluppo TAS → unit test → rilascio INT

**Flussi, Supporti Logici e Messaggi Logici**:
- **01:31:23-01:31:37**: Definizione supporto logico come sotto-entità
- **01:33:24-01:33:38**: Relazione flusso → supporti logici
- **01:34:04-01:34:16**: Evento "Diagnosis Enfanted" e dati supporto logico
- **01:46:38-01:46:49**: Incapsulamento supporti logici flat in XML
- **01:48:33-01:48:45**: Chiave CBI e attributi supporto logico
- **01:56:43-01:56:52**: Esempio messaggi logici OK/KO in supporto SEPA

**Formati Flussi e Ciclo di Vita**:
- **01:46:44-01:47:21**: Formato XML e supporti flat
- **01:46:57-01:47:10**: Chiave CBI per identificazione flusso
- **00:11:26-00:13:00**: Workflow inbound/outbound
- **00:32:00-00:35:00**: Elaborazione e routing flussi

---

## 14. FORMATI FLUSSI IN INGRESSO E CICLO DI VITA

### 14.1 Formati dei Flussi in Arrivo

#### 14.1.1 Formato XML (Attuale)
**Fonte**: Timestamp 01:46:44-01:47:21

Il formato principale utilizzato per i flussi in ingresso al CHC è **XML**.

Citazione dalla trascrizione:
> *"All'interno di un flusso XML c'è un tag che incapsula i supporti logici flat"*

**Caratteristiche**:
- Formato moderno/standard attuale
- Struttura gerarchica con tag XML
- Contiene **embedded** i supportti logici in formato flat
- Include metadati per identificazione e routing

#### 14.1.2 Formato Flat (Legacy)
**Fonte**: Timestamp 01:46:44-01:47:21

- Formato **storico/precedente**
- I supporti logici erano file flat separati
- Attualmente vengono **incapsulati dentro XML**
- Mantenuto per retrocompatibilità

#### 14.1.3 Struttura XML del Flusso

Basandosi sulle informazioni dalla trascrizione (timestamp 01:46:57-01:47:10 e 01:33:38-01:34:00):

```xml
<Flusso>
  <!-- CHIAVE CBI: Identificazione Univoca del Flusso -->
  <MetadatiCBI>
    <Mittente>XXXXX</Mittente>           <!-- Codice GPA/Banca mittente -->
    <Destinatario>YYYYY</Destinatario>   <!-- Codice GPA/PA destinatario -->
    <TipoFlusso>SEPA</TipoFlusso>        <!-- Categoria servizio -->
    <NomeSupporto>F4</NomeSupporto>      <!-- Tipo specifico -->
    <DataCreazione>2026-01-16T10:30:00</DataCreazione>
  </MetadatiCBI>
  
  <!-- SUPPORTI LOGICI: Contenitori di transazioni -->
  <SupportoLogico id="SL001">
    <!-- Supporto logico flat incapsulato in XML -->
    <MessaggioLogico id="ML001" tipo="bonifico">
      <Importo>1500.00</Importo>
      <IBAN>IT60X0542811101000000123456</IBAN>
      <!-- Altri dati della transazione -->
    </MessaggioLogico>
    
    <MessaggioLogico id="ML002" tipo="bonifico">
      <Importo>2300.50</Importo>
      <IBAN>IT60X0542811101000000789012</IBAN>
    </MessaggioLogico>
  </SupportoLogico>
  
  <SupportoLogico id="SL002">
    <MessaggioLogico id="ML003" tipo="bonifico">
      <!-- Altro messaggio logico -->
    </MessaggioLogico>
  </SupportoLogico>
  
  <!-- Possono esserci N supporti logici -->
</Flusso>
```

#### 14.1.4 Chiave CBI (Identificazione Univoca)
**Fonte**: Timestamp 01:46:57-01:47:10

La **Chiave CBI** identifica univocamente un flusso ed è composta da:
- **Mittente**: codice identificativo GPA/Banca
- **Ricevente**: codice identificativo GPA/PA
- **Nome supporto**: tipo di flusso (es. "SEPA F4", "RiBa", ecc.)
- **Data creazione**: timestamp di generazione
- **Tipo flusso**: categoria del servizio

---

### 14.2 Ciclo di Vita Completo di un Flusso

#### 14.2.1 Attori del Sistema CHC

**Fonte**: Sezione 13 (Componenti CHC) e analisi trascrizione 00:04:00-00:13:00

| Attore | Ruolo | Responsabilità |
|--------|-------|----------------|
| **GPA/Banca Mittente** | Origine flusso | Genera e invia flussi XML al CHC |
| **GPA/Banca Destinatario** | Destinazione | Riceve ed elabora flussi dal CHC |
| **PA (Pubblica Amministrazione)** | Ente pubblico | Riceve/invia flussi tramite IQC |
| **Client CHC** | Frontiera batch/online | Prima ricezione flussi (inbound) |
| **CCR** (Client Component Router) | Router | Routing e smistamento interno |
| **Orchestrator** | Coordinatore | Orchestrazione workflow e componenti |
| **Corebatch** | Elaboratore | Elaborazioni batch, generazione "Diagnosis Enfanted" |
| **IQC** | Gateway PA | Interfacciamento con Pubblica Amministrazione |
| **C-Bill** | Servizio specifico | Gestione bollettini |
| **DAM** | Servizio dati | Gestione informazioni centralizzate |
| **Loader** | Batch processor | Prelievo eventi da RabbitMQ e carico in DB |
| **Dashboard** | Monitoraggio | Visualizzazione eventi e reportistica |
| **RabbitMQ** | Message Broker | Code eventi per comunicazione asincrona |
| **Oracle DB** | Persistenza | Memorizzazione eventi e dati flussi |
| **PKBox** | Crittografia | Gestione certificati e cifratura/decifratura |
| **Repository Centrale** | Storage | Archiviazione flussi e firmati (object storage) |
| **Splunk** | Monitoring | Monitoraggio log e alert sistemici |
| **IAM** | Autenticazione | Gestione identità e autorizzazioni utenti |

#### 14.2.2 Diagramma del Ciclo di Vita con Attori

**Fonte**: Sezioni timestamp 00:11:26-00:13:00, 00:32:00-00:35:00

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                    CICLO DI VITA FLUSSO CHC CON ATTORI                       │
└──────────────────────────────────────────────────────────────────────────────┘

╔═══════════════════════════════════════════════════════════════════════════╗
║ FASE 1: ORIGINE                                                           ║
╚═══════════════════════════════════════════════════════════════════════════╝
   ATTORE: GPA/Banca Mittente
   
   ┌─────────────────┐
   │  GPA/Banca      │  1. Genera flusso XML
   │  Mittente       │  2. Applica firma digitale (opzionale)
   │  (es. Intesa)   │  3. Prepara invio
   └────────┬────────┘
            │ SFTP/API
            │ Flusso XML
            ▼

╔═══════════════════════════════════════════════════════════════════════════╗
║ FASE 2: INGRESSO CHC                                                      ║
╚═══════════════════════════════════════════════════════════════════════════╝
   ATTORE: Client CHC (frontiera)
   
   ┌─────────────────┐
   │  Client CHC     │  1. Riceve flusso via SFTP/API
   │  (Gateway)      │  2. Assegna ID tratta
   │                 │  3. Genera evento "Flusso Ricevuto"
   └────────┬────────┘
            │ Evento → RabbitMQ
            ▼
   ┌─────────────────┐
   │   RabbitMQ      │  Queue: eventi.inbound
   │  (Message Bus)  │  
   └────────┬────────┘
            │
            ▼

╔═══════════════════════════════════════════════════════════════════════════╗
║ FASE 3: RICEZIONE E ROUTING                                               ║
╚═══════════════════════════════════════════════════════════════════════════╝
   ATTORI: CCR, Orchestrator
   
   ┌─────────────────┐
   │      CCR        │  1. Riceve flusso da Client
   │  (Router)       │  2. Analizza Chiave CBI
   │                 │  3. Determina routing
   └────────┬────────┘  4. Genera evento "Routing Determinato"
            │
            ▼
   ┌─────────────────┐
   │  Orchestrator   │  1. Coordina workflow
   │                 │  2. Assegna a componente elaboratore
   │                 │  3. Genera evento "Preso in Carico"
   └────────┬────────┘
            │
            ▼

╔═══════════════════════════════════════════════════════════════════════════╗
║ FASE 4: CONTROLLI FORMALI E SICUREZZA                                     ║
╚═══════════════════════════════════════════════════════════════════════════╝
   ATTORI: PKBox, Repository Centrale
   
   ┌─────────────────┐
   │     PKBox       │  1. Verifica firma digitale
   │  (Crittografia) │  2. Valida certificati
   │                 │  3. Decifratura (se cifrato)
   └────────┬────────┘
            │ OK/KO
            ▼
   ┌─────────────────┐
   │  Repository     │  1. Archivia flusso originale
   │  Centrale       │  2. Archivia firmato (se presente)
   │  (Object Store) │  3. Genera riferimento storage
   └────────┬────────┘
            │
            ├─► KO → Genera evento "Errore Controlli"
            │        └─► Dashboard: flusso SCARTATO
            │
            └─► OK → Genera evento "Controlli OK"
                     └─► Prosegue elaborazione
                     
            ▼

╔═══════════════════════════════════════════════════════════════════════════╗
║ FASE 5: CARICAMENTO DATABASE                                              ║
╚═══════════════════════════════════════════════════════════════════════════╝
   ATTORI: Loader, Oracle DB
   
   ┌─────────────────┐
   │     Loader      │  1. Preleva eventi da RabbitMQ
   │  (Batch µsvc)   │  2. Processa eventi
   │                 │  3. INSERT INTO Oracle
   └────────┬────────┘  4. Genera evento "Flusso Caricato"
            │
            │ JDBC
            ▼
   ┌─────────────────┐
   │   Oracle DB     │  Tabelle:
   │                 │  • IQCI01_TB_FLUSSO_IDX (metadati)
   │                 │  • IQCI01_TB_EVENTI (eventi)
   └─────────────────┘  • IQCI01_TB_SUPPORTI_LOGICI
                        • IQCI01_TB_MESSAGGI_LOGICI
            │
            ▼ Dati persistiti

╔═══════════════════════════════════════════════════════════════════════════╗
║ FASE 6: ELABORAZIONE BUSINESS                                             ║
╚═══════════════════════════════════════════════════════════════════════════╝
   ATTORI: Corebatch, C-Bill (se bollettini), IQC (se PA)
   
   ┌─────────────────┐
   │   Corebatch     │  1. Elabora supporti logici
   │                 │  2. Valida messaggi logici
   │                 │  3. Applica business rules
   └────────┬────────┘  4. Genera "Diagnosis Enfanted" (evento cardine!)
            │           5. Genera eventi per ogni supporto
            │
            ├─► Per ogni Supporto Logico:
            │   │
            │   ├─ Evento "Elaborazione Avviata"
            │   ├─ Evento "Diagnosis Enfanted" ◄── EVENTO CARDINE
            │   │    (contiene: OK/KO messaggi, importi, dati supporto)
            │   └─ Evento "Supporto Elaborato"
            │
            ▼
   ┌─────────────────┐
   │   C-Bill        │  (Solo se servizio bollettini)
   │  (Bollettini)   │  1. Elaborazione specifica bollettini
   └─────────────────┘  2. Genera eventi specifici
            │
            ▼
   ┌─────────────────┐
   │      IQC        │  (Solo se flussi PA)
   │  (Gateway PA)   │  1. Interfacciamento Pubblica Amministrazione
   └─────────────────┘  2. Trasformazione formati PA
                        3. Genera eventi PA-specific
            │
            ▼

╔═══════════════════════════════════════════════════════════════════════════╗
║ FASE 7: TRASFORMAZIONE E PREPARAZIONE OUTPUT                              ║
╚═══════════════════════════════════════════════════════════════════════════╝
   ATTORI: Orchestrator, CCR, PKBox
   
   ┌─────────────────┐
   │  Orchestrator   │  1. Coordina preparazione output
   │                 │  2. Routing verso destinatario
   └────────┬────────┘  3. Genera evento "Routing Completato"
            │
            ▼
   ┌─────────────────┐
   │      CCR        │  1. Prepara XML output
   │  (Transformer)  │  2. Arricchimento dati (se necessario)
   └────────┬────────┘  3. Genera evento "Output Preparato"
            │
            ▼
   ┌─────────────────┐
   │     PKBox       │  (Se richiesto)
   │  (Firma)        │  1. Applica firma digitale
   └─────────────────┘  2. Cifratura (se necessario)
            │
            ▼

╔═══════════════════════════════════════════════════════════════════════════╗
║ FASE 8: SPEDIZIONE                                                        ║
╚═══════════════════════════════════════════════════════════════════════════╝
   ATTORI: Client CHC (outbound), Repository Centrale
   
   ┌─────────────────┐
   │  Client CHC     │  1. Trasmissione flusso
   │  (Outbound)     │  2. Invio via SFTP/API
   └────────┬────────┘  3. Genera evento "Flusso Spedito"
            │           4. Attende ACK (se previsto)
            │
            ▼
   ┌─────────────────┐
   │  Repository     │  1. Archivia flusso in uscita
   │  Centrale       │  2. Log spedizione
   └─────────────────┘
            │
            │ SFTP/API
            │ Flusso XML
            ▼

╔═══════════════════════════════════════════════════════════════════════════╗
║ FASE 9: DESTINAZIONE                                                      ║
╚═══════════════════════════════════════════════════════════════════════════╝
   ATTORE: GPA/Banca Destinatario (o PA)
   
   ┌─────────────────┐
   │  GPA/Banca      │  1. Riceve flusso
   │  Destinatario   │  2. Invia ACK (acknowledgment)
   │  (es. Unicredit)│  3. Elabora contenuto
   └────────┬────────┘
            │
            │ ACK
            ▼
   ┌─────────────────┐
   │  Client CHC     │  1. Riceve ACK
   │  (Inbound)      │  2. Genera evento "Flusso Consegnato"
   └─────────────────┘  3. Chiude ciclo di vita
            │
            ▼

╔═══════════════════════════════════════════════════════════════════════════╗
║ FASE 10: MONITORAGGIO (continuo durante tutto il ciclo)                   ║
╚═══════════════════════════════════════════════════════════════════════════╝
   ATTORI: Dashboard, Splunk, IAM
   
   ┌─────────────────┐         ┌─────────────────┐
   │   Dashboard     │◄────────│   Oracle DB     │
   │   (Frontend)    │  Query  │  (Eventi)       │
   │                 │         └─────────────────┘
   │  • Angular SPA  │
   │  • BFF Layer    │         ┌─────────────────┐
   │  • CoreAPI      │◄────────│      IAM        │
   └────────┬────────┘  Auth   │  (Autenticaz.)  │
            │                  └─────────────────┘
            │
            │ Visualizzazione per:
            │
            ├─► Utenti Banche/GPA (Monitoraggio Applicativo)
            │   └─ Ricerca flussi specifici
            │   └─ Timeline eventi per ID tratta
            │
            ├─► Utenti Nexi/CBI (Monitoraggio Sistemico)
            │   └─ Viste aggregate
            │   └─ Volumi e statistiche
            │   └─ Alert e anomalie
            │
            └─► Operations (Report)
                └─ Report schedulati via JASPER
   
   ┌─────────────────┐
   │     Splunk      │  1. Monitoring log applicativi
   │  (Monitoring)   │  2. Alert sistemici
   └─────────────────┘  3. Dashboard sistemistica
```

#### 14.2.3 Timeline Eventi per Attore

**Fonte**: Analisi eventi trascrizione 00:04:00-00:07:00, 01:31:00-01:35:00

| Timestamp | Fase | Attore | Evento Generato | Descrizione |
|-----------|------|--------|-----------------|-------------|
| T0 | 1 | GPA Mittente | - | Genera flusso XML |
| T1 | 2 | Client CHC | `Flusso Ricevuto` | Ricezione in CHC |
| T2 | 3 | CCR | `Routing Determinato` | Analisi Chiave CBI |
| T3 | 3 | Orchestrator | `Preso in Carico` | Assegnazione workflow |
| T4 | 4 | PKBox | `Controlli OK` / `Errore` | Verifica firma/certificati |
| T5 | 4 | Repository Centrale | `Flusso Archiviato` | Salvataggio object storage |
| T6 | 5 | Loader | `Flusso Caricato` | INSERT in Oracle DB |
| T7 | 6 | Corebatch | `Elaborazione Avviata` | Inizio processamento |
| T8 | 6 | Corebatch | `Diagnosis Enfanted` | **EVENTO CARDINE** per supporto logico |
| T9 | 6 | Corebatch | `Supporto Elaborato` | Fine elaborazione supporto |
| T10 | 6 | IQC (se PA) | `Interfacciamento PA OK` | Validazione PA |
| T11 | 7 | Orchestrator | `Routing Completato` | Determinato destinatario |
| T12 | 7 | CCR | `Output Preparato` | XML output generato |
| T13 | 7 | PKBox | `Firma Applicata` | Firma digitale output |
| T14 | 8 | Client CHC | `Flusso Spedito` | Trasmissione effettuata |
| T15 | 8 | Repository Centrale | `Outbound Archiviato` | Archiviazione flusso uscita |
| T16 | 9 | GPA Destinatario | - | Ricezione flusso |
| T17 | 9 | Client CHC | `Flusso Consegnato` | ACK ricevuto |
| continuo | 10 | Dashboard | - | Visualizzazione real-time |
| continuo | 10 | Splunk | Alert generati | Anomalie rilevate |

#### 14.2.4 Fasi Dettagliate con Responsabilità Attori

**FASE 1 - ORIGINE**
- **Attore principale**: GPA/Banca Mittente
- **Responsabilità**:
  - Generazione flusso XML secondo standard CBI
  - Applicazione firma digitale (opzionale, dipende da contratto)
  - Invio tramite canale concordato (SFTP/API)
- **Output**: Flusso XML pronto per invio

**FASE 2 - INGRESSO CHC**
- **Attore principale**: Client CHC (frontiera)
- **Responsabilità**:
  - Ricezione file/chiamata API
  - Assegnazione ID tratta univoco
  - Primo evento su RabbitMQ
- **Output**: Evento "Flusso Ricevuto" su queue

**FASE 3 - RICEZIONE E ROUTING**
- **Attori principali**: CCR, Orchestrator
- **Responsabilità CCR**:
  - Parsing Chiave CBI
  - Determinazione routing (quale componente deve elaborare)
  - Smistamento verso componente corretto
- **Responsabilità Orchestrator**:
  - Coordinamento workflow complessivo
  - Assegnazione priorità
  - Monitoring stato elaborazione
- **Output**: Flusso instradato al componente elaboratore

**FASE 4 - CONTROLLI FORMALI E SICUREZZA**
- **Attori principali**: PKBox, Repository Centrale
- **Responsabilità PKBox**:
  - Verifica firma digitale
  - Validazione certificati (non scaduti, trusted)
  - Decifratura contenuto (se cifrato)
- **Responsabilità Repository Centrale**:
  - Archiviazione flusso originale (object storage)
  - Archiviazione firmato separato
  - Generazione riferimento per retrieval futuro
- **Output**: 
  - Se OK → Prosegue a Fase 5
  - Se KO → Scarto + Evento errore → Dashboard mostra anomalia

**FASE 5 - CARICAMENTO DATABASE**
- **Attori principali**: Loader, Oracle DB
- **Responsabilità Loader**:
  - Polling code RabbitMQ
  - Processing eventi in batch
  - Ottimizzazione INSERT (performance critiche)
  - Minimizzare elaborazione (dati già pronti da componenti)
- **Responsabilità Oracle DB**:
  - Persistenza eventi
  - Indicizzazione per query Dashboard
  - Gestione transazioni
- **Output**: Dati persistiti, disponibili per query Dashboard

**FASE 6 - ELABORAZIONE BUSINESS**
- **Attori principali**: Corebatch, C-Bill (bollettini), IQC (PA)
- **Responsabilità Corebatch**:
  - Elaborazione supporti logici
  - Validazione messaggi logici
  - Applicazione business rules
  - **Generazione "Diagnosis Enfanted"** (evento cardine!)
  - Marking messaggi OK/KO
- **Responsabilità C-Bill** (solo bollettini):
  - Elaborazioni specifiche servizio bollettini
  - Validazioni peculiari bollettistica
- **Responsabilità IQC** (solo flussi PA):
  - Interfacciamento con Pubblica Amministrazione
  - Trasformazione formati PA-specific
  - Validazioni normative PA
- **Output**: Supporti logici elaborati con esiti (OK/KO)

**FASE 7 - TRASFORMAZIONE E PREPARAZIONE OUTPUT**
- **Attori principali**: Orchestrator, CCR, PKBox
- **Responsabilità Orchestrator**:
  - Coordinamento preparazione output
  - Decisione routing verso destinatario
- **Responsabilità CCR**:
  - Generazione XML output
  - Arricchimento dati (es. campi addizionali)
  - Formattazione secondo standard destinatario
- **Responsabilità PKBox**:
  - Applicazione firma digitale output (se richiesto)
  - Cifratura (se richiesto da destinatario)
- **Output**: Flusso XML pronto per spedizione

**FASE 8 - SPEDIZIONE**
- **Attori principali**: Client CHC (outbound), Repository Centrale
- **Responsabilità Client CHC**:
  - Trasmissione via SFTP/API al destinatario
  - Gestione retry (se fallimento)
  - Attesa ACK (se protocollato)
  - Generazione evento "Flusso Spedito"
- **Responsabilità Repository Centrale**:
  - Archiviazione flusso in uscita
  - Log trasmissione per audit
- **Output**: Flusso trasmesso al destinatario

**FASE 9 - DESTINAZIONE**
- **Attore principale**: GPA/Banca Destinatario (o PA)
- **Responsabilità**:
  - Ricezione flusso
  - Invio ACK al CHC (se previsto)
  - Elaborazione locale del contenuto
- **Responsabilità Client CHC** (ricezione ACK):
  - Processing ACK
  - Generazione evento "Flusso Consegnato"
  - Chiusura ciclo di vita in Dashboard
- **Output**: Ciclo di vita completo

**FASE 10 - MONITORAGGIO** (continuo)
- **Attori principali**: Dashboard, Splunk, IAM
- **Responsabilità Dashboard**:
  - Query Oracle DB per eventi
  - Visualizzazione timeline flussi
  - Aggregazioni viste sistemiche
  - Gestione alert configurati
  - Generazione report via JASPER
- **Responsabilità IAM**:
  - Autenticazione utenti (banche, GPA, Nexi, CBI)
  - Autorizzazione accessi (profilazione)
  - Single Sign-On (se integrato)
- **Responsabilità Splunk**:
  - Monitoring log applicativi di tutti i componenti
  - Correlazione log per troubleshooting
  - Alert sistemici (es. servizio down)
  - Dashboard sistemistica per operations
- **Output**: Visibilità completa stato CHC

---

### 14.3 Matrice Responsabilità (RACI)

**Fonte**: Analisi organica della trascrizione

| Attività | Client | CCR | Orchestrator | Corebatch | Loader | Dashboard | PKBox | Repository | RabbitMQ | Oracle |
|----------|--------|-----|--------------|-----------|--------|-----------|-------|------------|----------|--------|
| Ricezione flusso | **R** | C | I | - | - | I | - | - | - | - |
| Routing | C | **R** | **A** | - | - | - | - | - | - | - |
| Validazione firma | - | - | I | - | - | I | **R** | C | - | - |
| Archiviazione | - | - | I | - | - | - | - | **R** | - | - |
| Carico eventi DB | - | - | - | - | **R** | C | - | - | C | **A** |
| Elaborazione business | - | C | **A** | **R** | - | - | - | - | - | C |
| "Diagnosis Enfanted" | - | - | I | **R** | C | C | - | - | C | A |
| Preparazione output | C | **R** | **A** | - | - | - | C | - | - | - |
| Firma output | - | - | I | - | - | - | **R** | - | - | - |
| Spedizione | **R** | C | I | - | - | I | - | C | - | - |
| Monitoraggio | - | - | - | - | - | **R** | - | - | - | **A** |
| Alert | - | - | - | - | - | **R** | - | - | - | C |

**Legenda RACI**:
- **R** (Responsible): Esegue l'attività
- **A** (Accountable): Responsabile finale/approva
- **C** (Consulted): Consultato/fornisce input
- **I** (Informed): Informato del risultato

---

### 14.4 Tempi Medi e SLA

**Fonte**: Timestamp 00:35:00-00:37:00 (Vista Sistemica - Monitoraggio)

#### 14.4.1 Tempi Medi per Fase

La **Vista Sistemica di Monitoraggio** della Dashboard mostra:
- **Tempo medio di elaborazione** per fascia oraria
- **Distribuzione temporale** dei transiti
- **Confronto con SLA** contrattualizzati

**Nota**: I tempi specifici non sono dettagliati nella trascrizione, ma la Dashboard permette di:
- Monitorare tempi per singolo GPA
- Identificare colli di bottiglia
- Alert automatici su superamento soglie SLA

#### 14.4.2 Protocolli di Trasporto per Attore

**Fonte**: Sezione 8.7-8.8 (Stack e Integrazioni)

| Attore | Protocollo Input | Protocollo Output | Note |
|--------|------------------|-------------------|------|
| GPA/Banca → CHC | SFTP, API REST | - | Invio flussi |
| Client CHC | SFTP, HTTP/S | RabbitMQ (AMQP) | Frontiera |
| CCR | RabbitMQ | RabbitMQ | Interno CHC |
| Orchestrator | RabbitMQ | RabbitMQ | Coordinamento |
| Corebatch | RabbitMQ | RabbitMQ | Elaborazione |
| Loader | RabbitMQ | JDBC | Batch processing |
| Oracle DB | JDBC | JDBC | Persistenza |
| Dashboard → DB | JDBC | - | Query eventi |
| Dashboard → Utenti | - | HTTP/S (Angular) | Frontend |
| CHC → GPA/Banca | - | SFTP, API REST | Invio output |
| PKBox | API interna | API interna | Crittografia |
| Repository Centrale | API Object Storage | API Object Storage | S3-like |
| Splunk | Syslog, HTTP | - | Log collector |

---

### 14.5 Gestione Errori per Attore

**Fonte**: Timestamp 01:31:23-01:31:37 (Eventi), 00:35:00-00:37:00 (Alert)

#### 14.5.1 Tipologie di Errore e Responsabilità

| Errore | Fase | Attore Responsabile | Azione | Visibilità |
|--------|------|---------------------|--------|------------|
| Schema XML non valido | 4 | PKBox | Scarto immediato | Dashboard + Alert |
| Firma non verificabile | 4 | PKBox | Scarto + notifica mittente | Dashboard + Alert |
| Certificato scaduto | 4 | PKBox | Scarto + alert certificati | Dashboard + Splunk |
| Messaggio logico KO | 6 | Corebatch | "Diagnosis Enfanted" | Dashboard (dettaglio) |
| Business rule fallita | 6 | Corebatch/C-Bill/IQC | Marcatura KO + prosegui | Dashboard |
| Destinatario non raggiungibile | 8 | Client CHC | Retry automatico | Dashboard + Alert |
| Timeout trasmissione | 8 | Client CHC | Retry + escalation | Dashboard + Splunk |
| DB non disponibile | 5 | Loader | Queue accodate + alert | Splunk + Alert critico |
| RabbitMQ disconnesso | tutti | Orchestrator | Retry + alert sistemico | Splunk + Operations |

#### 14.5.2 Escalation per Attore

**Percorso di escalation** (inferito dalla trascrizione):

1. **Errore rilevato** → Evento su RabbitMQ
2. **Loader** → INSERT evento errore in Oracle
3. **Dashboard** → Visualizzazione errore in UI
4. **Splunk** → Correlazione log + alert
5. **Operations Nexi/CBI** → Notifica via Dashboard/email
6. **Team di supporto** → Intervento manuale (se necessario)

---

### 14.6 Nota sulla Documentazione

La trascrizione **non include**:
- Standard XML specifici (es. ISO 20022, CBI-Ops)
- Strutture XSD complete
- Esempi di payload XML reali
- Regole di validazione business dettagliate
- Specifiche SLA per fascia oraria
- Dettagli protocolli autenticazione SFTP/API

Queste informazioni sono presumibilmente disponibili nella:
- **Documentazione tecnica dei tracciati CBI**
- **Manuali di integrazione per GPA**
- **Specifiche tecniche contratti di servizio**
- **Documentazione architetturale CHC completa**

---

*Documento generato da trascrizione KT del 13/11/2025*
