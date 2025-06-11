package model;

import java.util.concurrent.Semaphore;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Escritor extends Ator {
    private final Semaphore db;

    Image scientist_static = new Image(getClass().getResourceAsStream("/assets/scientist_static.png")); // Pega a imagem do botao verde
    Image scientist_idle = new Image(getClass().getResourceAsStream("/assets/scientist_idle.gif")); // Pega a imagem do botao cinza
    Image scientist_taking_notes = new Image(getClass().getResourceAsStream("/assets/scientist_taking_notes.gif")); // Pega a imagem do botao cinza

    public Escritor(int id, Semaphore db, Label statusLabel, ImageView sprite, Slider pesquisarSlider, Slider publicarSlider) {
        super(id, statusLabel, sprite, pesquisarSlider, publicarSlider);
        this.db = db;
    }

    @Override
    public void run() {
        try {
          setStatus("Iniciando...", scientist_static);
          Thread.sleep(1000);
            while (executando) {
              setStatus("Pesquisando...", scientist_taking_notes);
              Thread.sleep(2000); 
              // quero que isso seja chamado apenas quando ele já tiver 
              setStatus("Bloqueado...", scientist_idle);
            
              db.acquire();// Pede acesso exclusivo
              setStatus("Publicando Livro...", scientist_taking_notes);
              Thread.sleep(3000); // Tempo de escrita

              db.release(); // Libera acesso
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}