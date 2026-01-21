# Lerout - Guia de Instalação

Este guia apresenta o processo completo de instalação do sistema Lerout.

## Pré-requisitos

- Sistema operacional Windows
- Permissões de administrador para instalação

## Passo a Passo

### 1. Download e Extração

Faça o download do arquivo **Lerout Setup.zip** e descompacte-o em um local temporário de sua preferência.

### 2. Configuração do Java

Após descompactar, você encontrará as pastas `jdk-24` e `javafx-sdk-25.0.1`. Siga os passos:

1. Navegue até o diretório:
   ```
   C:\Program Files\Java
   ```
   
2. **Importante:** Se a pasta `Java` não existir, crie-a manualmente.

3. Mova (recorte e cole) as pastas `jdk-24` e `javafx-sdk-25.0.1` para dentro de `C:\Program Files\Java`

### 3. Instalação do Lerout

1. Crie a pasta de instalação do sistema:
   ```
   C:\Program Files\Lerout
   ```

2. Copie o arquivo `Lerout.jar` para dentro desta pasta.

### 4. Configuração das Variáveis de Ambiente

Esta etapa é crucial para o funcionamento correto do sistema.

1. Na barra de pesquisa do Windows, digite **"variáveis de ambiente"**

2. Execute o programa **"Editar as variáveis de ambiente do sistema"**

3. Clique no botão **"Variáveis de Ambiente..."**

4. Configure o Path tanto nas **variáveis de usuário** (bloco superior) quanto nas **variáveis do sistema** (bloco inferior):
   
   - Selecione a variável **Path**
   - Clique em **Editar**
   - Clique em **Novo**
   - Adicione o seguinte caminho:
     ```
     C:\Program Files\Java\jdk-24\bin
     ```
   - Com o novo caminho selecionado, clique em **Mover para Acima** repetidamente até que ele fique em **primeiro lugar** na lista
   - Clique em **OK** para salvar

5. Clique em **OK** em todas as janelas para aplicar as alterações

### 5. Executando o Lerout

Após concluir todos os passos anteriores, execute o arquivo `Lerout.exe` para iniciar o sistema.

## Arquivos de Dados e Logs

O sistema armazena automaticamente seus dados nos seguintes locais:

- **Banco de dados SQLite:** `C:\Users\[seu_usuário]\AppData\Roaming\Lerout`
- **Log de erros:** `C:\Users\[seu_usuário]\AppData\Roaming\Lerout`

> **Nota:** Substitua `[seu_usuário]` pelo nome do seu usuário Windows.

## Solução de Problemas

Caso o sistema não execute corretamente:

1. Verifique se todas as pastas foram movidas para os locais corretos
2. Confirme que as variáveis de ambiente foram configuradas adequadamente
3. Certifique-se de que o caminho do JDK está em primeiro lugar na variável Path
4. Reinicie o computador após configurar as variáveis de ambiente

---

**Desenvolvido por:** Kauê Gatti
**Versão:** 1.0
**Data:** Janeiro 2026
