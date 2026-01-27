Feature: Account registry

  Scenario: User is able to create 2 accounts
    Given Account registry is empty
    When I create an account using name "kurt" last name "cobain" pesel "89092909246"
    And I create an account using name "tadeusz" last name "szcześniak" pesel "79101011234"
    Then Number of accounts in registry equals "2"
    And Account with pesel "89092909246" exists in registry
    And Account with pesel "79101011234" exists in registry

  Scenario: User is able to update surname of already created account
    Given Account registry is empty
    And I create an account using name "nata" last name "haydamaky" pesel "95092909876"
    When I update "surname" of account with pesel "95092909876" to "filatov"
    Then Account with pesel "95092909876" has "surname" equal to "filatov"

  Scenario: User is able to delete created account
    Given Account registry is empty
    And I create an account using name "parov" last name "stelar" pesel "01092909876"
    When I delete account with pesel "01092909876"
    Then Account with pesel "01092909876" does not exist in registry
    And Number of accounts in registry equals "0"

  Scenario: User is able to update name of already created account
    Given Account registry is empty
    And I create an account using name "nata" last name "haydamaky" pesel "95092909876"
    When I update "name" of account with pesel "95092909876" to "anna"
    Then Account with pesel "95092909876" has "name" equal to "anna"

  Scenario: Created account has all fields correctly set
    Given Account registry is empty
    When I create an account using name "jan" last name "kowalski" pesel "85010112345"
    Then Account with pesel "85010112345" has the following fields correctly set

  Scenario: User can make a valid outgoing transfer
    Given Account registry is empty
    And I create an account using name "jan" last name "kowalski" pesel "85010112345"
    And I create an account using name "anna" last name "nowak" pesel "92020254321"
    And I transfer 100.0 to account with pesel "85010112345"
    When I transfer 100.0 from account with pesel "85010112345" as "OUTGOING"
    Then The transfer is successful

  Scenario: Transfer with negative amount returns 400
    Given Account registry is empty
    And I create an account using name "jan" last name "kowalski" pesel "85010112345"
    And I create an account using name "anna" last name "nowak" pesel "92020254321"
    When I transfer -100.0 from account with pesel "85010112345" as "OUTGOING"
    Then The transfer fails with status 422

  Scenario: Transfer fails due to business rules returns 422
    Given Account registry is empty
    And I create an account using name "jan" last name "kowalski" pesel "85010112345"
    And I create an account using name "anna" last name "nowak" pesel "92020254321"
    When I transfer 1000000.0 from account with pesel "85010112345" as "OUTGOING"
    Then The transfer fails with status 422