package pl.bankapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.bankapp.entity.PersonalAccount;

import java.util.Optional;

@Repository
public interface PersonalAccountRepository extends JpaRepository<PersonalAccount, Long> {
    Optional<Object> findByIdentification(String identification);
}
