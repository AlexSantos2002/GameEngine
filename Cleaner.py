import os

def deletar_arquivos_class(diretorio):
    for raiz, dirs, arquivos in os.walk(diretorio):
        for arquivo in arquivos:
            if arquivo.endswith(".class"):
                caminho_completo = os.path.join(raiz, arquivo)
                try:
                    os.remove(caminho_completo)
                    print(f"Deletado: {caminho_completo}")
                except Exception as e:
                    print(f"Erro ao deletar {caminho_completo}: {e}")

if __name__ == "__main__":
    caminho_diretorio = input("Digite o caminho do diretório: ").strip()
    if os.path.isdir(caminho_diretorio):
        deletar_arquivos_class(caminho_diretorio)
    else:
        print("Diretório inválido.")
