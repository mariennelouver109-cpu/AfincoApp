package AfincoTeam;

/**
 Classe principal da aplicação.
 Esta classe é responsável por iniciar a aplicação Spring Boot
 */
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// anotação para indicar que esta é a classe principal
@SpringBootApplication
public class AfincoAppApplication {
	public static void main(String[] args) {

		// método principal para iniciar a aplicação Spring Boot, chama o método run da classe SpringApplication, passando a classe principal e os argumentos da linha de comando
		SpringApplication.run(AfincoAppApplication.class, args); 
	}
}
