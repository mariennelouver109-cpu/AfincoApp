# AfincoApp - Documentação de Arquitetura

## Sumário
1. [Arquitetura do Sistema](#arquitetura-do-sistema)
2. [Arquitetura em Camadas](#arquitetura-em-camadas)
3. [Interações dos Componentes](#interacao-dos-componentes)
4. [Fluxo de Dados](#fluxo-de-dados)
5. [Esquema do Banco de Dados](#esquema-do-banco-de-dados)
6. [Arquitetura de Segurança](#arquitetura-de-seguranca)
7. [Arquitetura de Deployment](#arquitetura-de-deployment)

---

## Arquitetura de Sistema

### Visualização de Alto-nível

```
┌─────────────────────────────────────────────────────────────┐
│                Camada de Cliente (Web Browser)              │
│                                                             │
│  login.html  → cadastro.html → menu.html → conteudos.html   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
                        ↓ HTTP/HTTPS ↓
┌──────────────────────────────────────────────────────────────┐
│              Camada de Aplicação Spring Boot Web             │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐    │
│  │       Spring Security e Gerenciamento de Sessão      │    │
│  └──────────────────────────────────────────────────────┘    │
│                           ↓                                  │
│  ┌──────────────────────────────────────────────────────┐    │
│  │  Controladores (User, Content, Questionnaire, etc.)  │    │
│  └──────────────────────────────────────────────────────┘    │
│                           ↓                                  │
│  ┌──────────────────────────────────────────────────────┐    │
│  │     Services (Regras de Negócios e Operações)        │    │
│  └──────────────────────────────────────────────────────┘    │
│                           ↓                                  │
│  ┌──────────────────────────────────────────────────────┐    │
│  │    Camada de Acesso de Dados (Repositórios JPA)      │    │ 
│  └──────────────────────────────────────────────────────┘    │
│                           ↓                                  │
└──────────────────────────────────────────────────────────────┘
           ↓ JDBC/SQL ↓                    ↓ REST API ↓
     ┌──────────────────┐            ┌──────────────────┐
     │  PostgreSQL DB   │            │  Supabase Cloud  │
     │  (Supabase)      │            │  Storage (s3)    │
     └──────────────────┘            └──────────────────┘
```

---

## Arquitetura em Camadas

A aplicação segue um padrão de arquitetura em 4 camadas:

### Camada 1: Camada de Apresentação (MVC View)

**Componentes**:
- Modelos HTML Thymeleaf
- Estilização CSS

**Responsibilidade**:
- Renderização de interface de usuário
- Envio e apresentação de formulários
- Apresentação de dados

**Tecnologias**:
- Thymeleaf (motor de modelagem server-side)
- HTML5
- CSS3

**Arquivos**:
```
src/main/resources/templates/
├── login.html
├── cadastro.html
├── menu.html
├── perfil.html
├── conteudos.html
├── questionario.html
├── questionario-result.html
├── boletim.html
└── error.html
```

### Camada 2: Camada de Controle (MVC Controller)

**Componentes**:
- `UserController` - User management endpoints
- `ContentController` - Content management endpoints
- `QuestionnaireController` - Questionnaire handling
- `DashboardController` - Dashboard/menu display
- `SubjectController` - Subject management

**Responsibilidade**:
- Handle HTTP requests
- Route requests to services
- Return responses (views or JSON)
- Session management

**Processamento de Requisições**:
```
Requisição HTTP → Controller → Service → Repository → Database
```

**Anotações-chave**:
```java
@Controller              // Marca como controller MVC
@GetMapping("/path")     // Mapeia requisição GET do HTTP
@PostMapping("/path")    // Mapeia requisição POST do HTTP
@RequestParam            // Extrai parâmetros de formulário
@PathVariable            // Extrai path variables
@SessionAttribute        // Acessa atributos de sessão
```

### Camada 3: Camada de Serviço (Lógica de Negócios)

**Componentes**:
- `UserService` - Autenticação e operações de usuário
- `ContentService` - Lógica de gerenciamento de conteúdos
- `QuestionnaireService` - Operações de questionário
- `ExamService` - Gerenciamento de avaliações
- `SubjectService` - Operações de disciplinas
- `ReportEntriesService` - Criação de entradas de boletim
- `SupabaseStorageService` - Operações de Armazenamento em Nuvem

**Responsibilidades**:
- Implementa lógica de negócios
- Validação de dados
- Orquestra chamada de repositórios
- Lida com transações
- codificação de senhas e segurança

**Padrão de Desenho**:
```java
// padrão de Injeção de dependência
@Service
public class UserService {
    private final UserRepository repository;
    
    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }
}
```

### Camada 4: Camada de Acesso de Dados (Persistência)

**Componentes**:
- JPA Repositories
- Entity Models
- ORM (Hibernate)

**Responsibilidades**:
- Database queries
- CRUD operations
- Relationship management
- Transaction handling

**Repositórios**:
```
AfincoTeam/repository/
├── UserRepository
├── ContentRepository
├── ExamRepository
├── SubjectRepository
└── ReportEntriesRepository
```

**Modelos de Entidades**:
```
AfincoTeam/model/
├── UserModel
├── ContentModel
├── QuestionModel
├── QuestionnaireModel
├── ExamModel
├── SubjectModel
└── ReportEntriesModel
```

---

## Interações dos Componentes

### Diagrama de Fluxo de Autenticação

```
User
  ↓
  └→ GET /login
     ↓
  [LoginPage displayed]
     ↓
  User enters credentials
     ↓
  POST /login (username, password)
     ↓
  UserController.login()
     ↓
  UserService.authenticate(username, password)
     ↓
  UserRepository.findByUsername(username)
     ↓
  [Query to PostgreSQL]
     ↓
  BCryptPasswordEncoder.matches(inputPassword, storedHash)
     ↓
  If Match: UserService returns Optional[UserModel]
  If No Match: UserService returns Optional.empty()
     ↓
  UserController: If successful
  └→ session.setAttribute("loggedUser", username)
  └→ Redirect to /menu (302)
  └→ Set-Cookie: JSESSIONID
     ↓
  [User now authenticated]
```

### Fluxo de recuperação de conteúdos

```
User
  ↓
  GET /materia/{slug}
     ↓
  SubjectController.getSubjectContent()
     ↓
  SubjectService.getSubjectBySlug(slug)
  ContentService.getContentsBySubject()
  ExamService.getExamsBySubject()
     ↓
  [Hibernate generates SQL]
     ↓
  SELECT * FROM contents, exams WHERE subject_id = ?
     ↓
  [PostgreSQL executes]
     ↓
  [ResultSet mapped to ContentModel and ExamModel lists]
     ↓
  model.addAttribute("materia", subjectName)
  model.addAttribute("periods", periodDTOs)
     ↓
  conteudos.html rendered with data
     ↓
  [HTML response sent to browser]
```

### Questionnaire Submission Flow

```
User completes a question and submits answer
  ↓
  POST /questionario/proxima
  (examId, indiceAtual, resposta, pontuacao)
     ↓
  QuestionnaireController.nextQuestion()
     ↓
  QuestionnaireService.isAnswerCorrect()
  Evaluates if the previous answer was correct
     ↓
  Calculate new score (score + 1 if correct)
     ↓
  If more questions remain:
    → showQuestion() returns next question
    → User sees questionnaire.html with next question
  
  If last question completed:
    → ReportEntriesService.saveExamResult()
    → ReportEntriesRepository.save()
       ↓
    [Save to PostgreSQL report_entries table]
       ↓
    Redirect to display results
       ↓
    model.addAttribute("score", score)
    model.addAttribute("total", total)
    model.addAttribute("percent", percent)
       ↓
    questionario-result.html rendered
       ↓
    [Results displayed to user]
```

---

## Fluxo de Dados

### Complete User Journey Data Flow

```
1. REGISTRATION
   ┌─────────────────────┐
   │ User Registration   │
   │ Form Submission     │
   └─────────────────────┘
           ↓
   ┌─────────────────────┐
   │ UserController      │
   │ .cadastro()         │
   └─────────────────────┘
           ↓
   ┌─────────────────────┐
   │ UserService         │
   │ - Validate          │
   │ - Encode password   │
   │ - Check uniqueness  │
   └─────────────────────┘
           ↓
   ┌─────────────────────┐
   │ UserRepository      │
   │ .save(user)         │
   └─────────────────────┘
           ↓
   ┌─────────────────────┐
   │ PostgreSQL Database │
   │ INSERT INTO users   │
   └─────────────────────┘

2. LOGIN
   ┌─────────────────────┐
   │ Login Form Submit   │
   └─────────────────────┘
           ↓
   ┌─────────────────────┐
   │ UserController      │
   │ .login()            │
   └─────────────────────┘
           ↓
   ┌─────────────────────┐
   │ UserService         │
   │ .authenticate()     │
   │ - Match passwords   │
   └─────────────────────┘
           ↓
   ┌─────────────────────┐
   │ HttpSession         │
   │ Store username      │
   └─────────────────────┘

3. CONTENT ACCESS
   ┌─────────────────────┐
   │ View Contents       │
   │ GET /materia/{slug} │
   └─────────────────────┘
           ↓
   ┌─────────────────────┐
   │ SubjectController   │
   │ .getSubjectContent()│
   └─────────────────────┘
           ↓
   ┌─────────────────────┐
   │ SubjectService      │
   │ .getSubjectBySlug() │
   │ ContentService      │
   │ .getContentsBySubject()
   │ ExamService         │
   │ .getExamsBySubject()│
   └─────────────────────┘
           ↓
   ┌─────────────────────┐
   │ Repositories        │
   │ .findBySubjectId()  │
   └─────────────────────┘
           ↓
   ┌─────────────────────┐
   │ PostgreSQL Database │
   │ SELECT * FROM...    │
   │ WHERE subject_id=?  │
   └─────────────────────┘

4. QUESTIONNAIRE COMPLETION
   ┌─────────────────────┐
   │ Submit Answer       │
   │ POST /questionario/ │
   │        proxima      │
   └─────────────────────┘
           ↓
   ┌─────────────────────┐
   │ QuestionnaireController
   │ .nextQuestion()     │
   └─────────────────────┘
           ↓
   ┌─────────────────────┐
   │ QuestionnaireService│
   │ - Check answer      │
   │ - Calculate score   │
   └─────────────────────┘
           ↓
   [If last question]
           ↓
   ┌─────────────────────┐
   │ ReportEntriesService│
   │ .saveExamResult()   │
   └─────────────────────┘
           ↓
   ┌─────────────────────┐
   │ ReportEntriesRepository
   │ .save()             │
   └─────────────────────┘
           ↓
   ┌─────────────────────┐
   │ PostgreSQL Database │
   │ INSERT report entry │
   └─────────────────────┘

5. REPORT GENERATION
   ┌─────────────────────┐
   │ View Report         │
   │ GET /boletim        │
   └─────────────────────┘
           ↓
   ┌─────────────────────┐
   │ DashboardController │
   │ .boletim()          │
   └─────────────────────┘
           ↓
   ┌─────────────────────┐
   │ ReportEntriesService│
   │ .getBoletim()       │
   └─────────────────────┘
           ↓
   ┌─────────────────────┐
   │ ReportEntriesRepository
   │ .findByUsername()   │
   └─────────────────────┘
           ↓
   ┌─────────────────────┐
   │ PostgreSQL Database │
   │ SELECT report_entries
   │ WHERE username = ?  │
   └─────────────────────┘
           ↓
   ┌─────────────────────┐
   │ Create BoletimDTO   │
   │ (Data Transfer)     │
   └─────────────────────┘
           ↓
   ┌─────────────────────┐
   │ Render Template     │
   │ boletim.html        │
   └─────────────────────┘
```

---

## Esquema do Banco de Dados

### Relações Conceituais

```
┌──────────────────┐
│      users       │
├──────────────────┤
│ PK: id (UUID)    │
│    username (U)  │
│    password      │
└──────────────────┘
        │ 1:M
        ├────────────────────────────┐
        │                            │
        ↓                            ↓
┌────────────────────────┐    ┌──────────────────────┐
│    report_entries      │    │  (other relations)   │
├────────────────────────┤    └──────────────────────┘
│ PK: id (UUID)          │
│ FK: user_id            │
│ FK: exam_id            │
│ FK: subject_id         │
│    grade               │
│    is_approved         │
│    attempt             │
└────────────────────────┘

┌──────────────────┐
│     subjects     │
├──────────────────┤
│ PK: id (Integer) │
│    subject_name  │
│    slug (U)      │
└──────────────────┘
        │ 1:M
        │
        ├───────────────────────┐
        │                       │
        ↓                       ↓
┌──────────────────┐    ┌──────────────────────┐
│    contents      │    │      exams           │
├──────────────────┤    ├──────────────────────┤
│ PK: id (Integer) │    │ PK: id (UUID)        │
│ FK: subject_id   │    │ FK: subject_id       │
│    period        │    │    exam_name         │
│    content_name  │    │    period (Short)    │
│    type          │    │    questions_json    │
│    url           │    └──────────────────────┘
└──────────────────┘
```

### Esquema Físico

**tabela users**:
```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);
```

**tabela subjects**:
```sql
CREATE TABLE subjects (
    id INTEGER PRIMARY KEY,
    subject_name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL
);
```

**tabela contents**:
```sql
CREATE TABLE contents (
    id INTEGER PRIMARY KEY,
    subject_id INTEGER NOT NULL,
    period INTEGER,
    content_name VARCHAR(255) NOT NULL,
    type VARCHAR(50),
    url TEXT,
    FOREIGN KEY (subject_id) REFERENCES subjects(id)
);
```

**tabela exams**:
```sql
CREATE TABLE exams (
    id UUID PRIMARY KEY,
    exam_name VARCHAR(255) NOT NULL,
    period SMALLINT NOT NULL,
    questions_json TEXT NOT NULL,
    subject_id INTEGER NOT NULL,
    FOREIGN KEY (subject_id) REFERENCES subjects(id)
);
```

**tabela report_entries**:
```sql
CREATE TABLE report_entries (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    exam_id UUID NOT NULL,
    subject_id INTEGER NOT NULL,
    grade DOUBLE PRECISION,
    is_approved BOOLEAN,
    attempt VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (exam_id) REFERENCES exams(id),
    FOREIGN KEY (subject_id) REFERENCES subjects(id)
);
```

**Notas Importantes**:
- A tabela `exams` armazena `questions_json` como TEXT, que pode conter JSON inline ou um caminho para arquivo no Supabase Storage
- A tabela `questions` não existe; as questões estão embutidas na estrutura QuestionnaireModel, armazenadas na coluna `questions_json` do exame
- `report_entries` não possui coluna `user_id` nativa em JPA, mas é mapeada via ManyToOne relationship para UserModel

---

## Arquitetura de Segurança

### Arquitetura de autenticação

```
┌─────────────────────────────────────────┐
│    HTTP Request with Credentials        │
└─────────────────────────────────────────┘
           ↓
┌─────────────────────────────────────────┐
│   Spring Security Filter Chain          │
│  - CSRF Filter (disabled for dev)       │
│  - Session Filter                       │
│  - Authentication Filter                │
└─────────────────────────────────────────┘
           ↓
┌─────────────────────────────────────────┐
│   SecurityConfig                        │
│   @EnableWebSecurity                    │
│   - Todos requerimentos permitidos      │
│   - CSRF disabled                       │
└─────────────────────────────────────────┘
           ↓
┌─────────────────────────────────────────┐
│   UserController.login()                │
│   - Recebe username + password        │
└─────────────────────────────────────────┘
           ↓7
┌─────────────────────────────────────────┐
│   UserService.authenticate()            │
│   1. Query DB for user by username      │
│   2. If found, proceed to step 3        │
│   3. If not found, return Optional.empty
└─────────────────────────────────────────┘
           ↓
┌─────────────────────────────────────────┐
│   BCryptPasswordEncoder.matches()       │
│   - Compute hash of input password      │
│   - Compare with stored hash            │
│   - Return true/false                   │
└─────────────────────────────────────────┘
           ↓
        SUCCESS?
        /      \
       /        \
      YES        NO
      ↓          ↓
   Session   Error Message
   Created   Displayed
      ↓
   Redirect
   to /menu
```

### Segurança de senhas

```
User Registration:
┌────────────────┐
│ Plain Password │
└────────────────┘
       ↓
┌────────────────────────────────────┐
│ BCryptPasswordEncoder.encode()     │
│ - Generate salt                    │
│ - Hash password with salt (rounds) │
│ - Return: $2a$10$...               │
└────────────────────────────────────┘
       ↓
┌────────────────┐
│ Hashed Password│
│ $2a$10$....... │
└────────────────┘
       ↓
[Store in Database]

User Login:
┌────────────────┐
│ Input Password │
└────────────────┘
       ↓
┌────────────────────────────────────┐
│ BCryptPasswordEncoder.matches()     │
│ - Hash input password with         │
│   salt from stored hash            │
│ - Compare computed hash with stored│
└────────────────────────────────────┘
       ↓
[Match/No Match]
```

### Gerenciamento de sessões

```
Session Lifecycle:
┌─────────────────────────────────┐
│ User Logs In Successfully       │
└─────────────────────────────────┘
       ↓
┌─────────────────────────────────┐
│ session.setAttribute()          │
│ "loggedUser" = username         │
└─────────────────────────────────┘
       ↓
┌─────────────────────────────────┐
│ Server generates JSESSIONID     │
│ Sends Set-Cookie header         │
└─────────────────────────────────┘
       ↓
┌─────────────────────────────────┐
│ Browser stores JSESSIONID       │
│ in cookie jar                   │
└─────────────────────────────────┘
       ↓
┌─────────────────────────────────┐
│ Each subsequent request         │
│ includes Cookie: JSESSIONID=... │
└─────────────────────────────────┘
       ↓
┌─────────────────────────────────┐
│ Session timeout: 8 hours        │
│ (28800 seconds)                 │
└─────────────────────────────────┘
       ↓
┌─────────────────────────────────┐
│ Session expires or logout       │
│ User redirected to /login       │
└─────────────────────────────────┘
```

---

## Arquitetura de Deployment

### Estrutura da Aplicação Spring Boot

```
┌───────────────────────────────────────┐
│        Spring Boot Application        │
│                                       │
│  ┌─────────────────────────────────┐  │
│  │ Servidor Tomcat Embedded        │  │
│  │ (Port 8080 default)             │  │
│  └─────────────────────────────────┘  │
│           ↓                           │
│  ┌─────────────────────────────────┐  │
│  │ Contexto de Aplicação Spring    │  │
│  │ - Bean Management               │  │
│  │ - Dependency Injection          │  │
│  │ - Configuration                 │  │
│  └─────────────────────────────────┘  │
│           ↓                           │
│  ┌─────────────────────────────────┐  │
│  │ Servlet do Dispatcher           │  │
│  │ (Spring MVC Front Controller)   │  │
│  └─────────────────────────────────┘  │
│           ↓                           │
│  ┌─────────────────────────────────┐  │
│  │ Handler Mapping e Controllers   │  │
│  │ - Route requests                │  │
│  │ - Executa Regra de Negócios     │  │
│  └─────────────────────────────────┘  │
│                                       │
└───────────────────────────────────────┘
         ↓              ↓              ↓
   PostgreSQL    Supabase Storage  Static Files
      (DB)          (S3)          (CSS/JS)
```

### Runtime Configuration

**Application Properties Hierarchy**:
```
1. application.properties
   ├── Database configuration
   ├── JPA/Hibernate settings
   ├── Session configuration
   └── Supabase settings

2. Environment Variables (optional)
   └── Override properties

3. Command-line Arguments (optional)
   └── Override environment
```

### Sequência de Iniciação

```
1. JVM starts
2. Spring Boot's SpringApplication.run() called
3. Spring Context initialization
4. Bean creation and wiring
5. DataSource configured (PostgreSQL)
6. JPA/Hibernate initialized
7. Embedded Tomcat started on port 8080
8. Application ready for requests
```

---

## Tratamento de Erros

### Fluxo de Erro Actual

A aplicação utiliza try-catch em controllers para tratar exceções:

```
Requisição do Usuário
       ↓
Controller recebe requisição
       ↓
Try: Controller chama Service
       ↓
  ├─ Sucesso: Retorna model com dados
  └─ Erro: Service lança exceção
       ↓
Catch: Controller captura exceção
       ↓
Controller adiciona mensagem de erro ao model
       ↓
Return view (geralmente a mesma página)
       ↓
Thymeleaf renderiza com atributo "erro"
       ↓
Usuário vê mensagem de erro na página
```

### Cenários de Erro Comuns

**Cenário 1: Login Inválido**
```
POST /login
    ↓
UserController.login()
    ↓
service.login() retorna false
    ↓
model.addAttribute("erro", "Login inválido")
    ↓
Retorna view "login"
    ↓
Usuário vê mensagem de erro
```

**Cenário 2: Registro de Usuário com Erro**
```
POST /cadastro
    ↓
UserService.register() lança IllegalArgumentException
    ↓
Catch bloco captura exceção
    ↓
model.addAttribute("erro", ex.getMessage())
    ↓
Retorna view "cadastro"
    ↓
Erro exibido para o usuário
```

**Cenário 3: Recurso Não Encontrado**
```
GET /materia/inexistente
    ↓
SubjectService.getSubjectBySlug() lança exceção
    ↓
Controller retorna error.html
    ↓
Página de erro exibida
```

**Cenário 4: Prova/Exame Não Encontrado**
```
GET /questionario/invalid-id
    ↓
QuestionnaireService falha ao carregar
    ↓
model.addAttribute("error", mensagem)
    ↓
Retorna error.html
```

---

## Características de Implementação

### Limitações Atuais
- Single instance deployment (sem load balancing)
- Session stored na memória da aplicação
- Sem camada de cache
- Sem replicação de banco de dados

---

## Padrões de Design Utilizados

### 1. **Padrão MVC**
- Controllers lidam com requisições HTTP
- Services implementam lógica de negócio
- Views renderizam respostas via Thymeleaf

### 2. **Padrão de Repositório**
- Repositories abstraem acesso a dados
- Repositórios do Spring Data JPA para CRUD

### 3. **Injeção de Dependências**
- Injeção por construtor (constructor-based)
- Spring Container gerencia dependências

### 4. **Padrão da Camada de Serviços**
- Services contêm lógica de negócio
- Controllers delegam para services

### 5. **Padrão dos Modelo de Entidade/Domínio**
- Modelos JPA representam entidades do banco de dados
- Mapeamento automático de banco para objetos

---

## Otimizações de Performance

### Configurações de Banco de Dados Implementadas
- **Connection Pooling**: HikariCP com max pool size = 5
- **Batch Operations**: batch_size=20 para JPA
- **Query Result Fetching**: fetch_size=50
- **Idle Timeout**: 10 minutos para reciclagem de conexões
- **Max Lifetime**: 30 minutos vida máxima da conexão

### Estratégia de Armazenamento
- Integração com Supabase Storage para arquivos
- Compressão e otimização de arquivos no lado do cliente
- URLs públicas do Supabase são cacheadas no navegador

### Otimizações de Frontend
- Stylesheet único (style.css) incluído em todas as páginas
- Thymeleaf para renderização otimizada no servidor
- Templates HTML5 para melhor performance de parser

---

**Última Atualização**: April 23, 2026
**Versão**: 1.0
**Aplicação**: AfincoApp v0.0.1-SNAPSHOT
