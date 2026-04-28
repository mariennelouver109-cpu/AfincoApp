# AfincoApp - Plataforma de Acesso Educacional

## Visão Geral

**AfincoApp** é uma aplicação web Spring Boot projetada para gerenciar conteúdo educacional, questionários e avaliações de usuários. A plataforma permite que educadores e administradores criem e distribuam questionários, enquanto os alunos podem realizar avaliações e receber feedback de desempenho.

### Principais Funcionalidades

- **Gerenciamento do Usuário**: Assegurar autenticação de usuário e gerenciamento de perfil.
- **Gerenciamento de Questionários**: Criar, organizar, e distribuir questionários.
- **Gerenciamento de Conteúdos**: Organizar e disponibilizar conteúdo por disciplina e etapa.
- **Gerenciamento de Disciplinas**: Organização hierarquizada por disciplina e etapa.
- **Gerenciamento de Progresso**: Monitorar o desempenho do exame e gerar relatórios
- **Armazenamento em Nuvem**: Integração com Supabase para armazenamento e arquivos.
- **Autenticação Baseada em Sessão**: Gerenciamento de sessão HTTP para autenticação de usuários

---

## Arquitetura

### Stack Tecnológica

| Componente | Tecnologia | Versão |
|-----------|-----------|---------|
| **Framework** | Spring Boot | 4.0.5 |
| **Java Version** | Java | 17 |
| **ORM** | Spring Data JPA | 4.0.5 |
| **Security** | Spring Security | Latest |
| **Template Engine** | Thymeleaf | 4.0.5 |
| **Database** | PostgreSQL | Latest |
| **Storage** | Supabase Storage | Cloud |
| **Build Tool** | Maven | 3.9+ |

### Estrutura do Projeto

```
AfincoApp/
├── src/
│   ├── main/
│   │   ├── java/AfincoTeam/
│   │   │   ├── AfincoAppApplication.java       # Ponto de entrada principal da aplicação Spring Boot
│   │   │   ├── config/                         # Classes de Configuração
│   │   │   │   ├── SecurityConfig.java         # Configuração do Spring Security
│   │   │   │   └── SupabaseStorageConfig.java  # configuração de armazenamento no Supabase
│   │   │   ├── controller/                     # Controllers MVC
│   │   │   │   ├── UserController.java         # Autenticação de usuário e perfil
│   │   │   │   ├── ContentController.java      # Gerenciamento de conteúdo educacional
│   │   │   │   ├── QuestionnaireController.java # Gerenciamento de questionário
│   │   │   │   ├── DashboardController.java    # Display de dashboard
│   │   │   │   └── SubjectController.java      # Gerenciamento de disciplinas
│   │   │   ├── model/                          # Modelos de entidades JPA
│   │   │   │   ├── UserModel.java              # Entidade de usuário
│   │   │   │   ├── QuestionnaireModel.java     # Estrutura de questionário
│   │   │   │   ├── QuestionModel.java          # Questões individuais
│   │   │   │   ├── ContentModel.java           # Conteúdo educacional
│   │   │   │   ├── ExamModel.java              # Entidade de avaliação
│   │   │   │   ├── SubjectModel.java           # Entidade de disciplina
│   │   │   │   └── ReportEntriesModel.java     # Entidade de entrada de relatório
│   │   │   ├── repository/                     # Repositórios Spring Data JPA
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── ContentRepository.java
│   │   │   │   ├── ExamRepository.java
│   │   │   │   ├── SubjectRepository.java
│   │   │   │   └── ReportEntriesRepository.java
│   │   │   ├── Services/                       # Serviços de regras de negócio
│   │   │   │   ├── UserService.java            # autenticação e gerenciamento de usuários
│   │   │   │   ├── ContentService.java         # Operações de conteúdos
│   │   │   │   ├── QuestionnaireService.java   # Lógica de questionário
│   │   │   │   ├── ExamService.java            # Gerenciamento de avaliações
│   │   │   │   ├── SubjectService.java         # Operações de disciplinas
│   │   │   │   ├── ReportEntriesService.java   # Criação de entrada de relatório
│   │   │   │   └── SupabaseStorageService.java # Operações de Armazenamento em Nuvem
│   │   │   └── dto/                            # Objetos de Transferência de Dados
│   │   │       ├── BoletimDTO.java             # DTO de Relatório/Boletim
│   │   │       └── BoletimEntryDTO.java        # DTO de entrada de Boletim
│   │   ├── resources/
│   │   │   ├── application.properties          # Configurações da Aplicação
│   │   │   ├── static/
│   │   │   │   └── style.css                   # Stylesheet
│   │   │   └── templates/                      # Templates HTML do Thymeleaf
│   │   │       ├── login.html                  # Página de Login do Usuário
│   │   │       ├── cadastro.html               # Página de registro de Usuário
│   │   │       ├── menu.html                   # Menu de Navegação Principal
│   │   │       ├── perfil.html                 # Página de perfil do Usuário
│   │   │       ├── conteudos.html              # Página de listagem de conteúdo
│   │   │       ├── questionario.html           # Página de questionário
│   │   │       ├── questionario-result.html    # Resultado de questionário
│   │   │       ├── boletim.html                # Página de Boletim
│   │   │       └── error.html                  # Tela genérica de erro
│   └── test/
│       └── java/AfincoTeam/AfincoApp/          # Diretório de testes unitários
├── pom.xml                                     # Configuração do projeto Maven
├── mvnw / mvnw.cmd                             # Maven wrapper scripts
└── target/                                     # Saída compilada
```

