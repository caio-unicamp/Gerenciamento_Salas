#!/bin/bash

# Limpa a pasta de saída e a recria
rm -rf out
mkdir -p out

# Compila todos os arquivos .java, preservando a estrutura de pacotes
find . -name "*.java" -not -path "./out/*" | xargs javac -d out

# Copia os arquivos de recursos para a pasta de saída
cp -r resources out/

# Executa a classe principal
java -cp out Main