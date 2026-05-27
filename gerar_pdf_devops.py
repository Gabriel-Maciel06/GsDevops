import os
from reportlab.lib.pagesizes import A4
from reportlab.pdfgen import canvas
from reportlab.lib import colors

def gerar_pdf():
    # Caminho do PDF de saída no diretório atual
    pdf_filename = "Entrega_Final_Devops.pdf"
    
    # Criar canvas A4
    c = canvas.Canvas(pdf_filename, pagesize=A4)
    width, height = A4
    
    # Cabeçalho da FIAP
    c.setFillColor(colors.darkblue)
    c.setFont("Helvetica-Bold", 18)
    c.drawCentredString(width/2, height - 80, "FIAP - Faculdade de Informática e Administração Paulista")
    
    c.setStrokeColor(colors.lightgrey)
    c.setLineWidth(1)
    c.line(50, height - 100, width - 50, height - 100)
    
    # Título do Projeto e Disciplina
    c.setFillColor(colors.black)
    c.setFont("Helvetica-Bold", 14)
    c.drawString(70, height - 140, "Disciplina: DevOps Tools & Cloud Computing")
    c.setFont("Helvetica", 12)
    c.drawString(70, height - 165, "Projeto: AeroSoil AI - Plataforma Inteligente de Agricultura de Precisão")
    c.drawString(70, height - 185, "Sprint: Global Solution (GS) - 2026")
    
    # Integrantes
    c.setFont("Helvetica-Bold", 12)
    c.drawString(70, height - 240, "Integrantes do Grupo (Turma 2TDSR):")
    c.setFont("Helvetica", 11)
    
    integrantes = [
        "- Gabriel Maciel Alves de Oliveira - RM 562795 (Representante)",
        "- Vitória Rodrigues Martins - RM 565160",
        "- Augusto Bonomo Junior - RM 565155",
        "- Thomas Fontes - RM 562254",
        "- Matheus Pereira Molina - RM 563399"
    ]
    
    y_pos = height - 270
    for integrante in integrantes:
        c.drawString(90, y_pos, integrante)
        y_pos -= 25
        
    c.line(50, y_pos - 15, width - 50, y_pos - 15)
    
    # Links de Entrega
    y_pos -= 45
    c.setFillColor(colors.darkblue)
    c.setFont("Helvetica-Bold", 12)
    c.drawString(70, y_pos, "Links Oficiais de Entrega:")
    
    c.setFillColor(colors.black)
    c.setFont("Helvetica-Bold", 11)
    c.drawString(90, y_pos - 30, "Repositório GitHub:")
    c.setFont("Helvetica", 11)
    c.drawString(90, y_pos - 50, "https://github.com/Gabriel-Maciel06/GsDevops.git")
    
    c.setFont("Helvetica-Bold", 11)
    c.drawString(90, y_pos - 90, "Vídeo de Demonstração no YouTube:")
    c.setFont("Helvetica", 11)
    # Placeholder que o usuário pode atualizar se necessário, ou manter conforme gerado
    c.drawString(90, y_pos - 110, "https://youtu.be/SEU_VIDEO_AQUI")
    c.setFont("Helvetica-Oblique", 9)
    c.setFillColor(colors.red)
    c.drawString(90, y_pos - 125, "*Nota: Substitua o link acima pelo vídeo gravado demonstrando os comandos.")
    
    # Resumo DevOps
    c.setFillColor(colors.black)
    c.line(50, y_pos - 155, width - 50, y_pos - 155)
    
    c.setFont("Helvetica-Bold", 12)
    c.drawString(70, y_pos - 185, "Resumo da Infraestrutura Conteinerizada:")
    
    c.setFont("Helvetica", 10)
    c.drawString(90, y_pos - 210, "• API Java Spring Boot: Contêiner 'api-562795' construído via Dockerfile multi-stage.")
    c.drawString(90, y_pos - 225, "  - Rodando com usuário não-privilegiado 'appuser' e WORKDIR '/app'.")
    c.drawString(90, y_pos - 245, "• Banco de Dados Oracle: Contêiner 'db-562795' com volume nomeado 'oracle_data_volume'.")
    c.drawString(90, y_pos - 260, "  - Inicialização automática do schema via scripts DML/DDL.")
    c.drawString(90, y_pos - 275, "• Rede Isolada: Comunicação segura dos contêineres na rede bridge 'aerosoil_network'.")
    
    c.setFont("Helvetica-Oblique", 9)
    c.drawString(70, 50, "Gerado automaticamente pelo Agente Antigravity para entrega acadêmica.")
    
    c.save()
    print(f"PDF de folha de rosto gerado com sucesso: {pdf_filename}")

if __name__ == "__main__":
    gerar_pdf()
