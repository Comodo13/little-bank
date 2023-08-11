package korunovacni.dmitri.littlebank.controller;

import korunovacni.dmitri.littlebank.domain.dto.CustomerDto;
import korunovacni.dmitri.littlebank.exception.RequestFormatException;
import korunovacni.dmitri.littlebank.exception.ResourceNotFoundException;
import korunovacni.dmitri.littlebank.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/v1/customers")
@RestController
@AllArgsConstructor
public class CustomerController {

    @Autowired
    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody CustomerDto customerDto) throws RequestFormatException {
        return customerService.createCustomer(customerDto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<CustomerDto> editCustomer(@RequestBody CustomerDto customerDto, @PathVariable Long id)
            throws RequestFormatException, ResourceNotFoundException {
        return customerService.editCustomer(customerDto, id);
    }

    @DeleteMapping("/{id}")
    public void deleteCustomerById(@PathVariable Long id) throws RequestFormatException, ResourceNotFoundException {
        customerService.deleteCustomerById(id);
    }
}
