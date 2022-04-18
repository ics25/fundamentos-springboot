package com.fundamentosplatzi.springboot.fundamentos;

import com.fundamentosplatzi.springboot.fundamentos.bean.MyBean;
import com.fundamentosplatzi.springboot.fundamentos.bean.MyBeanWithDependency;
import com.fundamentosplatzi.springboot.fundamentos.bean.MyBeanWithProperties;
import com.fundamentosplatzi.springboot.fundamentos.component.ComponentDependency;
import com.fundamentosplatzi.springboot.fundamentos.entity.User;
import com.fundamentosplatzi.springboot.fundamentos.pojo.UserPojo;
import com.fundamentosplatzi.springboot.fundamentos.repository.UserRepository;
import com.fundamentosplatzi.springboot.fundamentos.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class FundamentosApplication implements CommandLineRunner {
	private final Log LOGGER = LogFactory.getLog(FundamentosApplication.class);
	private UserPojo userPojo;
	private MyBean myBean;
	private ComponentDependency componentDependency;
	//De la siguiente forma se inyectan las depencias con springBoot
	//En versiones recientes de spring boot no es necesario hacer uso de la anotación @Autowired
	//Con las inyecciones de dependencias podemos tener n implementaciones a partir de una dependencia y
	//usando la anotación @Qualifier llamamos a la dependencia que queremos inyectar

	private MyBeanWithDependency myBeanWithDependency;
	private MyBeanWithProperties myBeanWithProperties;
	private UserRepository userRepository;
	private UserService userService;

	public FundamentosApplication(@Qualifier("componentTwoImplement") ComponentDependency componentDependency, MyBean myBean, MyBeanWithDependency myBeanWithDependency, MyBeanWithProperties myBeanWithProperties, UserPojo userPojo, UserRepository userRepository, UserService userService) {
		this.componentDependency = componentDependency;
		this.myBean = myBean; //Con esto inyectamos nuestra propia dependencia
		this.myBeanWithDependency = myBeanWithDependency;
		this.myBeanWithProperties = myBeanWithProperties;
		this.userPojo = userPojo;
		this.userRepository = userRepository;
		this.userService = userService;
	}


	public static void main(String[] args) {
		SpringApplication.run(FundamentosApplication.class, args);
	}

	//Este método lo que hace es ejecutarnos en la aplicación todo lo que queremos
	@Override
	public void run(String... args) throws Exception {
		//ejemplosAnteriores();
		saveUserInDataBse();
		getInformationJpqlFromUser();
		saveWithErrorTranasactional();
	}

	private void saveWithErrorTranasactional(){
		User user1 = new User("TestTransactional1", "TestTransactional1@mail.com",LocalDate.now());
		User user2 = new User("TestTransactional2", "TestTransactional2@mail.com",LocalDate.now());
		User user3 = new User("TestTransactional3", "TestTransactional1@mail.com",LocalDate.now());
		User user4 = new User("TestTransactional4", "TestTransactional4@mail.com",LocalDate.now());

		List<User> users = Arrays.asList(user1,user2, user3, user4);

		try {
			userService.saveTransactional(users);
		}catch (Exception e) {
			LOGGER.error("Ésta es una excepción dentro del método transaccional..." + e);
		}

		userService.getAllUsers().stream()
				.forEach(user -> LOGGER.info("Este es el usuario dentro del método transaccional " + user));
	}

	private void getInformationJpqlFromUser(){
		LOGGER.info("Usuario con el método findByUserEmail: "+userRepository.findByUserEmail("juan@domain.com").
				orElseThrow(()->new RuntimeException("no se encontró el usuario")));

		/*
		//Búsqueda a partir de un parámetro
		 userRepository.findAndSort("user", Sort.by("id").ascending())
				.stream()
				.forEach(user -> LOGGER.info("Usuario con método sort: " + user));

		userRepository.findByName("Juan")
				.stream()
				.forEach(user -> LOGGER.info("Usuario con Query method: "+user));

		LOGGER.info("Usuario con query method findByEmailAndName: " +userRepository.findByEmailAndName("juan@domain.com", "Juan")
				.orElseThrow(()->new RuntimeException("Usuario no encontrado")) );

		userRepository.findByNameLike("%use%")
				.stream()
				.forEach(user -> LOGGER.info("Usuario findByNameLike: " + user));

		userRepository.findByNameOrEmail("user4", null)
				.stream()
				.forEach(user -> LOGGER.info("Usuario findByNameOrEmail: "+user));


		 */

		userRepository.findByBirthDateBetween(LocalDate.of(2021,2,1), LocalDate.of(2021,4,2))
				.stream()
				.forEach(user -> LOGGER.info("Usuario con intervalo de fechas: "+user));

		userRepository.findByNameLikeOrderByIdDesc("%user%")
				.stream()
				.forEach(user -> LOGGER.info("Usuario encontrado con like y ordenado: " + user));

		userRepository.findByNameContainingOrderByIdDesc("user")
				.stream()
				.forEach(user -> LOGGER.info("Usuario encontrado con contained y ordenado: " + user));

		userRepository.getAllByBirthDateAndEmail(LocalDate.of(2021,04,1), "ivan@domain.com")
				.stream()
				.forEach(userDto -> LOGGER.info("Usuario encontrado con getAllByBirthDateAndEmail: " + userDto));

	}

	private void saveUserInDataBse() {
		User user1 = new User("Juan", "juan@domain.com", LocalDate.of(2021,03,20));
		User user2 = new User("Iván", "ivan@domain.com", LocalDate.of(2021,04,1));
		User user3 = new User("Juan", "juan2@domain.com", LocalDate.of(2021,05,4));
		User user4 = new User("user4", "user4@domain.com", LocalDate.of(2021,05,4));
		User user5 = new User("user5", "user5@domain.com", LocalDate.of(2021,05,4));
		User user6 = new User("user6", "user6@domain.com", LocalDate.of(2021,05,4));


		List<User> list = Arrays.asList(user1,user2,user3,user4,user5,user6);

		list.stream().forEach(userRepository::save);
		list.stream().forEach(user -> System.out.println("Nombre: " +user.getName()
			+ " Email: " +user.getEmail() + " Cumpleaños: " +user.getBirthDate().toString()
		));
	}
	private void ejemplosAnteriores(){
		componentDependency.saludar();
		myBean.print();
		myBeanWithDependency.printWithDependency();
		System.out.println(myBeanWithProperties.function());
		System.out.println(userPojo.getEmail() + " - " + userPojo.getPassword());

		try {
			int value = 19/0;
			LOGGER.debug("Mi valor: "+value);
		} catch(Exception e) {
			LOGGER.error("Esto es un error al dividir por cero. "+ e.getMessage());
		}
	}
}
