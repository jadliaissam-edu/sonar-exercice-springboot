package com.jadli.bookstore.level2;

import com.jadli.bookstore.domain.BookOrder;
import com.jadli.bookstore.repository.BookOrderRepository;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import org.springframework.stereotype.Service;

@Service
public class OrderExportService {

    private final BookOrderRepository orders;

    public OrderExportService(BookOrderRepository orders) {
        this.orders = orders;
    }

    public int exportOrders(Path target) throws IOException {
        FileWriter writer = new FileWriter(target.toFile());
        writer.write("id,userId,status,total\n");
        int count = 0;
        for (BookOrder order : orders.findAll()) {
            writer.write(order.getId() + "," + order.getUserId() + "," + order.getStatus() + "," + order.getTotal() + "\n");
            count++;
        }
        writer.flush();
        return count;
    }
}
