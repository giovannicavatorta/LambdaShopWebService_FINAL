package com.cwgio97.webservice.service;

import com.cwgio97.webservice.model.Customer;
import com.cwgio97.webservice.repository.CustomersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

// Implementazione dell'interfaccia CustomersService.

// Si deve effettuare code injection dello strato di persistenza all'interno dello strato di servizio,
// infatti la classe Controller non deve mai poter accedere direttamente allo strato di persistenza.
// Questo perché è lo strato di servizio a gestire le business logic: ricavo i dati grezzi
// dal database, li sottopongo, eventualmente, a modifiche nello strato di servizio e poi
// li restituisco alla classe Controller. Questo è lo schema di funzionamento di qualsiasi app
// che si basi sul paradigma MVC, e ciò vale anche per le webAPI.

@Service // A indicare che questo è lo strato di servizio
@Slf4j // Lombok per log
public class CustomersServiceImplementation implements CustomersService {

    // Code injection, come visto sopra:
    @Autowired
    CustomersRepository customersRepository;

    // Implementazione dei metodi dello strato di persistenza
    @Override
    public Flux<Customer> findAll() {
        // findAll() restituisce un Flux con tutti gli elementi nel database
        return customersRepository.findAll();
    }

    @Override
    public Mono<Customer> save(Customer customer) {
        // insert(<Entity>) per il solo inserimento
        // In questo caso uso save(<Entity>) per avere anche la modifica
        return customersRepository.save(customer);
    }

    @Override
    public Mono<Void> delete(String id) {
        // deleteById(String id)
        return customersRepository.deleteById(id);
    }

    @Override
    public Flux<Customer> findByCodiceLike(String codice) {
        return customersRepository.findByCodiceLike(codice);
    }

    @Override
    public Flux<Customer> findByNomeLike(String nome) {
        return customersRepository.findByNomeLike(nome);
    }

    @Override
    public Flux<Customer> findByPunti(int punti) {
        return customersRepository.findByPunti(punti);
    }
}