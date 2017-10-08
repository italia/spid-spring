
# SPID SPRING-REST
REST enpoints per SPID-SPRING.
Questo progetto consente l'utilizzo dell'estensione Java Spring per SPID da parte di applicazioni client, che possono essere web application o app per qualsiasi sistema operativo.

# Configurazione dell'applicazione
L'applicazione è basata su Spring Boot, pertanto è possibile attivare gli endpoint semplicemente lanciando il file jar eseguibile prodotto dalla build. La build viene effettuata con Maven, il presente progetto è un modulo del progetto padre "Spid-Spring" (TODO: inserire LINK).

Mandando in esecuzione il jar, gli endpoint vengono esposti su http://localhost:8080/

Inoltre è possibile utilizzare la classe @RestController (it.italia.developers.spid.spidspringrest.controller.SpidSpringRestController) all'interno dei progetti web che necessitano di esporre i servizi REST per l'autenticazione Spid.

# Specifiche e test dei servizi REST
I servizi REST sono documentati per mezzo di Swagger, accedendo all'indirizzo http://localhost:8080/swagger-ui.html . Lo strumento swagger consente inoltre di inserire i paramteri di input e testare gli endpoint per mezzo del pulsante "Try it out!".

![Swagger](https://github.com/Gianluke/spid-spring/blob/master/spid-spring-rest/SPID-SPRING-REST.png?raw=true)
