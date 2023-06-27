// entities.ts
export interface Comment {
    id: string;
    text: string;
    expenseId: string;
}

export interface Expense {
    id: string;
    amount: number;
    date: string;
    createdAt: string;
    categoryName: string;
}

export interface Category {
    id: string;
    name: string;
    budgetedAmount: number;
    createdAt: string;
}

export interface Budget {
    id: string;
    name: string;
    moneyPool: number;
    createdAt: string;
}

export interface User {
    id: string;
    email: string;
}