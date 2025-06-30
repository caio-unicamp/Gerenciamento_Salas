#!/bin/bash

# Define o diretório de bibliotecas
LIB_DIR="libs"

# Limpa a pasta de saída e a recria
rm -rf out
mkdir -p out

# Cria o diretório de libs se não existir
mkdir -p "$LIB_DIR"

# Adiciona todos os JARs do diretório lib ao classpath
# O '*' garante que todos os arquivos .jar sejam incluídos.
CP_LIBS=$(JARS=("$LIB_DIR"/*.jar); IFS=:; echo "${JARS[*]}")

# Compila todos os arquivos .java, preservando a estrutura de pacotes
# Adiciona as bibliotecas ao classpath da compilação
find . -name "*.java" -not -path "./out/*" | xargs javac -d out -cp "$CP_LIBS"

# Copia os arquivos de recursos para a pasta de saída
if [ -d "resources" ]; then
    cp -r resources out/
fi

# Executa a classe principal, adicionando o diretório 'out' e as bibliotecas ao classpath
java -cp "out:$CP_LIBS" Main