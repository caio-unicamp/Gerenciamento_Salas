# Guia de Execução no Windows

Este guia descreve como compilar e executar o projeto de Gerenciamento de Salas em um ambiente Windows nativo, utilizando o Prompt de Comando (CMD) ou o PowerShell.

## Pré-requisitos

* **JDK (Java Development Kit):** Versão 11 ou superior instalado e configurado nas variáveis de ambiente do Windows.
* **Dependências:** Certifique-se de que a pasta `libs/`, contendo o arquivo JAR do FlatLaf, está presente na raiz do projeto.

### Verificando a Instalação do Java

Abra o CMD ou PowerShell e execute os seguintes comandos para verificar se o JDK está corretamente configurado:

```powershell
java -version
javac -version
```

Se os comandos não forem reconhecidos, você precisará adicionar o diretório `bin` da sua instalação do JDK à variável de ambiente `PATH`.

## Compilando o Projeto

1.  **Abra o terminal** (CMD ou PowerShell) na pasta raiz do projeto (a pasta que contém o diretório `src/` e a pasta `libs/`).

2.  **Crie o diretório de saída:**

    ```powershell
    mkdir out
    ```

3.  **Compile o código-fonte.** O comando a seguir compila todos os arquivos `.java` da pasta `src/` e coloca os arquivos `.class` compilados na pasta `out/`, incluindo as bibliotecas da pasta `libs/` no classpath.

    ```powershell
    javac -d out -cp "libs/*" src/gui/*.java src/manager/*.java src/model/*.java src/exception/*.java src/util/*.java src/Main.java
    ```

    *Observação: A listagem explícita dos pacotes garante a compilação em ordem. Se preferir, em alguns sistemas, um curinga como `src/**/*.java` pode funcionar, mas é menos garantido no CMD.*

## Executando a Aplicação

Após a compilação bem-sucedida, você pode executar a aplicação com o seguinte comando. Ele informa ao Java para procurar as classes compiladas no diretório `out/` e as dependências na pasta `libs/`.

```powershell
java -cp "out;libs/*" Main
```

*Atenção: Note o uso do ponto e vírgula (`;`) como separador no classpath, que é o padrão para Windows.*

A janela principal do "Sistema de Gerenciamento de Salas" deverá aparecer.