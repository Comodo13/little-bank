
HOW TO RUN:
To run this app just change database info in recources/application.properties. I'm using MySQL.

CUSTOMERS:
To create Customer POST JSON http://localhost:8080/api/v1/customers/
You have to fill almost all fields.
{
    "name": "Dzmitry",
    "surname": "Rabotkin",
    "sex": "male",
    "nationality": "blr",
    "dateOfBirth":"1992-11-30",
    "cardNumber": 123456781234,
    "cardDateOfIssue": "2017-10-12",
    "cardDateOfExpiry": "2023-11-12"
}
To modify Customer PUT JSON http://localhost:8080/api/v1/customers/{customerId}
Only send fields you want to be changed.

To delete Customer HTTP DELETE http://localhost:8080/api/v1/customers/{customerId}

ACCOUNTS:
To create Account POST JSON http://localhost:8080/api/v1/accounts/
After creation all accounts have 1000 on balance.
List of acceptable currencies: [EUR,CAD,CZK,HKD,LVL,PLN,USD,UAH,JPY,RUB,GBP]
IBAN length is limited from 8 to 32 symbols.
{
    "currency": "rub",
    "customerId": 1,
    "IBAN": "NL62INGB26564673455"
}
To modify Account PUT JSON http://localhost:8080/api/v1/accounts/{accountId}
Only send fields you want to be changed.

To delete Account DELETE http://localhost:8080/api/v1/accounts/{accountId}
To get account summary GET http://localhost:8080/api/v1/accounts/{accountId}


TRANSACTIONS:
To make a transaction POST JSON http://localhost:8080/api/v1/transactions
{
    "amount": 100,
    "debtorIBAN": "NL62INGB26564",
    "creditorIBAN": "NL62INGB2650400811",
    "message": "usd to rub"
}
If accounts have different currencies - amount will be converted according to currency rates that are stored in hashmap.
So i don't have to add additional JSON parser library for getting rates from APIs.

To view history of transactions GET http://localhost:8080/api/v1/transactions/
To find transfers By IBAN GET http://localhost:8080/api/v1/transactions/iban/{IBAN}
To find transfers By message GET http://localhost:8080/api/v1/transactions/search/message={message}
To find transfers By amount GET http://localhost:8080/api/v1/transactions/search/amount={amount}


What can I do better?
1. Rest endpoints design. Probably add search by multiple parameters and make transaction endpoint look like this:
api/v1/transactions/?amount=10&message=czk%to%usd
2. Bigger test coverage.
3. I wasn't thinking from the concurrency point of view while developing this project.

Thank you for checking it out.
