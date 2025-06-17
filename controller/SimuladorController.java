/* ***************************************************************
* Autor............: Fernando Nardes Ferreira Neto
* Matricula........: 202410403
* Inicio...........: 09/06/2025
* Ultima alteracao.: 16/06/2025
* Nome.............: SimuladorController.java
* Funcao...........: Classe controladora principal da aplicacao. Ela gerencia
*                    a interface grafica definida no 'simulacao.fxml',
*                    orquestra a criacao e o ciclo de vida das threads de
*                    Leitores e Escritores, e lida com os eventos de
*                    interacao do usuario, como reset e pausa.
*************************************************************** */
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

  // Variaveis injetadas pelo FXML para todos os componentes da interface
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

  // Carrega as imagens dos botoes de pausa/play uma unica vez para otimizacao
  private final Image pause_button = new Image(getClass().getResourceAsStream("/assets/pause_button.png"));
  private final Image play_button = new Image(getClass().getResourceAsStream("/assets/play_button.png"));

  // Listas para armazenar as threads ativas, permitindo para-las durante o reset
  private List<Leitor> leitoresAtivos = new ArrayList<>();
  private List<Escritor> escritoresAtivos = new ArrayList<>();

  /* ***************************************************************
  * Metodo: iniciarSimulacao
  * Funcao: Configura e inicia uma nova instancia da simulacao. Cria os
  * semaforos, agrupa os componentes da UI em listas e instancia
  * as threads de Leitores e Escritores, configurando seus
  * respectivos botoes de pausa e iniciando sua execucao.
  * Parametros: Nenhum
  * Retorno: void
  *************************************************************** */
  private void iniciarSimulacao(){
    // Limpa as listas da simulacao anterior para evitar IllegalThreadStateException
    leitoresAtivos.clear();
    escritoresAtivos.clear();

    // 1. Cria os objetos de sincronizacao do problema Leitor/Escritor
    Semaphore mutex = new Semaphore(1); // Para proteger o contador 'rc'
    Semaphore db = new Semaphore(1);    // Para garantir acesso exclusivo dos escritores ao 'banco de dados'
    Contador rc = new Contador();       // Conta o numero de leitores ativos

    // 2. Agrupa os componentes FXML em listas para facilitar a manipulacao em loops
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
    
    // Agrupa os botoes de pausa em uma lista unica para facilitar a atribuicao
    List<ImageView> botoesPausa = Arrays.asList(botaoPause1, botaoPause2, botaoPause3, botaoPause4, botaoPause5, botaoPause6, botaoPause7, botaoPause8, botaoPause9, botaoPause10);

    // Conecta os 5 primeiros botoes aos 5 leitores
    for (int i = 0; i < leitores.size(); i++) {
        final Leitor leitor = leitores.get(i); // Variavel precisa ser final para ser usada na expressao lambda
        ImageView botao = botoesPausa.get(i);
        botao.setImage(pause_button); // Define a imagem inicial como 'pausa'
        aplicarAnimacaoBotao(botao);
        
        // Define a acao de clique para o botao de pausa
        botao.setOnMouseClicked(event -> {
            leitor.alternarPausa(); // Chama o metodo de pausa da thread correspondente
            // Alterna a imagem do botao entre 'play' e 'pause'
            if(leitor.isPaused()){
              botao.setImage(play_button);
            } else{
              botao.setImage(pause_button);
            }
        });
    }

    // Conecta os 5 ultimos botoes aos 5 escritores
    for (int i = 0; i < escritores.size(); i++) {
        final Escritor escritor = escritores.get(i);
        ImageView botao = botoesPausa.get(i + 5); // Comeca do indice 5 da lista de botoes
        botao.setImage(pause_button);
        aplicarAnimacaoBotao(botao);

        botao.setOnMouseClicked(event -> {
            escritor.alternarPausa();
            if(escritor.isPaused()){
              botao.setImage(play_button);
            } else{
              botao.setImage(pause_button);
            }
        });
    }

    // 3. Adiciona as novas threads às listas de controle
    leitoresAtivos.addAll(leitores);
    escritoresAtivos.addAll(escritores);

    // 4. Inicia a execucao de todas as threads
    for(Leitor leitor : leitoresAtivos){
      leitor.start();
    }
    for(Escritor escritor : escritoresAtivos){
      escritor.start();
    }
  }

  /* ***************************************************************
  * Metodo: pararSimulacao
  * Funcao: Interrompe de forma segura todas as threads que estao
  * em execucao, chamando o metodo 'parar()' de cada uma e,
  * em seguida, limpa as listas de controle.
  * Parametros: Nenhum
  * Retorno: void
  *************************************************************** */
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

  /* ***************************************************************
  * Metodo: aplicarAnimacaoBotao
  * Funcao: Adiciona efeitos visuais de hover e clique a um conjunto
  * de ImageViews para que se comportem como botoes.
  * Parametros: ImageViews - um array de uma ou mais ImageViews.
  * Retorno: void
  *************************************************************** */
  public void aplicarAnimacaoBotao(ImageView... ImageViews) {
    ColorAdjust efeitoHover = new ColorAdjust();
    efeitoHover.setBrightness(0.4); 
    ColorAdjust efeitoPressionado = new ColorAdjust();
    efeitoPressionado.setBrightness(-0.4);

    for(ImageView imageView : ImageViews){
      imageView.setOnMouseEntered(e -> imageView.setEffect(efeitoHover)); 
      imageView.setOnMouseExited(e -> imageView.setEffect(null));
      imageView.setOnMousePressed(e -> imageView.setEffect(efeitoPressionado));
      imageView.setOnMouseReleased(e -> imageView.setEffect(efeitoHover));
    }// Fim do For
  }// Fim do metodo aplicarAnimacaoBotao

  /* ***************************************************************
  * Metodo: fechar
  * Funcao: Metodo acionado pelo botao de fechar. Encerra a aplicacao.
  * Parametros: Nenhum
  * Retorno: void
  *************************************************************** */
  @FXML
  private void fechar(){
    System.exit(0);
  }

  /* ***************************************************************
  * Metodo: reset
  * Funcao: Metodo acionado pelo botao de reset. Para a simulacao
  * atual e inicia uma nova em seguida.
  * Parametros: Nenhum
  * Retorno: void
  *************************************************************** */
  @FXML
  private void reset(){
    pararSimulacao();
    iniciarSimulacao();
  }

  /* ***************************************************************
  * Metodo: initialize
  * Funcao: Metodo padrao do JavaFX chamado automaticamente apos o FXML
  * ser carregado. É o ponto de entrada para configurar o
  * controller, iniciando a simulacao e as animacoes dos botoes.
  * Parametros: Nenhum
  * Retorno: void
  *************************************************************** */
  @FXML
  public void initialize() {
    iniciarSimulacao();
    aplicarAnimacaoBotao(botaoReset, botaoFechar);
  }
} // Fim da classe SimuladorController