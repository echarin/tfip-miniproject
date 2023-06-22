package ibf2022.tfipminiproject.exceptions;

public class BudgetNotFoundException extends ResourceNotFoundException {
    public BudgetNotFoundException(String message) {
        super(message);
    }
}
