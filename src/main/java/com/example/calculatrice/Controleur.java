package com.example.calculatrice;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Le controleur doit assurer la liaison entre l'accumulateur qui est la memoire de la pile et de l'interface graphique GUI qui affiche le contenu de la pile
 */
public class Controleur implements PropertyChangeListener, EventHandler<MouseEvent> {//implémentation des interfaces PropertyChangeListener et EventHandler

    Accumulateur accumulateur = new Accumulateur(new Pile());
    public final GUI GUI;
    private boolean historique_resultat = false;
    PropertyChangeSupport support = new PropertyChangeSupport(this);

    /**
     * Constructeur controleur avec la structure MVC
     * @param GUI permet de faire des manipulations a l'interface graphique telles que le changement d'affichage
     *                           du resultat etc...
     */
    public Controleur(GUI GUI) {
        this.GUI = GUI;
        support.addPropertyChangeListener(this);
        //Ajout du listener controleur dans accumulateur
        accumulateur.addPropertyChangeListener(this);
    }

    /**
     * Detecte tous evenements lies au changement de l'etat de la pile
      * @param evt A PropertyChangeEvent object describing the event source
     *          and the property that has changed.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //Traitement des différents cas : opérations ou opérandes
        switch(evt.getPropertyName()){
            case "+" : case "-" : case "x" :
            case "/" :{
                double dernier_nombre = accumulateur.pile.peek();

                //if/else : on traite les zéros inutiles après la virgule si on a un nombre de type double
                if(dernier_nombre%1 == 0){
                    GUI.resultat = String.valueOf((int) dernier_nombre);}
                else{
                    GUI.resultat = String.valueOf(dernier_nombre);
                }
                //Affichage du résultat après opération
                GUI.updateAffichageResultat();

                //Mise à jour du message/commentaires
                if(evt.getPropertyName().equals("+")){
                    GUI.message = "Opération addition effectuée";
                }
                else if(evt.getPropertyName().equals("-")){
                    GUI.message = "Opération soustraction effectuée";
                }
                else if(evt.getPropertyName().equals("x")){
                    GUI.message = "Opération multiplication effectuée";
                }
                else if(evt.getPropertyName().equals("/")){
                    GUI.message = "Opération division effectuée";
                }
                GUI.updateAffichageMessage();

                historique_resultat = true;

                //Mise à jour de l'affichage de la pile
                GUI.affichagePile.setText(String.valueOf(accumulateur.pile));
                break;
            }
            case "pushNombre" : {
                //Affichage du nombre 0
                GUI.updateHistorique();
                GUI.resultat = "0";
                GUI.updateAffichageResultat();

                //Efface le label message/commentaires dès qu'on appuie sur un chiffre
                if(!GUI.message.equals("")){
                    GUI.message = "";
                    GUI.updateAffichageMessage();
                }

                //Mise à jour de l'affichage de la pile
                GUI.affichagePile.setText(String.valueOf(accumulateur.pile));
                break;
            }
            case "Clear" : {
                //Réinitialisation du résultat
                GUI.resultat = "0";
                GUI.updateAffichageResultat();

                //Commentaires
                GUI.message = "Effacement de la mémoire terminé";
                GUI.updateAffichageMessage();

                //On efface l'historique
                GUI.resetHistorique();

                //Mise à jour de l'affichage de la pile
                GUI.affichagePile.setText("");
                break;
            }
        }
    }

    /**
     * Detecte tous les evenements lies a la souris
     * @param mouseEvent parametre qui contient toutes les informations de la souris
     */
    @Override
    public void handle(MouseEvent mouseEvent) {

        //Récupération du texte du bouton qu'on a appuyé
        Button button = (Button) mouseEvent.getSource();
        String k = button.getText();

        //On distingue les evenement de la souris en fonction de chaque bouton appuyé
        if(mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED){
            switch (k) {
                case "0" : update("0"); break;
                case "1" : update("1"); break;
                case "2" : update("2"); break;
                case "3" : update("3"); break;
                case "4" : update("4"); break;
                case "5" : update("5"); break;
                case "6" : update("6"); break;
                case "7" : update("7"); break;
                case "8" : update("8"); break;
                case "9" : update("9"); break;

                case "+" : case "-" : case "x" :
                case "/" : operation(k); break;

                case "=" : push(); break;

                //Effacement de la pile
                case "C" : accumulateur.clear(); break;

                case "," : virgule(); break;
                case "±" : negatif(); break;
                case "%" : pourcentage(); break;
                case "⌫" : supprimer(); break;
            }
        }
        mouseEvent.consume();
    }

