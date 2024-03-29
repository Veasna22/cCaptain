package io.veasna.ccaptain.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 26/3/24 10:34
 */
@Data
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Stats {
    private int totalCustomers;
    private int totalInvoices;
    private double totalBilled;
}
