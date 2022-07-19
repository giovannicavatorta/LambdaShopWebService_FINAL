package com.cwgio97.webservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

// Classe "entity" di un database NoSQL: la notazione è "@Document"
// Avendo due collection devo specificare quella di riferimento
@Document(collection = "customer")
// Notazioni Lombok: riduce il codice, fra cui getters e setters
@Data // Crea getters e setters in automatico
@AllArgsConstructor // Crea il costruttore con tutti i parametri
@NoArgsConstructor // Crea il costruttore di base
public class Customer implements Serializable {
    // Serializable: Stream che permettono di passare oggetti interi su una "socket" in modo tale che il
    // ricevitore possa ricevere non una stringa ma un oggetto della data classe...

    // Genero il relativo ID univoco
    private static final long serialVersionUID = 4316259206272543930L;

    // Inserisco i parametri che caratterizzano la classe
    @Id // Mongo crea automaticamente un campo id univoco
    private String id;

    @Field("codice")
    private String codice;

    @Field("nome")
    private String nome;

    @Field("punti")
    private int punti;

    @Field("email")
    private String email;

    @Field("indirizzo")
    private String indirizzo;

}