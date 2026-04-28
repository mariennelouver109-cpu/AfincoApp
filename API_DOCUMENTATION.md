# AfincoApp - Documentação de API

## Visão Geral

Este documento provê a documentação dos pontos de saída da API para a aplicação Spring Boot AfincoApp. Todos os pontos de saída são baseados em REST e usam métodos HTTP (GET, POST).

## URL Base

```
http://localhost:8080
```

---

## Autenticação

### Autenticação Baseada em Sessão
- Autenticação usa sessões HTTP
- IDs de sessões são armazenados em cookies
- Timeout de sessão: 8 horas (28800 segundos)
- Depois do login, a sessão de usuário é mantida através de requisições

### Fluxo de Autenticação
1. Usuário envia credenciais para `/login`
2. O sistema valida credenciais via `UserService.authenticate()`
3. Com sucesso, o nome de usuário é armazenado em `HttpSession`
4. ID de sessão é retornado em cookies de resposta
5. Requisições subsequentes usam cookies de sessão para autenticação

---

## Saídas de Autenticação de Usuário

### 1. Mostrar Tela de Login
```http
GET /login
```

**Descrição**: Exibe formulário de login

**Resposta**: 
- **Tipo de conteúdo**: `text/html`
- **Estado**: `200 OK`
- **Body**: HTML login form (login.html)

**Exemplo**:
```bash
curl http://localhost:8080/login
```

---

### 2. Login de Usuário
```http
POST /login
```

**Descrição**: Autentica usuário com username e password

**Parâmetros Requisitados**:
| Parâmetro | Tipo | Requerido | Descrição |
|-----------|------|-----------|-----------|
| `username` | String | Sim | Nome de usuário |
| `password` | String | Sim | Senha |

**Exemplo**:
```bash
curl -X POST http://localhost:8080/login \
  -d "username=student1&password=secure123"
```

**Resposta - Sucesso** (`302 Found`):
- **Localização**: `/menu`
- **Set-Cookie**: Cookie de sessão com identificador de usuário
- **Sessão**: `loggedUser` = username

**Resposta - Falha** (`200 OK`):
- **Body**: login.html
- **Modelo**: `erro` = "Login inválido"

---

### 3. Mostrar Tela de Registro
```http
GET /cadastro
```

**Descrição**: Exibe formulário de registro

**Resposta**: 
- **Tipo de Conteúdo**: `text/html`
- **Estado**: `200 OK`
- **Body**: formulário de registro HTML (cadastro.html)

---

### 4. Registrar Usuário
```http
POST /cadastro
```

**Descrição**: Cria nova conta de usuário

**Parâmetros da Requisição**:
| Parâmetro | Tipo | Requerido | Descrição |
|-----------|------|-----------|-----------|
| `username` | String | Sim | Nome de usuário desejado (deve ser único) |
| `password` | String | Sim | Senha da conta |

**Exemplo**:
```bash
curl -X POST http://localhost:8080/cadastro \
  -d "username=newstudent&password=Password123"
```

**Resposta - Sucesso**: 
- **Estado**: `200 OK`
- **Body**: cadastro.html
- **Modelo**: `mensagem` = "Cadastro realizado com sucesso. Agora faça login."

**Resposta - Falha**: 
- **Estado**: `200 OK`
- **Body**: cadastro.html
- **Modelo**: `erro` = descrição do erro

**Lógica de Negócio**:
- Validação de nome de usuário (verificação de unicidade)
- Validação de senha
- Senha codificada com BCryptPasswordEncoder antes do armazenamento

---

### 5. Mostrar Perfil do Usuário
```http
GET /perfil
```

**Descrição**: Exibe informações do perfil do usuário autenticado

**Autenticação**: Requerido

**Resposta**: 
- **Tipo de Conteúdo**: `text/html`
- **Estado**: `200 OK`
- **Body**: página de perfil (perfil.html)

**Atributos do Modelo**:
- `username`: Nome de usuário do usuário autenticado

---

### 6. Atualizar Perfil do Usuário
```http
POST /update
```

**Descrição**: Atualiza nome de usuário do usuário autenticado

**Autenticação**: Requerido

**Parâmetros da Requisição**:
| Parâmetro | Tipo | Requerido | Descrição |
|-----------|------|-----------|-----------|
| `username` | String | Sim | Novo nome de usuário |

**Exemplo**:
```bash
curl -X POST http://localhost:8080/update \
  -H "Cookie: JSESSIONID=..." \
  -d "username=newusername"
```

