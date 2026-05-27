#!/bin/bash

# ==========================================
# Script de Automação de Cloud Azure - AeroSoil AI
# ==========================================

# Variáveis do Projeto
RESOURCE_GROUP="rg-aerosoil-devops"
LOCATION="eastus"
VM_NAME="vm-aerosoil-app"
IMAGE="Ubuntu2204"
ADMIN_USER="azureuser"
VM_SIZE="Standard_D2s_v3"

echo "========================================="
echo "🚀 Iniciando Provisionamento AeroSoil AI na Azure..."
echo "========================================="

# 1. Criar o Grupo de Recursos
echo "[1/6] Criando Resource Group ($RESOURCE_GROUP)..."
az group create --name $RESOURCE_GROUP --location $LOCATION -o none

# 2. Criar a Máquina Virtual (Linux) com script de inicialização injetado
echo "[2/6] Criando Máquina Virtual ($VM_NAME) de tamanho $VM_SIZE..."
az vm create \
  --resource-group $RESOURCE_GROUP \
  --name $VM_NAME \
  --image $IMAGE \
  --admin-username $ADMIN_USER \
  --size $VM_SIZE \
  --generate-ssh-keys \
  --custom-data cloud-init.txt \
  --output json

# 3. Abrir as Portas no Security Group
echo "[3/6] Abrindo portas no firewall (8080 para API, 1521 para Oracle DB)..."
az vm open-port --resource-group $RESOURCE_GROUP --name $VM_NAME --port 8080 --priority 1001 -o none
az vm open-port --resource-group $RESOURCE_GROUP --name $VM_NAME --port 1521 --priority 1002 -o none

# 4. Aguardar o cloud-init concluir as instalações na VM
echo "[4/6] Aguardando cloud-init concluir a instalação do Docker na VM (isso pode levar cerca de 2-3 minutos)..."
az vm run-command invoke \
  --command-id RunShellScript \
  --name $VM_NAME \
  --resource-group $RESOURCE_GROUP \
  --scripts "cloud-init status --wait" \
  --query "value[0].message" -o tsv

# 5. Clonar o projeto e rodar o Docker Compose de forma 100% automatizada dentro da VM
echo "[5/6] Clonando repositório e iniciando containers Docker na nuvem..."
az vm run-command invoke \
  --command-id RunShellScript \
  --name $VM_NAME \
  --resource-group $RESOURCE_GROUP \
  --scripts "git clone https://github.com/Gabriel-Maciel06/GsDevops.git /home/azureuser/GsDevops && chown -R azureuser:azureuser /home/azureuser/GsDevops && cd /home/azureuser/GsDevops && docker compose up -d"

# 6. Finalização e Teste de Conexão
PUBLIC_IP=$(az vm show -d -g $RESOURCE_GROUP -n $VM_NAME --query publicIps -o tsv)

echo "========================================="
echo "✅ Provisionamento Concluído com Sucesso!"
echo "========================================="
echo "IP Público da VM: $PUBLIC_IP"
echo "Acesse a documentação Swagger online em:"
echo "  http://$PUBLIC_IP:8080/swagger-ui.html"
echo "========================================="

echo "Aguardando 15 segundos para dar tempo do banco de dados iniciar na nuvem..."
sleep 15

echo "Testando endpoint de documentação na nuvem (http://$PUBLIC_IP:8080/v3/api-docs)..."
curl -I http://$PUBLIC_IP:8080/v3/api-docs
