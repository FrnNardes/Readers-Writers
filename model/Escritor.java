/* ***************************************************************
* Autor............: Fernando Nardes Ferreira Neto
* Matricula........: 202410403
* Inicio...........: 09/06/2025
* Ultima alteracao.: 16/06/2025
* Nome.............: Escritor.java
* Funcao...........: Representa um escritor na simulacao do problema dos
*                    leitores e escritores. Usa semáforo para controlar
*                    acesso exclusivo à base de dados.
*************************************************************** */

package model;

import java.util.concurrent.Semaphore;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Escritor extends Ator {
  private final Semaphore db; // Semáforo para controle exclusivo da base de dados

  // Imagens que representam os estados do escritor
  private final Image scientist_static = new Image(getClass().getResourceAsStream("/assets/scientist_static.png")); // Cientista parado (estado inicial)
  private final Image scientist_idle = new Image(getClass().getResourceAsStream("/assets/scientist_idle.gif")); // Cientista aguardando
  private final Image scientist_searching = new Image(getClass().getResourceAsStream("/assets/scientist_searching.gif")); // Cientista pesquisando
  private final Image scientist_taking_notes = new Image(getClass().getResourceAsStream("/assets/scientist_taking_notes.gif")); // Cientista publicando

  /* ***************************************************************
  * Construtor: Escritor
  * Funcao: Inicializa um escritor com os componentes gráficos e controle
  *         de concorrência necessário para simulação.
  * Parametros: id - identificador do escritor
  *             db - semáforo para controle do banco
  *             statusLabel - componente visual de status
  *             sprite - imagem principal do ator
  *             iconImage - ícone indicativo de status
  *             pesquisarSlider - controla tempo de pesquisa
  *             publicarSlider - controla tempo de escrita/publicação
  *************************************************************** */
  public Escritor(int id, Semaphore db, Label statusLabel,
                  ImageView sprite, ImageView iconImage,
                  Slider pesquisarSlider, Slider publicarSlider) {
    super(id, statusLabel, sprite, iconImage, pesquisarSlider, publicarSlider);
    this.db = db;
  }

  /* ***************************************************************
  * Metodo: run
  * Funcao: Executa o ciclo de vida do escritor: pesquisa dados, adquire 
  *         acesso exclusivo, escreve na base e repete enquanto estiver ativo.
  * Parametros: nenhum
  * Retorno: void
  *************************************************************** */
  @Override
  public void run() {
    try {
      setStatus("INICIANDO...", scientist_static); // Status inicial
      Thread.sleep(3000); // Tempo de setup inicial

      while (executando) {
        // Atividade de pesquisa antes de tentar escrever
        slicedSleep((long) (1000 * slider1.getValue()), scientist_static, "PESQUISANDO...", scientist_searching);

        // Estado ocioso aguardando acesso ao banco
        setStatus(scientist_idle);

        // Seção crítica protegida por semáforo (escrita)
        db.acquire(); // Obtem acesso exclusivo à base de dados

        // Escrita na base de dados (atividade de publicação)
        slicedSleep((long) (1000 * slider2.getValue()), scientist_static, "PUBLICANDO...", scientist_taking_notes);

        db.release(); // Libera a base de dados para outros escritores ou leitores
      }

    } catch (InterruptedException e) {
      Thread.currentThread().interrupt(); // Respeita a interrupção da thread
    }
  } // Fim do método run
} // Fim da classe Escritor
