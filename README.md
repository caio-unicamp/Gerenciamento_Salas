# Sistema de Gerenciamento de Reservas de Salas de Aula

Este projeto implementa um sistema de gerenciamento de reservas de salas de aula utilizando conceitos de Programação Orientada a Objetos (POO) em Java, com uma interface gráfica (GUI) desenvolvida em Swing. O sistema permite cadastrar salas, usuários (administradores e alunos) e realizar/cancelar reservas, com persistência de dados em arquivos.

## 1. Objetivo

Estudar e implementar os conceitos de programação orientada a objetos abordados na disciplina, por meio do desenvolvimento de um sistema definido pelo grupo. Este trabalho contempla a proposta, modelagem e implementação de um sistema orientado a objetos. O sistema foi descrito e desenvolvido em sua versão completa, estando pronto para manipulação por parte do usuário.

## 2. Requisitos do Sistema

Para compilar e executar este projeto, você precisará dos seguintes softwares:

* **Java Development Kit (JDK):** Versão 11 ou superior (preferencialmente a versão LTS mais recente, como JDK 17).
* **Ambiente Gráfico:** O sistema possui uma interface gráfica (GUI)10, portanto, ele deve ser executado em um ambiente que suporte a exibição de janelas (como Windows, macOS, ou um ambiente de desktop Linux).
    * Se estiver utilizando **WSL (Windows Subsystem for Linux)**, um servidor X no Windows é necessário.

## 3. Tópicos de POO Abordados

O projeto incorpora os seguintes conceitos de programação orientada a objetos:

* **Classes, variáveis e métodos:** O sistema contempla classes, seus atributos e métodos, incluindo sobrecarga.
* **Visibilidade:** Aplicação correta de modificadores de visibilidade (public, private, protected).
* **Herança:** Ao menos uma estrutura de herança foi implementada (ex: `User` com `Student` e `Administrator`).
* **Variáveis e métodos estáticos:** Inclui pelo menos uma variável e um método de classe (ex: `nextReservationId` em `Reservation`).
* **Arrays:** Utilizado quando necessário (ex: `ArrayList` para armazenar coleções de objetos).
* **Enumerações:** Incluído ao menos um exemplo de uso (ex: `ReservationStatus`).
* **Entrada e saída de dados:** Permite entrada e/ou saída de dados via GUI e arquivos.
* **Relacionamentos (associação, agregação ou composição):** Contempla ao menos um tipo de relacionamento (ex: `ReservationManager` agrega `Classroom` e `Reservation`).
* **Classes abstratas:** Implementada ao menos uma (ex: `User`).
* **Polimorfismo:** Inclui exemplos de polimorfismo de tipo e de método (ex: `User` e seus subtipos, sobrecarga de `findAvailableClassrooms`).
* **Interface gráfica:** O sistema é controlado por interface gráfica (GUI).
* **Tratamento de exceções:** Trata exceções, incluindo pelo menos uma exceção definida pelo grupo (`ReservationConflictException`).
* **Arquivos (leitura e gravação):** O sistema realiza operações de leitura e escrita em arquivos relevantes ao projeto (`data/classrooms.txt`, `data/reservations.txt`, `data/users.txt`).

## 4. Como Configurar e Rodar o Projeto

Siga os passos abaixo para preparar seu ambiente e executar a aplicação.

### 4.1. Instalação do JDK

Certifique-se de ter o JDK instalado. Você pode baixá-lo do site da Oracle ou usar uma distribuição OpenJDK (como Adoptium Temurin, Amazon Corretto, etc.).

Para verificar a instalação, abra um terminal (Prompt de Comando no Windows, Terminal no Linux/macOS, ou Terminal WSL) e digite:

```bash
java -version
javac -version

### 4.2. Configuração para Usuários WSL (Windows Subsystem for Linux)
Se você estiver rodando o projeto dentro do WSL e quiser que a interface gráfica apareça no Windows, siga estes passos adicionais:

Instale um Servidor X no Windows:

Recomenda-se o VcXsrv.

Download: Baixe o VcXsrv (procure por "VcXsrv Windows X Server" no SourceForge).

Instalação: Execute o instalador e siga as instruções.

Configuração e Inicialização:

Após a instalação, procure por "XLaunch" no menu Iniciar do Windows e execute-o.

Na primeira tela ("Display settings"), escolha "Multiple windows" (ou "One large window").

CRÍTICO: Marque a opção "Disable access control". Isso permite que o WSL se conecte ao servidor X sem problemas de permissão.

Prossiga com as configurações padrão nas próximas telas e clique em "Finish". Um ícone do VcXsrv aparecerá na bandeja do sistema do Windows, indicando que está em execução.

Configurar Firewall do Windows:

Certifique-se de que o Firewall do Windows não está bloqueando o VcXsrv.

No Windows, pesquise por "Permitir um aplicativo através do Firewall do Windows".

Clique em "Alterar configurações" e procure por "VcXsrv".

Marque as caixas "Privado" e "Público" para VcXsrv. Se não estiver na lista, clique em "Permitir outro aplicativo...", navegue até o executável (C:\Program Files\VcXsrv\vcxsrv.exe) e adicione-o.

Alternativamente, você pode adicionar uma regra de entrada para a porta TCP 6000 (porta padrão do X11) diretamente no "Firewall do Windows Defender com Segurança Avançada".

Configurar Variável DISPLAY no WSL:

Abra seu terminal WSL (ex: Ubuntu).

Adicione as seguintes linhas ao seu arquivo de configuração do shell (~/.bashrc para Bash, ou ~/.zshrc para Zsh):