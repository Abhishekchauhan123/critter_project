package com.udacity.jdnd.course3.critter.pet;

import com.udacity.jdnd.course3.critter.entity.Customer;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.service.CustomerService;
import com.udacity.jdnd.course3.critter.service.PetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pet")
public class PetController {

    private final PetService petService;
    private final CustomerService customerService;

    public PetController(PetService petService, CustomerService customerService) {
        this.petService = petService;
        this.customerService = customerService;
    }

    // -------- SAVE PET --------
    @PostMapping
    public PetDTO savePet(@RequestBody PetDTO petDTO) {

        Pet pet = new Pet();
        pet.setName(petDTO.getName());
        pet.setType(petDTO.getType());
        pet.setBirthDate(petDTO.getBirthDate());
        pet.setNotes(petDTO.getNotes());

        if (petDTO.getOwnerId() != null) {
            Customer owner = customerService
                    .getCustomerById(petDTO.getOwnerId())
                    .orElseThrow();
            pet.setOwner(owner);
            owner.getPets().add(pet);
        }

        Pet saved = petService.savePet(pet);
        return convertToDTO(saved);
    }

    // -------- GET ALL PETS --------
    @GetMapping
    public List<PetDTO> getAllPets() {
        return petService.getAllPets()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // -------- GET PETS BY OWNER --------
    @GetMapping("/owner/{ownerId}")
    public List<PetDTO> getPetsByOwner(@PathVariable long ownerId) {
        return petService.getPetsByOwnerId(ownerId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // -------- ENTITY → DTO --------
    private PetDTO convertToDTO(Pet pet) {
        PetDTO dto = new PetDTO();
        dto.setId(pet.getId());
        dto.setName(pet.getName());
        dto.setType(pet.getType());
        dto.setBirthDate(pet.getBirthDate());
        dto.setNotes(pet.getNotes());

        if (pet.getOwner() != null) {
            dto.setOwnerId(pet.getOwner().getId());
        }
        return dto;
    }
    @GetMapping("/{petId}")
    public PetDTO getPet(@PathVariable long petId) {
        Pet pet = petService.getPetById(petId).orElseThrow();
        return convertToDTO(pet);
    }

}
