import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


public class AccountsRegistry {
    private List<PersonalAccount> accounts = new ArrayList<>();

    public void addAccount(PersonalAccount account){
        accounts.add(account);
    }

    public PersonalAccount findAccountByPesel(String pesel){
        return accounts
                .stream()
                .filter(p -> p.getIdentification().equals(pesel))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("No Account with provided pesel " + pesel));
    }

    public List<PersonalAccount> getAllAccounts(){
        return accounts;
    }

    public int getAmountOfAccounts(){
        return accounts.size();
    }
}
