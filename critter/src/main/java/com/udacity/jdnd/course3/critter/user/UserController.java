package com.udacity.jdnd.course3.critter.user;

import com.udacity.jdnd.course3.critter.entity.Customer;
import com.udacity.jdnd.course3.critter.entity.Employee;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.service.CustomerService;
import com.udacity.jdnd.course3.critter.service.EmployeeService;
import com.udacity.jdnd.course3.critter.service.PetService;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

    private final CustomerService customerService;
    private final EmployeeService employeeService;
    private final PetService petService;

    public UserController(CustomerService customerService,
                          EmployeeService employeeService,
                          PetService petService) {
        this.customerService = customerService;
        this.employeeService = employeeService;
        this.petService = petService;
    }

    // ---------------- CUSTOMER ----------------

    @PostMapping("/customer")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO dto) {
        Customer customer = new Customer();
        customer.setName(dto.getName());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customer.setNotes(dto.getNotes());

        Customer saved = customerService.saveCustomer(customer);

        dto.setId(saved.getId());
        return dto;
    }

    @GetMapping("/customer")
    public List<CustomerDTO> getAllCustomers() {
        return customerService.getAllCustomers()
                .stream()
                .map(this::convertCustomerToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/customer/pet/{petId}")
    public CustomerDTO getOwnerByPet(@PathVariable long petId) {
        Pet pet = petService.getPetById(petId).orElseThrow();
        Customer owner = customerService.getCustomerByPet(pet);
        return convertCustomerToDTO(owner);
    }

    // ---------------- EMPLOYEE ----------------

    @PostMapping("/employee")
    public EmployeeDTO saveEmployee(@RequestBody EmployeeDTO dto) {
        Employee employee = new Employee();
        employee.setName(dto.getName());
        employee.setSkills(dto.getSkills());
        employee.setDaysAvailable(dto.getDaysAvailable());

        Employee saved = employeeService.saveEmployee(employee);
        dto.setId(saved.getId());
        return dto;
    }

    @PostMapping("/employee/{employeeId}")
    public EmployeeDTO getEmployee(@PathVariable long employeeId) {
        Employee employee = employeeService.getEmployeeById(employeeId).orElseThrow();
        return convertEmployeeToDTO(employee);
    }

    @PutMapping("/employee/{employeeId}")
    public void setAvailability(@RequestBody Set<DayOfWeek> daysAvailable,
                                @PathVariable long employeeId) {
        employeeService.setAvailability(employeeId, daysAvailable);
    }

    @GetMapping("/employee/availability")
    public List<EmployeeDTO> findEmployeesForService(@RequestBody EmployeeRequestDTO dto) {
        DayOfWeek day = dto.getDate().getDayOfWeek();

        return employeeService.getAllEmployees()
                .stream()
                .filter(e -> e.getDaysAvailable().contains(day))
                .filter(e -> e.getSkills().containsAll(dto.getSkills()))
                .map(this::convertEmployeeToDTO)
                .collect(Collectors.toList());
    }

    // ---------------- CONVERTERS ----------------

    private CustomerDTO convertCustomerToDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setPhoneNumber(customer.getPhoneNumber());
        dto.setNotes(customer.getNotes());

        if (customer.getPets() != null) {
            dto.setPetIds(
                    customer.getPets()
                            .stream()
                            .map(Pet::getId)
                            .collect(Collectors.toList())
            );
        }
        return dto;
    }

    private EmployeeDTO convertEmployeeToDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setName(employee.getName());
        dto.setSkills(employee.getSkills());
        dto.setDaysAvailable(employee.getDaysAvailable());
        return dto;
    }
}
