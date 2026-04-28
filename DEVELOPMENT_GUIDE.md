# AfincoApp - Guia do Desenvolvedor

## Sumário
1. [Organização do Código](#organizacao-do-codigo)
2. [Classes-Chave e Seus Papéis](#classes-chave-e-seus-papeis)
3. [Adicionando Novas Funcionalidades](#adicionando-novas-funcionalidades)
4. [Trabalhando com Serviços](#trabalhando-com-serviços)
5. [Operações de Banco de Dados](#operações-de-banco-de-dados)
6. [Desenvolvimento Frontend](#desenvolvimento-frontend)
7. [Testes](#testes)
8. [Estilo de Código e Melhores Práticas](#estilo-de-codigo-e-melhores-praticas)
9. [Tarefas Comuns de Desenvolvimento](#tarefas-comuns-de-desenvolvimento)

---

## Organização do Código

### Estrutura de Diretórios

```
src/main/java/AfincoTeam/
│
├── AfincoAppApplication.java           # Ponto de entrada da aplicação Spring Boot
│
├── config/                             # Classes de configuração
│   ├── SecurityConfig.java            # Configuração do Spring Security
│   └── SupabaseStorageConfig.java     # Configuração de armazenamento em nuvem
│
├── controller/                         # Controladores MVC
│   ├── UserController.java            # Autenticação e perfil de usuário
│   ├── ContentController.java         # Gerenciamento de conteúdo
│   ├── QuestionnaireController.java   # Questionários
│   ├── DashboardController.java       # Dashboard e menu
│   └── SubjectController.java         # Matérias/Disciplinas
│
├── model/                              # Entidades JPA
│   ├── UserModel.java
│   ├── ContentModel.java
│   ├── QuestionModel.java
│   ├── QuestionnaireModel.java
│   ├── ExamModel.java
│   ├── SubjectModel.java
│   └── ReportEntriesModel.java
│
├── repository/                         # Camada de Acesso a Dados
│   ├── UserRepository.java
│   ├── ContentRepository.java
│   ├── ExamRepository.java
│   ├── SubjectRepository.java
│   └── ReportEntriesRepository.java
│
├── Services/                           # Lógica de Negócios
│   ├── UserService.java
│   ├── ContentService.java
│   ├── QuestionnaireService.java
│   ├── ExamService.java
│   ├── SubjectService.java
│   ├── ReportEntriesService.java
│   └── SupabaseStorageService.java
│
└── dto/                                # Objetos de Transferência de Dados
    ├── BoletimDTO.java
    └── BoletimEntryDTO.java
```

---

## Classes-Chave e Seus Papéis

### Classe Principal da Aplicação

**Arquivo**: `AfincoAppApplication.java`

```java
@SpringBootApplication
public class AfincoAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(AfincoAppApplication.class, args);
    }
}
```

**Propósito**: 
- Ponto de entrada da aplicação Spring Boot
- Habilita auto-configuração
- Realiza varredura de componentes no pacote

### Controladores

#### UserController

**Responsabilidades**:
- Gerenciar login/logout
- Gerenciar registro de usuários
- Gerenciar perfil de usuário
- Deletar usuário

**Métodos-Chave**:
```java
@GetMapping("/login")
public String loginPage() { ... }

@PostMapping("/login")
public String login(@RequestParam String username,
                    @RequestParam String password,
                    Model model, HttpSession session) { ... }

@PostMapping("/cadastro")
public String cadastro(@RequestParam String username,
                       @RequestParam String password,
                       Model model) { ... }

@PostMapping("/delete")
public String delete(HttpSession session) { ... }
```

#### ContentController

**Responsabilidades**:
- Exibir conteúdo educacional
- Recuperar conteúdo por ID
- Listar conteúdos
- Download de conteúdo

**Métodos-Chave**:
```java
@GetMapping("/conteudos")
public String listContents(Model model) { ... }

@GetMapping("/conteudos/{id}")
public String getContent(@PathVariable Integer id, Model model) { ... }

@GetMapping("/conteudos/{id}/download")
public ResponseEntity<ByteArrayResource> downloadContent(@PathVariable Integer id) { ... }
```

#### QuestionnaireController

**Responsabilidades**:
- Exibir questionários
- Processar submissões
- Mostrar resultados

**Métodos-Chave**:
```java
@GetMapping("/questionario")
public String displayQuestionnaire(@RequestParam Integer id, Model model) { ... }

@PostMapping("/questionario")
public String submitAnswers(@RequestParam Integer questionnaireId,
                            @RequestParam String[] answers) { ... }

@GetMapping("/questionario-result")
public String displayResults(Model model) { ... }
```

### Serviços

#### UserService

**Responsabilidades-Chave**:
- Autenticação de usuários
- Validação de senha
- Registro de usuários
- Alteração de senha
- Atualização de perfil

**Métodos Importantes**:
```java
public UserModel register(String username, String senha) { ... }

public Optional<UserModel> authenticate(String username, String password) { ... }

public boolean login(String username, String password) { ... }

public void changePassword(String username, String currentPassword, String newPassword) { ... }

public void update(UUID id, String novoUsername) { ... }
```

**Características de Segurança**:
- Usa BCryptPasswordEncoder para criptografia de senhas
- Valida unicidade de nome de usuário
- Valida força da senha

#### ContentService

**Responsabilidades-Chave**:
- Operações CRUD de conteúdo
- Recuperação de conteúdo por matéria/período
- Download de arquivos de conteúdo
- Geração de URLs públicas

**Métodos Típicos**:
```java
public ContentModel getContent(Integer contentId) { ... }

public List<ContentModel> getContentsBySubject(Integer subjectId) { ... }

public byte[] downloadContent(Integer contentId) { ... }

public String getPublicUrl(Integer contentId) { ... }
```

#### QuestionnaireService

**Responsabilidades-Chave**:
- Gerenciamento de questionários
- Avaliação de respostas
- Cálculo de pontuação

**Métodos-Chave**:
```java
public QuestionnaireModel getQuestionnaire(Integer id) { ... }

public Integer evaluateAnswers(List<String> answers) { ... }

public Integer calculateScore(List<String> answers) { ... }
```

#### SupabaseStorageService

**Responsabilidades-Chave**:
- Upload de arquivos para armazenamento em nuvem
- Recuperação de arquivos
- Geração de URLs públicas
- Deleção de arquivos

**Métodos-Chave**:
```java
public String uploadFile(MultipartFile file, String contentType) { ... }

public byte[] downloadFile(String fileName) { ... }

public String getPublicFileUrl(String fileName) { ... }

public void deleteFile(String fileName) { ... }
```

### Modelos (Entidades JPA)

#### UserModel

```java
@Entity
@Table(name = "users")
public class UserModel {
    @Id
    private UUID id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    // getters/setters
}
```

#### ContentModel

```java
@Entity
@Table(name = "contents")
public class ContentModel {
    @Id
    private Integer id;
    
    @Column(name = "subject_id")
    private Integer subjectId;
    
    @Column(name = "period")
    private Integer period;
    
    @Column(name = "content_name")
    private String contentName;
    
    private String type;
    private String url;
    
    // getters/setters
}
```

### Repositórios

**Exemplo**: UserRepository

```java
public interface UserRepository extends JpaRepository<UserModel, UUID> {
    Optional<UserModel> findByUsername(String username);
}
```

---

## Adicionando Novas Funcionalidades

### Cenário: Adicionar um Novo Tipo de Conteúdo

#### Passo 1: Atualizar Modelo (se necessário)

Nenhuma alteração necessária em `ContentModel.java` - o campo `type` já suporta diferentes tipos.

#### Passo 2: Atualizar Controlador

```java
// Em ContentController.java

@PostMapping("/conteudos/create")
public String createContent(@RequestParam String contentName,
                            @RequestParam Integer subjectId,
                            @RequestParam Integer period,
                            @RequestParam String type,
                            @RequestParam String url,
                            Model model) {
    try {
        ContentModel content = new ContentModel();
        content.setContentName(contentName);
        content.setSubjectId(subjectId);
        content.setPeriod(period);
        content.setType(type);
        content.setUrl(url);
        
        ContentService.saveContent(content);
        return "redirect:/conteudos";
    } catch (Exception e) {
        model.addAttribute("error", "Erro ao criar conteúdo");
        return "conteudos";
    }
}
```

#### Passo 3: Adicionar Método de Serviço

```java
// Em ContentService.java

public ContentModel createNewContent(ContentModel content) {
    validateContent(content);
    return contentRepository.save(content);
}

private void validateContent(ContentModel content) {
    if (content.getContentName() == null || content.getContentName().isEmpty()) {
        throw new IllegalArgumentException("Nome do conteúdo não pode estar vazio");
    }
    if (content.getUrl() == null || content.getUrl().isEmpty()) {
        throw new IllegalArgumentException("URL do conteúdo não pode estar vazia");
    }
}
```

#### Passo 4: Criar Template

Criar `src/main/resources/templates/create-content.html`:

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Criar Conteúdo</title>
    <link rel="stylesheet" th:href="@{/style.css}">
</head>
<body>
    <form th:action="@{/conteudos/create}" method="POST">
        <input type="text" name="contentName" placeholder="Nome do Conteúdo" required>
        <input type="number" name="subjectId" placeholder="ID da Matéria" required>
        <input type="number" name="period" placeholder="Período" required>
        <input type="text" name="type" placeholder="Tipo (vídeo, pdf, etc)" required>
        <input type="text" name="url" placeholder="URL do Conteúdo" required>
        <button type="submit">Criar</button>
    </form>
</body>
</html>
```

---

## Trabalhando com Serviços

### Padrão de Design de Serviço

```java
@Service
public class SeuNovoServico {
    
    private final SeuRepositorio repositorio;
    private final OutroServico outroServico;  // Injetar outros serviços
    
    // Injeção de construtor (recomendado)
    public SeuNovoServico(SeuRepositorio repositorio,
                         OutroServico outroServico) {
        this.repositorio = repositorio;
        this.outroServico = outroServico;
    }
    
    // Métodos de lógica de negócios
    public SeuModelo realizarOperacao(String parametro) {
        // Validar entrada
        validarEntrada(parametro);
        
        // Chamar repositório
        SeuModelo modelo = repositorio.findByParametro(parametro);
        
        // Lógica adicional
        modelo.setProcessadoEm(LocalDateTime.now());
        
        // Chamar outros serviços se necessário
        outroServico.fazerAlgo(modelo);
        
        // Persistir e retornar
        return repositorio.save(modelo);
    }
    
    // Métodos de validação
    private void validarEntrada(String parametro) {
        if (parametro == null || parametro.isEmpty()) {
            throw new IllegalArgumentException("Parâmetro não pode estar vazio");
        }
    }
}
```

### Melhores Práticas de Injeção de Dependência

```java
// ✅ BOM: Injeção de construtor
@Service
public class MeuServico {
    private final MeuRepositorio repositorio;
    
    public MeuServico(MeuRepositorio repositorio) {
        this.repositorio = repositorio;
    }
}

// ❌ EVITAR: Injeção de campo
@Service
public class MeuServico {
    @Autowired
    private MeuRepositorio repositorio;
}

// ❌ EVITAR: Injeção de setter
@Service
public class MeuServico {
    private MeuRepositorio repositorio;
    
    @Autowired
    public void setRepositorio(MeuRepositorio repositorio) {
        this.repositorio = repositorio;
    }
}
```

---

## Operações de Banco de Dados

### Métodos de Repositório JPA

```java
public interface ContentRepository extends JpaRepository<ContentModel, Integer> {
    // Auto-gerados pelo Spring Data JPA:
    
    // Encontrar registro único
    Optional<ContentModel> findById(Integer id);
    
    // Encontrar todos os registros
    List<ContentModel> findAll();
    
    // Métodos de consulta personalizados (auto-implementados)
    List<ContentModel> findBySubjectId(Integer subjectId);
    
    Optional<ContentModel> findByContentName(String contentName);
    
    List<ContentModel> findByPeriod(Integer period);
    
    // Operações de deleção
    void deleteById(Integer id);
}
```

### Métodos de Consulta Personalizados

```java
public interface RepositorioPersonalizado extends JpaRepository<Modelo, Integer> {
    
    // Consulta por nome de método
    List<Modelo> findByNomeCampoAndOutroCampo(String nomeCampo, String outroCampo);
    
    // Use anotação @Query para consultas complexas
    @Query("SELECT m FROM Modelo m WHERE m.nomeCampo = :nomeCampo")
    List<Modelo> buscarPorCampoPersonalizado(@Param("nomeCampo") String nomeCampo);
    
    // Consulta SQL nativa
    @Query(value = "SELECT * FROM tabela WHERE coluna = ?1", nativeQuery = true)
    List<Modelo> buscarPorConsultaNativa(String valor);
}
```

### Gerenciamento de Transações

```java
@Service
public class MeuServico {
    
    private final MeuRepositorio repositorio;
    
    // Transação garante propriedades ACID
    @Transactional
    public void realizarMultiplasOperacoes() {
        // Todas as operações abaixo estão em uma transação
        Modelo modelo1 = new Modelo();
        repositorio.save(modelo1);
        
        Modelo modelo2 = new Modelo();
        repositorio.save(modelo2);
        
        // Se alguma falhar, todas as operações são revertidas
    }
    
    // Transação somente leitura (otimizada)
    @Transactional(readOnly = true)
    public List<Modelo> obterTodosModelos() {
        return repositorio.findAll();
    }
}
```

---

## Desenvolvimento Frontend

### Fundamentos de Template Thymeleaf

```html
<!-- Declaração de Template -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Título da Página</title>
</head>
<body>

    <!-- Variáveis do modelo -->
    <h1 th:text="${usuario.nome}">Nome Padrão</h1>
    
    <!-- Loops -->
    <ul>
        <li th:each="item : ${items}" th:text="${item.nome}">Item</li>
    </ul>
    
    <!-- Condicionais -->
    <div th:if="${usuario.isAdmin}">
        <p>Conteúdo de administrador</p>
    </div>
    
    <!-- Links de URL -->
    <a th:href="@{/conteudos/{id}(id=${content.id})}">Ver Conteúdo</a>
    
    <!-- Formulários -->
    <form th:action="@{/login}" method="POST">
        <input type="text" name="username" required>
        <input type="password" name="password" required>
        <button type="submit">Login</button>
    </form>
    
    <!-- Expressões inline -->
    <p th:text="'Total de itens: ' + ${items.size()}">Total de itens: 0</p>
    
</body>
</html>
```

### Atributos Comuns de Thymeleaf

| Atributo | Propósito | Exemplo |
|----------|----------|---------|
| `th:text` | Definir conteúdo de texto | `<p th:text="${mensagem}">Padrão</p>` |
| `th:value` | Definir valor de input | `<input th:value="${usuario.nome}">` |
| `th:href` | Definir URL do link | `<a th:href="@{/caminho}">Link</a>` |
| `th:action` | Definir ação do formulário | `<form th:action="@{/enviar}">` |
| `th:each` | Loop em coleção | `<li th:each="item : ${items}">` |
| `th:if` | Renderização condicional | `<div th:if="${condicao}">` |
| `th:unless` | Condicional invertida | `<div th:unless="${condicao}">` |
| `th:object` | Vincular objeto | `<form th:object="${usuario}">` |

### Estilo

Adicione CSS personalizado em `src/main/resources/static/style.css`:

```css
/* Estilos globais */
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body {
    font-family: Arial, sans-serif;
    line-height: 1.6;
    color: #333;
}

/* Cabeçalho */
header {
    background: #2c3e50;
    color: white;
    padding: 1rem;
}

/* Formulários */
form {
    max-width: 400px;
    margin: 2rem auto;
}

input, select, textarea {
    width: 100%;
    padding: 0.5rem;
    margin: 0.5rem 0;
    border: 1px solid #ddd;
    border-radius: 4px;
}

button {
    background: #3498db;
    color: white;
    padding: 0.5rem 1rem;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}

button:hover {
    background: #2980b9;
}
```

---

## Testes

### Testes Unitários

Criar arquivo de teste: `src/test/java/AfincoTeam/AfincoApp/UserServiceTest.java`

```java
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    private UserService userService;
    
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository);
    }
    
    @Test
    public void testRegistroUsuarioValido() {
        // Arrange
        String username = "usuarioteste";
        String senha = "SenhaSegura123";
        
        // Act
        UserModel usuario = userService.register(username, senha);
        
        // Assert
        assertNotNull(usuario);
        assertEquals(username, usuario.getUsername());
        assertNotEquals(senha, usuario.getPassword()); // Senha deve estar criptografada
    }
    
    @Test
    public void testLoginSucesso() {
        // Arrange
        String username = "usuarioteste";
        String senha = "SenhaSegura123";
        
        // Act
        boolean resultado = userService.login(username, senha);
        
        // Assert
        assertTrue(resultado);
    }
    
    @Test
    public void testLoginFalha() {
        // Arrange
        String username = "usuarioteste";
        String senhaErrada = "SenhaErrada";
        
        // Act
        boolean resultado = userService.login(username, senhaErrada);
        
        // Assert
        assertFalse(resultado);
    }
}
```

### Executando Testes

```bash
# Executar todos os testes
mvn test

# Executar classe de teste específica
mvn test -Dtest=UserServiceTest

# Executar método de teste específico
mvn test -Dtest=UserServiceTest#testLoginSucesso

# Pular testes durante a construção
mvn clean install -DskipTests
```

---

## Estilo de Código e Melhores Práticas

### Convenções de Nomenclatura

```java
// Classes: PascalCase, Substantivo
public class UserController { }
public class UserService { }
public class UserModel { }

// Métodos: camelCase, Verbo
public String getNomeUsuario() { }
public void setNomeUsuario(String nome) { }
public List<Usuario> obterTodosUsuarios() { }

// Variáveis: camelCase
String nomeUsuario;
Integer idUsuario;

// Constantes: UPPER_SNAKE_CASE
public static final String PAPEL_PADRAO = "USUARIO";
public static final Integer NUMERO_MAX_TENTATIVAS_LOGIN = 3;
```

### Organização do Código

```java
@Controller
public class UserController {
    
    // 1. Dependências injetadas (no topo)
    private final UserService userService;
    
    // 2. Construtor
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    // 3. Endpoints GET
    @GetMapping("/login")
    public String mostrarLogin() { }
    
    // 4. Endpoints POST
    @PostMapping("/login")
    public String processarLogin() { }
    
    // 5. Métodos auxiliares
    private void validarEntrada() { }
}
```

### Segurança contra Nulos

```java
// ✅ BOM: Verificações de nulo
if (usuario != null) {
    String nome = usuario.getNome();
}

// ✅ BOM: Optional
Optional<Usuario> usuario = repositorio.findById(id);
usuario.ifPresent(u -> processar(u));

// ✅ BOM: Objects.requireNonNull()
Objects.requireNonNull(usuario, "Usuário não pode ser nulo");

// ❌ EVITAR: Potencial NPE
String nome = usuario.getNome(); // Pode lançar NPE
```

### Tratamento de Erros

```java
// ✅ BOM: Exceções específicas
try {
    usuario = userService.authenticate(username, password);
} catch (InvalidCredentialsException e) {
    model.addAttribute("erro", "Credenciais inválidas");
    return "login";
} catch (UserNotFoundException e) {
    model.addAttribute("erro", "Usuário não encontrado");
    return "login";
}

// ❌ EVITAR: Capturar exceção genérica
try {
    usuario = userService.authenticate(username, password);
} catch (Exception e) {
    // Muito amplo, difícil de debugar
    return "erro";
}
```

---

## Tarefas Comuns de Desenvolvimento

### Tarefa 1: Adicionar uma Nova Tabela de Banco de Dados

1. **Criar Classe de Entidade**:
```java
@Entity
@Table(name = "nova_tabela")
public class NovoModelo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String nome;
    
    // getters/setters
}
```

2. **Criar Repositório**:
```java
public interface NovoRepositorio extends JpaRepository<NovoModelo, Integer> {
    List<NovoModelo> findByNome(String nome);
}
```

3. **Criar Serviço**:
```java
@Service
public class NovoServico {
    private final NovoRepositorio repositorio;
    
    public NovoServico(NovoRepositorio repositorio) {
        this.repositorio = repositorio;
    }
    
    public NovoModelo salvar(NovoModelo modelo) {
        return repositorio.save(modelo);
    }
}
```

4. **Migração**: Hibernate criará a tabela automaticamente com `spring.jpa.hibernate.ddl-auto=update`

### Tarefa 2: Adicionar um Novo Endpoint REST

```java
// 1. Adicionar ao controlador
@Controller
public class MeuControlador {
    
    @PostMapping("/api/meuendpoint")
    public String processarMeuEndpoint(@RequestParam String parametro) {
        // Processar requisição
        return "resposta";
    }
}

// 2. Testar via navegador ou curl
curl -X POST "http://localhost:8080/api/meuendpoint?parametro=valor"
```

### Tarefa 3: Adicionar Verificação de Autenticação

```java
// Em método do controlador
@GetMapping("/protegido")
public String paginaProtegida(HttpSession session) {
    String usuarioLogado = (String) session.getAttribute("loggedUser");
    
    if (usuarioLogado == null) {
        return "redirect:/login";
    }
    
    // Processar requisição autenticada
    return "pagina_protegida";
}
```

---

## Recursos Úteis

- **Documentação Spring Boot**: https://spring.io/projects/spring-boot
- **Spring Data JPA**: https://spring.io/projects/spring-data-jpa
- **Documentação Thymeleaf**: https://www.thymeleaf.org/
- **Documentação PostgreSQL**: https://www.postgresql.org/docs/
- **Documentação Supabase**: https://supabase.com/docs

---

**Última Atualização**: 27 de Abril de 2026
**Versão**: 1.0 (Português)
**Aplicação**: AfincoApp v0.0.1-SNAPSHOT
**Verificação de Precisão**: ✅ Código verificado contra implementação atual
