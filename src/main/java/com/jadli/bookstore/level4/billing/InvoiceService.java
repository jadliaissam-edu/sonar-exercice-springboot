package com.jadli.bookstore.level4.billing;

import com.jadli.bookstore.domain.AppUser;
import com.jadli.bookstore.domain.BookOrder;
import com.jadli.bookstore.domain.OrderItem;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService {

    public String buildInvoice(BookOrder order, AppUser customer, String street, String city, String postCode, String country) {
        StringBuilder sb = new StringBuilder();
        sb.append("INVOICE #").append(order.getId()).append("\n");
        sb.append("Customer: ").append(customer.getUsername()).append("\n");
        String line1 = street == null ? "" : street.trim();
        String line2 = city == null ? "" : city.trim().toUpperCase();
        String line3 = postCode == null ? "" : postCode.trim().replace(" ", "");
        String line4 = country == null ? "LT" : country.trim().toUpperCase();
        if (line1.isEmpty()) {
            line1 = "(no street)";
        }
        if (line2.isEmpty()) {
            line2 = "(no city)";
        }
        if (line3.length() > 8) {
            line3 = line3.substring(0, 8);
        }
        if (line4.length() > 2) {
            line4 = line4.substring(0, 2);
        }
        sb.append(line1).append("\n");
        sb.append(line2).append(" ").append(line3).append("\n");
        sb.append(line4).append("\n");
        sb.append("--------------------\n");
        for (OrderItem item : order.getItems()) {
            sb.append(item.getQuantity()).append(" x ").append(item.getBookId())
                    .append(" @ ").append(item.getUnitPrice()).append("\n");
        }
        sb.append("TOTAL: ").append(order.getTotal()).append("\n");
        return sb.toString();
    }
}
