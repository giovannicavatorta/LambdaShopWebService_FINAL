package com.cwgio97.webservice.service;

import com.cwgio97.webservice.model.Gift;
import com.cwgio97.webservice.repository.GiftRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

// Implementazione dell'interfaccia GiftService.

// Si deve effettuare code injection dello strato di persistenza all'interno dello strato di servizio,
// infatti la classe Controller non deve mai poter accedere direttamente allo strato di persistenza.
// Questo perché è lo strato di servizio a gestire le business logic: ricavo i dati grezzi
// dal database, li sottopongo, eventualmente, a modifiche nello strato di servizio e poi
// li restituisco alla classe Controller. Questo è lo schema di funzionamento di qualsiasi app
// che si basi sul paradigma MVC, e ciò vale anche per le webAPI.

@Service // A indicare che questo è lo strato di servizio
@Slf4j // Lombok per log
public class GiftServiceImplementation implements GiftService {

    // Code injection, come visto sopra:
    @Autowired
    GiftRepository giftRepository;

    // Implementazione dei metodi dello strato di persistenza
    @Override
    public Flux<Gift> findAll() {
        // findAll() restituisce un Flux con tutti gli elementi nel database
        return giftRepository.findAll();
    }

    @Override
    public Mono<Gift> save(Gift gift) {
        // insert(<Entity>) per il solo inserimento
        // In questo caso uso save(<Entity>) per avere anche la modifica
        return giftRepository.save(gift);
    }

    @Override
    public Mono<Void> delete(String id) {
        // deleteById(String id)
        return giftRepository.deleteById(id);
    }

    @Override
    public Flux<Gift> findByCodiceLike(String codice) {
        return giftRepository.findByCodiceLike(codice);
    }

    @Override
    public Flux<Gift> findByNomeLike(String nome) {
        return giftRepository.findByNomeLike(nome);
    }

    @Override
    public Flux<Gift> findByPrezzo(int prezzo) {
        return giftRepository.findByPrezzo(prezzo);
    }
}