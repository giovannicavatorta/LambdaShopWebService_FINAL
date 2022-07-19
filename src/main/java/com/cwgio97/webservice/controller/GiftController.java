package com.cwgio97.webservice.controller;

import com.cwgio97.webservice.model.Gift;
import com.cwgio97.webservice.service.GiftService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// Permette di ottenere i dati dal client.
// In questa classe saranno presenti tutti i dati di un nuovo premio.
@RestController
@RequestMapping("/api/gifts") // Tutte le chiamate al servizio inizieranno con questo mapping
@Slf4j // Notazione Lombok. Permette di attivare il servizio di log del web service
@CrossOrigin(origins = "http://localhost:3000") // Abilito il CORS con origine front-end
public class GiftController {
    // Effettuo code injection dello strato di servizio, perché come già visto la classe Controller
    // si interfaccia allo strato di servizio che a sua volta si interfaccia allo strato di persistenza.
    @Autowired
    GiftService giftService;

    // Gestisco l'autenticazione: interfacciandosi a questo endpoint si ottiene una stringa di conferma.
    // Lo inserisco solo in questo controller. Il client poi si interfaccerà a questo endpoint di verifica
    @GetMapping(value="/auth", produces = "application/json")
    public Mono<String> checkBAuthentication() {
        return Mono.just("Utente autenticato correttamente.");
    }

    // Dato che il metodo HTTP che utilizzo per poter inserire i dati nel database è
    // un POST, devo usare la notazione PostMapping con un determinato endpoint, che
    // produrrà un tipo in formato .json
    @PostMapping(value = "/insert", produces = "application/json")
    // Metodo che restituisce un ResponseEntity di tipo Mono<Gift>, perché
    // lo strato di persistenza non solo inserirà i dati del premio, ma restituirà
    // anche i dati del premio appena inserito. @RequestBody avrà come tipo Gift.
    public ResponseEntity<Mono<Gift>> insertGift(@RequestBody Gift newGift) {
        // Inserisco il log di riferimento. "log" si ottiene da Lombok,
        // per cui non serve scrivere ulteriori classi
        log.info("------ Inserimento nuovo premio ------");
        // Utilizzo il metodo di GiftService salvando l'oggetto che ottengo dal
        // body della chiamata al web service e otterrò come return una classe di tipo Mono<Gift>
        Mono<Gift> gift = giftService.save(newGift);
        // La chiamata restituirà, nel body, il formato .json dell'articolo inserito e lo status "CREATED"
        return new ResponseEntity<Mono<Gift>>(gift, HttpStatus.CREATED);
    }

    // Metodo che restituisce tutti i premi in un Flux<Gift>.
    // Specifico quindi la notazione (non più @PostMapping, ma @RequestMapping: notazione generica
    // per specificare endpoint, metodo e ciò che il metodo restituisce - quando non si specifica
    // @GetMapping | @PostMapping | @PutMapping va infatti specificato anche il metodo):
    @RequestMapping(value = "/find/all", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Flux<Gift>> getAllGifts() {
        // Inserisco log di riferimento (Lombok)
        log.info("------ Ottengo tutti i premi ------");
        // Uso il metodo SelectAll() da GiftService.
        Flux<Gift> gifts = giftService.findAll()
                // Gestisco anche un'eventuale eccezione di collection vuota - switchIfEmpty(...) -
                // che ritorna un Mono con uno status HTTP NOT FOUND e un messaggio
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Lista premi vuota.")));
        // Se il metodo invece ci restituisce qualcosa
        return new ResponseEntity<Flux<Gift>>(gifts, HttpStatus.OK);
    }

    // Ricerca per codice prodotto
    // Uso un Mono perché il codice è univoco
    @RequestMapping(value = "/find/code/{codice}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Flux<Gift>> findGiftByCodice(@PathVariable("codice") String codice) {
        // Inserisco log di riferimento (Lombok)
        log.info("------ Cerco il premio con codice " + codice + " ------");
        // Uso il metodo FindByCodice(String codice) da GiftService.
        Flux<Gift> gift = giftService.findByCodiceLike(codice)
                // Gestisco anche un'eventuale eccezione di collection vuota - switchIfEmpty(...) -
                // che ritorna un Mono con uno status HTTP NOT FOUND e un messaggio
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Premi non trovati!")));
        // Se il metodo invece ci restituisce qualcosa
        return new ResponseEntity<Flux<Gift>>(gift, HttpStatus.OK);
    }

    // Ricerca per nome prodotto
    @GetMapping(value = "/find/name/{nome}", produces = "application/json")
    public ResponseEntity<Flux<Gift>> findGiftByNome(@PathVariable("nome") String nome) {
        log.info("------ Cerco i premi con nome " + nome + " ------");
        Flux<Gift> gift = giftService.findByNomeLike(nome)
                // Nel caso di Flux vuoto
                .switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Premi non trovati.")));
        // Se il metodo invece ci restituisce qualcosa
        return new ResponseEntity<Flux<Gift>>(gift, HttpStatus.OK);
    }

    // Ricerca per massimo prezzo
    @GetMapping(value = "/find/price/{prezzo}", produces = "application/json")
    public ResponseEntity<Flux<Gift>> findGiftByPrezzo(@PathVariable("prezzo") int prezzo) {
        log.info("------ Cerco i premi con prezzo massimo di " + prezzo + " punti ------");
        Flux<Gift> gift = giftService.findByPrezzo(prezzo)
                // Nel caso di Flux vuoto
                .switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Premi non trovati.")));
        // Se il metodo invece ci restituisce qualcosa
        return new ResponseEntity<Flux<Gift>>(gift, HttpStatus.OK);
    }

    // Come notazione utilizzo DeleteMapping, che identifica direttamente un metodo HTTP_DELETE
    // con valore dell'endpoint seguito dallo specifico id e come riferimento alla variabile l'id
    @DeleteMapping(value = "/delete/id/{id}", produces = "application/json")
    // Il metodo prevede un parametro che sarà l'id univoco d'identificazione del record su MongoDB
    public ResponseEntity<Mono<Void>> deleteGift(@PathVariable("id") String id) {
        // Log Lombok
        log.info("------ Elimino premio ------");
        // Restituisco un ResponseEntity di tipo Mono<Void> dove utilizzo il metodo "Delete" di
        // GiftService e restituisco un HTTP_STATUS_OK
        return new ResponseEntity<Mono<Void>>(giftService.delete(id), HttpStatus.OK);
    }
}