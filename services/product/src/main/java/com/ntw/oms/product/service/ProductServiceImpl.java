//////////////////////////////////////////////////////////////////////////////
// Copyright 2020 Anurag Yadav (anurag.yadav@newtechways.com)               //
//                                                                          //
// Licensed under the Apache License, Version 2.0 (the "License");          //
// you may not use this file except in compliance with the License.         //
// You may obtain a copy of the License at                                  //
//                                                                          //
//     http://www.apache.org/licenses/LICENSE-2.0                           //
//                                                                          //
// Unless required by applicable law or agreed to in writing, software      //
// distributed under the License is distributed on an "AS IS" BASIS,        //
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. //
// See the License for the specific language governing permissions and      //
// limitations under the License.                                           //
//////////////////////////////////////////////////////////////////////////////

package com.ntw.oms.product.service;

import com.ntw.oms.product.cache.CacheManager;
import com.ntw.oms.product.dao.ProductDao;
import com.ntw.oms.product.dao.ProductDaoFactory;
import com.ntw.oms.product.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by anurag on 30/05/17.
 */
@Configuration
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@Component
public class ProductServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductDaoFactory productDaoFactory;

    private ProductDao productDaoBean;

    @Value("${database.type}")
    private String productDBType;

    @Autowired
    private CacheManager cacheManager;

    @PostConstruct
    public void postConstruct()
    {
        this.productDaoBean = productDaoFactory.getProductDao(productDBType);
    }

    public ProductDao getProductDaoBean() {
        return productDaoBean;
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    /////// Redis Product cache is set to no eviction policy as the default policy ////////
    ///////////////////////////////////////////////////////////////////////////////////////

    public List<Product> getProducts() {
        List<String> productIds = cacheManager.getProductIds();
        if (productIds == null || productIds.isEmpty()) {
            productIds = getProductDaoBean().getProductIds();
            cacheManager.addProductIds(productIds);
        }
        if (productIds != null && !productIds.isEmpty()) {
            List<Product> products = getProductsByIds(productIds);
            if (products != null) {
                products.sort(new Comparator<Product>() {
                    @Override
                    public int compare(Product o1, Product o2) {
                        return o1.getId().compareTo(o2.getId());
                    }
                });
                return products;
            }
        }
        return new LinkedList<>();
    }

    public List<Product> getProductsByIds(List<String> ids) {
        Map<String, Product> allProductMap = new HashMap<>();
        List<String> cacheMissIds;
        List<Product> cacheProducts = cacheManager.getProducts(ids);
        if (cacheProducts == null) {
            cacheMissIds = ids;
        } else {
            // Find missing products in cache
            cacheProducts.forEach(product -> {
                if (product != null) allProductMap.put(product.getId(), product);
            });
            cacheMissIds = new LinkedList<>();
            ids.forEach(id -> {
                Product product = allProductMap.get(id);
                if (product == null) cacheMissIds.add(id);
            });
        }
        if (cacheMissIds.size() == 0) {
            // All products found in cache
            return cacheProducts;
        }
        // Get missing products from DB
        List<Product> missingProducts = getProductDaoBean().getProducts(cacheMissIds);
        cacheManager.addProducts(missingProducts);
        // Put missing products in map to make it complete
        missingProducts.forEach(product -> allProductMap.put(product.getId(), product));
        // Get all products from map in the order of ids
        List<Product> allProducts = new LinkedList<>();
        ids.forEach(id -> {
            allProducts.add(allProductMap.get(id));
        });
        return allProducts;
    }

    public Product getProduct(String id) {
        Product product = cacheManager.getProduct(id);
        if (product == null) {
            product = getProductDaoBean().getProduct(id);
            cacheManager.addProduct(product);
        }
        return product;
    }

    public boolean addProduct(Product product) {
        boolean success = getProductDaoBean().addProduct(product);
        if (success)
            cacheManager.addProduct(product);
        return success;
    }

    public Product modifyProduct(Product product) {
        product = getProductDaoBean().modifyProduct(product);
        if (product != null)
            cacheManager.addProduct(product);
        return product;
    }

    public boolean removeProduct(String id) {
        cacheManager.removeProduct(id);
        return getProductDaoBean().removeProduct(id);
    }

    public boolean removeProducts() {
        cacheManager.removeProducts();
        return getProductDaoBean().removeProducts();
    }

}
