package com.cwgio97.webservice.service;

import com.cwgio97.webservice.model.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// Il code injection dello strato di servizio si basa sull'utilizzo delle interfacce...
public interface CustomersService {
    // Seleziona tutti gli elementi: utilizzo il Flux nella SelectAll() perché quando
    // si usano tecniche reactive non si parla più di "collezioni" ma di flussi (--> Flux) di "customer".
    public Flux<Customer> findAll();
    // Nelle tecniche reactive viene restituito, al momento del salvataggio,
    // un altro elemento: il "Mono": rappresenta UNA sola classe cliente;
    // --> FLUX quando ne abbiamo più di una, MONO quando è una soltanto.
    public Mono<Customer> save(Customer customer);
    public Mono<Void> delete(String id);
    // Ricerche personalizzate
    public Flux<Customer> findByCodiceLike(String codice);
    public Flux<Customer> findByNomeLike(String nome);
    public Flux<Customer> findByPunti(int punti);
}
