package io.veasna.ccaptain.repository;

import io.veasna.ccaptain.domain.Customer;
import io.veasna.ccaptain.domain.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 25/3/24 14:16
 */
public interface InvoiceRepository extends PagingAndSortingRepository<Invoice, Long>, ListCrudRepository<Invoice, Long> {

//    Page<Invoice> findByNameContaining(String name, Pageable pageable);
//
//


}
