Feature: Account registry

  Scenario: User is able to create 2 accounts
    Given Account registry is empty
    When I create an account using name "kurt" last name "cobain" pesel "89092909246"
    And I create an account using name "tadeusz" last name "szcześniak" pesel "79101011234"
    Then Number of accounts in registry equals "2"
    And Account with pesel "89092909246" exists in registry
    And Account with pesel "79101011234" exists in registry