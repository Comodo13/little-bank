package korunovacni.dmitri.littlebank.domain.dto.converter;

import korunovacni.dmitri.littlebank.domain.Account;
import korunovacni.dmitri.littlebank.domain.dto.AccountDto;

public class AccountConverter {

    private AccountConverter() {
    }


    /**
     * Convert Account entity to dto
     *
     * @param account
     * @return dto representation of Account
     */
    public static AccountDto toDto(Account account) {
        return AccountDto.builder()
                .currency(account.getCurrency().toString())
                .customerId(account.getCustomer().getId().intValue())
                .IBAN(account.getIBAN())
                .balance(account.getBalance().toString())
                .build();
    }
}
