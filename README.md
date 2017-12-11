[![Build Status](https://travis-ci.org/italia/spid-spring.svg?branch=master)](https://travis-ci.org/italia/spid-spring)

# spid-spring

Questo progetto rappresenta la risposta alla issue ["Sviluppo estensione Java Spring per SPID #1"](https://github.com/italia/spid-spring/issues/1) del team hack.developers 2017 di Lecce.

Il codice sorgente è suddiviso nei due seguenti moduli maven.
1. **spid-spring-integration** è una libreria JAR che fornisce un supporto alle web application Spring che hanno la necessità di integrarsi in single sign-on con un Identity Provider SPID.
2. **spid-spring-rest** è una applicazione Spring Boot che funge da proof of concept dell'estensione Java Spring per SPID implementata.

L'applicazione Spring Boot espone tre servizi REST per un ipotetico client che consentono di conoscere la lista degli Identity Provider ufficiali, di produrre una request da inviare ad uno specifico Identity Provider e infine di recuperare il contenuto dell response finale inviata dall'Identity Provider.

I dettagli dei due moduli maven sono riportati nei seguenti file README.md: [spid-spring-integration](https://github.com/lucastle/spid-spring/blob/master/spid-spring-integration/README.md) e [spid-spring-rest](https://github.com/lucastle/spid-spring/blob/master/spid-spring-rest/README.md)

## TODO List
- Completare il test della chiamata per la generazione della Authn Request.
- Completare la chiamata per l'estrazione dei dati dell'utente dalla response dell'Identity Provider.