---

## Componentes Principais

### Models (Entidades)

#### **UserModel**
- **Propósito**: Representa usuários da aplicação
- **Campos Principais**: 
  - `id` (UUID): Identificador único de usuário
  - `username` (String): nome de usuário
  - `password` (String): senha codada em BCrypt
- **tabela no BD**: `users`

#### **QuestionnaireModel**
- **Propósito**: Contâiner para dados de questionário
- **Campos Principais**: Lista de objetos `QuestionModel`
- **Métodos**: 
  - `getTotalQuestions()`: Retorna contagem de questões
  - `getQuestao(index)`: Retorna questão específica

#### **ContentModel**
- **Propósito**: Representa conteúdo educacional (vídeos e PDFs)
- **Campos Principais**:
  - `id` (Integer): Identificador de conteúdo
  - `subjectId` (Integer): Disciplinas associadas
  - `period` (Integer): Etapa pedagógica
  - `contentName` (String): Título do conteúdo
  - `type` (String): Tipo de conteúdo (link, pdf, etc.)
  - `url` (String): URL de acesso
- **Tabela no BD**: `contents`

#### **Outros Modelos**
- **ExamModel**: Dados de acesso
- **SubjectModel**: informação de Disciplinas
- **QuestionModel**: Acesso de quesões individuais
- **ReportEntriesModel**: Dados de acompanhamento de progresso

### Services (Serviços)

#### **UserService**
- trabalha com registros e autenticações
- Usa BCryptPasswordEncoder para a segurança de senhas
- Métodos:
  - `register(username, password)`: Cria novo usuário
  - `authenticate(username, password)`: Verifica credenciais
  - `login(username, password)`: login da sessão

#### **ContentService**
- Gerencia operações de conteúdos educacionais
- Leitura de conteúdos

#### **QuestionnaireService**
- Gerencia e lê questionários

#### **SupabaseStorageService**
- Integra ao Armazenamento em Nuvem do Supabase
- Retorna arquivos e links do armazém

#### **ReportEntriesService**
- Cria relatórios de performance
- Monitora dados de avaliação do usuário

### Controllers (Controladores)

#### **UserController**
- **Login**: `GET/POST /login` - Autenticações de Usuários
- **Registration**: `POST /cadastro` - Novos registros
- **Profile**: `GET/POST /perfil` - Gerenciamento de perfis de Usuário

#### **ContentController**
- Listagem e retorno de conteúdos
- filtragem de conteúdo baseado em disciplinas.

#### **QuestionnaireController**
- Apresentação de questionários
- Leitura de respostas e retorno de notas
- Apresentação de resultados

#### **DashboardController**
- Apresentação do menu
- Navegação

#### **SubjectController**
- Listagem de disciplinas
- Organização de disciplinas

