package com.nighthawk.spring_portfolio.mvc.person;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// PURPOSE: Retrieve user details based on username

@Service
@Transactional
public class PersonDetailsService implements UserDetailsService { // "implements" ties ModelRepo to Spring Security
    // Encapsulate many object into a single Bean (Person, Roles, and Scrum)
    @Autowired // Inject PersonJpaRepository
    private PersonJpaRepository personJpaRepository;
    @Autowired // Inject RoleJpaRepository
    private PersonRoleJpaRepository personRoleJpaRepository;

    // @Autowired // Inject PasswordEncoder
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * UserDetailsService Overrides and maps Person & Roles POJO into Spring
     * Security
     */
    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        // Retrieve user details based on the email (username) from the database
        Person person = personJpaRepository.findByEmail(email); // setting variable user equal to the method finding the
                                                                // username in the database
        if (person == null) {
            throw new UsernameNotFoundException("User not found with username: " + email);
        }

        // Map the user roles to SimpleGrantedAuthority objects
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        person.getRoles().forEach(role -> { // loop through roles
            authorities.add(new SimpleGrantedAuthority(role.getName())); // create a SimpleGrantedAuthority by passed in
                                                                         // role, adding it all to the authorities list,
                                                                         // list of roles gets past in for spring
                                                                         // security
        });
        // Create and return a UserDetails object for Spring Security
        return new org.springframework.security.core.userdetails.User(person.getEmail(), person.getPassword(),
                authorities);
    }

    /* Person Section */

    public List<Person> listAll() {
        return personJpaRepository.findAllByOrderByNameAsc();
    }

    // Custom query to find match to name or email
    public List<Person> list(String name, String email) {
        return personJpaRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(name, email);
    }

    // Custom query to find anything containing term in name or email ignoring case
    public List<Person> listLike(String term) {
        return personJpaRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(term, term);
    }

    // Custom query to find anything containing term in name or email ignoring case
    public List<Person> listLikeNative(String term) {
        String like_term = String.format("%%%s%%", term); // Like required % rappers
        return personJpaRepository.findByLikeTermNative(like_term);
    }

    // Encode password prior to save
    public void save(Person person) {
        person.setPassword(passwordEncoder().encode(person.getPassword()));
        personJpaRepository.save(person);
    }

    public Person get(long id) {
        return (personJpaRepository.findById(id).isPresent())
                ? personJpaRepository.findById(id).get()
                : null;
    }

    public Person getByEmail(String email) {
        return (personJpaRepository.findByEmail(email));
    }

    public void delete(long id) {
        personJpaRepository.deleteById(id);
    }

    public void defaults(String password, String roleName) {
        for (Person person : listAll()) {
            if (person.getPassword() == null || person.getPassword().isEmpty() || person.getPassword().isBlank()) {
                person.setPassword(passwordEncoder().encode(password));
            }
            if (person.getRoles().isEmpty()) {
                PersonRole role = personRoleJpaRepository.findByName(roleName);
                if (role != null) { // verify role
                    person.getRoles().add(role);
                }
            }
        }
    }

    public int getEco(long id) {
        Person person = get(id);
        if (person != null) {
            return person.getEco();
        }
        return 0;
    }

    public void changeEco(String email, int eco) {
        Person player = personJpaRepository.findByEmail(email);
        int currentEco = player.getEco();
        int newEco = currentEco + eco;
        player.setEco(newEco);
    }

    public int getCash(long id) {
        Person person = get(id);
        if (person != null) {
            return person.getCash();
        }
        return 0;
    }

    public void changeCash(String email, int cash) {
        Person player = personJpaRepository.findByEmail(email);
        int currentCash = player.getCash();
        int newCash = currentCash + cash;
        player.setCash(newCash);
    }

    /* Roles Section */
    public void saveRole(PersonRole role) {
        PersonRole roleObj = personRoleJpaRepository.findByName(role.getName());
        if (roleObj == null) { // only add if it is not found
            personRoleJpaRepository.save(role);
        }
    }

    public List<PersonRole> listAllRoles() {
        return personRoleJpaRepository.findAll();
    }

    public PersonRole findRole(String roleName) {
        return personRoleJpaRepository.findByName(roleName);
    }

    public void addRoleToPerson(String email, String roleName) { // by passing in the two strings you are giving the
                                                                 // user that certain role
        Person person = personJpaRepository.findByEmail(email);
        if (person != null) { // verify person
            PersonRole role = personRoleJpaRepository.findByName(roleName);
            if (role != null) { // verify role
                boolean addRole = true;
                for (PersonRole roleObj : person.getRoles()) { // only add if user is missing role
                    if (roleObj.getName().equals(roleName)) {
                        addRole = false;
                        break;
                    }
                }
                if (addRole)
                    person.getRoles().add(role); // everything is valid for adding role
            }
        }
    }

}