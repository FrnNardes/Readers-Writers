/* ***************************************************************
* Autor............: Fernando Nardes Ferreira Neto
* Matricula........: 202410403
* Inicio...........: 09/06/2025
* Ultima alteracao.: 16/06/2025
* Nome.............: Leitor.java
* Funcao...........: Implementa o comportamento de um leitor na simulacao 
*                    do problema dos leitores e escritores. Gerencia acesso 
*                    concorrente à base de dados usando semáforos e controle 
*                    de leitura.
*************************************************************** */

package model;

import java.util.concurrent.Semaphore;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import util.Contador;

public class Leitor extends Ator {
  private final Semaphore mutex; // Controla o acesso à variável de leitura compartilhada
  private final Semaphore db; // Controla o acesso à base de dados
  private final Contador rc; // Contador de leitores ativos

  // Imagens representativas do estado do leitor
  private final Image student_static = new Image(getClass().getResourceAsStream("/assets/student_static.png"));
  private final Image student_thinking = new Image(getClass().getResourceAsStream("/assets/student_thinking.gif"));
  private final Image student_idle = new Image(getClass().getResourceAsStream("/assets/student_idle.gif"));
  private final Image student_reading = new Image(getClass().getResourceAsStream("/assets/student_reading.gif"));

  /* ***************************************************************
  * Construtor: Leitor
  * Funcao: Inicializa um leitor com controle de semáforos, contador,
  *         componentes visuais e sliders de tempo.
  * Parametros: id - identificador do leitor
  *             mutex - semáforo para exclusão mútua do contador
  *             db - semáforo de controle de acesso ao banco
  *             rc - contador de leitores
  *             statusLabel - componente visual de status
  *             sprite - imagem principal do ator
  *             iconImage - ícone indicativo de status
  *             procurarSlider - controla tempo de busca
  *             lerSlider - controla tempo de leitura
  *************************************************************** */
  public Leitor(int id, Semaphore mutex, Semaphore db, Contador rc,
                Label statusLabel, ImageView sprite, ImageView iconImage,
                Slider procurarSlider, Slider lerSlider) {
    super(id, statusLabel, sprite, iconImage, procurarSlider, lerSlider);
    this.mutex = mutex;
    this.db = db;
    this.rc = rc;
  }

  /* ***************************************************************
  * Metodo: run
  * Funcao: Executa o ciclo de vida do leitor: espera, entra na leitura,
  *         sai da leitura e utiliza os dados lidos. Utiliza semáforos
  *         para garantir a consistência e segurança no acesso ao recurso.
  * Parametros: nenhum
  * Retorno: void
  *************************************************************** */
  @Override
  public void run() {
    try {
      setStatus("INICIANDO...", student_static); // Status inicial
      Thread.sleep(3000); // Tempo inicial de setup

      while (executando) {
        // Estado ocioso antes de tentar ler
        setStatus(student_idle);
        slicedSleep(2000, student_static, "", student_idle);

        // Entrando na seção crítica de leitura
        mutex.acquire(); // Bloqueia o acesso ao contador
        rc.incrementar(); // Incrementa número de leitores ativos
        if (rc.getValor() == 1) {
          db.acquire(); // Primeiro leitor bloqueia os escritores
        }
        mutex.release(); // Libera o acesso ao contador

        // Acesso à base de dados (leitura)
        slicedSleep((long) (1000 * slider1.getValue()), student_static, "PROCURANDO...", student_thinking);

        // Saindo da seção crítica
        mutex.acquire(); // Bloqueia novamente para decrementar
        rc.decrementar(); // Diminui o número de leitores ativos
        if (rc.getValor() == 0) {
          db.release(); // Último leitor libera os escritores
        }
        mutex.release(); // Libera o acesso ao contador

        // Utilização do dado lido (simboliza leitura do livro)
        slicedSleep((long) (1000 * slider2.getValue()), student_static, "LENDO LIVRO...", student_reading);
      }

    } catch (InterruptedException e) {
      Thread.currentThread().interrupt(); // Respeita a interrupção da thread
    }
  } // Fim do metodo run
} // Fim da classe Leitor
