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
      Thread.sleep(3000);
      while (executando) {

        setStatus(student_idle);
        slicedSleep(2000, student_static, "", student_idle);

        // --- SEMAFORO ---
        mutex.acquire();
        rc.incrementar();
        if (rc.getValor() == 1) {
          db.acquire(); // Bloqueia escritores se for o primeiro
        }
        mutex.release();

        // --- ATIVIDADE: leBaseDeDados() ---
        slicedSleep((long) (1000 * slider2.getValue()), student_static, "PROCURANDO...", student_thinking); // Tempo de leitura

        // --- SEMAFORO ---
        mutex.acquire();
        rc.decrementar();
        if (rc.getValor() == 0) {
          db.release(); // Libera escritores se for o último
        }
        mutex.release();

        // ATIVIDADE: utilizaDadoLido() ---
        slicedSleep( (long) (1000 * slider1.getValue()), student_static, "LENDO LIVRO...", student_reading); // Tempo de uso
      }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
  }
}