package ibf2022.tfipminiproject.services;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import ibf2022.tfipminiproject.dtos.BudgetDTO;
import ibf2022.tfipminiproject.exceptions.ResourceNotFoundException;
import ibf2022.tfipminiproject.mappers.BudgetMapper;
import ibf2022.tfipminiproject.sqlentities.Budget;
import ibf2022.tfipminiproject.sqlentities.User;
import ibf2022.tfipminiproject.sqlrepositories.BudgetRepository;
import ibf2022.tfipminiproject.sqlrepositories.UserRepository;

@SpringBootTest
public class BudgetServiceTest {

    /* 
     * Use mocks when you want to completely control the behaviour of the dependent object e.g. userRepo
     * and isolate the class under test e.g. BudgetService
     * This is a good fit for unit tests where you want to test a single class in isolation
     * 
     * Use spies when you want to control only part of the behaviour (like stubbing some methods but not others)
     * and the real method invocations don't have side effects that interfere with the test
     */
    @Spy
    private UserRepository userRepository;

    @Spy
    private BudgetRepository budgetRepository;

    @Spy
    private BudgetMapper budgetMapper;

    @InjectMocks
    private BudgetService budgetService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindByUser() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        Budget budget = new Budget();
        BudgetDTO budgetDTO = new BudgetDTO();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        // doThrow(new RuntimeException("Test exception")).when(userRepository).findById(any(UUID.class));
        when(budgetRepository.findByUser(user)).thenReturn(Optional.of(budget));
        when(budgetMapper.budgetToBudgetDTO(budget)).thenReturn(budgetDTO);

        BudgetDTO result = budgetService.findByUser(userId);
        assertSame(budgetDTO, result);

        verify(userRepository).findById(userId);
        verify(budgetRepository).findByUser(user);
        verify(budgetMapper).budgetToBudgetDTO(budget);
    }

    @Test
    public void testSave() {
        UUID userId = UUID.randomUUID();
        BudgetDTO budgetDTO = new BudgetDTO();
        budgetDTO.setName("Test Budget");
        budgetDTO.setMoneyPool(new BigDecimal("1000.00"));

        User user = new User();
        Budget savedBudget = new Budget();
        savedBudget.setName("Test Budget");
        savedBudget.setMoneyPool(new BigDecimal("1000.00"));
        BudgetDTO savedBudgetDTO = new BudgetDTO();
        savedBudgetDTO.setName("Test Budget");
        savedBudgetDTO.setMoneyPool(new BigDecimal("1000.00"));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(budgetRepository.findByUser(user)).thenReturn(Optional.of(savedBudget));
        when(budgetMapper.budgetToBudgetDTO(savedBudget)).thenReturn(savedBudgetDTO);

        BudgetDTO result = budgetService.save(userId, budgetDTO);
        assertSame(savedBudgetDTO, result);

        verify(userRepository).findById(userId);
        verify(userRepository).save(user);
        verify(budgetRepository).findByUser(user);
        verify(budgetMapper).budgetToBudgetDTO(savedBudget);
    }

    @Test
    public void testDeleteUserNotFound() {
        UUID userId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> budgetService.delete(userId, budgetId));

        verify(userRepository).findById(userId);
    }

    @Test
    public void testDeleteBudgetNotFound() {
        UUID userId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();

        User user = new User();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> budgetService.delete(userId, budgetId));

        verify(userRepository).findById(userId);
        verify(budgetRepository).findById(budgetId);
    }

    @Test
    public void testDeleteNotOwner() {
        UUID userId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();

        User user = new User();
        Budget budget = new Budget();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));
        when(budgetRepository.existsByUserAndId(user, budgetId)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> budgetService.delete(userId, budgetId));

        verify(userRepository).findById(userId);
        verify(budgetRepository).findById(budgetId);
        verify(budgetRepository).existsByUserAndId(user, budgetId);
    }

    @Test
    public void testDeleteSuccess() {
        UUID userId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();

        User user = new User();
        Budget budget = new Budget();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));
        when(budgetRepository.existsByUserAndId(user, budgetId)).thenReturn(true);

        budgetService.delete(userId, budgetId);

        verify(userRepository).findById(userId);
        verify(budgetRepository).findById(budgetId);
        verify(budgetRepository).existsByUserAndId(user, budgetId);
        verify(userRepository).save(user);
    }
}
