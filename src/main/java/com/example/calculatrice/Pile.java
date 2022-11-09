package com.example.calculatrice;

import java.util.Stack;

/**
 * Classe Pile qui herite de Stack et ne prend comme éléments uniquement des doubles
 * l'héritage permet de récupérer toutes les méthodes de Stack
 */
public class Pile extends Stack<Double> {//héritage

    /**
     * Recupere le <b>dernier</b> element de la pile
     * @return dernier element de la pile
     */
    public double getLast(){
        return this.get(this.size()-1);
    }

}
