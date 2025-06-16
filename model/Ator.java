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
  protected final ImageView pauseButton;

  // Flag de controle de execucao
  protected volatile boolean executando = true;
  protected volatile boolean pausado = false;

  public Ator(int id, Label statusLabel, ImageView sprite, ImageView iconImage, Slider slider1, Slider slider2, ImageView pauseButton) {
    this.id = id;
    this.statusLabel = statusLabel;
    this.iconImage = iconImage;
    this.sprite = sprite;
    this.slider1 = slider1;
    this.slider2 = slider2;
    this.pauseButton = pauseButton;
    this.setDaemon(true); // Garante que a thread não impeça o programa de fechar
  }

  public void parar() {
    this.executando = false;
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

  public void setPause(){
    if(pausado == false){
      pausado = true;
    } else {
      pausado = false;
    }
  }

  // Sobrescrevemos o método run para adicionar a verificação da flag
  @Override
  public abstract void run();
}