package org.example.restapi.database;

import org.example.restapi.models.Product;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHandler {
    private Map<Integer, Product> products = new HashMap<>();
    private int idCounter = 1;

    public DatabaseHandler() {
        // 添加示例产品
        insertSampleProducts();

    }

    private void insertSampleProducts() {
        products.put(idCounter, new Product(idCounter++, "Laptop Pro", "Leistungsstarker Laptop für Profis", 1299.99, 10));
        products.put(idCounter, new Product(idCounter++, "Bluetooth-Kopfhörer", "Kabellose Kopfhörer mit Noise-Cancelling", 199.99, 30));
        products.put(idCounter, new Product(idCounter++, "Smartwatch", "Fitness-Tracker mit vielen Funktionen", 249.99, 15));
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    public Product getProductById(int id) {
        return products.get(id);
    }

    public void insertProduct(Product product) {
        if (product.getId() == null) {
            product.setId(idCounter++);
        }
        products.put(product.getId(), product);
    }

    public boolean updateProduct(Product product) {
        if (!products.containsKey(product.getId())) {
            return false;
        }
        products.put(product.getId(), product);
        return true;
    }

    public boolean deleteProduct(int id) {
        if (!products.containsKey(id)) {
            return false;
        }
        products.remove(id);
        return true;
    }
}