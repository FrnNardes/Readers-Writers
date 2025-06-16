package model;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextAlignment;

// Classe base para os participantes da simulação (Leitores e Escritores).
public abstract class Ator extends Thread {
  protected final int id;
  private final Label statusLabel;
  private final ImageView iconImage;
  private final ImageView sprite;
  protected final Slider slider1;
  protected final Slider slider2;

  // Flag de controle de execucao
  protected volatile boolean executando = true;
  protected volatile boolean pausado = false;

  public Ator(int id, Label statusLabel, ImageView sprite, ImageView iconImage, Slider slider1, Slider slider2) {
    this.id = id;
    this.statusLabel = statusLabel;
    this.iconImage = iconImage;
    this.sprite = sprite;
    this.slider1 = slider1;
    this.slider2 = slider2;
    this.setDaemon(true); // Garante que a thread não impeça o programa de fechar
  }

  public void parar() {
    this.executando = false;
    this.pausado = false;
    this.slider1.setValue(5);
    this.slider2.setValue(5);
    this.interrupt(); // Interromper a thread caso ela esteja dormindo (em Thread.sleep)
  }

  /**
   * Método seguro para as classes filhas atualizarem o texto do Label na UI.
   * O Platform.runLater continua sendo essencial aqui.
   */
  protected void setStatus(String mensagem, Image image) {
    Platform.runLater(() -> {
      iconImage.setVisible(false);
      statusLabel.setVisible(true);
      statusLabel.setText(mensagem);
      sprite.setImage(image);
      statusLabel.setTextAlignment(TextAlignment.CENTER);
    });
  }

  protected void setStatus(Image image) {
    Platform.runLater(() -> {
      iconImage.setVisible(true);;
      sprite.setImage(image);
      statusLabel.setVisible(false);
    });
  }

  protected ImageView getSprite(){
    return sprite;
  }

  // O método de verificação agora implementa o busy-wait.
  protected void verificarPausa(Image image) {
    while (isPaused()) {
      try {
          Thread.sleep(50); // Um pequeno sleep para "aliviar" o processador.
          setStatus("PAUSADO", image);
          if (!executando) {
            break;
          }
      } catch (InterruptedException e) {
          // A interrupção pode quebrar o loop de pausa se o reset for chamado.
          this.executando = false; 
      }
    }
  }

  protected void slicedSleep(long duracaoTotalMs, Image image, String status, Image imageStatus) throws InterruptedException {
    long tempoDormido = 0; // Acumulador de tempo efetivamente dormido/trabalhado
    long FATIA_DE_TEMPO = 50; // Verificamos a pausa a cada 50ms

    while (executando && tempoDormido < duracaoTotalMs) {
        // 1. Verifica se precisa pausar ANTES de fazer qualquer trabalho.
        // Se pausado, a thread ficará presa aqui dentro até ser retomada.
        verificarPausa(image);
        
        if(!isPaused()){
          setStatus(status, imageStatus);
          if(status.equals("")){
            setStatus(imageStatus);
          }
        }

        // 2. Trabalha/Dorme por um pequeno pedaço de tempo.
        Thread.sleep(FATIA_DE_TEMPO);

        // 3. Só então, adiciona o tempo trabalhado ao acumulador.
        // Se a thread estava pausada, este código não é alcançado e o tempo não é somado.
        tempoDormido += FATIA_DE_TEMPO;
    }
  }

  public void alternarPausa() {
    this.pausado = !this.pausado;
  }

  public boolean isPaused(){
    return pausado;
  }

  // Sobrescrevemos o método run para adicionar a verificação da flag
  @Override
  public abstract void run();
}