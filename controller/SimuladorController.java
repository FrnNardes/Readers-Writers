package controller;

import util.Contador;
import model.Leitor;
import model.Escritor;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;

public class SimuladorController {

  @FXML private FlowPane studentGrid;

  // Injeção dos componentes da View
  @FXML private Label leitor1Status, leitor2Status, leitor3Status, leitor4Status, leitor5Status;
  @FXML private Label escritor1Status, escritor2Status, escritor3Status, escritor4Status, escritor5Status;
  @FXML private ImageView student1, student2, student3, student4, student5;
  @FXML private ImageView scientist1, scientist2, scientist3, scientist4, scientist5;
  @FXML private ImageView botaoReset, botaoFechar;
  @FXML private ImageView escritorIcon1, escritorIcon2, escritorIcon3, escritorIcon4, escritorIcon5;
  @FXML private ImageView leitorIcon1, leitorIcon2, leitorIcon3, leitorIcon4, leitorIcon5;
  @FXML private Slider procurarSlider1, procurarSlider2, procurarSlider3, procurarSlider4, procurarSlider5;
  @FXML private Slider lerSlider1, lerSlider2, lerSlider3, lerSlider4, lerSlider5;
  @FXML private Slider pesquisarSlider1, pesquisarSlider2, pesquisarSlider3, pesquisarSlider4, pesquisarSlider5;
  @FXML private Slider publicarSlider1, publicarSlider2, publicarSlider3, publicarSlider4, publicarSlider5;
  @FXML private ImageView botaoPause1, botaoPause2, botaoPause3, botaoPause4, botaoPause5, botaoPause6, botaoPause7, botaoPause8, botaoPause9, botaoPause10;

  private final Image pause_button = new Image(getClass().getResourceAsStream("/assets/pause_button.png")); // Pega a imagem do botao cinza
  private final Image play_button = new Image(getClass().getResourceAsStream("/assets/play_button.png")); // Pega a imagem do botao cinza

  // Campos para gerenciar a simulação
  private List<Leitor> leitoresAtivos = new ArrayList<>();
  private List<Escritor> escritoresAtivos = new ArrayList<>();

  private void iniciarSimulacao(){
    // 1. Cria a lógica e os dados do Model
    Semaphore mutex = new Semaphore(1);
    Semaphore db = new Semaphore(1);
    Contador rc = new Contador();

    // 2. Cria as instâncias dos Atores (Model)
    // (Vou remover a passagem do botão de pausa do construtor, pois vamos configurá-lo aqui)
    List<Leitor> leitores = Arrays.asList(
        new Leitor(1, mutex, db, rc, leitor1Status, student1, leitorIcon1, procurarSlider1, lerSlider1),
        new Leitor(2, mutex, db, rc, leitor2Status, student2, leitorIcon2, procurarSlider2, lerSlider2),
        new Leitor(3, mutex, db, rc, leitor3Status, student3, leitorIcon3, procurarSlider3, lerSlider3),
        new Leitor(4, mutex, db, rc, leitor4Status, student4, leitorIcon4, procurarSlider4, lerSlider4),
        new Leitor(5, mutex, db, rc, leitor5Status, student5, leitorIcon5, procurarSlider5, lerSlider5)
    );

    List<Escritor> escritores = Arrays.asList(
        new Escritor(1, db, escritor1Status, scientist1, escritorIcon1, pesquisarSlider1, publicarSlider1),
        new Escritor(2, db, escritor2Status, scientist2, escritorIcon2, pesquisarSlider2, publicarSlider2),
        new Escritor(3, db, escritor3Status, scientist3, escritorIcon3, pesquisarSlider3, publicarSlider3),
        new Escritor(4, db, escritor4Status, scientist4, escritorIcon4, pesquisarSlider4, publicarSlider4),
        new Escritor(5, db, escritor5Status, scientist5, escritorIcon5, pesquisarSlider5, publicarSlider5)
    );

    // --- A CONEXÃO DOS BOTÕES ACONTECE AQUI ---
    
    // Agrupa os botões em uma lista para facilitar
    List<ImageView> botoesPausa = Arrays.asList(botaoPause1, botaoPause2, botaoPause3, botaoPause4, botaoPause5, botaoPause6, botaoPause7, botaoPause8, botaoPause9, botaoPause10);

    // Conecta os 5 primeiros botões aos 5 leitores
    for (int i = 0; i < leitores.size(); i++) {
        final Leitor leitor = leitores.get(i); // Variável final para ser usada na lambda
        ImageView botao = botoesPausa.get(i);
        botao.setImage(pause_button);
        aplicarAnimacaoBotao(botoesPausa.get(i));
        
        botao.setOnMouseClicked(event -> {
            leitor.alternarPausa();
            if(leitor.isPaused()){
              botao.setImage(play_button);
            } else{
              botao.setImage(pause_button);
            }
        });
    }

    // Conecta os 5 últimos botões aos 5 escritores
    for (int i = 0; i < escritores.size(); i++) {
      final Escritor escritor = escritores.get(i);
      ImageView botao = botoesPausa.get(i + 5); // Começa do índice 5 da lista de botões
      botao.setImage(pause_button);
      aplicarAnimacaoBotao(botoesPausa.get(i));

      botao.setOnMouseClicked(event -> {
        escritor.alternarPausa();
        if(escritor.isPaused()){
          botao.setImage(play_button);
        } else{
          botao.setImage(pause_button);
        }
      });
    }

    // --- FIM DA CONEXÃO ---

    // 3. Salvando os leitores e escritores da execucao atual
    leitoresAtivos.addAll(leitores);
    escritoresAtivos.addAll(escritores);

    // 4. Iniciando simualcao
    for(Leitor leitor : leitoresAtivos){
      leitor.start();
    }
    for(Escritor escritor : escritoresAtivos){
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
    leitoresAtivos.clear();
    escritoresAtivos.clear();
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