**Resposta - Sucesso**: 
- **Estado**: `200 OK`
- **Body**: perfil.html
- **Modelo**: `mensagem` = "Nome de usuário atualizado com sucesso"
- **Sessão**: `loggedUser` atualizado com novo nome de usuário

**Resposta - Falha**: 
- **Estado**: `200 OK`
- **Body**: perfil.html
- **Modelo**: `erro` = descrição do erro (ex: nome de usuário já existe)

---

### 7. Deletar Conta de Usuário
```http
POST /delete
```

**Descrição**: Deleta conta do usuário e invalida sessão

**Autenticação**: Requerido

**Exemplo**:
```bash
curl -X POST http://localhost:8080/delete \
  -H "Cookie: JSESSIONID=..."
```

**Resposta**: 
- **Estado**: `302 Found`
- **Localização**: `/login`
- **Sessão**: Invalidada

---

### 8. Sair (Logout)
```http
GET /logout
```

**Descrição**: Faz logout do usuário e invalida sessão

**Autenticação**: Opcional

**Exemplo**:
```bash
curl http://localhost:8080/logout
```

**Resposta**: 
- **Estado**: `302 Found`
- **Localização**: `/login`
- **Sessão**: Invalidada

---

## Pontos de Saída de Disciplinas e Conteúdos

### 1. Obter Disciplina com Conteúdos e Provas
```http
GET /materia/{slug}
```

**Descrição**: Exibe conteúdos e provas de uma matéria organizados por período (bimestre)

**Parâmetros de Caminho**:
| Parâmetro | Tipo | Descrição |
|-----------|------|-----------|
| `slug` | String | Identificador de slug da disciplina (ex: "matematica", "portugues") |

**Autenticação**: Não requerido mas recomendado

**Exemplo**:
```bash
curl http://localhost:8080/materia/matematica
```

**Resposta**: 
- **Estado**: `200 OK`
- **Tipo de Conteúdo**: `text/html`
- **Body**: página conteudos.html

**Atributos do Modelo**:
- `materia`: Nome da disciplina (String)
- `periods`: Lista de períodos, cada um contendo:
  - `number`: Número do período (1, 2, 3, 4)
  - `nome`: Nome legível do período (ex: "1º Bimestre")
  - `conteudos`: Lista de ContentDTO para esse período:
    - `id`: ID do conteúdo
    - `nome`: Nome/título do conteúdo
    - `tipo`: Tipo de conteúdo (vídeo, pdf, etc)
    - `url`: URL pública para download
  - `exames`: Lista de ExamDTO para esse período:
    - `id`: ID do exame
    - `nome`: Nome do exame
    - `url`: URL para iniciar o exame

**Exemplo de Estrutura de Resposta**:
```html
<!-- Página mostrando: -->
<!-- 1ª Etapa -->
<!--   Conteúdo: Algebra Básica (vídeo) [Download] -->
<!--   Conteúdo: Equações Lineares (pdf) [Download] -->
<!--   Prova: av1 - Matemática [Iniciar] -->
<!-- 2ª Etapa -->
<!--   Conteúdo: Polinominais (vídeo) [Download] -->
<!--   Prova: av2 - Matemática [Iniciar] -->
```

---

### 2. Obter Detalhes do Conteúdo
```http
GET /api/content/{contentId}
```

**Descrição**: Obtém detalhes de um conteúdo incluindo URL pública

**Parâmetros de Caminho**:
| Parâmetro | Tipo | Descrição |
|-----------|------|-----------|
| `contentId` | Integer | Identificador do conteúdo |

**Resposta - Sucesso**: 
- **Estado**: `200 OK`
- **Tipo de Conteúdo**: `application/json`
- **Body**:
```json
{
  "id": 1,
  "contentName": "Algebra Basics",
  "type": "video",
  "subjectId": 5,
  "publicUrl": "https://supabase.storage.../file.mp4"
}
```

**Resposta - Não Encontrado**:
- **Estado**: `404 Not Found`
- **Body**: `{"error": "Conteúdo não encontrado"}`

**Exemplo**:
```bash
curl http://localhost:8080/api/content/1
```

---

### 3. Fazer Download de Conteúdo
```http
GET /api/content/download/{contentId}
```

**Descrição**: Faz download do arquivo de conteúdo do Supabase Storage

**Parâmetros de Caminho**:
| Parâmetro | Tipo | Descrição |
|-----------|------|-----------|
| `contentId` | Integer | Identificador do conteúdo |

**Resposta - Sucesso**: 
- **Estado**: `200 OK`
- **Tipo de Conteúdo**: `application/octet-stream`
- **Body**: Conteúdo binário do arquivo
- **Header**: `Content-Disposition: attachment; filename="filename.ext"`

