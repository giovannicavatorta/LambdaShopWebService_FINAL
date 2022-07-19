package com.cwgio97.webservice.service;

import com.cwgio97.webservice.model.Gift;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// Il code injection dello strato di servizio si basa sull'utilizzo delle interfacce...
public interface GiftService {
    // Seleziona tutti gli elementi: uso un Flux nella SelectAll() perché quando
    // si usano tecniche reactive non si parla più di "collezioni" ma di flussi (--> Flux) di "gift".
    public Flux<Gift> findAll();
    // Nelle tecniche reactive viene restituito, al momento del salvataggio,
    // un altro elemento: il "Mono": rappresenta UNA sola classe gift;
    // --> FLUX quando ne abbiamo più di una, MONO quando è una soltanto.
    public Mono<Gift> save(Gift gift);
    public Mono<Void> delete(String id);
    // Ricerche personalizzate
    public Flux<Gift> findByCodiceLike(String codice);
    public Flux<Gift> findByNomeLike(String nome);
    public Flux<Gift> findByPrezzo(int prezzo);
}
