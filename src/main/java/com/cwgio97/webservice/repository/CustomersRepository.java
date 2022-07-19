package com.cwgio97.webservice.repository;

import com.cwgio97.webservice.model.Customer;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// Creo lo strato di persistenza, cioè lo strato che gestisce il rapporto con il database MongoDB
// e che permette di eseguire operativamente l'inserimento e la selezione dei dati da MongoDB stesso.
// L'interfaccia estende la classe ReactiveMongoRepository, da cui eredita i metodi per eseguire
// i comandi fondamentali sul database Mongo.
// Esiste anche una ReactiveCrudRepository, ma quella in uso è ottimizzata per MongoDB.
// Nei generici bisogna inserire la classe di entity da utilizzare (Customer) e il tipo della chiave
// primaria della entity, in questo caso String.
public interface CustomersRepository extends ReactiveMongoRepository<Customer, String> {
    // Estendo le funzionalità di ReactiveMongoRepository

    // Voglio poter utilizzare il codice secondario (e l'id univoco di MongoDB)
    // per poter lavorare sui Customers. Per farlo, uso una sintassi del tipo: findBy[...]()
    public Mono<Customer> findByCodice(String codice); // Per eliminazione: singolo elemento corrispondente
    public Flux<Customer> findByCodiceLike(String codice); // Per ricerca, generico => va bene anche un Flux

    // Selezionare i clienti per nominativo, che non è un filtro preciso:
    // simile a LIKE in SQL
    public Flux<Customer> findByNomeLike(String nome);

    // Applicare un filtro sui punti: non si utilizzano più i metodi settati prima ma
    // le query native di MongoDB. Uso quindi la notazione @Query
    // con il filtro in sintassi MongoDB... "$gt" = "greater than" dove 0 indica
    // il primo parametro - il count parte da 0 - che saranno i punti specificati
    // dal filtro inserito
    @Query("{ 'punti': {$gt:?0} }")
    public Flux<Customer> findByPunti(int punti);
}