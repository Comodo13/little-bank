package korunovacni.dmitri.littlebank.domain.dto.converter;

import korunovacni.dmitri.littlebank.domain.Customer;
import korunovacni.dmitri.littlebank.domain.dto.CustomerDto;

public class CustomerConverter {

    public static CustomerDto toDto(Customer customer) {
        return CustomerDto.builder()
                .cardDateOfExpiry(customer.getCardDateOfExpiry())
                .cardDateOfIssue(customer.getCardDateOfIssue())
                .cardNumber(customer.getCardNumber())
                .name(customer.getName())
                .sex(customer.getSex())
                .nationality(customer.getNationality())
                .surname(customer.getSurname())
                .dateOfBirth(customer.getDateOfBirth())
                .build();
    }

    public static Customer toEntity(CustomerDto customerDto) {
        Customer customer = new Customer();
        customer.setCardDateOfExpiry(customerDto.getCardDateOfExpiry());
        customer.setCardDateOfIssue(customerDto.getCardDateOfIssue());
        customer.setCardNumber(customerDto.getCardNumber());
        customer.setDateOfBirth(customerDto.getDateOfBirth());
        customer.setSex(customerDto.getSex());
        customer.setNationality(customerDto.getNationality());
        customer.setName(customerDto.getName());
        customer.setSurname(customerDto.getSurname());
        return customer;
    }
}
