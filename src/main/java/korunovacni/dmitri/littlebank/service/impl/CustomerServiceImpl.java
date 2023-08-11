package korunovacni.dmitri.littlebank.service.impl;

import korunovacni.dmitri.littlebank.domain.Customer;
import korunovacni.dmitri.littlebank.domain.dto.CustomerDto;
import korunovacni.dmitri.littlebank.domain.dto.converter.CustomerConverter;
import korunovacni.dmitri.littlebank.exception.RequestFormatException;
import korunovacni.dmitri.littlebank.exception.ResourceNotFoundException;
import korunovacni.dmitri.littlebank.repo.CustomerRepository;
import korunovacni.dmitri.littlebank.service.CustomerService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private final CustomerRepository customerRepository;

    private final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    /**
     * Find Customer by id
     *
     * @param id inbound Long
     * @return Customer
     * @throws RequestFormatException
     */
    @Override
    public Customer getById(Long id) throws RequestFormatException, ResourceNotFoundException {
        if (id == null || id == 0L) {
            throw new RequestFormatException("Customer's Id can't be null or zero");
        }
        Optional<Customer> customerOpt = customerRepository.findById(id);
        if (customerOpt.isEmpty()) {
            logger.info("CustomerServiceImpl:getById: error can't find customer with Id: " + id);
            throw new ResourceNotFoundException("Customer with Id: " + id + " was not found");
        }
        return customerOpt.get();
    }

    /**
     * Validate cardNumber, name and date of birth. Create new customer from inbound dto
     *
     * @param customerDto inbound dto
     * @return CustomerDto representation of created Customer, wrapped in ResponseEntity
     * @throws RequestFormatException
     */
    @Override
    public ResponseEntity<CustomerDto> createCustomer(CustomerDto customerDto) throws RequestFormatException {
        if (customerDto.getCardNumber() == null ||
                customerDto.getName() == null ||
                customerDto.getDateOfBirth() == null) {
            throw new RequestFormatException("Enter all required data in format: name," +
                    " surname, sex, nationality, dateOfBirth, cardNumber, cardDateOfIssue, cardDateOfExpiry");
        }
        Customer customer = CustomerConverter.toEntity(customerDto);
        customerRepository.save(customer);
        CustomerDto customerResponse = CustomerConverter.toDto(customer);

        logger.info("CustomerServiceImpl:createCustomer: customer with name: " + customer.getName() + " was successfully created");
        return new ResponseEntity<>(customerResponse, HttpStatus.CREATED);
    }

    /**
     * Find Customer by Id, Check all fields of inbound dto and update Customer with given only
     *
     * @param id          inbound Long
     * @param customerDto inbound dto
     * @return CustomerDto representation of modified Customer, wrapped in ResponseEntity
     * @throws RequestFormatException
     */
    @Override
    public ResponseEntity<CustomerDto> editCustomer(CustomerDto customerDto, Long id)
            throws RequestFormatException, ResourceNotFoundException {
        if (id == null || id == 0L) {
            throw new RequestFormatException("Customer's Id can't be null or zero");
        }
        Optional<Customer> customerOpt = customerRepository.findById(id);
        if (customerOpt.isEmpty()) {
            throw new ResourceNotFoundException("Customer with Id: " + id + " wasn't found");
        }
        Customer customer = customerOpt.get();

        if (customerDto.getCardDateOfExpiry() != null) {
            customer.setCardDateOfExpiry(customerDto.getCardDateOfExpiry());
        }
        if (customerDto.getCardDateOfIssue() != null) {
            customer.setCardDateOfExpiry(customerDto.getCardDateOfExpiry());
        }
        if (customerDto.getCardNumber() != null) {
            customer.setCardNumber(customerDto.getCardNumber());
        }
        if (customerDto.getDateOfBirth() != null) {
            customer.setDateOfBirth(customerDto.getDateOfBirth());
        }
        if (customerDto.getName() != null) {
            customer.setName(customerDto.getName());
        }
        if (customerDto.getNationality() != null) {
            customer.setNationality(customerDto.getNationality());
        }
        if (customerDto.getSurname() != null) {
            customer.setSurname(customerDto.getSurname());
        }
        if (customerDto.getSex() != null) {
            customer.setSex(customerDto.getSex());
        }
        customerRepository.save(customer);
        CustomerDto dto = CustomerConverter.toDto(customer);

        return new ResponseEntity<>(dto, HttpStatus.ACCEPTED);
    }

    /**
     * Find and delete customer by inbound id
     *
     * @param id inbound Long
     * @throws RequestFormatException
     */
    @Override
    public void deleteCustomerById(Long id) throws RequestFormatException, ResourceNotFoundException {
        if (id == null || id == 0L) {
            throw new RequestFormatException("Customer's Id can't be null or zero");
        }
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);      //not the best option to call repository 2 times to do 1 action
        } else {
            logger.info("CustomerServiceImpl:deleteCustomerById: error can't find customer with id: " + id);
            throw new ResourceNotFoundException("Customer with Id: " + id + " was not found");
        }
    }
}


