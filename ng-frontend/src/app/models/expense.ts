export interface Expense {
    id: number;
    // Change to category: number next time when you associate it with category ID
    category: string;
    amount: number;
    date: string;
    description: string;
}