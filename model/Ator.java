/* ***************************************************************
* Autor............: Fernando Nardes Ferreira Neto
* Matricula........: 202410403
* Inicio...........: 09/06/2025
* Ultima alteracao.: 16/06/2025
* Nome.............: Ator.java
* Funcao...........: Classe abstrata base para os participantes da simulacao 
*                    (Leitores e Escritores), fornecendo estrutura e metodos 
*                    utilitarios comuns como controle de pausa, execucao e 
*                    atualizacao da interface.
*************************************************************** */

package model;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextAlignment;

// Classe base para os participantes da simulação (Leitores e Escritores).
public abstract class Ator extends Thread {
  protected final int id; // Identificador unico do ator
  private final Label statusLabel; // Label utilizado para exibir o status textual
  private final ImageView iconImage; // Ícone representativo do status atual
  private final ImageView sprite; // Imagem principal do ator (visual)
  protected final Slider slider1; // Slider de controle 1 (por exemplo: tempo de leitura)
  protected final Slider slider2; // Slider de controle 2 (por exemplo: tempo de escrita)

  // Flags de controle de execução da thread
  protected volatile boolean executando = true; // Controla se a thread deve continuar rodando
  protected volatile boolean pausado = false; // Controla se a thread está em pausa

  /* ***************************************************************
  * Construtor: Ator
  * Funcao: Inicializa os atributos do ator e define a thread como daemon.
  * Parametros: id - identificador unico
  *             statusLabel - Label de status textual
  *             sprite - imagem principal do ator
  *             iconImage - imagem secundaria/indicativa
  *             slider1, slider2 - sliders de controle da simulação
  *************************************************************** */
  public Ator(int id, Label statusLabel, ImageView sprite, ImageView iconImage, Slider slider1, Slider slider2) {
    this.id = id;
    this.statusLabel = statusLabel;
    this.iconImage = iconImage;
    this.sprite = sprite;
    this.slider1 = slider1;
    this.slider2 = slider2;
    this.setDaemon(true); // Garante que a thread nao bloqueie o encerramento da aplicacao
  }

  /* ***************************************************************
  * Metodo: parar
  * Funcao: Para a execucao do ator, reseta sliders e interrompe a thread.
  * Parametros: nenhum
  * Retorno: void
  *************************************************************** */
  public void parar() {
    this.executando = false;
    this.pausado = false;
    this.slider1.setValue(5);
    this.slider2.setValue(5);
    this.interrupt(); // Interrompe a thread se estiver dormindo
  }

  /* ***************************************************************
  * Metodo: setStatus
  * Funcao: Atualiza a interface graficamente com uma mensagem e imagem.
  * Parametros: mensagem - texto a ser exibido
  *             image - imagem correspondente ao status
  * Retorno: void
  *************************************************************** */
  protected void setStatus(String mensagem, Image image) {
    Platform.runLater(() -> {
      iconImage.setVisible(false);
      statusLabel.setVisible(true);
      statusLabel.setText(mensagem);
      sprite.setImage(image);
      statusLabel.setTextAlignment(TextAlignment.CENTER);
    });
  }

  /* ***************************************************************
  * Metodo: setStatus (sobrecarga)
  * Funcao: Atualiza a interface apenas com a imagem, ocultando o texto.
  * Parametros: image - imagem a ser exibida
  * Retorno: void
  *************************************************************** */
  protected void setStatus(Image image) {
    Platform.runLater(() -> {
      iconImage.setVisible(true);
      sprite.setImage(image);
      statusLabel.setVisible(false);
    });
  }

  /* ***************************************************************
  * Metodo: getSprite
  * Funcao: Retorna a ImageView (sprite) do ator.
  * Parametros: nenhum
  * Retorno: ImageView - sprite do ator
  *************************************************************** */
  protected ImageView getSprite() {
    return sprite;
  }

  /* ***************************************************************
  * Metodo: verificarPausa
  * Funcao: Mantem o ator em espera ativa (busy-wait) enquanto pausado.
  *         Exibe o status de pausa visualmente.
  * Parametros: image - imagem exibida durante a pausa
  * Retorno: void
  *************************************************************** */
  protected void verificarPausa(Image image) {
    while (isPaused()) {
      try {
        Thread.sleep(50); // Pequeno delay para reduzir carga da CPU
        setStatus("PAUSADO", image);
        if (!executando) break;
      } catch (InterruptedException e) {
        this.executando = false;
      }
    }
  }

  /* ***************************************************************
  * Metodo: slicedSleep
  * Funcao: Fragmenta o tempo de execucao em fatias para permitir
  *         verificacao constante da pausa.
  * Parametros: duracaoTotalMs - tempo total desejado em ms
  *             image - imagem exibida durante pausa
  *             status - mensagem a ser exibida
  *             imageStatus - imagem usada durante atividade
  * Retorno: void
  * Lanca: InterruptedException
  *************************************************************** */
  protected void slicedSleep(long duracaoTotalMs, Image image, String status, Image imageStatus) throws InterruptedException {
    long tempoDormido = 0;
    long FATIA_DE_TEMPO = 50;

    while (executando && tempoDormido < duracaoTotalMs) {
      verificarPausa(image);

      if (!isPaused()) {
        setStatus(status, imageStatus);
        if (status.equals("")) {
          setStatus(imageStatus);
        }
      }

      Thread.sleep(FATIA_DE_TEMPO);
      tempoDormido += FATIA_DE_TEMPO;
    }
  }

  /* ***************************************************************
  * Metodo: alternarPausa
  * Funcao: Alterna o estado de pausa do ator.
  * Parametros: nenhum
  * Retorno: void
  *************************************************************** */
  public void alternarPausa() {
    this.pausado = !this.pausado;
  }

  /* ***************************************************************
  * Metodo: isPaused
  * Funcao: Retorna o estado de pausa do ator.
  * Parametros: nenhum
  * Retorno: boolean - true se pausado, false caso contrario
  *************************************************************** */
  public boolean isPaused() {
    return pausado;
  }

  /* ***************************************************************
  * Metodo: run (abstrato)
  * Funcao: Deve ser implementado pelas subclasses para definir o
  *         comportamento da thread de cada ator.
  * Parametros: nenhum
  * Retorno: void
  *************************************************************** */
  @Override
  public abstract void run(); // Fim do metodo run
} // Fim da classe Ator
