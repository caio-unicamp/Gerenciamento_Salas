# Guia de Execução no Windows

Este guia descreve as duas principais maneiras de compilar e executar o projeto em um ambiente Windows:
1.  **Nativamente**, utilizando o Prompt de Comando (CMD) ou PowerShell.
2.  **Via WSL (Windows Subsystem for Linux)**, utilizando um servidor X11 para a interface gráfica.

---

## Opção 1: Executando no Windows Nativo (CMD/PowerShell)

Esta abordagem é a mais simples se você não utiliza o WSL.

### Pré-requisitos

* **JDK (Java Development Kit):** Versão 11 ou superior, com a variável de ambiente `PATH` configurada.
* **Dependências:** A pasta `libs/` com o arquivo JAR do FlatLaf deve estar presente na raiz do projeto.

### Verificando a Instalação

Abra o CMD ou PowerShell e verifique se o JDK está acessível:
```powershell
java -version
javac -version
```

### Passos para Compilação e Execução

1.  **Abra o terminal** na pasta raiz do projeto.

2.  **Crie o diretório de saída** para os arquivos compilados:

    ```powershell
    mkdir out
    ```

3.  **Compile o código-fonte.** Este comando compila todos os pacotes do projeto e coloca os arquivos `.class` na pasta `out/`.

    ```powershell
    javac -d out -cp "libs/*" src/gui/*.java src/manager/*.java src/model/*.java src/exception/*.java src/util/*.java src/Main.java
    ```

4.  **Execute a aplicação.** O comando a seguir utiliza o ponto e vírgula (`;`) como separador de classpath, padrão do Windows.

    ```powershell
    java -cp "out;libs/*" Main
    ```

A janela do sistema deverá aparecer.

-----

## Opção 2: Executando no Windows via WSL (com Servidor X11)

Esta abordagem é ideal para quem desenvolve dentro do ambiente Linux do WSL e precisa executar aplicações Java com interface gráfica.

### Visão Geral

O WSL não possui um ambiente gráfico nativo (exceto no Windows 11 com WSLg). Para exibir a interface do nosso programa, precisamos de um **Servidor X** rodando no Windows, que "receberá" a janela enviada pelo WSL.

### Passo 1: Instalar e Configurar um Servidor X no Windows

Recomendamos o **VcXsrv**, que é leve e gratuito.

1.  **Download e Instalação:**

      * Baixe o instalador do VcXsrv no [SourceForge](https://sourceforge.net/projects/vcxsrv/).
      * Execute o instalador e siga as instruções padrão.

2.  **Inicialização e Configuração (XLaunch):**

      * Após instalar, procure por **"XLaunch"** no Menu Iniciar e execute-o.
      * **Tela 1 (Display settings):** Escolha **"Multiple windows"**.
      * **Tela 2 (Client startup):** Deixe como **"Start no client"**.
      * **Tela 3 (Extra settings):**
          * Marque a opção **"Disable access control"**. **Este passo é crucial** para permitir que o WSL se conecte sem erros de permissão.
          * Salve a configuração para facilitar futuras inicializações.
      * Clique em "Finish". Um ícone do VcXsrv aparecerá na bandeja do sistema, indicando que ele está ativo.

3.  **Configuração do Firewall do Windows:**

      * O Windows pode pedir permissão para o VcXsrv se comunicar através do firewall. Permita o acesso, especialmente em redes **privadas**.

### Passo 2: Configurar a Variável `DISPLAY` no WSL

O WSL precisa saber para qual "tela" enviar a interface gráfica.

1.  **Abra seu terminal WSL** (ex: Ubuntu).
2.  **Edite o arquivo de configuração do seu shell.** O comando abaixo adiciona a configuração ao `~/.bashrc` (para o shell Bash).
    ```bash
    echo "export DISPLAY=\$(grep nameserver /etc/resolv.conf | awk '{print \$2}'):0.0" >> ~/.bashrc
    ```
3.  **Recarregue a configuração** para aplicar a mudança na sessão atual:
    ```bash
    source ~/.bashrc
    ```
4.  **Verifique:** Execute `echo $DISPLAY`. A saída deve ser um endereço de IP seguido de `:0.0` (ex: `172.27.112.1:0.0`).

### Passo 3: Compilar e Executar o Projeto no WSL

Agora você pode usar o script `run.sh` fornecido no projeto.

1.  Navegue até a pasta raiz do projeto dentro do seu terminal WSL.

2.  **Dê permissão de execução ao script:**

    ```bash
    chmod +x run.sh
    ```

3.  **Execute o script:**

    ```bash
    ./run.sh
    ```