**Resposta - Não Encontrado**:
- **Estado**: `404 Not Found`
- **Body**: `{"error": "Conteúdo não encontrado"}`

**Resposta - Erro de Armazenamento**:
- **Estado**: `400 Bad Request`
- **Body**: `{"error": "Erro ao acessar arquivo no Supabase Storage"}`

**Exemplo**:
```bash
curl http://localhost:8080/api/content/download/1 -o downloaded_file.mp4
```

---

## Pontos de Saída de Questionários

### 1. Iniciar Questionário
```http
GET /questionario/{examId}
```

**Descrição**: Inicia um exame/questionário e exibe primeira questão

**Parâmetros de Caminho**:
| Parâmetro | Tipo | Descrição |
|-----------|------|-----------|
| `examId` | UUID | Identificador do exame |

**Autenticação**: Não requerido mas recomendado

**Exemplo**:
```bash
curl http://localhost:8080/questionario/550e8400-e29b-41d4-a716-446655440000
```

**Resposta - Sucesso**: 
- **Estado**: `200 OK`
- **Tipo de Conteúdo**: `text/html`
- **Body**: questionario.html

**Atributos do Modelo**:
- `questao`: QuestionModel com:
  - `id`: ID da questão
  - `enunciado`: Texto da questão
  - `alternativas`: Array de opções de resposta
- `indiceAtual`: Índice da questão atual (começa em 0)
- `indiceProximo`: Índice da próxima questão
- `total`: Número total de questões

**Resposta - Não Encontrado**:
- **Estado**: `200 OK`
- **Body**: error.html
- **Modelo**: `error` = "Erro ao carregar a prova: ..."

---

### 2. Navegar e Enviar Resposta
```http
POST /questionario/proxima
```

**Descrição**: Avança para próxima questão, avalia resposta anterior, e gerencia pontuação

**Autenticação**: Requerido

**Parâmetros da Requisição**:
| Parâmetro | Tipo | Requerido | Descrição |
|-----------|------|-----------|-----------|
| `examId` | UUID | Sim | Identificador do exame |
| `indiceAtual` | Integer | Sim | Índice da questão atual |
| `resposta` | Integer | Sim | Índice da resposta selecionada |
| `pontuacao` | Integer | Sim | Pontuação acumulada atual |

**Exemplo**:
```bash
curl -X POST http://localhost:8080/questionario/proxima \
  -H "Cookie: JSESSIONID=..." \
  -d "examId=550e8400-e29b-41d4-a716-446655440000" \
  -d "indiceAtual=1" \
  -d "resposta=2" \
  -d "pontuacao=1"
```

**Resposta - Mais Questões Restantes**: 
- **Estado**: `200 OK`
- **Body**: questionario.html
- **Atributos do Modelo**: 
  - `questao`: Próxima questão
  - `indiceAtual`: Índice atualizado
  - `indiceProximo`: Próximo índice
  - `total`: Total de questões
  - `pontuacao`: Pontuação atualizada

**Resposta - Última Questão Completa**: 
- **Estado**: `200 OK`
- **Body**: questionario-result.html
- **Atributos do Modelo**:
  - `examName`: Nome do exame completo
  - `score`: Pontuação final (número de respostas corretas)
  - `total`: Pontos totais possíveis
  - `percent`: Pontuação percentual
  - `mensagem`: Mensagem de resultado
- **Efeito Colateral**: Resultado automaticamente salvo na tabela `report_entries`

---

## Pontos de Saída do Dashboard

### 1. Exibir Menu do Dashboard
```http
GET /menu
```

**Descrição**: Página principal do dashboard com lista de todas as matérias disponíveis

**Autenticação**: Requerido

**Exemplo**:
```bash
curl -H "Cookie: JSESSIONID=..." http://localhost:8080/menu
```

**Resposta**: 
- **Estado**: `200 OK`
- **Tipo de Conteúdo**: `text/html`
- **Body**: menu.html

**Atributos do Modelo**:
- `username`: Nome de usuário do usuário autenticado
- `subjects`: Lista de todos os objetos SubjectModel:
  - `id`: ID da disciplina
  - `subjectName`: Nome da disciplina
  - `slug`: Slug da disciplina (para roteamento /materia/{slug})

**Fluxo de Usuário**: Usuários clicam em uma disciplina para navegar para `/materia/{slug}`

---

### 2. Exibir Boletim de Desempenho
```http
GET /boletim
```

**Descrição**: Exibe boletim (relatório de notas) do usuário com estatísticas gerais

**Autenticação**: Requerido

**Exemplo**:
```bash
curl -H "Cookie: JSESSIONID=..." http://localhost:8080/boletim
```