    /**
     * Met à jour le resultat
     * @param nombre on entre le chiffre qu'on veut ajouter au resultat
     */
    public void update(String nombre){
        //Mise à jour de l'affichage du résultat dans l'historique
        if(historique_resultat){
            GUI.updateHistorique();

            //Ecrase la donnée lorsqu'on entre un nouveau chiffre
            GUI.resultat = "0";

            //Ne pas mettre à jour l'historique lorsqu'on tape sur les chiffres sans les avoir push
            historique_resultat = false;
        }

        //Mettre à jour l'affichage du nombre sur la calculatrice
        //si resultat est égal à "0" ou "-0" et ne contient pas de virgule ou si est égal à Error, on écrase la valeur de résulat
        if(((GUI.resultat.equals("0") || GUI.resultat.equals("-0")) && !nombre.equals(".")
                || GUI.resultat.equals("Error"))){
            GUI.resultat = nombre;
        }
        else{
            //Limite de 9 chiffres en tout
            if(GUI.resultat.length() >= 9){
                GUI.message = "Taille maximale atteinte !";
                GUI.updateAffichageMessage();
            }
            else{
                //Permet d'entrer un nombre
                GUI.resultat = GUI.resultat + nombre;

                //Affichage aisée du nombre
                /*System.out.println(interfaceGraphique.resultat);
                interfaceGraphique.resultat = interfaceGraphique.resultat.replaceAll(" ", "");
                int m = 0;
                for(int i = 1; i <= interfaceGraphique.resultat.length()-2; i++){
                    if(i % 3 == 0){

                        System.out.println(interfaceGraphique.resultat.substring(0, interfaceGraphique.resultat.length() -i));
                        System.out.println(interfaceGraphique.resultat.substring(interfaceGraphique.resultat.length()-i));
                        interfaceGraphique.resultat = interfaceGraphique.resultat.substring(0, interfaceGraphique.resultat.length() -i-m) + " " + interfaceGraphique.resultat.substring(interfaceGraphique.resultat.length()-i-m);
                    }
                }*/
            }
        }

        //Mettre à jour l'affichage du nombre sur la calculatrice
        GUI.updateAffichageResultat();

        //Efface le contenu du message et n'efface pas le contenu du message lorsqu'on atteint la taille limite de la pile
        if(!GUI.message.equals("") && GUI.resultat.length() < 9){
            GUI.message = "";
            GUI.updateAffichageMessage();
        }
    }

    /**
     * Methode push en tenant compte des erreurs qui peuvent apparaitre lorsque ce dernier est sollicite
     */
    public void push(){
        //Gestion d'erreur : si on essaye de push le string "Error"
        if(GUI.resultat.equals("Error")){
            GUI.message = "Veuillez sélectionner un chiffre";
            GUI.updateAffichageMessage();
        }

        //Gestion d'erreur: Impossibilité de convertir une chaine de caractère avec la présence d'espace en un double
        else if(GUI.resultat.contains(" ")){

            //Suppression de l'espace lors du stockage du nombre dans la pile
            GUI.resultat = GUI.resultat.replaceAll(" ", "");
            accumulateur.push(Double.parseDouble(GUI.resultat),"pushNombre");
        }

        //Gestion d'erreur: Si on essaye de stocker seulement la virgule dans la pile
        else if(GUI.resultat.equals(".")){
            GUI.resultat = "0";
            accumulateur.push(0, "pushNombre");
        }

        //Stocker "-0" ou "-0." ou "-0.0" reviennent à stocker "0" dans la pile
        else if(GUI.resultat.equals("-0") || GUI.resultat.equals("-0.")|| GUI.resultat.equals("-0.0")){
            GUI.resultat = "0";
            accumulateur.push(0, "pushNombre");
        }

        else{
            //Si pas d'erreur, on stock la valeur de resultat dans la pile
            accumulateur.push(Double.parseDouble(GUI.resultat),"pushNombre");
        }
    }