---

## Security (Segurança)

### Configuração
- **Spring Security**: Permite segurança web para a aplicação
- **CSRF Protection**: Desativado para permitir autenticação baseada em sessão
- **Password Encoding**: BCryptPasswordEncoder garante armazenamento seguro de senhas
- **Session Management**: limite de sessão de 8 horas configurado em `application.properties`

### Fluxo de Autenticação
1. Usuário envia credenciais por formulário
2. `UserController.login()` recebe pedido
3. `UserService.authenticate()` verifica credenciais
4. BCrypt compara senhas entregues com hash guardado
5. Autenticação bem-sucedida armazena usuário em sessão HTML
6. Usuário é redirecionado à tela de login

---

## Database (Banco de dados)

### Detalhes de Conexão
- **Tipo**: PostgreSQL
- **Provedor**: Supabase (Cloud)
- **Host**: `aws-1-sa-east-1.pooler.supabase.com`
- **Porta**: 5432
- **Região**: South America (São Paulo)

### Configuração
- **Hibernate DDL**: `update` - Automatically creates/updates schema
- **Connection Pooling**: HikariCP with optimized settings
- **Batch Size**: 20 (JPA batch operations)
- **Fetch Size**: 50 (Query result batching)

### Configuração de Pool de Conexões
| Setting | Value| Purpose |
|--------------|------|-----------|
| Max Pool Size | 5 | Máximo de conexões simultâneas |
| Min Idle | 2 | Mínimo de conexões ociosas |
| Idle Timeout | 10 min | Reciclagem de Conexão |
| Max Lifetime | 30 min | vida útil máxima da conexão |
| Connection Timeout | 60 sec | Tempo máximo para estabelecer conexão |

---

## Armazenamento em Nuvem

### Integração ao Supabase
- **Serviço**: Supabase Storage (REST)
- **Bucket**: `AfincoStorage`
- **Região**: Supabase hosted region
- **Configuração**: Gerenciado via `SupabaseStorageConfig.java`

### SupabaseStorageService
Responsável por:
- Recuperar conteúdo
- Gerenciar metadados

---

## Frontend

### Motor de Templates
- **Framework**: Thymeleaf
- **Localização**: `src/main/resources/templates/`

### Páginas
| Page | Purpose | Route |
|------|---------|-------|
| login.html | User authentication | `/login` |
| cadastro.html | User registration | `/cadastro` |
| menu.html | Main navigation | `/menu` |
| perfil.html | User profile | `/perfil` |
| conteudos.html | Content listing | `/conteudos` |
| questionario.html | Questionnaire form | `/questionario` |
| questionario-result.html | Results display | `/questionario-result` |
| boletim.html | Performance report | `/boletim` |
| error.html | Error handling | `/error` |

### Estilização
- **Stylesheet**: `src/main/resources/static/style.css`
- Provides consistent UI/UX across all pages

---

## Setup e Instalação

### Pré-requisitos
- Java 17 or higher
- Maven 3.9 or higher
- Conexão PostgreSQL (ou conta Supabase)
- Git

### Setup do Ambiente 

#### 1. Clone o Repositório
```bash
git clone <repository-url>
cd AfincoApp
```

#### 2. Configure Conexão com Banco de Dados
Edite `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://your-host:5432/your-db
spring.datasource.username=your-username
spring.datasource.password=your-password
```

#### 3. Configure Supabase Storage
Faça update do seguinte em `application.properties`:
```properties
supabase.storage.url=your-supabase-url
supabase.storage.api-key=your-api-key
supabase.storage.bucket-name=your-bucket-name
supabase.storage.project-id=your-project-id
```

#### 4. Construa a Aplicação
```bash
# Usando Maven wrapper (Windows)
mvnw.cmd clean install

# Usando Maven wrapper (Linux/Mac)
./mvnw clean install

# Usando system Maven
mvn clean install
```

#### 5. Rode a Aplicação
```bash
# Usando Maven
mvn spring-boot:run

# Ou rodando o arquivo JAR
java -jar target/AfincoApp-0.0.1-SNAPSHOT.jar
```

A aplicação estará disponível em: `http://localhost:8080`