**Resposta - Sucesso**: 
- **Estado**: `200 OK`
- **Tipo de Conteúdo**: `text/html`
- **Body**: boletim.html

**Atributos do Modelo**:
- `boletim`: BoletimDTO contendo:
  - `usuario`: Nome de usuário
  - `periodo`: Período acadêmico
  - `entries`: Lista de resultados de exames com:
    - `disciplina`: Nome da disciplina
    - `pontos`: Pontos obtidos
    - `totalPontos`: Total de pontos possíveis
    - `media`: Pontuação média
- `media`: Média geral (formatada para 2 casas decimais)

**Resposta - Erro**:
- **Estado**: `200 OK`
- **Body**: error.html
- **Modelo**: `error` = descrição do erro

---

## Jornada Completa do Usuário

### Fluxo Típico da Aplicação

```
1. GET /login                    → Exibir formulário de login
   
2. POST /login                   → Enviar credenciais
   username=student1
   password=secure123
   ↓
   Resposta: 302 → /menu

3. GET /menu                     → Exibir dashboard com disciplinas
   (com cookie JSESSIONID)
   ↓
   Resposta: Menu mostrando:
   - Matemática [link para /materia/matematica]
   - Português [link para /materia/portugues]
   - História [link para /materia/historia]

4. GET /materia/matematica       → Ver conteúdos e provas
   ↓
   Resposta: Página mostrando períodos com conteúdos e provas:
   
   1º Bimestre
   - Conteúdo: Algebra Básica [Download via /api/content/download/1]
   - Conteúdo: Equações [Download]
   - Prova: Quiz 1 [Clique para /questionario/{examId}]
   
   2º Bimestre
   - Conteúdo: Polinominais [Download]
   - Prova: Prova Intermediária [Clique para /questionario/{examId}]

5. GET /questionario/{examId}    → Iniciar exame, exibir primeira questão
   ↓
   Resposta: Questão 1 de 20 com opções

6. POST /questionario/proxima    → Enviar resposta, ir para próxima
   examId=...
   indiceAtual=0
   resposta=2
   pontuacao=0
   ↓
   Resposta: Questão 2 de 20 com opções

7. [Repetir passo 6 para questões 2-20]
   ↓
   Após última questão enviada:
   Resposta: Página de resultados mostrando:
   - Pontuação final: 17/20
   - Percentual: 85%
   - Mensagem: "Parabéns! Você passou!"

8. GET /boletim                  → Ver relatório de desempenho
   ↓
   Resposta: Página mostrando todos os resultados de exames e média geral

9. GET /logout                   → Fazer logout
   ↓
   Resposta: 302 → /login
```

---

## Testando a API

### Usando cURL

**Faça login e crie sessão**:
```bash
curl -c cookies.txt -X POST http://localhost:8080/login \
  -d "username=student1&password=secure123"
```

**Acesse pontos de saída protegidos**:
```bash
curl -b cookies.txt http://localhost:8080/menu
```

**Faça download de conteúdos**:
```bash
curl -b cookies.txt http://localhost:8080/api/content/download/1 -o file.mp4
```

### Usando Postman

1. Configure URL Base: `http://localhost:8080`
2. Habilite Cookie Jar: Configurações → Geral → Cookies
3. POST `/login` com dados de formulário (username, password)
4. Requisições subsequentes incluem automaticamente cookie de sessão

---

## Respostas de Erro

### Formatos de Erro Padrão

#### 400 Bad Request (Solicitação Inválida)
```json
{
  "error": "Bad Request",
  "message": "Parâmetros de requisição inválidos"
}
```

#### 401 Unauthorized (Não Autorizado)
```json
{
  "error": "Unauthorized",
  "message": "Autenticação requerida"
}
```

#### 404 Not Found (Não Encontrado)
```json
{
  "error": "Not Found",
  "message": "Recurso não encontrado"
}
```

#### 500 Internal Server Error (Erro Interno do Servidor)
```json
{
  "error": "Internal Server Error",
  "message": "Ocorreu um erro inesperado"
}
```

---

## Códigos de Estado HTTP

| Código | Nome | Descrição |
|--------|------|-----------|
| 200 | OK | Requisição bem-sucedida |
| 302 | Found | Redirecionamento (login, logout) |
| 400 | Bad Request | Parâmetros inválidos |
| 404 | Not Found | Recurso não existe |
| 500 | Server Error | Erro interno do servidor |

---

**Última Atualização**: 22 de Abril de 2026
**Versão da API**: 1.0
**Aplicação**: AfincoApp v0.0.1-SNAPSHOT