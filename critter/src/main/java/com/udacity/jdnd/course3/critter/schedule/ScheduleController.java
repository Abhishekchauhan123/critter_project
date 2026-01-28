package com.udacity.jdnd.course3.critter.schedule;

import com.udacity.jdnd.course3.critter.entity.Employee;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.entity.Schedule;
import com.udacity.jdnd.course3.critter.service.EmployeeService;
import com.udacity.jdnd.course3.critter.service.PetService;
import com.udacity.jdnd.course3.critter.service.ScheduleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final EmployeeService employeeService;
    private final PetService petService;

    public ScheduleController(ScheduleService scheduleService,
                              EmployeeService employeeService,
                              PetService petService) {
        this.scheduleService = scheduleService;
        this.employeeService = employeeService;
        this.petService = petService;
    }

    // ---------------- CREATE SCHEDULE ----------------
    @PostMapping
    public ScheduleDTO createSchedule(@RequestBody ScheduleDTO scheduleDTO) {

        Schedule schedule = new Schedule();
        schedule.setDate(scheduleDTO.getDate());
        schedule.setActivities(scheduleDTO.getActivities());

        List<Employee> employees =
                employeeService.getEmployeesById(scheduleDTO.getEmployeeIds());
        schedule.setEmployees(employees);

        List<Pet> pets =
                petService.getPetsById(scheduleDTO.getPetIds());
        schedule.setPets(pets);

        Schedule saved = scheduleService.saveSchedule(schedule);
        return convertToDTO(saved);
    }

    // ---------------- GET ALL ----------------
    @GetMapping
    public List<ScheduleDTO> getAllSchedules() {
        return scheduleService.getAllSchedules()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ---------------- BY PET ----------------
    @GetMapping("/pet/{petId}")
    public List<ScheduleDTO> getScheduleForPet(@PathVariable long petId) {
        return scheduleService.getScheduleForPet(petId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ---------------- BY EMPLOYEE ----------------
    @GetMapping("/employee/{employeeId}")
    public List<ScheduleDTO> getScheduleForEmployee(@PathVariable long employeeId) {
        return scheduleService.getScheduleForEmployee(employeeId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ---------------- BY CUSTOMER ----------------
    @GetMapping("/customer/{customerId}")
    public List<ScheduleDTO> getScheduleForCustomer(@PathVariable long customerId) {

        List<Pet> pets = petService.getPetsByOwnerId(customerId);

        return pets.stream()
                .flatMap(pet ->
                        scheduleService.getScheduleForPet(pet.getId()).stream()
                )
                .distinct()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    // ---------------- ENTITY → DTO ----------------
    private ScheduleDTO convertToDTO(Schedule schedule) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setId(schedule.getId());
        dto.setDate(schedule.getDate());
        dto.setActivities(schedule.getActivities());

        dto.setEmployeeIds(
                schedule.getEmployees()
                        .stream()
                        .map(Employee::getId)
                        .collect(Collectors.toList())
        );

        dto.setPetIds(
                schedule.getPets()
                        .stream()
                        .map(Pet::getId)
                        .collect(Collectors.toList())
        );

        return dto;
    }
}
