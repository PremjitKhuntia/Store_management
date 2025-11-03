package com.example.demo.service;

import com.example.demo.entity.Sale;
import com.example.demo.entity.Customer;
import com.example.demo.repository.SaleRepository;
import com.example.demo.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SaleService {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    // ✅ Create or update a sale
    public Sale saveSale(Sale sale) {

        sale.setSaleDate(java.time.LocalDate.now());
        sale.setTotalAmount(sale.getQuantity() * sale.getPrice());  // ✅ auto calculate

        // ✅ Save the sale
        Sale savedSale = saleRepository.save(sale);

        // ✅ Save or update customer
        if (sale.getCustomerEmail() != null) {

            Customer customer = customerRepository.findByEmail(sale.getCustomerEmail())
                    .orElse(new Customer());

            customer.setName(sale.getCustomerName());
            customer.setEmail(sale.getCustomerEmail());
            customer.setPhone(sale.getCustomerPhone());

            customerRepository.save(customer);
        }

        return savedSale;
    }

    // ✅ Fetch all sales
    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    public Optional<Sale> getSaleById(Long id) {
        return saleRepository.findById(id);
    }

    public void deleteSale(Long id) {
        saleRepository.deleteById(id);
    }

    public double getTotalRevenue() {
        return saleRepository.findAll()
                .stream()
                .mapToDouble(Sale::getTotalAmount)
                .sum();
    }

    // ✅ Fetch unique customers
    public List<Map<String, Object>> getAllCustomers() {
        List<Map<String, Object>> customers = new ArrayList<>();
        List<Customer> customerList = customerRepository.findAll();

        for (Customer customer : customerList) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", customer.getName());
            map.put("email", customer.getEmail());
            map.put("phone", customer.getPhone());
            customers.add(map);
        }

        return customers;
    }

    // ✅ update sale info
    public Sale updateSale(Long id, Sale updatedSale) {
        Sale existingSale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found with id: " + id));

        existingSale.setCustomerName(updatedSale.getCustomerName());
        existingSale.setProductName(updatedSale.getProductName());
        existingSale.setPrice(updatedSale.getPrice()); // ✅ added
        existingSale.setQuantity(updatedSale.getQuantity());
        existingSale.setTotalAmount(updatedSale.getQuantity() * updatedSale.getPrice()); // ✅ added
        existingSale.setSaleDate(updatedSale.getSaleDate());

        return saleRepository.save(existingSale);
    }
}