---

## Guia de Uso

### Fluxo de Trabalho do Usuário

1. **Registro**
   - Navegue para a tela de registro
   - Enter username and password
   - Credentials are encrypted and stored

2. **Login**
   - Acesse a tela de login
   - Insira credenciais
   - Sessão é estabelecida por até 8 horas

3. **Dashboard**
   - Veja menu principal com as operações possíveis
   - Navegue por matérias, perfil de usuário, ou boletim.

4. **Acesso à Conteúdos**
   - Procure por conteúdos por matéria/disciplina
   - Veja ou faça download dos conteúdos
   - Monitore o progresso

5. **Completação de Questionário**
   - Selecione um questionário disponível
   - Responda às perguntas
   - Envie as respostas
   - Veja os resultados detalhados

6. **Relatórios de Performance**
   - Acesse a tela de boletim
   - Veja as métricas de desempenho
   - Monitore auto-desenvolvimento ao longo do tempo.

---

## Desenvolvimento

### Configuração de Projeto
- **Compilador JAVA**: Java 17
- **Versão do Spring Boot**: 4.0.5
- **Build Tools**: Maven 3.9+
- **Gerenciamento de Dependências**: Spring Boot Parent POM

### Comandos Maven Comuns
```bash
# construção limpa
mvn clean install

# Roda testes
mvn test

# pula testes durante limpeza
mvn clean install -DskipTests

# Roda a aplicação
mvn spring-boot:run

# Cria JAR
mvn package
```

### Adicionando Dependências
Edite `pom.xml` e adicione dependências na sessão `<dependencies>`, então rode a aplicação:
```bash
mvn dependency:resolve
```

---

## Relacionamentos de Modelos de Dados

```
Users (1) ──→ (Muitos) Exams
     ↓
     └──→ (Muitos) Report_entries

Subjects (1) ──→ (Muitos) Contents
      ↓
      └──→ (Muitos) Questions

Exams (1) ──→ (Muitos) Questions
```

---

## Troubleshooting

### Problemas Comuns

| Problema | Solução |
|----------|---------|
| **Falha de Conexão com Banco de Dados** | Verifique PostgreSQL/Supabase credentials in `application.properties` |
| **Template Não Encontrada** | garanta que as templates estejam em `src/main/resources/templates/` |
| **Erro no Armazenamento Supabase** | verifique a chave da API e a configuração de nome do bucket |
| **Sessão Acabou** | Padrão são 8 horas; ajuste `server.servlet.session.timeout` se necessário |
| **Porta já em Uso** | Mude a porta em properties: `server.port=8081` |

---

## Documentação da API

Veja [API_DOCUMENTATION.md](./API_DOCUMENTATION.md) para especificações detalhadas dos pontos de saída.

---

## Detalhes de Arquitetura

Veja [ARCHITECTURE.md](./ARCHITECTURE.md) para descrição arquitetural detalhada.

---

## Dependências

### Spring Boot Starters
- `spring-boot-starter-data-jpa` - Acessar de Banco de Dados
- `spring-boot-starter-security` - Framework de Segurança
- `spring-boot-starter-thymeleaf` - Template engine
- `spring-boot-starter-webmvc` - Framework MVC web
- `spring-boot-starter-data-jpa-test` - Testar utilidades

### Bibliotecas Adicionais
- `jackson-databind` - Processamento de JSON
- `spring-security-crypto` - Criptografia de senha
- `postgresql` - Driver para Banco de Dados
- `sqlite-jdbc` - Suporte SQLite
- `spring-boot-devtools` - Utilidades para Desenvolvimento

---

## Licença e Distribuição

Este projeto foi desenvolvido por **AfincoTeam**.

---

## Contribuições

Para contribuições, por favor:
1. Crie uma ramificação
2. Faça as suas mudanças
3. Testes atenciosamente
4. Submeta uma Pull Request

---

## Suporte

Em caso de dúvidas, perguntas, ou sugestões, entre em contato com o time de desenvolvimento AfincoTeam: pcctprojeto@gmail.com

---

---

**Última Atualização**: 22 de Abril, 2026.
**Versão**: 0.0.1-SNAPSHOT