package model;

import java.util.concurrent.Semaphore;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import util.Contador;

public class Leitor extends Ator {
  private final Semaphore mutex;
  private final Semaphore db;
  private final Contador rc;

  private final Image student_static = new Image(getClass().getResourceAsStream("/assets/student_static.png")); // Pega a imagem do botao verde
  private final Image student_thinking = new Image(getClass().getResourceAsStream("/assets/student_thinking.gif")); // Pega a imagem do botao verde
  private final Image student_idle = new Image(getClass().getResourceAsStream("/assets/student_idle.gif")); // Pega a imagem do botao cinza
  private final Image student_reading = new Image(getClass().getResourceAsStream("/assets/student_reading.gif")); // Pega a imagem do botao cinza

  public Leitor(int id, Semaphore mutex, Semaphore db, Contador rc, Label statusLabel, ImageView sprite, ImageView iconImage, Slider procurarSlider, Slider lerSlider) {
    super(id, statusLabel, sprite, iconImage, procurarSlider, lerSlider);
    this.mutex = mutex;
    this.db = db;
    this.rc = rc;
  }

  @Override
  public void run() {
    try {
      setStatus("INICIANDO...", student_static);
      Thread.sleep(4000);
      while (executando) {
        setStatus(student_idle);

        mutex.acquire();
        rc.incrementar();
        if (rc.getValor() == 1) {
          db.acquire(); // Bloqueia escritores se for o primeiro
        }
        mutex.release();

        setStatus("PROCURANDO...", student_thinking);
        Thread.sleep( (long) (1000 * slider2.getValue())); // Tempo de leitura

        mutex.acquire();
        rc.decrementar();
        if (rc.getValor() == 0) {
          db.release(); // Libera escritores se for o último
        }
        mutex.release();
        
        setStatus("LENDO LIVRO...", student_reading);
        Thread.sleep( (long) (1000 * slider1.getValue())); // Tempo de uso
      }
  } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
  }
}
}