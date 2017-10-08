
# spid-spring-integration

Si tratta di una libreria sviluppata utilizzando le funzionalità di OpenSAML v3 che agevola eventuali applicazioni basate su Spring nel compito di aderire al circuito SPID e quindi di consentire ai propri utenti di autenticarsi usando le proprie credenziali SPID. La libreria è costituita da un file JAR utilizzabile come dipendenza in un'applicazione Spring. Essa espone le sue funzionalità attraverso i seguenti metodi pubblici di un'interfaccia SPIDIntegrationService.
- `List<IdpEntry> getAllIdpEntry()`
	 - Restituisce una lista degli Identity Provider SPID ufficiali. Di ogni provider il servizio fornisce un identificativo applicativo, l'entityID e il nome del provider, un URL da cui è possibile scaricare il logo del provider.
- `AuthRequest buildAuthenticationRequest(String entityId, int assertionConsumerServiceIndex)`
	 - Dati l'entityID del provider e l'indice di uno specifico consumer service fornito dal Service Provider, restituisce un oggetto contenente l’URL a cui inviare il messaggio di risposta alla richiesta di autenticazione e il contenuto del campo AuthnRequest da inviare all'Identity Provider.
- `ResponseDecoded processAuthenticationResponse(ResponseEncoded response)`
   - Richiede in ingresso la risposta codificata restituita dall'Identity Provider e restituisce una versione intellegibile delle informazioni in essa riportate.

## Indicazioni per eventuali evoluzioni future della libreria
In caso di evoluzione della libreria, occorre tener presente che per ogni nuovo Identity Provider da aggiungere si rendono necessari i seguenti passi:
1. aggiungere nella directory "spid-spring/spid-spring-integration/src/main/resources/metadata/idp" il file metadata che descrive l'Identity Provider;
2. aggiungere nel file "spid-spring/spid-spring-integration/src/main/resources/idplist.properties" quattro righe del tipo:

    spid.spring.integration.idp.[IdP-id].file=[IdP]-metadata.xml
    spid.spring.integration.idp.[IdP-id].name=[IdP-name]
    spid.spring.integration.idp.[IdP-id].imageUrl=[IdP-logo-url]
    spid.spring.integration.idp.[IdP-id].entityId=[IdP-entityID]
dove [IdP-id], in particolare, deve essere sostituito con l'id applicativo che distingue il nuovo Identity Provider;
3. aggiungere alla seguente riga una virgola e l'id applicativo che distingue il nuovo Identity Provider.
