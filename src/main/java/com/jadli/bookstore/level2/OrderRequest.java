package com.jadli.bookstore.level2;

import java.util.List;

public record OrderRequest(Long userId, List<OrderLine> lines) {
}
