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

    Image student_static = new Image(getClass().getResourceAsStream("/assets/student_static.png")); // Pega a imagem do botao verde
    Image student_thinking = new Image(getClass().getResourceAsStream("/assets/student_thinking.gif")); // Pega a imagem do botao verde
    Image student_idle = new Image(getClass().getResourceAsStream("/assets/student_idle.gif")); // Pega a imagem do botao cinza
    Image student_reading = new Image(getClass().getResourceAsStream("/assets/student_reading.gif")); // Pega a imagem do botao cinza

    public Leitor(int id, Semaphore mutex, Semaphore db, Contador rc, Label statusLabel, ImageView sprite, Slider procurarSlider, Slider lerSlider) {
        super(id, statusLabel, sprite, procurarSlider, lerSlider);
        this.mutex = mutex;
        this.db = db;
        this.rc = rc;
    }

    @Override
    public void run() {
        try {
            setStatus("Iniciando...", student_static);
            Thread.sleep(2000);
            while (executando) {
                setStatus("Aguardando para ler...", student_idle);
                Thread.sleep(3000);

                mutex.acquire();
                rc.incrementar();
                if (rc.getValor() == 1) {
                    db.acquire(); // Bloqueia escritores se for o primeiro
                }
                mutex.release();

                setStatus("Procurando livro...", student_thinking);
                Thread.sleep(3000); // Tempo de leitura

                mutex.acquire();
                rc.decrementar();
                if (rc.getValor() == 0) {
                    db.release(); // Libera escritores se for o último
                }
                mutex.release();

                setStatus("Lendo livro...", student_reading);
                Thread.sleep(5000); // Tempo de uso
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}