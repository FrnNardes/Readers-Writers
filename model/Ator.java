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
  private final ImageView sprite;
  private final Slider slider1;
  private final Slider slider2;

  // Flag de controle de execucao
  protected volatile boolean executando = true;

  public Ator(int id, Label statusLabel, ImageView sprite, Slider slider1, Slider slider2) {
    this.id = id;
    this.statusLabel = statusLabel;
    this.sprite = sprite;
    this.slider1 = slider1;
    this.slider2 = slider2;
    this.setDaemon(true); // Garante que a thread não impeça o programa de fechar
  }

  public void parar() {
    this.executando = false;
    this.slider1.setValue(2);
    this.slider2.setValue(2);
    this.interrupt(); // Interromper a thread caso ela esteja dormindo (em Thread.sleep)
  }

  /**
   * Método seguro para as classes filhas atualizarem o texto do Label na UI.
   * O Platform.runLater continua sendo essencial aqui.
   */
  protected void setStatus(String mensagem, Image image) {
    Platform.runLater(() -> {
      statusLabel.setText(mensagem);
      sprite.setImage(image);
      statusLabel.setTextAlignment(TextAlignment.CENTER);
    });
  }

  public ImageView getSprite(){
    return sprite;
  }

  // Sobrescrevemos o método run para adicionar a verificação da flag
    @Override
    public abstract void run();
}