export interface Expense {
    id: number;
    // Change to category: number next time when you associate it with category ID
    category: string;
    amount: number;
    date: string;
    description: string | null;
}

export interface Category {
    id: number;
    name: string;
    budgetedAmount: number;
    transactions: Expense[];
}

export interface CategoryGroup {
    id: number;
    name: string;
    categories: Category[];
}

export interface Budget {
    id: number;
    name: string;
    moneyPool: number;
    categoryGroups: CategoryGroup[];
}

export interface User {
    id: number;
    email: string;
    password: string;
    budget: Budget;
}