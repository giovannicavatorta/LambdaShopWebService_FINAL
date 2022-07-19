package com.cwgio97.webservice.controller;

import com.cwgio97.webservice.model.Customer;
import com.cwgio97.webservice.service.CustomersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// Permette di ottenere i dati dal client.
// In questa classe saranno presenti tutti i dati di un nuovo cliente.
@RestController
@RequestMapping("/api/customers") // Tutte le chiamate al servizio inizieranno con questo mapping
@Slf4j // Notazione Lombok. Permette di attivare il servizio di log del web service
@CrossOrigin(origins = "http://localhost:3000") // Abilito il CORS con origine front-end
public class CustomersController {
    // Effettuo code injection dello strato di servizio, perché come già visto la classe Controller
    // si interfaccia allo strato di servizio che a sua volta si interfaccia allo strato di persistenza.
    @Autowired
    CustomersService customersService;

    // Dato che il metodo HTTP che utilizzo per poter inserire i dati nel database è
    // un POST, devo usare la notazione PostMapping con un determinato endpoint, che
    // produrrà un tipo in formato .json
    @PostMapping(value = "/insert", produces = "application/json")
    // Metodo che restituisce un ResponseEntity di tipo Mono<Customer>, perché
    // lo strato di persistenza non solo inserirà i dati cliente, ma restituirà
    // anche i dati del cliente appena inserito. @RequestBody avrà come tipo Customer.
    public ResponseEntity<Mono<Customer>> insertCustomer(@RequestBody Customer newCustomer) {
        // Inserisco il log di riferimento. "log" si ottiene da Lombok,
        // per cui non serve scrivere ulteriori classi
        log.info("------ Inserimento nuovo cliente ------");
        // Utilizzo il metodo di CustomersService salvando l'oggetto che ottengo dal
        // body della chiamata al web service e otterrò come return una classe di tipo Mono<Customer>
        Mono<Customer> customer = customersService.save(newCustomer);
        // La chiamata restituirà, nel body, il formato .json dell'articolo inserito e lo status "CREATED"
        return new ResponseEntity<Mono<Customer>>(customer, HttpStatus.CREATED);
    }

    // Metodo che restituisce tutti i clienti in un Flux<Customer>.
    // Specifico quindi la notazione (non più @PostMapping, ma @RequestMapping: notazione generica
    // per specificare endpoint, metodo e ciò che il metodo restituisce - quando non si specifica
    // @GetMapping | @PostMapping | @PutMapping va infatti specificato anche il metodo):
    @RequestMapping(value = "/find/all", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Flux<Customer>> getAllCustomers() {
        // Inserisco log di riferimento (Lombok)
        log.info("------ Ottengo tutti i clienti ------");
        // Uso il metodo SelectAll() da CustomerService.
        Flux<Customer> customers = customersService.findAll()
                // Gestisco anche un'eventuale eccezione di collection vuota - switchIfEmpty(...) -
                // che ritorna un Mono con uno status HTTP NOT FOUND e un messaggio
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Lista clienti vuota.")));
        // Se il metodo invece ci restituisce qualcosa...
        return new ResponseEntity<Flux<Customer>>(customers, HttpStatus.OK);
    }

    // Ricerca per codice cliente
    // Uso un Mono perché il codice è univoco, quindi restituirà un solo cliente e dunque un solo oggetto
    @RequestMapping(value = "/find/code/{codice}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Flux<Customer>> findCustomerByCodice(@PathVariable("codice") String codice) {
        // Inserisco log di riferimento (Lombok)
        log.info("------ Cerco il cliente con codice " + codice + " ------");
        // Uso il metodo FindByCodice(String codice) da CustomerService.
        Flux<Customer> customer = customersService.findByCodiceLike(codice)
                // Gestisco anche un'eventuale eccezione di collection vuota - switchIfEmpty(...) -
                // che ritorna un Mono con uno status HTTP NOT FOUND e un messaggio
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Clienti non trovati!")));
        // Se il metodo invece ci restituisce qualcosa...
        return new ResponseEntity<Flux<Customer>>(customer, HttpStatus.OK);
    }

    // Ricerca per nome
    @GetMapping(value = "/find/name/{nome}", produces = "application/json")
    public ResponseEntity<Flux<Customer>> findCustomerByNome(@PathVariable("nome") String nome) {
        log.info("------ Cerco i clienti con nome " + nome + " ------");
        Flux<Customer> customer = customersService.findByNomeLike(nome)
                // Nel caso di Flux vuoto...
                .switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Clienti non trovati.")));
        // Se il metodo invece ci restituisce qualcosa...
        return new ResponseEntity<Flux<Customer>>(customer, HttpStatus.OK);
    }

    // Ricerca per monte punti
    @GetMapping(value = "/find/points/{punti}", produces = "application/json")
    public ResponseEntity<Flux<Customer>> findCustomerByPunti(@PathVariable("punti") int punti) {
        log.info("------ Cerco i clienti con almeno " + punti + " punti ------");
        Flux<Customer> customer = customersService.findByPunti(punti)
                // Nel caso di Flux vuoto...
                .switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Clienti non trovati.")));
        // Se il metodo invece ci restituisce qualcosa...
        return new ResponseEntity<Flux<Customer>>(customer, HttpStatus.OK);
    }

    // Come notazione utilizzo DeleteMapping, che identifica direttamente un metodo HTTP_DELETE
    // con valore dell'endpoint seguito dallo specifico id e come riferimento alla variabile l'id
    @DeleteMapping(value = "/delete/id/{id}", produces = "application/json")
    // Il metodo prevede un parametro che sarà l'id univoco d'identificazione del record su MongoDB
    public ResponseEntity<Mono<Void>> deleteCustomer(@PathVariable("id") String id) {
        // Log Lombok
        log.info("------ Elimino cliente ------");
        // Restituisco un ResponseEntity di tipo Mono<Void> dove utilizzo il metodo "Delete" di
        // CustomersService e restituisco un HTTP_STATUS_OK
        return new ResponseEntity<Mono<Void>>(customersService.delete(id), HttpStatus.OK);
    }
}