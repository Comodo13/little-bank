package korunovacni.dmitri.littlebank.service;

import korunovacni.dmitri.littlebank.domain.Customer;
import korunovacni.dmitri.littlebank.domain.dto.CustomerDto;
import korunovacni.dmitri.littlebank.exception.RequestFormatException;
import korunovacni.dmitri.littlebank.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;

public interface CustomerService {

    ResponseEntity<CustomerDto> createCustomer(CustomerDto customerDto) throws RequestFormatException;

    ResponseEntity<CustomerDto> editCustomer(CustomerDto customerDto, Long id) throws RequestFormatException, ResourceNotFoundException;

    void deleteCustomerById(Long id) throws RequestFormatException, ResourceNotFoundException;

    Customer getById(Long id) throws RequestFormatException, ResourceNotFoundException;

}
