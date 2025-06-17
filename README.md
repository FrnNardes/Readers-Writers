# Readers-Writers

## 📌 Sobre o Projeto
Implementação do problema clássico dos Leitores e Escritores, simulando o acesso concorrente a um recurso compartilhado. O projeto demonstra diferentes estratégias de sincronização para garantir a integridade dos dados em cenários com múltiplos leitores e escritores.

## 🛠️ Tecnologias Utilizadas
- **Java 8**
- **JavaFX e CSS**

## 🚀 Como Executar
1. **Clone o repositório:**
   ```sh
   git clone https://github.com/FrnNardes/Readers-Writers.git
   ```
2. **Acesse o diretório do projeto:**
   ```sh
   cd minecart-simulator
   ```
3. **Compile o projeto:**
   ```sh
   javac Principal.java
   ```
4. **Execute a simulação:**
   ```sh
   java Principal
   ```
   
## 🖥️ Execução
![Demonstração](assets/execucao.gif)

## ⚠️ Atenção
Certifique-se de instalar a versão 8 do Java, na qual já vem com o JavaFX!
```sh
java -version

java version "1.8.0_xxx"
Java(TM) SE Runtime Environment (build 1.8.0_xxx-xxx)
```

## 📂 Estrutura do Projeto
```
📂 minecart-simulator
 ├── 📂 model
 │   ├── Villager.java
 ├── 📂 view
 │   ├── menu.FXML
 │   ├── tutorial.FXML
 │   ├── simulacao.FXML
 │   ├── 📂 style
 │   │   └── style.css
 ├── 📂 controller
 │   ├── BaseController.java
 │   ├── MenuController.java
 │   ├── TutorialController.java
 │   ├── SimulacaoController.java
 ├── 📂 assets
 │   ├── backgroundDay.png
 │   ├── minecraftIcon.png
 │   ├── VillagerTopDownOnCart.png
 │   ├── ...
 ├── Principal.java
 ├── README.md
```

## 📄 Licença
Este projeto está sob a **MIT License**. Sinta-se livre para utilizá-lo e modificá-lo.

---
Criado por [Fernando Nardes](https://github.com/FrnNardes)
