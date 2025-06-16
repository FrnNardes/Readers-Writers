package model;

import java.util.concurrent.Semaphore;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Escritor extends Ator {
  private final Semaphore db;

  private final Image scientist_static = new Image(getClass().getResourceAsStream("/assets/scientist_static.png")); // Pega a imagem do cientista parado
  private final Image scientist_idle = new Image(getClass().getResourceAsStream("/assets/scientist_idle.gif")); // Pega a animacao do cientista parado
  private final Image scientist_searching = new Image(getClass().getResourceAsStream("/assets/scientist_searching.gif")); // Pega a animacao do cientista pesquisando
  private final Image scientist_taking_notes = new Image(getClass().getResourceAsStream("/assets/scientist_taking_notes.gif")); // Pega a animacao do cientista anotando

  public Escritor(int id, Semaphore db, Label statusLabel, ImageView sprite, ImageView iconImage, Slider pesquisarSlider, Slider publicarSlider, ImageView pauseButton) {
    super(id, statusLabel, sprite, iconImage, pesquisarSlider, publicarSlider, pauseButton);
    this.db = db;
  }

  @Override
  public void run() {
    try {
      setStatus("INICIANDO...", scientist_static);
      Thread.sleep(2000);
      while (executando) {
        setStatus("PESQUISANDO...", scientist_searching);
        Thread.sleep((long) (1000 * slider1.getValue())); 
        // quero que isso seja chamado apenas quando ele já tiver 
        setStatus(scientist_idle); // Bloqueia
      
        db.acquire();// Pede acesso exclusivo
        setStatus("PUBLICANDO...", scientist_taking_notes);
        Thread.sleep((long) (1000 * slider2.getValue())); // Tempo de escrita

        db.release(); // Libera acesso
      }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
  }
}