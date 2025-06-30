# Sistema de Gerenciamento de Reservas de Salas

Este projeto é um sistema de desktop para gerenciamento de reservas de salas, desenvolvido em Java com a biblioteca Swing para a interface gráfica. Ele foi criado como um estudo prático dos principais conceitos de Programação Orientada a Objetos (POO).

O sistema permite que usuários (alunos e administradores) se autentiquem, visualizem salas disponíveis, e realizem ou cancelem reservas. Os administradores possuem privilégios adicionais, como o gerenciamento completo de salas e a aprovação ou rejeição de reservas pendentes. Todos os dados são persistidos localmente.

## Funcionalidades Principais

* **Autenticação de Usuários:** Sistema de login que diferencia os papéis de Aluno e Administrador.
* **Gerenciamento de Salas:** Administradores podem adicionar, remover e visualizar todas as salas cadastradas.
* **Gerenciamento de Reservas:**
    * **Alunos:** Podem solicitar novas reservas, visualizar suas reservas (confirmadas, pendentes ou rejeitadas) e cancelar as que ainda não foram finalizadas.
    * **Administradores:** Têm uma visão geral de todas as reservas do sistema, podendo confirmar, rejeitar, cancelar ou deletar qualquer uma delas.
* **Visualização em Calendário:** Uma interface de calendário exibe de forma clara todas as reservas já confirmadas para facilitar a visualização de horários ocupados.
* **Persistência de Dados:** As informações de salas, usuários e reservas são salvas e carregadas de arquivos `.txt` na pasta `data/`, garantindo que os dados não sejam perdidos ao fechar o sistema.

## Detalhes Técnicos e Conceitos de POO

Este projeto foi estruturado para aplicar diversos conceitos fundamentais da Programação Orientada a Objetos.

### Herança e Polimorfismo

A estrutura de usuários é um exemplo de herança. A classe abstrata `User` define os atributos e métodos comuns a todos os usuários, como `username`, `password` e `name`.

-   `public abstract class User implements Serializable`

As classes `Student` e `Administrator` herdam de `User`, especializando o comportamento. `Student`, por exemplo, adiciona um campo `studentId`, enquanto `Administrator` tem um `role` que lhe concede permissões diferentes na interface.

O polimorfismo é utilizado em vários pontos, como no `ReservationManager`, que pode gerenciar uma lista de `User` sem precisar saber se o objeto específico é um `Student` ou um `Administrator`.

### Encapsulamento e Gerenciamento de Lógica

A classe `ReservationManager` centraliza toda a lógica de negócios, agindo como um "cérebro" para o sistema. Ela encapsula as listas de `classrooms`, `reservations` e `users`, expondo métodos públicos para interagir com esses dados de forma controlada.

**Exemplos de métodos importantes em `ReservationManager`:**
* `makeReservation(...)`: Verifica conflitos de horário e data antes de criar uma nova reserva. Lança uma `ReservationConflictException` se a sala já estiver ocupada.
* `addUser(...)`: Garante que não existam dois usuários com o mesmo `username`, lançando uma `UserConflictException` em caso de duplicidade.
* `confirmReservation(...)`: Altera o status de uma reserva de `PENDING` para `CONFIRMED`, validando novamente se não há conflitos.
* `loadData()` e `saveData()`: Métodos que utilizam a classe `FileUtil` para ler e escrever os dados do sistema, garantindo a persistência.

### Tratamento de Exceções Customizadas

Para um controle de erros mais semântico, foram criadas exceções específicas:
* `ReservationConflictException`: Lançada quando uma tentativa de reserva ou confirmação falha devido a um conflito com uma reserva já existente.
* `UserConflictException`: Lançada ao tentar registrar um `username` que já está em uso.

### Interface Gráfica (GUI com Swing)

A interface foi construída utilizando a biblioteca Swing.
* **`MainFrame`:** É a janela principal que controla a exibição dos painéis. Ela gerencia o estado da aplicação, mostrando a tela de `LoginPanel` ou, após o sucesso na autenticação, o `JTabbedPane` com as funcionalidades principais.
* **Painéis (`JPanel`):** O conteúdo é organizado em painéis modulares, como `ClassroomPanel`, `ReservationPanel` e `CalendarPanel`, cada um com sua responsabilidade específica.
* **Diálogos (`JDialog`):** Janelas modais como `AddClassroomDialog` e `RegisterDialog` são usadas para entrada de dados focada, melhorando a experiência do usuário.

## Como Executar (Linux/macOS)

### Pré-requisitos

* **JDK (Java Development Kit):** Versão 11 ou superior.

Para verificar a instalação, execute: `java -version`.

### Passos

1.  Clone o repositório e navegue até a pasta raiz do projeto.
2.  Dê permissão de execução ao script: `chmod +x run.sh`.
3.  Execute o script: `./run.sh`.
    *O script compilará os fontes para o diretório `out/` e iniciará a aplicação.*

## Como Executar (Windows)

Para instruções detalhadas de compilação e execução no Windows (via CMD ou PowerShell), consulte o arquivo [**README-Windows.md**](./README-Windows.md).

## Credenciais de Teste

Usuários padrão são criados na primeira execução do sistema:

* **Administrador:**
    * **Usuário:** `admin`
    * **Senha:** `admin123`
* **Aluno:**
    * **Usuário:** `aluno1`
    * **Senha:** `aluno123`