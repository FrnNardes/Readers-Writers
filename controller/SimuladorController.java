package controller;

import util.Contador;
import model.Leitor;
import model.Escritor;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;

public class SimuladorController {

  // Injeção dos componentes da View
  @FXML private Label leitor1Status, leitor2Status, leitor3Status, leitor4Status, leitor5Status;
  @FXML private Label escritor1Status, escritor2Status, escritor3Status, escritor4Status, escritor5Status;
  @FXML private ImageView student1, student2, student3, student4, student5;
  @FXML private ImageView scientist1, scientist2, scientist3, scientist4, scientist5;
  @FXML private ImageView botaoReset, botaoFechar;
  @FXML private Slider procurarSlider1, procurarSlider2, procurarSlider3, procurarSlider4, procurarSlider5;
  @FXML private Slider lerSlider1, lerSlider2, lerSlider3, lerSlider4, lerSlider5;
  @FXML private Slider pesquisarSlider1, pesquisarSlider2, pesquisarSlider3, pesquisarSlider4, pesquisarSlider5;
  @FXML private Slider publicarSlider1, publicarSlider2, publicarSlider3, publicarSlider4, publicarSlider5;

  // Campos para gerenciar a simulação
  private List<Leitor> leitoresAtivos = new ArrayList<>();
  private List<Escritor> escritoresAtivos = new ArrayList<>();

  private void iniciarSimulacao(){
    // 1. Cria a lógica e os dados do Model
    Semaphore mutex = new Semaphore(1);
    Semaphore db = new Semaphore(1);
    Contador rc = new Contador();

    // 2. Cria as instâncias dos Atores (Model)
    List<Leitor> leitores = Arrays.asList(
      new Leitor(1, mutex, db, rc, leitor1Status, student1, procurarSlider1, lerSlider1),
      new Leitor(2, mutex, db, rc, leitor2Status, student2, procurarSlider2, lerSlider2),
      new Leitor(3, mutex, db, rc, leitor3Status, student3, procurarSlider3, lerSlider3),
      new Leitor(4, mutex, db, rc, leitor4Status, student4, procurarSlider4, lerSlider4),
      new Leitor(5, mutex, db, rc, leitor5Status, student5, procurarSlider5, lerSlider5)
    );

    List<Escritor> escritores = Arrays.asList(
      new Escritor(1, db, escritor1Status, scientist1, pesquisarSlider1, publicarSlider1),
      new Escritor(2, db, escritor2Status, scientist2, pesquisarSlider2, publicarSlider2),
      new Escritor(3, db, escritor3Status, scientist3, pesquisarSlider3, publicarSlider3),
      new Escritor(4, db, escritor4Status, scientist4, pesquisarSlider4, publicarSlider4),
      new Escritor(5, db, escritor5Status, scientist5, pesquisarSlider5, publicarSlider5)
    );

    // 3. Salvando os leitores e escritores da execucao atual
    for(Leitor leitor : leitores){
      leitoresAtivos.add(leitor);
    }

    for(Escritor escritor : escritores){
      escritoresAtivos.add(escritor);
    }

    // 4. Inicia a simulação
    for (Leitor leitor : leitores) {
      aplicarAnimacaoBotao(leitor.getSprite());
      leitor.start();
    }
    for (Escritor escritor : escritores) {
      aplicarAnimacaoBotao(escritor.getSprite());
      escritor.start();
    }
  }

  private void pararSimulacao(){
    for(Leitor leitor : leitoresAtivos){
      leitor.parar();
    }
    for(Escritor escritor : escritoresAtivos){
      escritor.parar();
    }
  }

  public void aplicarAnimacaoBotao(ImageView... ImageViews) {
    ColorAdjust efeitoHover = new ColorAdjust();
    ColorAdjust efeitoPressionado = new ColorAdjust();

    efeitoHover.setBrightness(0.4); // Clareia a imagem ao passar o mouse
    efeitoPressionado.setBrightness(-0.4); // Escurece a imagem ao pressionar

    for(ImageView imageView : ImageViews){
      imageView.setOnMouseEntered(e -> imageView.setEffect(efeitoHover)); // Evento de hover (mouse entra na imagem
      imageView.setOnMouseExited(e -> imageView.setEffect(null)); // Evento de saída do hover (mouse sai da imagem)
      imageView.setOnMousePressed(e -> imageView.setEffect(efeitoPressionado)); // Evento de clique pressionado
      imageView.setOnMouseReleased(e -> imageView.setEffect(efeitoHover)); // Evento de clique liberado
    }// Fim do For
  }// Fim do metodo aplicarAnimacaoBotao

  @FXML
  private void fechar(){
    System.exit(0);
  }

  @FXML
  private void reset(){
    pararSimulacao();
    iniciarSimulacao();
  }

  @FXML
  public void initialize() {
    iniciarSimulacao();
    aplicarAnimacaoBotao(botaoReset, botaoFechar);
  }
}