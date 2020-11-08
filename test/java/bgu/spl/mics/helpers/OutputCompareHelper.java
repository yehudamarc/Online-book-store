package bgu.spl.mics.helpers;

import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

public class OutputCompareHelper {
    private int total;
    private BookInventoryInfo[] books;
    private Customer[] customers;
    private OrderReceipt[] receipts;

    public OutputCompareHelper(BookInventoryInfo[] books, Customer[] customers, OrderReceipt[] receipts, int total) {
        this.books = books;
        this.customers = customers;
        this.receipts = receipts;
        this.total = total;
    }

    public int getTotal() {
        return total;
    }

    public Customer[] getCustomers() {
        return customers;
    }

    public BookInventoryInfo[] getBooks() {
        return books;
    }

    public OrderReceipt[] getReceipts() {
        return receipts;
    }
}