    /**
     * Ajoute un signe - a un nombre lors de <b>l'affichage</b> de ce dernier
     * On ne modifie PAS la pile mais seulement l'affichage
     */
    public void negatif(){
        //Si le nombre est positif, on le rend négatif
        if(!String.valueOf(GUI.resultat.charAt(0)).equals("-") && !GUI.resultat.equals("Error")){
            GUI.resultat = "-" + GUI.resultat;
            GUI.updateAffichageResultat();
        }

        //Gestion d'erreur : On ne fait rien si on a "Error"
        else if(GUI.resultat.equals("Error")){
            assert true;
        }

        //Si resultat contient déja un -, on enlève le -
        else{
            GUI.resultat = GUI.resultat.substring(1);
            GUI.updateAffichageResultat();
        }
    }


    /**
     * Ajoute une virgule au resultat
     */
    public void virgule(){
        //On regarde si le resultat contient une virgule
        if(!GUI.resultat.contains("."))
            update(".");
    }


    /**
     * Effectue les operations et en isolant les erreurs liees aux operations
     * par exemple la division par zero
     * @param k on entre l'operateur "+" ou "-" ou "x" ou "/"
     */
    public void operation(String k){
        //Gestion d'erreur : Tant que la taille de la pile est supérieure à 2 alors on peut lui appliquer des opérations
        if(accumulateur.pile.size() >= 2){

            //Distinction des opérations
            switch (k) {
                case "+" : accumulateur.add(); break;
                case "-" : accumulateur.sub(); break;
                case "x" : accumulateur.mult(); break;
                case "/" : {
                    //Gestion d'erreur : Divison par 0
                    if (accumulateur.pile.peek() == 0) {
                        GUI.message = "Erreur division par 0 impossible";
                        GUI.updateAffichageMessage();
                        GUI.resultat = "Error";
                        GUI.updateAffichageResultat();
                    } else {
                        accumulateur.div();
                    }
                    break;
                }
            }
        }

        //Si on veut entrer une opération lorsque la taille de la pile est inférieure ou égale à 1
        else{
        GUI.message = "Veuillez sélectionner un chiffre";
        GUI.updateAffichageMessage();}
    }


    /**
     * Permet de multiplier par 0.01 le nombre
     */
    public void pourcentage(){
        //Gestion d'erreur: Si le résultat égal à "Error"
        if(!GUI.resultat.equals("Error")){
        GUI.resultat = String.valueOf(Double.parseDouble(GUI.resultat)/100);
        GUI.updateAffichageResultat();
        }
    }

    /**
     * Supprime 1 a 1 les chiffres de resultat (chiffres en chaine de caracteres)
     */
    public void supprimer(){
        //Gestion d'erreur : Si on essaie de supprimer la chaine de caractère "Error"
        if(GUI.resultat.equals("Error")){
            GUI.message = "Aucun chiffre à supprimer";
            GUI.updateAffichageMessage();
        }

        //Si resultat n'est pas égal à "0"
        else if(!GUI.resultat.equals("0")){

            //Si le résultat est un chiffre et si on essaie de le supprimer, on retourne à 0
            if(GUI.resultat.length() == 1){
                GUI.resultat = "0";
                GUI.updateAffichageResultat();
            }

            //Sinon on supprime le dernier chiffre de résultat
            else{
                GUI.resultat = GUI.resultat.substring(0, GUI.resultat.length()-1);
                GUI.updateAffichageResultat();
            }
        }

    }
}
