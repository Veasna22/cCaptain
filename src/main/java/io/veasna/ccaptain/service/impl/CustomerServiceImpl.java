package io.veasna.ccaptain.service.impl;

import io.veasna.ccaptain.domain.Customer;
import io.veasna.ccaptain.domain.Invoice;
import io.veasna.ccaptain.domain.Stats;
import io.veasna.ccaptain.repository.CustomerRepository;
import io.veasna.ccaptain.repository.InvoiceRepository;
import io.veasna.ccaptain.rowmapper.StatsRowMapper;
import io.veasna.ccaptain.service.CustomerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

import static io.veasna.ccaptain.query.CustomerQuery.STATS_QUERY;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.springframework.data.domain.PageRequest.of;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 25/3/24 14:14
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j

public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final InvoiceRepository invoiceRepository;
    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Customer createCustomer(Customer customer) {
        customer.setCreatedAt(new Date());
        return customerRepository.save(customer);
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Page<Customer> getCustomers(int page, int size) {
        return customerRepository.findAll(of(page, size));

    }

    @Override
    public Iterable<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Customer getCustomer(Long id) {
        return customerRepository.findById(id).get();
    }

    @Override
    public Page<Customer> searchCustomers(String name, int page, int size) {
        return customerRepository.findByNameContaining(name, of(page, size));
    }

    @Override
    public Invoice getInvoice(Long id) {
        return invoiceRepository.findById(id).get();
    }

    @Override
    public Invoice createInvoice(Invoice invoice) {
        invoice.setInvoiceNumber(randomAlphabetic(8).toUpperCase());
        return invoiceRepository.save(invoice);
    }

    @Override
    public Page<Invoice> getInvoices(int page, int size) {
        return invoiceRepository.findAll(of(page, size));
    }

    @Override
    public void addInvoiceToCustomer(Long id, Invoice invoice) {
        invoice.setInvoiceNumber(randomAlphabetic(8).toUpperCase());
        Customer customer = customerRepository.findById(id).get();
        invoice.setCustomer(customer);
        invoiceRepository.save(invoice);
    }

    @Override
    public Stats getStats() {
        return jdbc.queryForObject(STATS_QUERY, Map.of(), new StatsRowMapper());
    }

}
