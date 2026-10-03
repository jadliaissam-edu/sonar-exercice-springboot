package com.jadli.bookstore.level4.billing;

import com.jadli.bookstore.domain.AppUser;
import com.jadli.bookstore.domain.BookOrder;
import org.springframework.stereotype.Service;

@Service
public class ShippingLabelService {

    public String buildLabel(BookOrder order, AppUser customer, String street, String city, String postCode, String country) {
        StringBuilder sb = new StringBuilder();
        sb.append("SHIP TO ORDER #").append(order.getId()).append("\n");
        sb.append("Recipient: ").append(customer.getUsername()).append("\n");
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
        sb.append("====================\n");
        sb.append("Items: ").append(order.getItems().size()).append("\n");
        sb.append("Weight class: ").append(order.getItems().size() > 3 ? "HEAVY" : "LIGHT").append("\n");
        return sb.toString();
    }
}
