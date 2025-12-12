package br.edu.lampi.infrareport;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

import br.edu.lampi.infrareport.controller.dto.user.UserRequestDTO;
import br.edu.lampi.infrareport.model.building.Building;
import br.edu.lampi.infrareport.repository.UserRepository;
import br.edu.lampi.infrareport.service.BuildingService;
import br.edu.lampi.infrareport.service.UserService;

import br.edu.lampi.infrareport.service.BuildingService;
import br.edu.lampi.infrareport.controller.dto.building.BuildingRequestDTO;
import java.util.Arrays;
import java.util.Set;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class InfrareportApplication implements CommandLineRunner{

	@Autowired
	private UserService userService;

	@Autowired
    private UserRepository userRepository;

	//
	@Autowired
    private BuildingService buildingService;
	//
	public static void main(String[] args) {
		SpringApplication.run(InfrareportApplication.class, args);
	}

@Override
public void run(String... args) throws Exception {
    System.out.println(">>> CommandLineRunner started <<<");

    // User initialization
    if(this.userRepository.findByEmail("admin@gmail.com") == null){
        UserRequestDTO user = new UserRequestDTO("admin", "admin@gmail.com", "123456");
        this.userService.saveAdmin(user);
    }

    // Building initialization
    try {
        System.out.println("Checking for Test Building...");
        Building found = buildingService.findByName("Test Building");
        if (found == null) {
            System.out.println("Inserting Test Building...");
            BuildingRequestDTO dto = new BuildingRequestDTO(
                "Test Building",
                Set.of("First Floor", "Second Floor")
            );
            buildingService.saveNewBuilding(new br.edu.lampi.infrareport.model.building.Building(dto));
            System.out.println("Test Building inserted!");
        } else {
            System.out.println("Test Building already exists.");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}
	