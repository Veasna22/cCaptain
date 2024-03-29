package io.veasna.ccaptain.service;

import io.veasna.ccaptain.domain.Customer;
import io.veasna.ccaptain.domain.Invoice;
import io.veasna.ccaptain.domain.Stats;
import org.springframework.data.domain.Page;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 25/3/24 14:09
 */
public interface CustomerService {

    Customer createCustomer(Customer customer);
    Customer updateCustomer(Customer customer);
    Page<Customer> getCustomers(int page, int size);
    Iterable<Customer> getCustomers();
    Customer getCustomer(Long id);
    Page<Customer> searchCustomers(String name, int page, int size);
    Invoice getInvoice(Long id);
    Invoice createInvoice(Invoice invoice);
    Page<Invoice>  getInvoices(int page, int size);
    void addInvoiceToCustomer(Long id, Invoice invoice);


    Stats getStats();
}
