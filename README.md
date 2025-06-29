# Sistema de Reserva de Salas e Equipamentos

Este é um projeto de um sistema desktop para gerenciamento de reservas de salas e equipamentos, desenvolvido como parte de atividades acadêmicas. O objetivo é permitir que alunos solicitem reservas e que a secretaria possa aprovar ou negar esses pedidos de forma centralizada.

## Tecnologias Principais

  * **Java 17+**: Linguagem principal da aplicação.
  * **JavaFX 21+**: Framework para a construção da interface gráfica (GUI).
  * **SQLite**: Banco de dados leve e baseado em arquivo para persistência de dados.
  * **Maven**: Ferramenta para gerenciamento de dependências e automação do build do projeto.

## Estrutura do Projeto

O projeto segue uma arquitetura que visa separar as responsabilidades em diferentes camadas, facilitando a manutenção e a adição de novas funcionalidades.

```
reservas-sistema/
├── pom.xml                     # Arquivo de configuração do Maven
└── src/
    └── main/
        ├── java/
        │   └── com/feec/reservas/
        │       ├── controller/ # Controladores: A lógica por trás das telas
        │       ├── dao/        # Data Access Objects: Comunicação com o BD
        │       ├── exception/  # Exceções customizadas do sistema
        │       ├── model/      # Classes de domínio (Aluno, Reserva, etc.) - Representam objetos do sistema
        │       ├── service/    # Camada de lógica de negócio
        │       ├── Launcher.java       # Classe para iniciar a aplicação empacotada
        │       └── MainApplication.java  # Classe principal do JavaFX
        │
        └── resources/
            └── com/feec/reservas/
                └── fxml/       # Arquivos FXML que definem as interfaces (Semelhante ao HTML)
```

  * `pom.xml`: Arquivo fundamental que descreve o projeto, suas dependências (como JavaFX e SQLite) e como compilá-lo, utilizando plugins como o `maven-shade-plugin` para criar o executável final.
  * `/model`: Contém as classes que representam os dados do sistema, como `Usuario`, `Reserva`, `Sala`, etc. São os "tijolos" da nossa aplicação.
  * `/fxml`: Define a estrutura visual de cada tela. São como o HTML para uma página web, mas para uma aplicação JavaFX.
  * `/controller`: Faz a ponte entre as telas (`.fxml`) e os dados (`model`). Quando você clica em um botão, é um método no controller que é executado.
  * `/dao`: Camada de Acesso a Dados. Qualquer operação que precise ler ou escrever no banco de dados SQLite passa por uma classe DAO. Elas contêm as queries SQL.
  * `/service`: Orquestra as operações. Em vez de um controller chamar vários DAOs, ele chama um método no `GerenciadorDeReservas`, que contém a lógica de negócio mais complexa.
  * `Launcher.java`: Uma classe auxiliar simples cujo único propósito é iniciar a aplicação corretamente quando ela está empacotada em um arquivo `.jar`.

## Como Compilar e Executar

### Pré-requisitos

1.  **JDK 17 ou superior** instalado.
2.  **Apache Maven** instalado e configurado no PATH do sistema. A instalação é bem simples, para checar basta verificar se o comando `mvn --version` exibe a função do software.

### Compilando o Projeto

Para compilar todo o código-fonte e criar um arquivo `.jar` executável e portátil, utilize o Maven. Abra um terminal na pasta raiz do projeto (onde está o `pom.xml`) e execute:

```bash
mvn clean package
```

  * `clean`: Limpa compilações anteriores.
  * `package`: Compila o código e usa o `maven-shade-plugin` para gerar um "fat-jar" na pasta `target/`. Este JAR contém sua aplicação e todas as dependências necessárias.

### Executando a Aplicação

Após a compilação, o arquivo `reservas-sistema-1.0-SNAPSHOT.jar` será criado dentro da pasta `target`. Para executar o sistema, utilize o seguinte comando no terminal:

```bash
# Navegue até a pasta target
cd target

# Execute o arquivo JAR
java -jar reservas-sistema-1.0-SNAPSHOT.jar
```

Na primeira execução, um arquivo de banco de dados chamado `reservas.db` será criado no mesmo diretório do JAR.

## Como Adicionar Novas Funcionalidades

A arquitetura foi projetada para facilitar a expansão. Vamos supor que você queira adicionar uma nova tela para **"Cadastrar um Novo Item"**. O fluxo seria o seguinte:

#### Passo 1: Criar a View (FXML)

Crie um novo arquivo em `src/main/resources/com/feec/reservas/fxml/`, por exemplo, `cadastro-item-view.fxml`. Use o Scene Builder ou edite o FXML manualmente para adicionar os campos necessários (ex: `TextField` para o nome, `ComboBox` para o tipo, etc.).

#### Passo 2: Criar o Controller

Crie a classe `CadastroItemController.java` em `src/main/java/com/feec/reservas/controller/`. Ligue-a ao FXML e injete os componentes da tela usando a anotação `@FXML`. Crie o método para o botão de salvar.

```java
public class CadastroItemController {
    @FXML private TextField nomeField;
    // ... outros campos

    @FXML
    private void handleSalvarItemAction() {
        // Lógica para pegar os dados dos campos e chamar o service/dao
    }
}
```

#### Passo 3: Adicionar Lógica no DAO/Service

Se necessário, adicione um novo método ao `ItemReservavelDAO.java` para inserir um novo item no banco. Por exemplo, `public void criar(ItemReservavel item)`. Se houver regras de negócio, adicione-as ao `GerenciadorDeReservas.java`.

#### Passo 4: Integrar a Nova Tela

Para que o usuário possa acessar a nova tela, você precisa integrá-la ao fluxo existente. A melhor forma é usar o `MainController`.

1.  **Adicione um botão ou menu**: Abra o `main-view.fxml` e adicione um `MenuItem` no menu "Arquivo" com o texto "Cadastrar Novo Item" e um `onAction` apontando para um novo método, como `#handleCadastrarItem`.
2.  **Implemente o método no `MainController`**:
    ```java
    // Dentro de MainController.java
    @FXML
    private void handleCadastrarItem() {
        // Usa o método que já criamos para carregar qualquer tela
        carregarTela("/com/feec/reservas/fxml/cadastro-item-view.fxml", null);
    }
    ```

Pronto\! Seguindo este padrão (View -\> Controller -\> Service/DAO -\> Integração), você pode adicionar quantas funcionalidades quiser de forma organizada e escalável.